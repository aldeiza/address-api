package com.api.address.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.api.address.dao.IEnderecoRepositorio;
import com.api.address.dao.IMunicipioRepositorio;
import com.api.address.dao.IUFRepositorio;
import com.api.address.entity.Endereco;
import com.api.address.entity.Municipio;
import com.api.address.entity.UF;
import com.api.address.util.StringUtil;
import org.codehaus.plexus.util.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

@Service
public class EnderecoServico {
	private static final Logger LOGGER = Logger.getLogger(EnderecoServico.class.getName());
	private static final String ERRO = "ERRO: {0}";

	@Value("${cep.target}")
	private String targetCep;

	@Value("${cep.path}")
	private String pathCep;

	@Autowired
	private RestServico restServico;

	@Autowired
	private IEnderecoRepositorio enderecoDao;

	@Autowired
	private IMunicipioRepositorio municipioDao;

	@Autowired
	private IUFRepositorio ufDao;

	public boolean naoExistePorCep(String cep) {
		return !enderecoDao.findByCep(StringUtil.removerHifen(cep)).stream().findFirst().isPresent();
	}

	public void salvar(Endereco endereco) {
		enderecoDao.save(endereco);
	}

	public Collection<Endereco> buscarTodos(String nome) {
		return enderecoDao.buscaEnderecos(StringUtil.removerAcentosUpperCase(nome));
	}

	public Endereco buscarPorCep(String cep) {
		try {
			Optional<Endereco> endereco = enderecoDao.findByCep(StringUtil.removerHifen(cep)).stream().findFirst();
			// Caso não haja o cep no banco, procurar na api VIACEP
			if (endereco.isPresent()) {
				ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
				executorService.submit(() -> {
					try {
						Endereco enderecoApi = buscarEnderecoRest(cep);
						Endereco enderecoBanco = endereco.get();
						enderecoBanco.setBairro(enderecoApi.getBairro());
						enderecoBanco.setLogradouro(enderecoApi.getLogradouro());
						enderecoBanco.setMunicipio(enderecoApi.getMunicipio());				
						enderecoDao.save(enderecoBanco);
						LOGGER.info("Endereço atualizado.");
					} catch (Exception e) {
						LOGGER.log(Level.SEVERE, ERRO, ExceptionUtils.getStackTrace(e));
					}
				});
				LOGGER.info("Retornando endereço.");
				return endereco.get();
			} else {
				Endereco enderecoApi = buscarEnderecoRest(cep);
				enderecoDao.save(enderecoApi);
				LOGGER.info("Endereço atualizado.");
				return enderecoApi;
			}
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, ERRO, ExceptionUtils.getStackTrace(e));
			return null;
		}
	}

	private Endereco buscarEnderecoRest(String cep) throws Exception {
		LOGGER.info("Buscando endereço na VIACEP API.");
		Endereco enderecoApi = restServico.buscarEndereco(targetCep, StringUtil.removerHifen(cep) + pathCep);
		Municipio municipio = buscarPorNomeUF(enderecoApi.getLocalidade(), enderecoApi.getUf());
		enderecoApi.setMunicipio(municipio);
		enderecoApi.setCep(StringUtil.removerHifen(cep));
		return enderecoApi;
	}

	public void lerArquivoCSV(byte[] arquivo) {
		try {
			CSVParser parser = new CSVParserBuilder().withSeparator('\t').build();
			CSVReader reader = new CSVReaderBuilder(new InputStreamReader(new ByteArrayInputStream(arquivo), "UTF-8"))
					.withCSVParser(parser).build();
			String[] proximaLinha;
			while ((proximaLinha = reader.readNext()) != null) {
				if (todosOsValoresDaLinhaEstaoVazios(proximaLinha)) {
					String cep = proximaLinha[0];
					if (naoExistePorCep(StringUtil.removerHifen(cep))) {
						String[] localidade = proximaLinha[1].split("/");
						String nomeMunicipio = localidade[0];
						String siglaUF = localidade[1];
						String bairro = proximaLinha[2];
						String logradouro = proximaLinha[3];
						Municipio municipio = buscarPorNomeUF(nomeMunicipio, siglaUF);
						Endereco endereco = new Endereco(cep, logradouro, bairro, municipio);
						enderecoDao.save(endereco);
					}
				}
			}
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, ERRO, ExceptionUtils.getStackTrace(e));
		}
	}

	private Municipio buscarPorNomeUF(String nome, String ufNome) {
		Collection<UF> ufs = ufDao.buscarUnidadesFederativas(ufNome);
		return municipioDao
				.buscarMunicipiosPorUf(ufs.stream().findFirst().get().getId(), StringUtil.removerAcentosUpperCase(nome))
				.stream().findFirst().get();
	}
	
	private boolean todosOsValoresDaLinhaEstaoVazios(String[] linha) {
		boolean returnValue = false;
		for (String s : linha) {
			if (s.length() != 0) {
				returnValue = true;
			}
		}
		return returnValue;
	}

}

package com.api.address.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.api.address.util.ArquivoUtil;
import org.codehaus.plexus.util.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ConsultasProgramadas {
	private static final Logger LOGGER = Logger.getLogger(ConsultasProgramadas.class.getName());
	private static final String ERRO = "ERRO: {0}";
	private static final String NOME_CSV = "ceps.csv";

	@Value("${ibge.target}")
	private String targetIBGE;

	@Value("${csv.target}")
	private String targetCsv;

	@Value("${ibge.path}")
	private String pathIBGE;

	@Autowired
	private EnderecoServico enderecoServico;

	@Autowired
	private MunicipioServico municipioServico;

	@Autowired
	private RestServico restServico;

	@EventListener(ApplicationReadyEvent.class)
	@Scheduled(cron = "0 9 * * 1 ?", zone = "America/Recife")
	public void sincronizarMunicipios() {
		try {
			LOGGER.info("Consultando munícipios na IBGE API");
			restServico.buscarMunicipios(targetIBGE, pathIBGE).stream()
					.filter(municipio -> municipioServico.naoExistePorId(municipio.getId())).forEach((municipio -> {
						municipio.setUf(municipio.getMicrorregiao().getMesorregiao().getuF());
						municipioServico.salvar(municipio);
					}));
			LOGGER.info("Consulta aos munícipios da IBGE API finalizada");
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, ERRO, ExceptionUtils.getStackTrace(e));
		}
	}

	@EventListener(ApplicationReadyEvent.class)
	public void popularEnderecos() {
		try {
			LOGGER.info("Populando países com .csv da Cep.lá");
			byte[] csv = ArquivoUtil.baixarPorPath(targetCsv, NOME_CSV);
			enderecoServico.lerArquivoCSV(csv);
			LOGGER.info("Finalizada a população de endereços com .csv da Cep.lá");
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, ERRO, ExceptionUtils.getStackTrace(e));
		}
	}
}

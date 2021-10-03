package com.api.address.controller;

import java.util.Collection;

import com.api.address.entity.Endereco;
import com.api.address.service.EnderecoServico;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/enderecos")
public class EnderecoRecurso {

	@Autowired
	private EnderecoServico enderecoServico;
	
	/**
	 * Retorna os todos os endereços.
	 **/
	@GetMapping(path = "", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Collection<Endereco>> listarTodos(@RequestParam(defaultValue = "") String nome) {
		return ResponseEntity.ok().body(enderecoServico.buscarTodos(nome));
	}
	
	/**
	 * Retorna o endereço pelo cep passado como parâmetro.
	 **/
	@GetMapping(path = "/cep/{cep}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ResponseEntity<Endereco> listarPorCep(@PathVariable String cep) {
		return ResponseEntity.ok().body(enderecoServico.buscarPorCep(cep));
	}
}

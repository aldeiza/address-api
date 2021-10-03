package com.api.address.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UF {
	private static final long serialVersionUID = 1L;

	@Id
	private Long id;

	private String nome;
	private String sigla;

	// Getters and Setters

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
}

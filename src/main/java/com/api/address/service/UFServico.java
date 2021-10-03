package com.api.address.service;

import java.util.Collection;

import com.api.address.dao.IUFRepositorio;
import com.api.address.entity.UF;
import com.api.address.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UFServico  {

	@Autowired
	private IUFRepositorio dao;

	public UF buscarPorId(Long id) {
		return dao.findById(id).get();
	}

	public Collection<UF> buscarTodos(String nome) {
		return dao.buscarUnidadesFederativas(StringUtil.removerAcentosUpperCase(nome));
	}
}

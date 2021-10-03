package com.api.address.dao;

import java.util.Collection;

import com.api.address.entity.Endereco;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IEnderecoRepositorio extends JpaRepository<Endereco, Long> {
	
	Collection<Endereco> findByCep(String cep);

	@Query(value = "SELECT * FROM endereco e WHERE upper(unaccent(e.logradouro)) LIKE %:nome% ORDER BY e.nome ASC", nativeQuery = true)
	Collection<Endereco> buscaEnderecos(@Param("nome") String nome);

}

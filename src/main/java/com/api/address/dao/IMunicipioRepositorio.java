package com.api.address.dao;

import java.util.Collection;

import com.api.address.entity.Municipio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface IMunicipioRepositorio extends JpaRepository<Municipio, Long> {

	@Query(value = "SELECT * FROM municipio m WHERE upper(unaccent(m.nome)) LIKE %:nome% ORDER BY m.nome ASC", nativeQuery = true)
	Collection<Municipio> buscaMunicipios(@Param("nome") String nome);

	@Query(value = "SELECT * FROM municipio m WHERE m.uf_id = :uf AND upper(unaccent(m.nome)) LIKE %:nome% ORDER BY m.nome ASC", nativeQuery = true)
	Collection<Municipio> buscarMunicipiosPorUf(@Param("uf") Long uf, @Param("nome") String nome);

}

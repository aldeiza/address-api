package com.api.address.dao;

import java.util.Collection;

import com.api.address.entity.UF;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IUFRepositorio extends JpaRepository<UF, Long> {

    @Query(value = "SELECT * FROM uf m WHERE upper(unaccent(m.sigla)) LIKE %:nome% ORDER BY m.nome ASC", nativeQuery = true)
    Collection<UF> buscarUnidadesFederativas(@Param("nome") String nome);
}

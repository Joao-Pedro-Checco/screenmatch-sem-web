package com.naosei.screenmatch.repository;

import com.naosei.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    Optional<Serie> findByTituloIgnoreCase(String nomeSerie);

    List<Serie> findAllByTituloContainingIgnoreCase(String nomeSerie);

    List<Serie> findAllByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String ator, Double avaliacao);

    List<Serie> findTop5ByOrderByAvaliacaoDesc();
}

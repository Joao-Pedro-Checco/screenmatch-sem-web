package com.naosei.screenmatch.repository;

import com.naosei.screenmatch.model.Categoria;
import com.naosei.screenmatch.model.Episodio;
import com.naosei.screenmatch.model.Serie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    Optional<Serie> findByTituloIgnoreCase(String nomeSerie);

    List<Serie> findAllByTituloContainingIgnoreCase(String nomeSerie);

    List<Serie> findAllByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(String ator, Double avaliacao);

    List<Serie> findTop5ByOrderByAvaliacaoDesc();

    List<Serie> findByGenero(Categoria categoria);

    @Query("select s from Serie s where s.totalTemporadas <= :temporadas and s.avaliacao >= :avaliacao")
    List<Serie> seriesPorTemporadaEAvaliacao(Integer temporadas, Double avaliacao);

    @Query("select e from Serie s join s.episodios e where e.titulo ilike %:nomeEpisodio%")
    List<Episodio> episodiosPorNome(String nomeEpisodio);
}

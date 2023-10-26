package com.naosei.screenmatch.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.OptionalDouble;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Episodio {
    private String titulo;
    private Integer temporada;
    private Integer numero;
    private Double avaliacao;
    private LocalDate lancamento;

    public Episodio(Integer temporada, DadosEpisodio dadosEpisodio) {
        this.temporada = temporada;
        this.titulo = dadosEpisodio.titulo();
        this.numero = dadosEpisodio.numero();
        this.avaliacao = OptionalDouble.of(Double.parseDouble(dadosEpisodio.avaliacao())).orElse(0);
        try {
            this.lancamento = LocalDate.parse(dadosEpisodio.lancamento());
        } catch (DateTimeParseException e) {
            this.lancamento = null;
        }
    }
}

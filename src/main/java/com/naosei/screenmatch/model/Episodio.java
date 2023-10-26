package com.naosei.screenmatch.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.OptionalDouble;

@Entity(name = "episodio")
@Table(name = "episodios")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@ToString
public class Episodio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private Integer temporada;
    private Integer numero;
    private Double avaliacao;
    private LocalDate lancamento;

    @ManyToOne
    private Serie serie;

    public Episodio(Integer temporada, DadosEpisodio dadosEpisodio) {
        this.temporada = temporada;
        this.titulo = dadosEpisodio.titulo();
        this.numero = dadosEpisodio.numero();
        try {
            this.avaliacao = Double.parseDouble(dadosEpisodio.avaliacao());
            this.lancamento = LocalDate.parse(dadosEpisodio.lancamento());
        } catch (NumberFormatException e) {
          this.avaliacao = 0.0;
        } catch (DateTimeParseException e) {
            this.lancamento = null;
        }
    }
}

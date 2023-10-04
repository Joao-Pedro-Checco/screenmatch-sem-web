package com.naosei.screenmatch.model;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

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
        try {
            this.avaliacao = Double.valueOf(dadosEpisodio.avaliacao());
            this.lancamento = LocalDate.parse(dadosEpisodio.lancamento());
        } catch (NumberFormatException e) {
            this.avaliacao = 0.0;
        } catch (DateTimeParseException e) {
            this.lancamento = null;
        }
    }

    public String getTitulo() {
        return this.titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Integer getTemporada() {
        return this.temporada;
    }

    public void setTemporada(Integer temporada) {
        this.temporada = temporada;
    }

    public Integer getNumero() {
        return this.numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Double getAvaliacao() {
        return this.avaliacao;
    }

    public void setAvaliacao(Double avaliacao) {
        this.avaliacao = avaliacao;
    }

    public LocalDate getLancamento() {
        return this.lancamento;
    }

    public void setLancamento(LocalDate lancamento) {
        this.lancamento = lancamento;
    }

    @Override
    public String toString() {
        return "Temporada: " + this.temporada + " - " +
                "Título: " + this.titulo + " - " +
                "Número: " + this.numero + " - " +
                "Avaliação: " + this.avaliacao + " - " +
                "Lançamento: " + this.lancamento;
    }
}

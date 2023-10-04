package com.naosei.screenmatch.main;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.naosei.screenmatch.model.DadosEpisodio;
import com.naosei.screenmatch.model.DadosSerie;
import com.naosei.screenmatch.model.DadosTemporada;
import com.naosei.screenmatch.service.ConsumoApi;
import com.naosei.screenmatch.service.ConverteDados;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private final Scanner scanner = new Scanner(System.in);
    private final ConsumoApi consumoApi = new ConsumoApi();
    private final ConverteDados converteDados = new ConverteDados();

    private final String API_KEY = "9c1586bd";

    public void exibeMenu() throws JsonProcessingException {
        // Pega o nome e formata o nome da série
        String nomeSerie = pegaNomeSerie();
        String endereco = String.format("https://www.omdbapi.com/?t=%s&apikey=%s", nomeSerie, API_KEY);

        // Consome a API
        var json = consumoApi.obterDados(endereco);

        // Converte os dados da API
        DadosSerie dados = converteDados.obterDados(json, DadosSerie.class);
        System.out.println(dados);

        // Exibe todas as temporadas da série
        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i <= dados.totalTemporadas(); i++) {
            var link = String.format("https://www.omdbapi.com/?t=%s&season=%d&apikey=%s", nomeSerie, i, API_KEY);
            json = consumoApi.obterDados(link);
            DadosTemporada dadosTemporada = converteDados.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }

        // Exibe todos os episódio de cada temporada
        for (int i = 0; i < dados.totalTemporadas(); i++) {
            List<DadosEpisodio> episodios = temporadas.get(i).episodios();
            System.out.println("Temporada " + (i+1) + ":");
            for (int j = 0; j < episodios.size(); j++) {
                System.out.println("  " + (j+1) + "- " + episodios.get(j).titulo());
            }
        }

        // Mesma coisa de cima com lambda
        // temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

        System.out.println("Fim da execução");
    }

    private String pegaNomeSerie() {
        System.out.print("Digite o nome da série que você quer ver: ");
        return scanner.nextLine().replace(" ", "+");
    }
}

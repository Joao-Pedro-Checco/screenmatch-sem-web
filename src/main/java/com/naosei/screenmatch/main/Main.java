package com.naosei.screenmatch.main;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.naosei.screenmatch.model.DadosSerie;
import com.naosei.screenmatch.model.DadosTemporada;
import com.naosei.screenmatch.model.Serie;
import com.naosei.screenmatch.service.ConsumoApi;
import com.naosei.screenmatch.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class Main {
    private final Scanner scanner = new Scanner(System.in);
    private final ConsumoApi consumoApi = new ConsumoApi();
    private final ConverteDados converteDados = new ConverteDados();

    private final String API_KEY = "9c1586bd";
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final List<DadosSerie> dadosSeries = new ArrayList<>();

    public void exibeMenu() throws JsonProcessingException {
        int opcao = -1;
        while (opcao != 0) {
            String menu = """
                1- Buscar Séries
                2- Buscar Episódios
                3- Listar Séries buscadas
                
                0- Sair
                """;

            System.out.println(menu);
            System.out.print("Escolha uma das opções acima: ");
            opcao = scanner.nextInt();
            scanner.nextLine();

            switch (opcao) {
                case 1 -> buscarSerieWeb();
                case 2 -> buscarEpisodioPorSerie();
                case 3 -> listarSeriesBuscadas();
                case 0 -> System.out.println("Saindo...");
                default -> System.out.println("Operação inválida!");
            }
        }
    }

    private void buscarSerieWeb() throws JsonProcessingException {
        DadosSerie dadosSerie = getDadosSerie();
        dadosSeries.add(dadosSerie);
        System.out.println(dadosSerie);
    }

    private DadosSerie getDadosSerie() throws JsonProcessingException {
        System.out.print("Digite o nome da séria para busca: ");
        String nomeSerie = scanner.nextLine().replace(" ", "+");
        String json = consumoApi.obterDados(ENDERECO + nomeSerie + "&apikey=" + API_KEY);
        return converteDados.obterDados(json, DadosSerie.class);
    }

    private void buscarEpisodioPorSerie() throws JsonProcessingException {
        DadosSerie dadosSerie = getDadosSerie();
        List<DadosTemporada> temporadas = new ArrayList<>();

        for (int i = 1; i <= dadosSerie.totalTemporadas(); i++) {
            String tituloSerie = dadosSerie.titulo().replace(" ", "+");
            String json = consumoApi.obterDados(ENDERECO + tituloSerie + "&season=" + i + "&apikey=" + API_KEY);
            DadosTemporada dadosTemporada = converteDados.obterDados(json, DadosTemporada.class);
            temporadas.add(dadosTemporada);
        }

        temporadas.forEach(System.out::println);
    }

    private void listarSeriesBuscadas() {
        List<Serie> series = dadosSeries.stream().map(Serie::new).toList();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }
}

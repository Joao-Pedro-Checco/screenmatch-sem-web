package com.naosei.screenmatch.main;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.naosei.screenmatch.model.DadosEpisodio;
import com.naosei.screenmatch.model.DadosSerie;
import com.naosei.screenmatch.model.DadosTemporada;
import com.naosei.screenmatch.model.Episodio;
import com.naosei.screenmatch.service.ConsumoApi;
import com.naosei.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
//        for (int i = 0; i < dados.totalTemporadas(); i++) {
//            List<DadosEpisodio> episodios = temporadas.get(i).episodios();
//            System.out.println("Temporada " + (i+1) + ":");
//            for (int j = 0; j < episodios.size(); j++) {
//                System.out.println("  " + (j+1) + "- " + episodios.get(j).titulo());
//            }
//        }

        System.out.println();

        // Mesma coisa de cima com lambda
        // temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));

        // Top N melhores episódios
        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d))
                ).collect(Collectors.toList());

        System.out.println("Top 10 melhores episódios");
        episodios.stream()
                .sorted(Comparator.comparing(Episodio::getAvaliacao).reversed())
                .limit(10)
                .forEach(System.out::println);

        System.out.println();

        // Episódios filtrados por ano
//        System.out.print("Ver episódios a partir de qual ano? ");
//        int ano = scanner.nextInt();
//
//        LocalDate dataBusca = LocalDate.of(ano, 1, 1);
//
//        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//        episodios.stream()
//                .filter(e -> e.getLancamento() != null && e.getLancamento().isAfter(dataBusca))
//                .forEach(e -> System.out.println(
//                        "Temporada: " + e.getTemporada() + " - " +
//                        "Episódio: " + e.getTitulo() + " - " +
//                        "Lançamento: " + e.getLancamento().format(formatador)
//                ));

        // Buscar episódio por nome
//        System.out.print("Digite o nome do episódio: ");
//        var trechoTitulo = scanner.nextLine().toLowerCase();
//        var resultado = episodios.stream()
//                .filter(e -> e.getTitulo().toLowerCase().contains(trechoTitulo))
//                .findFirst();
//        if (resultado.isPresent()) {
//            System.out.println("Episódio encontrado");
//            System.out.println("Temporada: " + resultado.get().getTemporada());
//        } else {
//            System.out.println("Episódio não encontrado");
//        }
//
//        System.out.println("Fim da execução");

        // Média de avaliação por temporadas
        Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getAvaliacao)));
        //System.out.println(avaliacoesPorTemporada);

        DoubleSummaryStatistics est = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
        System.out.println("Média: " + est.getAverage());
        System.out.println("Melhor episódio: " + est.getMax());
        System.out.println("Pior episódio: " + est.getMin());
        System.out.println("Quantidade de episódios: " + est.getCount());
    }

    private String pegaNomeSerie() {
        System.out.print("Digite o nome da série que você quer ver: ");
        return scanner.nextLine().replace(" ", "+");
    }
}

package com.naosei.screenmatch.main;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.naosei.screenmatch.model.*;
import com.naosei.screenmatch.repository.SerieRepository;
import com.naosei.screenmatch.service.ConsumoApi;
import com.naosei.screenmatch.service.ConverteDados;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private final Scanner scanner = new Scanner(System.in);
    private final ConsumoApi consumoApi = new ConsumoApi();
    private final ConverteDados converteDados = new ConverteDados();

    private final String API_KEY = "9c1586bd";
    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final SerieRepository repository;
    private List<Serie> series = new ArrayList<>();

    public Main(SerieRepository repository) {
        this.repository = repository;
    }

    public void exibeMenu() throws JsonProcessingException {
        int opcao = -1;
        while (opcao != 0) {
            String menu = """
                1- Buscar Séries
                2- Buscar Episódios
                3- Listar Séries buscadas
                4- Buscar Séries por título
                5- Buscar Séries por ator
                
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
                case 4 -> buscarSeriesPorTitulo();
                case 5 -> buscarSeriesPorAtor();
                case 0 -> System.out.println("Saindo...");
                default -> System.out.println("Operação inválida!");
            }
        }
    }

    private void buscarSerieWeb() throws JsonProcessingException {
        Serie serie = new Serie(getDadosSerie());
        repository.save(serie);
    }

    private DadosSerie getDadosSerie() throws JsonProcessingException {
        System.out.print("Digite o nome da série para busca: ");
        String nomeSerie = scanner.nextLine().replace(" ", "+");
        String json = consumoApi.obterDados(ENDERECO + nomeSerie + "&apikey=" + API_KEY);
        return converteDados.obterDados(json, DadosSerie.class);
    }

    private void buscarEpisodioPorSerie() throws JsonProcessingException {
        listarSeriesBuscadas();
        System.out.print("Escolha uma série pelo nome: ");
        String nomeSerie = scanner.nextLine();
        Optional<Serie> serie = repository.findByTituloIgnoreCase(nomeSerie);

        if (serie.isEmpty()) {
            System.out.println("Série não encontrada!");
        } else {
            var serieEncontrada = serie.get();
            List<DadosTemporada> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                String tituloSerie = serieEncontrada.getTitulo().replace(" ", "+");
                String json =
                        consumoApi.obterDados(ENDERECO + tituloSerie + "&season=" + i + "&apikey=" + API_KEY);
                DadosTemporada dadosTemporada = converteDados.obterDados(json, DadosTemporada.class);
                temporadas.add(dadosTemporada);
            }

            temporadas.forEach(System.out::println);
            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream().map(e -> new Episodio(d.numero(), e))).toList();

            serieEncontrada.setEpisodios(episodios);
            repository.save(serieEncontrada);
        }
    }

    private void listarSeriesBuscadas() {
        series = repository.findAll();
        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);
    }

    private void buscarSeriesPorTitulo() {
        System.out.print("Digite o título da série: ");
        String nomeSerie = scanner.nextLine();
        List<Serie> seriesEncontradas = repository.findAllByTituloContainingIgnoreCase(nomeSerie);
        if (seriesEncontradas.isEmpty()) {
            System.out.println("Série não encontrada!");
        } else {
            System.out.println("Resultados encontrados para '" + nomeSerie + "':");
            seriesEncontradas.forEach(System.out::println);
        }
    }

    private void buscarSeriesPorAtor() {
        System.out.print("Digite o nome do ator: ");
        String nomeAtor = scanner.nextLine();
        System.out.print("Com avaliações a partir de qual nota? ");
        Double avaliacao = scanner.nextDouble();
        scanner.nextLine();
        List<Serie> seriesEncontradas =
                repository.findAllByAtoresContainingIgnoreCaseAndAvaliacaoGreaterThanEqual(nomeAtor, avaliacao);

        System.out.println("Séries com '" + nomeAtor + "':");
        seriesEncontradas.forEach(s -> System.out.println(s.getTitulo() + " | Avaliação: " + s.getAvaliacao()));
    }
}

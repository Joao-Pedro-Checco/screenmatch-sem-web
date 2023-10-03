package com.naosei.screenmatch;

import com.naosei.screenmatch.model.DadosSerie;
import com.naosei.screenmatch.service.ConsumoApi;
import com.naosei.screenmatch.service.ConverteDados;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		ConsumoApi api = new ConsumoApi();
		var json = api.obterDados("https://www.omdbapi.com/?t=the+boys&apikey=9c1586bd");
		ConverteDados conversor = new ConverteDados();
		DadosSerie dados = conversor.obterDados(json, DadosSerie.class);
		System.out.println(dados);
	}
}

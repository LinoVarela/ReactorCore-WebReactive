package com.project.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;

import com.project.demo.entity.Media;

import reactor.core.publisher.Flux;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ReactiveClientApplication {

    private static final Logger logger = LoggerFactory.getLogger(ReactiveClientApplication.class);
    private final WebClient webClient;
    private final String[] filePaths; // Array of file paths


    public ReactiveClientApplication(WebClient.Builder webClientBuilder, String baseUrl, String[] filePaths) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
        this.filePaths = filePaths;
    }

    // Método para obter o titulo da media (teste para o endpoint), com logs 
    private void getAllMediaAndWriteToFile(CountDownLatch latch) {
        webClient.get()
                .uri("/api/media")
                .retrieve()
                .bodyToFlux(Media.class)
                .map(media -> "Title: " + media.getTitle())
                .doOnNext(mediaTitle -> logger.info("Processing media title:", mediaTitle))
                .doOnComplete(() -> {
                    logger.info("Completed fetching all media titles");
                    latch.countDown(); // uso do latch para fazer sinal quando o Flux completar a tarefa
                })
                .doOnError(error -> {
                    logger.error("Error occurred while fetching media titles", error);
                    latch.countDown(); // diminuir o count do latch
                })
                .subscribe(
                    mediaTitle -> {
                        writeToFile(mediaTitle, filePaths[0]); // escrever no ficheiro
                        logger.info("Written to file: ", mediaTitle);
                    },
                    error -> logger.error("Error during subscription: ", error)
                );
    }

    // método para obter os titulos e datas das medias
    private void getMediaTitlesAndDates(CountDownLatch latch){ 
        webClient.get()
            .uri("/api/media")
            .retrieve()
            .bodyToFlux(Media.class)
            .map(media -> "Title: " + media.getTitle() + "; Release Date: " + media.getReleaseDate())
            .doOnComplete(() -> {
                logger.info("Error getting titles and dates");
                latch.countDown();
            })
            .subscribe(
                data -> writeToFile(data,filePaths[1]),
                error -> logger.error("Error doing the subscription for titles and dates", error)
            );              
        }

    private void getTotalCountMedia(CountDownLatch latch){
        webClient.get()
                .uri("/api/media")
                .retrieve()
                .bodyToFlux(Media.class)
                .count() //contar numero de values
                .doOnSuccess(null);
    }
    
                    


    // Método para escrever no ficheiro
    private void writeToFile(String string, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) { // Append mode
            writer.write(string);
            writer.newLine();
        } catch (IOException e) {
            logger.error("Failed to write media title to file", e);
        }
    }

    public static void main(String[] args) {
        String baseUrl = "http://localhost:8080";
        
        String[] filePaths = {
            "demo/src/outputs/all_media.txt",
            "demo/src/outputs/titles_and_dates.txt",
            "demo/src/outputs/total_media_count.txt",
            "demo/src/outputs/high_rated_media_count.txt",
            "demo/src/outputs/subscribed_media_count.txt",
            "demo/src/outputs/media_from_80s.txt",
            "demo/src/outputs/ratings_stats.txt",
            "demo/src/outputs/oldest_media.txt",
            "demo/src/outputs/avg_users_per_media.txt",
            "demo/src/outputs/user_count_per_media.txt",
            "demo/src/outputs/complete_user_data.txt"
    };

        WebClient.Builder webClientBuilder = WebClient.builder();
        ReactiveClientApplication clientApp = new ReactiveClientApplication(webClientBuilder, baseUrl, filePaths);

        logger.info("Starting CLIENT");

        CountDownLatch latch = new CountDownLatch(1); // Create a latch to wait for completion Criar um latch para se esperar a execução das tarefas

        
        clientApp.getAllMediaAndWriteToFile(latch);

        clientApp.getMediaTitlesAndDates(latch);

        // Esperar que o latch chegue a 0 
        try {
            latch.await(); 
        } catch (InterruptedException e) {
            logger.error("Main thread interrupted while waiting", e);
        }

        logger.info("Finished all operations.");
    }
}

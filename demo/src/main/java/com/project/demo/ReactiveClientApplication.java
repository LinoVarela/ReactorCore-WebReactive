package com.project.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;

import com.project.demo.entity.Consumer;
import com.project.demo.entity.ConsumerMedia;
import com.project.demo.entity.Media;

import reactor.core.publisher.Flux;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
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
                .map(media -> "Title: " + media.getTitle() + "; Rating - " + media.getAverageRating() + " ; Release Date : + " + media.getReleaseDate())
                .doOnComplete(() -> {
                    logger.info("Completed fetching all media titles");
                    latch.countDown(); // uso do latch para fazer sinal quando o Flux completar a tarefa
                })
                .subscribe(
                    mediaTitle -> writeToFile(mediaTitle, filePaths[0]),// escrever no ficheiro
                    error -> logger.error("Error during subscription: ", error)
                );
    }

    private void getAllRelationships(CountDownLatch latch) {
        webClient.get()
                .uri("/api/relationships") // 
                .retrieve()
                .bodyToFlux(ConsumerMedia.class) // Fetch the relationships
                .map(relationship -> "Consumer ID: " + relationship.getConsumerId() + "; Media ID: " + relationship.getMediaId()) // Customize the output format
                .doOnComplete(() -> {
                    logger.info("Completed fetching all consumer-media relationships");
                    latch.countDown(); // Signal when all tasks are complete
                })
                .subscribe(
                    relationshipInfo -> writeToFile(relationshipInfo, filePaths[2]), // Assuming index 4 for relationships
                    error -> logger.error("Error during subscription for relationships: ", error)
                );
    }
    private void getAllConsumers(CountDownLatch latch) {
        webClient.get()
                .uri("/api/consumers") 
                .retrieve()
                .bodyToFlux(Consumer.class) 
                .map(consumer -> "Consumer ID: " + consumer.getId() + "; Name: " + consumer.getName()) 
                .doOnComplete(() -> {
                    logger.info("Completed fetching all consumers");
                    latch.countDown(); 
                })
                .subscribe(
                    consumerInfo -> writeToFile(consumerInfo, filePaths[1]), 
                    error -> logger.error("Error during subscription for consumers: ", error)
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
                logger.info("Getting titles and dates Completed");
                latch.countDown();
            })
            .subscribe(
                data -> writeToFile(data,filePaths[3]),
                error -> logger.error("Error doing the subscription for titles and dates", error)
            );              
        }

    private void getTotalCountMedia(CountDownLatch latch){
        webClient.get()
            .uri("/api/media")
            .retrieve()
            .bodyToFlux(Media.class)
            .count()  // Counts the total items and returns a Mono<Long>
            .doOnSuccess(count -> {
                logger.info("Total media count retrieved successfully: {}", count);
                latch.countDown();
            })
            .subscribe(
                count -> writeToFile("Total Media Count: " + count, filePaths[4]),
                error -> logger.error("Error fetching media count", error)
            );
    }

    
    private void getCountMediaMoreThan8(CountDownLatch latch) {
        webClient.get()
                .uri("/api/media")
                .retrieve()
                .bodyToFlux(Media.class)
                .filter(media -> media.getAverageRating() > 8) // Filter for media with average rating above 8
                .count() // Count the filtered items
                .doOnSuccess(count -> {
                    logger.info("Total media count of media with average rating above 8 retrieved successfully: {}", count);
                    latch.countDown(); // Signal completion
                })
                .subscribe(
                    count -> writeToFile("Total Media Count (Rating > 8): " + count, filePaths[5]), // Write the count to the file
                    error -> logger.error("Error fetching media count", error) // Handle error
                );
    }


    private void getCountSubscribedMedia(CountDownLatch latch) {
        webClient.get()
                .uri("/api/relationships") 
                .retrieve()
                .bodyToFlux(ConsumerMedia.class)
                .map(ConsumerMedia::getMediaId) // Extract media IDs from relationships
                .distinct() // Ensure we only count unique media IDs
                .count() // Count the distinct media IDs
                .doOnSuccess(count -> {
                    logger.info("Total subscribed media count retrieved successfully: {}", count);
                    latch.countDown(); // Signal completion
                })
                .doOnError(error -> {
                    logger.error("Error fetching total subscribed media count", error);
                    latch.countDown(); // Decrement latch on error
                })
                .subscribe(
                    count -> writeToFile("Total Subscribed Media Count: " + count, filePaths[6]), // Write to file
                    error -> logger.error("Error during subscription: ", error)
                );
    }           


    private void getMediaFrom80s(CountDownLatch latch) {
        webClient.get()
                .uri("/api/media")
                .retrieve()
                .bodyToFlux(Media.class) 
                .filter(media -> {
                    LocalDate releaseDate = media.getReleaseDate(); //Buscsar data de lancamento
                    //utilizar LocalDate para datas
                    LocalDate start_80s = LocalDate.of(1980, 1, 1); // primeiro dia dos anos 80
                    LocalDate end_80s = LocalDate.of(1989, 12, 31); // ultimo dia dos anos 80
                    return !releaseDate.isBefore(start_80s) && !releaseDate.isAfter(end_80s); // metodos de comparacao de datas 
                })
                .sort((media1, media2) -> Double.compare(media2.getAverageRating(), media1.getAverageRating())) // 
                .map(media -> "Title: " + media.getTitle() + "; Rating: " + media.getAverageRating() + "; Release Date: " + media.getReleaseDate())
                .doOnComplete(() -> {
                    logger.info("Completed fetching 1980s media items sorted by rating");
                    latch.countDown(); // Signal task completion
                })
                .subscribe(
                    mediaInfo -> writeToFile(mediaInfo, filePaths[7]), // Assuming filePaths[5] is for 1980s media
                    error -> logger.error("Error fetching 1980s media items", error)
                );
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
            "demo/src/outputs/all_consumers.txt",
            "demo/src/outputs/all_relationships.txt",
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

        CountDownLatch latch = new CountDownLatch(8); // Create a latch to wait for completion Criar um latch para se esperar a execução das tarefas

        
        clientApp.getAllMediaAndWriteToFile(latch);

        clientApp.getAllConsumers(latch);
        
        clientApp.getAllRelationships(latch);

        clientApp.getMediaTitlesAndDates(latch);

        clientApp.getTotalCountMedia(latch);;

        clientApp.getCountMediaMoreThan8(latch);

        clientApp.getCountSubscribedMedia(latch);
        
        clientApp.getMediaFrom80s(latch);

        // Esperar que o latch chegue a 0 
        try {
            latch.await(); 
        } catch (InterruptedException e) {
            logger.error("Main thread interrupted while waiting", e);
        }

        logger.info("Finished all operations.");
    }
}

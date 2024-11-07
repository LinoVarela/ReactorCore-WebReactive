package com.project.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;

import com.project.demo.entity.Consumer;
import com.project.demo.entity.ConsumerMedia;
import com.project.demo.entity.Media;

import reactor.util.retry.Retry;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
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
    private void getAllMedia(CountDownLatch latch) {
        webClient.get()
                .uri("/api/media")
                .retrieve()
                .bodyToFlux(Media.class)
                .map(media -> "ID: " + media.getId() +"Title: " + media.getTitle() + "; Rating - " + media.getAverageRating() + " ; Release Date : + " + media.getReleaseDate())//output
                .doOnComplete(() -> {
                    logger.info("Completed fetching all media titles");
                    latch.countDown(); // uso do latch para fazer sinal quando o Flux completar a tarefa
                })
                .subscribe(
                    mediaTitle -> writeToFile(mediaTitle, filePaths[0]),// escrever no ficheiro
                    error -> logger.error("Error during subscription: ", error)
                );
    }

    //Método para obter informacao das relaçoes entre users e a media
    private void getAllRelationships(CountDownLatch latch) {
        webClient.get()
                .uri("/api/relationships") //endpoint
                .retrieve()
                .bodyToFlux(ConsumerMedia.class) // Buscar relacoes
                .map(relationship -> "Consumer ID: " + relationship.getConsumerId() + "; Media ID: " + relationship.getMediaId()) //output
                .doOnComplete(() -> {
                    logger.info("Completed fetching all consumer-media relationships");
                    latch.countDown(); 
                })
                .subscribe(
                    relationshipInfo -> writeToFile(relationshipInfo, filePaths[2]), 
                    error -> logger.error("Error during subscription for relationships: ", error)
                );
    }

    //Método para obter informacoes dos consumers
    private void getAllConsumers(CountDownLatch latch) {
        webClient.get()
                .uri("/api/consumers") 
                .retrieve()
                .bodyToFlux(Consumer.class) 
                .map(consumer -> "Consumer ID: " + consumer.getId() + "; Name: " + consumer.getName() + "; Age : " + consumer.getAge()) 
                .doOnComplete(() -> {
                    logger.info("Completed fetching all consumers");
                    latch.countDown(); 
                })
                .subscribe(
                    consumerInfo -> writeToFile(consumerInfo, filePaths[1]), 
                    error -> logger.error("Error during subscription for consumers: ", error)
                );
    }
    
    

    // QUERIE 1: Método para obter os titulos e datas das medias
    private void getMediaTitlesAndDates(CountDownLatch latch){ 
        webClient.get()
            .uri("/api/media") 
            .retrieve()
            .bodyToFlux(Media.class)
            .map(media -> "Title: " + media.getTitle() + "; Release Date: " + media.getReleaseDate()) //Buscar titulo e data de lancamento de media
            .doOnComplete(() -> {
                logger.info("Getting titles and dates Completed");
                latch.countDown(); //Diminuir o lacth
            })
            .subscribe(
                data -> writeToFile(data,filePaths[3]), //Escrever no ficheiro correspondente
                error -> logger.error("Error doing the subscription for titles and dates", error)
            );              
        }

        // QUERIE 2: Método para obter o numero total de media items
        private void getTotalCountMedia(CountDownLatch latch){
        webClient.get()
            .uri("/api/media")
            .retrieve()
            .bodyToFlux(Media.class)
            .count()  // Conta o numero total de items 
            .doOnSuccess(count -> {
                logger.info("Total media count retrieved successfully: {}", count);
                latch.countDown();
            })
            .subscribe(
                count -> writeToFile("Total Media Count: " + count, filePaths[4]),
                error -> logger.error("Error fetching media count", error)
            );
    }

    
    // QUERIE 3: Método para obter o numero total de media items com rating acima de 8
    private void getCountMediaMoreThan8(CountDownLatch latch) {
        webClient.get()
                .uri("/api/media")
                .retrieve()
                .bodyToFlux(Media.class)
                .filter(media -> media.getAverageRating() > 8) // Filtrar por media com rating acima de 8
                .count() // fazer contagem
                .doOnSuccess(count -> {
                    logger.info("Total media count of media with average rating above 8 retrieved successfully: {}", count);
                    latch.countDown(); 
                })
                .subscribe(
                    count -> writeToFile("Total Media Count (Rating > 8): " + count, filePaths[5]), 
                    error -> logger.error("Error fetching media count", error) 
                );
    }


    //QUERIE  4: Metodo para contar numero de medias que têm subscritores
    private void getCountSubscribedMedia(CountDownLatch latch) {
        webClient.get()
                .uri("/api/relationships") 
                .retrieve()
                .bodyToFlux(ConsumerMedia.class)
                .map(ConsumerMedia::getMediaId) // Extrair media IDs das relationships
                .distinct() // Contar apenas IDs unicos
                .count() // Fazer contagem
                .doOnSuccess(count -> {
                    logger.info("Total subscribed media count retrieved successfully: {}", count);
                    latch.countDown(); 
                })
                .doOnError(error -> {
                    logger.error("Error fetching total subscribed media count", error);
                    latch.countDown(); 
                })
                .subscribe(
                    count -> writeToFile("Total Subscribed Media Count: " + count, filePaths[6]), 
                    error -> logger.error("Error during subscription: ", error)
                );
    }           

    //QUERIE 5: Metodo para obter medias dos anos 80
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
                .sort((media1, media2) -> Double.compare(media2.getAverageRating(), media1.getAverageRating())) // Utilizar metodo sort para ordernar
                .map(media -> "Title: " + media.getTitle() + "; Rating: " + media.getAverageRating() + "; Release Date: " + media.getReleaseDate())
                .doOnComplete(() -> {
                    logger.info("Completed fetching 1980s media items sorted by rating");
                    latch.countDown(); 
                })
                .subscribe(
                    mediaInfo -> writeToFile(mediaInfo, filePaths[7]), 
                    error -> logger.error("Error fetching 1980s media items", error)
                );
    }

    //QUERIE 6: Metodo para obter média e desvio padrao
    private void averageAndStandardDeviation(CountDownLatch latch) {
        webClient.get()
                .uri("/api/media")
                .retrieve()
                .bodyToFlux(Media.class)
                .reduce(new ArrayList<Double>(), (acc, media) -> {
                    acc.add(media.getAverageRating().doubleValue()); // criar arraylist e colocar lá os ratings para os calculos
                    return acc;
                })
                .doOnTerminate(() -> {
                    logger.info("Completed calculating average and standard deviation of ratings");
                    latch.countDown(); 
                })
                .subscribe(
                    list -> {
                        int count = list.size();
    
                        if (count > 0) {
                            // Passo 1: Calcular a média
                            double sum = 0;
                            for (Double rating : list) {
                                sum += rating;
                            }
                            double average = sum / count;
    
                            // Passo 2: Calcular a soma dos quadrados das diferencas entre os ratings e a média para a variancia
                            double sumSquares = 0;
                            for (Double rating : list) {
                                sumSquares += Math.pow(rating - average, 2);
                            }
    
                            // Passo 3: Variancia e desvio padrao
                            double variance = sumSquares / (count - 1);
                            double standardDeviation = Math.sqrt(variance);
    
                            String result = String.format("Average Rating: %.2f, Standard Deviation: %.2f", average, standardDeviation);
                            writeToFile(result, filePaths[8]);
                        } else {
                            writeToFile("No ratings available", filePaths[8]);
                        }
                    },
                    error -> logger.error("Error calculating average and standard deviation of ratings", error)
                );
    }

    //QUERIE 7: Método para obter a media mais antiga
    private void getOldestMediaItem(CountDownLatch latch) {
        webClient.get()
                .uri("/api/media")
                .retrieve()
                .bodyToFlux(Media.class)
                .sort((media1, media2) -> media1.getReleaseDate().compareTo(media2.getReleaseDate())) // Sort por datas de release
                .next() // escolher o primeiro item, que vai ser o mais antigo
                .doOnSuccess(media1 -> {
                    logger.info("Sucess retrieving oldest media: {}", media1);
                    latch.countDown(); 
                })
                .subscribe(
                    oldestMedia -> {
                        if (oldestMedia != null) {
                            String result = "Oldest Media Item: " + oldestMedia.getTitle() + ", Release Date: " + oldestMedia.getReleaseDate();
                            writeToFile(result, filePaths[9]);
                        } else {
                            writeToFile("No media available", filePaths[9]);
                        }
                    },
                    error -> logger.error("Error fetching oldest media item", error)
                );
    }
    
    //QUERIE 8: Metodo para obter o numero medio de users por media item
    private void averageNumberOfUsersPerMedia(CountDownLatch latch) {
        // Obter primerio os media items 
        webClient.get()
                .uri("/api/media")
                .retrieve()
                .bodyToFlux(Media.class) 
                .flatMap(media -> 
                    webClient.get()  // Obter as relationships por cada media
                        .uri("/api/relationships")
                        .retrieve()
                        .bodyToFlux(ConsumerMedia.class)
                        .filter(relationship -> relationship.getMediaId().equals(media.getId()))  // Filtrar  relationships por mediaId
                        .count()  //contar numero de users subscritos à media item
                        .map(userCount -> new Object[]{media.getId(), userCount}))  // Retornar par de mediaId e user count
                .reduce(new Object[]{0L, 0L}, (acc, mediaUserCount) -> {
                    //juntar total de users e media
                    acc[0] = (Long) acc[0] + (Long) mediaUserCount[1];  // Adicionar user count
                    acc[1] = (Long) acc[1] + 1;  // Increment media count
                    //logger.info("number of users: {}",acc[1]);
                    //logger.info("number of Media: {}",acc[0]);
                    return acc;  // Retornar result
                    
                })
                .doOnTerminate(() -> {
                    logger.info("Completed calculating the average number of users per media item");
                    latch.countDown(); 
                })
                .subscribe(
                    result -> {
                        long totalUsers = (Long) result[0];
                        long mediaCount = (Long) result[1];
    
                        //Fazer calculo do numero medio de users por media item
                        if (mediaCount > 0) {
                            double averageUsers = (double) totalUsers / mediaCount;
                            String resultMessage = String.format("Average number of users per media item: %.2f", averageUsers);
                            writeToFile(resultMessage, filePaths[10]);  
                        } else {
                            writeToFile("No media items found", filePaths[10]);
                        }
                    },
                    error -> {
                        logger.error("Error during subscription: ", error);
                        latch.countDown();  
                    }
                );
    }


    //QUERIE 9: Nome e numero de users por media item, por order decrescente
    private void usersPerMediaSorted(CountDownLatch latch) {
        webClient.get()
                .uri("/api/media")
                .retrieve()
                .bodyToFlux(Media.class)  // obter media
                .flatMap(media ->
                    webClient.get()  // obter relacoes
                            .uri("/api/relationships")
                            .retrieve()
                            .bodyToFlux(ConsumerMedia.class)  
                            .filter(relationship -> relationship.getMediaId().equals(media.getId()))  // Filtar por mediaId
                            .flatMap(relationship ->
                                webClient.get()  // Por cada relationship, buscar a info do user (consumer)
                                        .uri("/api/consumers/" + relationship.getConsumerId())  
                                        .retrieve()
                                        .bodyToMono(Consumer.class)  // Buscar info de cada user (Mono)
                                        .map(consumer -> new Object[]{media, consumer}))  // Map dos media e consumer 
                )
                .groupBy(entry -> ((Media) entry[0]).getId())  // Group por mediaId (media.getId())
                .flatMap(groupedFlux -> 
                    groupedFlux
                            .sort((entry1, entry2) -> {
                                // Ordenar por idade 
                                Consumer consumer1 = (Consumer) entry1[1];  
                                Consumer consumer2 = (Consumer) entry2[1];  
                                return consumer2.getAge().compareTo(consumer1.getAge());  //Ordenar por ordem decrescente
                            })
                            .map(user -> {
                                Media media = (Media) user[0];  // buscar a media do par
                                Consumer consumer = (Consumer) user[1];  // buscar consumer
                                return new Object[]{media.getTitle(), consumer.getName(), consumer.getAge()};
                            })
                )
                .doOnTerminate(() -> {
                    logger.info("Completed fetching name and user count per media item, sorted by user age");
                    latch.countDown();  
                })
                .subscribe(
                    result -> {
                        String mediaTitle = (String) result[0];
                        String consumerName = (String) result[1];
                        int age = (Integer) result[2];
    
                        String resultMessage = String.format("Media: %s, User: %s, Age: %d", mediaTitle, consumerName, age);
                        writeToFile(resultMessage, filePaths[11]);
                    },
                    error -> {
                        logger.error("Error during subscription: ", error);
                        latch.countDown();  
                    }
                );
    }
    

    //QUERIE 10: mostrar users e as medias a que estao associados
    private void usersAndTheirMedia(CountDownLatch latch) {
        webClient.get()
                .uri("/api/consumers")  
                .retrieve()
                .bodyToFlux(Consumer.class)  // buscar users
                .flatMap(consumer ->
                    webClient.get()  // buscar relacoes para cada user (consumer)
                            .uri("/api/relationships")
                            .retrieve()
                            .bodyToFlux(ConsumerMedia.class)  
                            .filter(relationship -> relationship.getConsumerId().equals(consumer.getId()))  // Filtrar por consumerId
                            .flatMap(relationship ->
                                webClient.get()  // para cada relacao, buscar a media a q o user esta subscrito (subscription)
                                        .uri("/api/media/" + relationship.getMediaId())  
                                        .retrieve()
                                        .bodyToMono(Media.class)  
                                        .map(media -> new Object[]{consumer, media}))  // Mapa para  consumer e a media 
                )
                .doOnTerminate(() -> {
                    logger.info("Completed fetching complete data for all users with their subscribed media");
                    latch.countDown();  
                })
                .subscribe(
                    result -> {
                        Consumer consumer = (Consumer) result[0];  
                        Media media = (Media) result[1];  
    
                        
                        String resultMessage = String.format("User: %s, Media Subscribed: %s", consumer.getName(), media.getTitle());
                        writeToFile(resultMessage, filePaths[12]);  
                    },
                    error -> {
                        logger.error("Error during subscription: ", error);
                        latch.countDown();  
                    }
                );
    }


    //Metodo das simulacoes de falhas no servidor
    private void fetchMediaWithRetries(CountDownLatch latch) {
        webClient.get()
                .uri("/api/media/test") 
                .retrieve()
                .bodyToFlux(Media.class)
                .map(media -> String.format("Title: %s, Date: %s", media.getTitle(), media.getReleaseDate()))
                .retryWhen(
                    Retry.fixedDelay(3, Duration.ofSeconds(2)) // Retry 3 vezes (com 2 segundos de delay)
                         .doBeforeRetry(retrySignal -> logger.warn("Retrying due to network error... Attempt: {}", retrySignal.totalRetries() + 1))
                )
                .doOnTerminate(() -> {
                    logger.info("Retry logic");
                    latch.countDown(); 
                })
                .subscribe(
                    mediaTitles -> {
                        if (mediaTitles.isEmpty()) {
                            writeToFile("No media items found", filePaths[1]);
                        } else {
                            writeToFile(String.join("\n", mediaTitles), filePaths[13]);
                        }
                    },
                    error -> {
                        logger.error("Failed to fetch media after 3 attempts: ", error);
                        latch.countDown(); 
                    }
                );
    }
    
    

    


    // Método para escrever no ficheiro
    private void writeToFile(String string, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) { 
            writer.write(string);
            writer.newLine();
        } catch (IOException e) {
            logger.error("Failed to write media title to file", e);
        }
    }

    private void clearFiles() {
        for (String filePath : filePaths) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, false))) { 
                writer.write("");  // Clear the file by writing an empty string
                logger.info("Cleared file: {}", filePath);
            } catch (IOException e) {
                logger.error("Failed to clear file: {}", filePath, e);
            }
        }
    }

    public static void main(String[] args) {
        String baseUrl = "http://localhost:8080"; //url e port
        

        //Definir os caminhos para um folder com os ficheiros de texto
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
            "demo/src/outputs/complete_user_data.txt",
            "demo/src/outputs/serverError_titles_and_dates.txt",
        };

        WebClient.Builder webClientBuilder = WebClient.builder();
        ReactiveClientApplication clientApp = new ReactiveClientApplication(webClientBuilder, baseUrl, filePaths);

        logger.info("Starting CLIENT");

        clientApp.clearFiles();

        CountDownLatch latch = new CountDownLatch(14); // Criar um latch para se esperar a execução das tarefas


        //Métodos de suporte para obter os dados de cada entity
        clientApp.getAllMedia(latch);
        clientApp.getAllConsumers(latch);
        clientApp.getAllRelationships(latch);


        //Requisitos:
        //Titulo e datas das medias
        clientApp.getMediaTitlesAndDates(latch);

        //Numero total de media
        clientApp.getTotalCountMedia(latch);;

        //Numero de meidas com um rating average acima de 8 
        clientApp.getCountMediaMoreThan8(latch);

        //Numero de medias que têm subscrição
        clientApp.getCountSubscribedMedia(latch);
        
        //Media dos anos 80
        clientApp.getMediaFrom80s(latch);

        //Média e desvio padrao
        clientApp.averageAndStandardDeviation(latch);

        //Media mais antiga
        clientApp.getOldestMediaItem(latch);

        // Numero medio de utilizadores (subscricao) por media
        clientApp.averageNumberOfUsersPerMedia(latch);

        //Info dos users com subscriaçao (e info da media), por ordem de idade
        clientApp.usersPerMediaSorted(latch);
        
        //Info dos users com as suas subscricoes
        clientApp.usersAndTheirMedia(latch);

        clientApp.fetchMediaWithRetries(latch);

        // Esperar que o latch chegue a 0 
        try {
            latch.await(); 
        } catch (InterruptedException e) {
            logger.error("Main thread interrupted while waiting", e);
        }

        logger.info("Finished all operations.");
    }
}

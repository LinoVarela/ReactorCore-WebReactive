package com.project.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;

import com.project.demo.entity.Consumer;
import com.project.demo.entity.ConsumerMedia;
import com.project.demo.entity.Media;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
                .map(media -> "ID: " + media.getId() +"Title: " + media.getTitle() + "; Rating - " + media.getAverageRating() + " ; Release Date : + " + media.getReleaseDate())
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
                    relationshipInfo -> writeToFile(relationshipInfo, filePaths[2]), 
                    error -> logger.error("Error during subscription for relationships: ", error)
                );
    }
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
                    mediaInfo -> writeToFile(mediaInfo, filePaths[7]), 
                    error -> logger.error("Error fetching 1980s media items", error)
                );
    }

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
                    latch.countDown(); // Signal task completion
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
    
    private void getOldestMediaItem(CountDownLatch latch) {
        webClient.get()
                .uri("/api/media")
                .retrieve()
                .bodyToFlux(Media.class)
                .sort((media1, media2) -> media1.getReleaseDate().compareTo(media2.getReleaseDate())) // Sort por datas de release
                .next() // escolher o primeiro item, que vai ser o mais antigo
                .doOnSuccess(media1 -> {
                    logger.info("Sucess retrieving oldest media: {}", media1);
                    latch.countDown(); // Signal completion
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
    
    private void averageNumberOfUsersPerMedia(CountDownLatch latch) {
        // Fetch media items first and accumulate counts per media
        webClient.get()
                .uri("/api/media")
                .retrieve()
                .bodyToFlux(Media.class)  // Get all media items
                .flatMap(media -> 
                    webClient.get()  // Fetch relationships for each media
                        .uri("/api/relationships")
                        .retrieve()
                        .bodyToFlux(ConsumerMedia.class)
                        .filter(relationship -> relationship.getMediaId().equals(media.getId()))  // Filter relationships by mediaId
                        .count()  // Count the number of users associated with this media item
                        .map(userCount -> new Object[]{media.getId(), userCount}))  // Return mediaId and user count pair
                .reduce(new Object[]{0L, 0L}, (acc, mediaUserCount) -> {
                    // Accumulate the total number of users and media count
                    acc[0] = (Long) acc[0] + (Long) mediaUserCount[1];  // Add user count
                    acc[1] = (Long) acc[1] + 1;  // Increment media count
                    //logger.info("number of users: {}",acc[1]);
                    //logger.info("number of Media: {}",acc[0]);
                    return acc;  // Return the accumulated result
                    
                })
                .doOnTerminate(() -> {
                    logger.info("Completed calculating the average number of users per media item");
                    latch.countDown();  // Signal completion
                })
                .subscribe(
                    result -> {
                        long totalUsers = (Long) result[0];
                        long mediaCount = (Long) result[1];
    
                        // Calculate the average number of users per media item
                        if (mediaCount > 0) {
                            double averageUsers = (double) totalUsers / mediaCount;
                            String resultMessage = String.format("Average number of users per media item: %.2f", averageUsers);
                            writeToFile(resultMessage, filePaths[10]);  // Write result to file
                        } else {
                            writeToFile("No media items found", filePaths[10]);
                        }
                    },
                    error -> {
                        logger.error("Error during subscription: ", error);
                        latch.countDown();  // Signal completion on error
                    }
                );
    }
    
    private void usersPerMediaSorted(CountDownLatch latch) {
        webClient.get()
                .uri("/api/media")
                .retrieve()
                .bodyToFlux(Media.class)  // Get all media items
                .flatMap(media ->
                    webClient.get()  // Fetch relationships for each media
                            .uri("/api/relationships")
                            .retrieve()
                            .bodyToFlux(ConsumerMedia.class)  // Get all relationships
                            .filter(relationship -> relationship.getMediaId().equals(media.getId()))  // Filter by mediaId
                            .flatMap(relationship ->
                                webClient.get()  // For each relationship, fetch the user (consumer) info
                                        .uri("/api/consumers/" + relationship.getConsumerId())  // Assuming the user info is fetched like this
                                        .retrieve()
                                        .bodyToMono(Consumer.class)  // Get user details
                                        .map(consumer -> new Object[]{media, consumer}))  // Map media and consumer details
                )
                .groupBy(entry -> ((Media) entry[0]).getId())  // Group by mediaId (media.getId())
                .flatMap(groupedFlux -> 
                    groupedFlux
                            .sort((entry1, entry2) -> {
                                // Sorting by the age of the first user in the list (this assumes you want the first user to define the age)
                                Consumer consumer1 = (Consumer) entry1[1];  // Get the consumer from the pair
                                Consumer consumer2 = (Consumer) entry2[1];  // Get the consumer from the pair
                                return consumer2.getAge().compareTo(consumer1.getAge());  // Sort by age descending
                            })
                            .map(user -> {
                                Media media = (Media) user[0];  // Get media from the pair
                                Consumer consumer = (Consumer) user[1];  // Get the consumer from the pair
                                return new Object[]{media.getTitle(), consumer.getName(), consumer.getAge()};
                            })
                )
                .doOnTerminate(() -> {
                    logger.info("Completed fetching name and user count per media item, sorted by user age");
                    latch.countDown();  // Signal completion
                })
                .subscribe(
                    result -> {
                        String mediaTitle = (String) result[0];
                        String consumerName = (String) result[1];
                        int age = (Integer) result[2];
    
                        // Writing the result to the file
                        String resultMessage = String.format("Media: %s, User: %s, Age: %d", mediaTitle, consumerName, age);
                        writeToFile(resultMessage, filePaths[11]);  // Assuming the file path for query 9
                    },
                    error -> {
                        logger.error("Error during subscription: ", error);
                        latch.countDown();  // Signal completion on error
                    }
                );
    }
    
    private void usersAndTheirMedia(CountDownLatch latch) {
        webClient.get()
                .uri("/api/consumers")  // Fetch all users (consumers)
                .retrieve()
                .bodyToFlux(Consumer.class)  // Get all consumers
                .flatMap(consumer ->
                    webClient.get()  // Fetch relationships for each consumer (user)
                            .uri("/api/relationships")
                            .retrieve()
                            .bodyToFlux(ConsumerMedia.class)  // Get all relationships
                            .filter(relationship -> relationship.getConsumerId().equals(consumer.getId()))  // Filter by consumerId
                            .flatMap(relationship ->
                                webClient.get()  // For each relationship, fetch the media (subscription)
                                        .uri("/api/media/" + relationship.getMediaId())  // Assuming you fetch media details by ID
                                        .retrieve()
                                        .bodyToMono(Media.class)  // Get media details
                                        .map(media -> new Object[]{consumer, media}))  // Map consumer and media details
                )
                .doOnTerminate(() -> {
                    logger.info("Completed fetching complete data for all users with their subscribed media");
                    latch.countDown();  // Signal completion
                })
                .subscribe(
                    result -> {
                        Consumer consumer = (Consumer) result[0];  // Get the consumer (user)
                        Media media = (Media) result[1];  // Get the media (subscription)
    
                        // Process and write the result to a file
                        String resultMessage = String.format("User: %s, Media Subscribed: %s", consumer.getName(), media.getTitle());
                        writeToFile(resultMessage, filePaths[12]);  // Assuming the file path for query 10
                    },
                    error -> {
                        logger.error("Error during subscription: ", error);
                        latch.countDown();  // Signal completion on error
                    }
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
        String baseUrl = "http://localhost:8080"; //url e port
        

        
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

        CountDownLatch latch = new CountDownLatch(13); // Criar um latch para se esperar a execução das tarefas

        
        clientApp.getAllMedia(latch);

        clientApp.getAllConsumers(latch);
        
        clientApp.getAllRelationships(latch);

        clientApp.getMediaTitlesAndDates(latch);

        clientApp.getTotalCountMedia(latch);;

        clientApp.getCountMediaMoreThan8(latch);

        clientApp.getCountSubscribedMedia(latch);
        
        clientApp.getMediaFrom80s(latch);

        clientApp.averageAndStandardDeviation(latch);

        clientApp.getOldestMediaItem(latch);

        clientApp.averageNumberOfUsersPerMedia(latch);

        clientApp.usersPerMediaSorted(latch);
        
        clientApp.usersAndTheirMedia(latch);

        // Esperar que o latch chegue a 0 
        try {
            latch.await(); 
        } catch (InterruptedException e) {
            logger.error("Main thread interrupted while waiting", e);
        }

        logger.info("Finished all operations.");
    }
}

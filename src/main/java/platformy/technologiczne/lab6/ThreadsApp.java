package platformy.technologiczne.lab6;


import platformy.technologiczne.lab6.streamformatter.StreamFormatter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ThreadsApp {

    /**
     * Main function in program
     */
    public static void main(String[] args) {
        if (args.length <= 0) {
            System.out.println("There is not any arguments!");
        } else {
            ThreadsApp app = new ThreadsApp();
            long time = app.start(args, 8);
            System.out.println("Time with 8 workers = " + time / 1000.0 + "s");
        }
    }

    /**
     * Start the application
     */
    public long start(String[] args, int nbOfWorkers) {

        // variables for statistic
        long startTime, stopTime;

        // threads ready to work
        ForkJoinPool workers = new ForkJoinPool(nbOfWorkers);

        // get paths from program arguments
        Path sourcePath = Path.of(args[0].substring(1));
        Path destinationPath = Path.of(args[1].substring(1));

        List<Path> pathsList = getAllPaths(sourcePath);

        StreamFormatter sf = new StreamFormatter(sourcePath, destinationPath);

        startTime = System.currentTimeMillis();

        try{
            workers.submit(() -> {
                pathsList.stream()
                        .parallel()
                        .map(sf::createNameWithImagePair)
                        .map(sf::createNameWithNewImagePair)
                        .forEach(sf::save);
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Something goes wrong in stream");
            e.printStackTrace();
        }

        stopTime = System.currentTimeMillis();
        return stopTime - startTime;
    }


    /**
     * Get all paths from source
     * @param source source path
     * @return List of paths
     */
    public List<Path> getAllPaths(Path source){

        List<Path> files = null;

        try (Stream<Path> stream = Files.list(source)) {
            files = stream
                    .filter(path -> path.getFileName().toString().matches(".*.jpg"))
                    .collect(Collectors.toList());
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return files;
    }
}

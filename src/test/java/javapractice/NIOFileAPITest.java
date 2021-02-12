package javapractice;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.IntStream;

public class NIOFileAPITest {
        private static String home = System.getProperty("user.home");
        private static String playWithNIO = "TempPlayGround";

        @Test
        public void givenPathWhenCheckThenConfirm() throws IOException {
            Path homePath = Paths.get(home);
            Assertions.assertTrue(Files.exists(homePath));

            //Delete file and check file not exist
            Path playPath = Paths.get(home + "/"+playWithNIO);
            if(Files.exists(playPath)) FileUtils.deleteFiles(playPath.toFile());
            Assertions.assertTrue(Files.notExists(playPath));

            //create Directory
            Files.createDirectory(playPath);
            Assertions.assertTrue(Files.exists(playPath));

            //create File
            IntStream.range(1, 10).forEach(cntr ->{
                Path tempFile = Paths.get(playPath + "/temp"+cntr);
                Assertions.assertTrue(Files.notExists(tempFile));
                try{
                    Files.createFile(tempFile);
                }catch(IOException e){ }
                Assertions.assertTrue(Files.exists(tempFile));
            });

            //List Files, Directories as well as files with extension
            Files.list(playPath).filter(Files::isRegularFile).forEach(System.out::println);
            Files.newDirectoryStream(playPath).forEach(System.out::println);
            Files.newDirectoryStream(playPath, path -> path.toFile().isFile() && path.toString().startsWith("temp"))
                    .forEach(System.out::println);
        }
        @Test
        public void givenDirectoryWhenWatchedListAllTheActivities() throws IOException
        {
            Path dir = Paths.get(home+ "/" +playWithNIO);
            Files.list(dir).filter(Files::isRegularFile).forEach(System.out::println);
            new Java8WatchedServiceExample(dir).processEvents();
        }

}


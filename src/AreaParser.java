import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class AreaParser {

    public static SkiArea parse(String filepath) {

        Path path = Paths.get(filepath);
        List<String> lines = null;

        try {
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        SkiArea newArea = new SkiArea();

        for (String line : lines) {
            System.out.println(line);
        }

        return newArea;
    }
}

import parser.Parser;
import parser.Segment;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SEPO {
    public static void main(String[] args) throws IOException {
        // parse file
        Path file = FileSystems.getDefault().getPath("..", "1.s");
        byte[] fileArray = Files.readAllBytes(file);
        String fileString = new String(fileArray);
        // Instantiate parser
        Parser parser = new Parser();
        List<Segment> segments = parser.parse(fileString);
        // get traces
        System.out.println("hello");
        // optimize
    }
}

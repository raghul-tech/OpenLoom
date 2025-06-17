import com.raghultech.openloom.OpenLoom;

import java.nio.file.Path;
import java.nio.file.Paths;

public class StreamLinesExampleWithPath {
    public static void main(String[] args) {
        OpenLoom openLoom = new OpenLoom();
        Path path = Paths.get("sample.txt");

        openLoom.streamLines(path, line -> System.out.println("Line: " + line));
    }
}

import com.raghultech.openloom.OpenLoom;

import java.io.File;

public class StreamLinesExampleWithFile {
    public static void main(String[] args) {
        OpenLoom openLoom = new OpenLoom();
        File file = new File("sample.txt");

        openLoom.streamLines(file, line -> System.out.println("Line: " + line));
    }
}

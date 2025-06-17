import com.raghultech.openloom.OpenLoom;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ReadExampleWithPath {
    public static void main(String[] args) {
        OpenLoom openLoom = new OpenLoom();
        Path path = Paths.get("sample.txt");

        StringBuilder content = openLoom.read(path);
        System.out.println(content);
    }
}

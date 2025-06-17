import com.raghultech.openloom.OpenLoom;

import java.io.File;

public class ReadExampleWithFile {
    public static void main(String[] args) {
        OpenLoom openLoom = new OpenLoom();
        File file = new File("sample.txt");

        StringBuilder content = openLoom.read(file);
        System.out.println(content);
    }
}

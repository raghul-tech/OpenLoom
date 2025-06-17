import com.raghultech.openloom.OpenLoom;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public class StreamLinesAsyncExampleWithFile {
    public static void main(String[] args) {
        OpenLoom openLoom = new OpenLoom();
        File file = new File("sample.txt");

        CompletableFuture<Void> future = openLoom.streamLinesAsync(file, line -> System.out.println("Async Line: " + line));
        future.join(); // Wait for async to finish
    }
}

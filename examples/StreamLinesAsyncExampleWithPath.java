import com.raghultech.openloom.OpenLoom;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

public class StreamLinesAsyncExampleWithPath {
    public static void main(String[] args) {
        OpenLoom openLoom = new OpenLoom();
        Path path = Paths.get("sample.txt");

        CompletableFuture<Void> future = openLoom.streamLinesAsync(path, line -> System.out.println("Async Line: " + line));
        future.join(); // Wait for async to finish
    }
}

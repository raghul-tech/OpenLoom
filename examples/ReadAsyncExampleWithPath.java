import com.raghultech.openloom.OpenLoom;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

public class ReadAsyncExampleWithPath {
    public static void main(String[] args) {
        OpenLoom openLoom = new OpenLoom();
        Path path = Paths.get("sample.txt");

        CompletableFuture<StringBuilder> future = openLoom.readAsync(path);

        future.thenAccept(content -> {
            System.out.println("Async Content:");
            System.out.println(content);
        }).join();
    }
}

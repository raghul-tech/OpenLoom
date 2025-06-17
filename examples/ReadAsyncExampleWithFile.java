import com.raghultech.openloom.OpenLoom;

import java.io.File;
import java.util.concurrent.CompletableFuture;

public class ReadAsyncExampleWithFile {
    public static void main(String[] args) {
        OpenLoom openLoom = new OpenLoom();
        File file = new File("sample.txt");

        CompletableFuture<StringBuilder> future = openLoom.readAsync(file);

        future.thenAccept(content -> {
            System.out.println("Async Content:");
            System.out.println(content);
        }).join();
    }
}

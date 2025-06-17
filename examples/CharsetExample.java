
import com.raghultech.openloom.OpenLoom;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

/**
 * Demonstrates custom charset handling
 */
public class CharsetExample {
    public static void main(String[] args) {
        // Create reader with specific charset
        OpenLoom reader = new OpenLoom(StandardCharsets.ISO_8859_1);
        
        System.out.println("Current charset: " + reader.getCharset());
        
        // Read ISO-8859-1 encoded file
        StringBuilder content = reader.read(
            Paths.get("sample.txt"));
        System.out.println("File content:\n " + content.substring(0, 500) + "...");
        
        // Switch to UTF-16 mid-operation
        reader.setCharset(StandardCharsets.UTF_16);
        System.out.println("New charset: " + reader.getCharset());
    }
}
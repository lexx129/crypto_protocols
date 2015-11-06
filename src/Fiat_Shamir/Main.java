package Fiat_Shamir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by admin on 30.10.2015.
 */
public class Main {
    public static void main(String[] args) throws IOException {
//        File file = new File("C:\\Users\\admin\\123.txt");
        Path path = Paths.get("C:\\Users\\admin\\123.txt");
//        FileReader fr = new FileReader(file);
        byte[] message = Files.readAllBytes(path);

        FiatShamir fs = new FiatShamir(160, message);
    }
}

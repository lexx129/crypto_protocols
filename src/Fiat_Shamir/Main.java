package Fiat_Shamir;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Created by admin on 30.10.2015.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));
        bw.write("Введите режим работы: ");
        bw.flush();
        String mode = br.readLine();
        switch (mode) {
            case "sign":
                bw.write("Введите путь к файлу для подписи: \n");
                bw.flush();
                String source = br.readLine();
                bw.write("Введите длину чисел в битах: \n");
                bw.flush();
                int bitLength = Integer.parseInt(br.readLine());
                System.out.println(bitLength);
                Path path = Paths.get(source);
                byte[] message = Files.readAllBytes(path);
                FiatShamir fs = new FiatShamir(bitLength, message);

                break;
            case "check":
                bw.write("Введите путь к подписанному файлу: \n");
                source = br.readLine();
                path = Paths.get(source);
                message = Files.readAllBytes(path);
                bw.write("Ваша подпись: \n");
                BigInteger[] signature = new BigInteger[2];
                bw.write("s = \n");
                BigInteger s = new BigInteger(br.readLine());
                bw.write("t = \n");
                BigInteger t = new BigInteger(br.readLine());


        }


//        FiatShamir fs = new FiatShamir(160, message);
    }
}

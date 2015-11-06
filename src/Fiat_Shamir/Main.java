package Fiat_Shamir;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by admin on 30.10.2015.
 */
public class Main {
    private static BigInteger[] parseOpenKey(String raw){
        String[] prep = raw.split("[.]");
        BigInteger[] res = new BigInteger[prep.length];
        for (int i = 0; i < prep.length; i++) {
            res[i] = new BigInteger(prep[i]);
        }
        return res;
    }

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
//                System.out.println(bitLength);
                Path path = Paths.get(source);
                byte[] message = Files.readAllBytes(path);
                FiatShamir fs = new FiatShamir(bitLength, message);

                break;
            case "check":
                bw.write("Введите путь к подписанному файлу: \n");
                bw.flush();
                source = br.readLine();
                path = Paths.get(source);
                message = Files.readAllBytes(path);
                bw.write("Ваш открытый ключ: \n");
                bw.flush();
                BigInteger[] openKey = parseOpenKey(br.readLine());
                bw.write("Введите n, использованное при подписывании: ");
                bw.flush();
                BigInteger n = new BigInteger(br.readLine());
                bw.write("Ваша подпись: \n");
                bw.flush();
                BigInteger[] signature = new BigInteger[2];
                bw.write("s = \n");
                bw.flush();
                signature[0] = new BigInteger(br.readLine()); //s
                bw.write("t = \n");
                bw.flush();
                signature[1] = new BigInteger(br.readLine()); //t
                fs = new FiatShamir(message, n, openKey, signature);
                break;
            default:
                System.err.println("Такой режим неизвестен.");
                break;

        }


//        FiatShamir fs = new FiatShamir(160, message);
    }
}

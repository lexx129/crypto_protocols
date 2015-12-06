package Guillou_Quisquater;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;

/**
 * Created by admin on 04.12.2015.
 */
public class Checker {
    public static void main(String[] args) throws IOException {
        SmartCard sc = new SmartCard();
        System.out.println("Enter n: ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        BigInteger n = new BigInteger(br.readLine());
        System.out.println("***n = " + n);
        System.out.println("Enter v: ");
        BigInteger v = new BigInteger(br.readLine());
        System.out.println("***v = " + v);
        System.out.println("Enter j: ");
        BigInteger j = new BigInteger(br.readLine());
        System.out.println("***j = " + j);


        System.out.println("Ожидание T...");
        System.out.println("T = ");
//        String temp = br.readLine();
        BigInteger T = new BigInteger(br.readLine());
//        BigInteger T = new BigInteger(temp);
        System.out.println("***T is " + T);
        System.out.println("Generating d...");
        BigInteger d = sc.randomNumber(false, v.subtract(BigInteger.ONE).bitLength());
        while (d.compareTo(v.subtract(BigInteger.ONE)) > 0)
            d = sc.randomNumber(false, v.subtract(BigInteger.ONE).bitLength());
        System.out.println("d = " + d);
//        System.out.println("???????? ??????...");
        System.out.println("D = ");
        BigInteger D = new BigInteger(br.readLine());
        System.out.println("***D is " + D);
        BigInteger anotherT = (D.modPow(v, n)).multiply(j.modPow(d, n)).mod(n);
        System.out.println("T = " + T + "\nT`(mod n) = " + anotherT.mod(n));

    }
}

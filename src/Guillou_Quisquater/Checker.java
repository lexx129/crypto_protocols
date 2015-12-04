package Guillou_Quisquater;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Created by admin on 04.12.2015.
 */
public class Checker {
    public static void main(String[] args) throws IOException {
        SmartCard sc = new SmartCard();
        BigInteger n = sc.getN();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("???????? T...");
        System.out.println("T = ");
        BigInteger T = new BigInteger(br.readLine().getBytes());
        System.out.println("**T is " + T);
        BigInteger v = sc.getV();
        System.out.println("Snatched v = " + v);
        BigInteger d = sc.randomNumber(false, v.subtract(BigInteger.ONE).bitLength());
        System.out.println("d = " + d);
        System.out.println("???????? ??????...");
        System.out.println("D = ");
        BigInteger D = new BigInteger(br.readLine().getBytes());
        System.out.println("**D is " + D);
        BigInteger j = sc.getJ();
        BigInteger anotherT = D.modPow(v, n).multiply(j.modPow(d, n));
        System.out.println("T = " + T + "\nT` = " + anotherT);
    }
}

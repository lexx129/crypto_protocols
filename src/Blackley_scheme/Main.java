package Blackley_scheme;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        int m = 40;
        int n = 3;
        int k = 2;
        System.out.println("Please wait while generating points...");
        Secret secret = new Secret(m, n, k);
        secret.generatePoint();
        System.out.println("Generated bs: " + Arrays.toString(secret.getBs()));
        secret.secretDivide();
        BigInteger[] bs = secret.getBs();
        BigInteger[] ds = secret.getDs();
        HashMap<Integer, BigInteger[]> as = secret.getAs();
        System.out.println("\nCalculated parts of secret: ");
        for (int i = 0; i < ds.length; i++) {
            for (int j = 0; j < bs.length; j++) {
                System.out.print(as.get(i)[j] + " * " + bs[j] + " + ");
            }
            System.out.println(ds[i]);
        }
        BigInteger[][] system = new BigInteger[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
//                system[i][j] =
            }
        }
    }
}

package Blackley_scheme;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        BigInteger m = new BigInteger(String.valueOf("12345678900987654321"));

//        int m = 6;
        int n = 10;
        int k = 5;
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
        BigInteger[][] system = new BigInteger[k][k + 1];
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < k; j++) {
                system[i][j] = as.get(i)[j];
            }
            system[i][k] = ds[i];
        }
        BigInteger[] revealedSecret = secret.gaussResolve(system);
        System.out.println(Arrays.toString(revealedSecret));
    }


}

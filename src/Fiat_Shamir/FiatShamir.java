package Fiat_Shamir;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Created by admin on 30.10.2015.
 */
public class FiatShamir {
    //Открытые данные
    private int bitSize;
    private int k, t;

    //Секретные данные
    private BigInteger p, q;
    private BigInteger n, v[];
    private SecureRandom rand;
    private BigInteger s[];


    public FiatShamir(int bitSize, int k, int t) {
        this.bitSize = bitSize;
        this.k = k;
        this.t = t;
        this.rand = new SecureRandom();
        generateParameters();
    }

    public FiatShamir(BigInteger n, BigInteger s[]) {
        this.n = n;
        this.bitSize = n.bitLength();
        this.rand = new SecureRandom();
        this.k = v.length;
        this.v = v;
        generateDS();
    }

    public static String byteArrayToHexString(byte[] b) {
        String result = "";
        for (int i = 0; i < b.length; i++) {
            result +=
                    Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }

    public static String toSHA1(byte[] convertme) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert md != null;
        return byteArrayToHexString(md.digest(convertme));
    }

    private void generateParameters() {
        p = randomNumber(true, bitSize);
        q = randomNumber(true, bitSize);
        n = p.multiply(q);
        generateDS();
    }

    private void generateSecret() {
    }

    private BigInteger randomNumber(boolean prime, int size) {
        if (prime)
            return BigInteger.probablePrime(size, rand);
        BigInteger number = null;
        byte bNumber[] = new byte[(int) Math.ceil(size / 8.0)];
        do {
            rand.nextBytes(bNumber);
            number = new BigInteger(bNumber);
        } while (number.compareTo(BigInteger.ZERO) <= 0);
        return number;
    }

    private void generateDS() {
        System.out.println("Current secret key = ");
        for (int i = 0; i < bitSize; i++) {
            BigInteger s_temp = randomNumber(false, 15);
            while (!(s_temp.gcd(n).equals(BigInteger.ONE)))
                s_temp = randomNumber(false, bitSize);
            s[i] = s_temp;
            System.out.print(s[i]);
        }
        System.out.println("------------------");
    }
}


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
    private BigInteger b[];
    private byte[] message;
    private String hash;

    //Секретные данные
    private BigInteger p, q;
    private BigInteger n, v[];
    private SecureRandom rand;
    private BigInteger a[];
    private BigInteger u, r;


    //конструктор для подписи
    public FiatShamir(int bitSize, byte[] message) {
        this.bitSize = bitSize;
        this.message = message;
//        this.k = k;
//        this.t = t;
        this.rand = new SecureRandom();
        generateParameters();
    }
    //конструктор для проверки подписи
    public FiatShamir(BigInteger n, BigInteger a[]) {
        this.n = n;
        this.bitSize = n.bitLength();
        this.rand = new SecureRandom();
        this.k = v.length;
        this.v = v;
        generateDS();
    }

    public static String byteArrayToHexString(byte[] b) {
        String result = "";
        for (byte aB : b) {
            result +=
                    Integer.toString((aB & 0xff) + 0x100, 16).substring(1);
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
        System.out.println("Generated p = " + p);
        q = randomNumber(true, bitSize);
        System.out.println("Generated q = " + q);
        n = p.multiply(q);
        r = randomNumber(false, bitSize);
        System.out.println("Generated r = " + r);
        u = r.modPow(new BigInteger(new byte[]{2}), n);
        System.out.println("Calculated u = " + u);
        generateDS();
        generateOpenKey();
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
        hash = toSHA1(message);
        System.out.println("Current secret key = ");
        for (int i = 0; i < hash.length(); i++) {
            BigInteger s_temp = randomNumber(false, 15);
            while (!(s_temp.gcd(n).equals(BigInteger.ONE)))
                s_temp = randomNumber(false, bitSize);
            a[i] = s_temp;
            System.out.print(a[i]);
        }
        System.out.println("------------------");
    }

    public BigInteger inverse(BigInteger a, BigInteger n) {
//        BigInteger a = new BigInteger(this.toString());
        BigInteger b = n, x = BigInteger.ZERO, d = BigInteger.ONE;
        while (a.compareTo(BigInteger.ZERO) == 1)//a>0
        {
            BigInteger q = b.mod(a);
            BigInteger y = a;
            a = b.mod(a);
            b = y;
            y = d;
            d = x.subtract(q.multiply(d));
            x = y;
        }
        x = x.mod(n);
        if (x.compareTo(BigInteger.ZERO) == -1)//x<0
        {
            x = (x.add(n)).mod(n);
        }
        return x;
    }

    private void generateOpenKey() {
        for (int i = 0; i < hash.length(); i++) {
            b[i] = inverse(a[i], n).modPow(new BigInteger(String.valueOf(2)), n);
        }
    }
}


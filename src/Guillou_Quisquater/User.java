package Guillou_Quisquater;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Created by admin on 04.12.2015.
 */
public class User {
    private String name;


    //Secret data
    private BigInteger p;
    private BigInteger q;
    private BigInteger b;
    public SecureRandom random;

    //Public data
    private String j;
    private BigInteger J;
    private BigInteger n;

    //In-process generating data
    private BigInteger r;
    private BigInteger T;
    private BigInteger d;
    protected BigInteger D;


    //Constructor for Peggy
    public User(String name) {
        this.name = name;
        this.random = new SecureRandom();
        generateParams();
    }

    private void generateParams() {
        p = randomNumber(true, 40);
        q = randomNumber(true, 40);
        n = p.multiply(q);
        j = byteArrayToHexString(toSHA1(name.getBytes()));
    }

    private BigInteger randomNumber(boolean prime, int size) {
        if (prime)
            return BigInteger.probablePrime(size, random);
        BigInteger number = null;
        byte bNumber[] = new byte[(int) Math.ceil(size / 8.0)];
        do {
            random.nextBytes(bNumber);
            number = new BigInteger(bNumber);
        } while (number.compareTo(BigInteger.ZERO) <= 0);
        return number;
    }

    public static byte[] toSHA1(byte[] convertme) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        assert md != null;
//        return byteArrayToHexString(md.digest(convertme));
        return md.digest(convertme);
    }

    public static String byteArrayToHexString(byte[] b) {
        String result = "";
        for (byte aB : b) {
            result +=
                    Integer.toString((aB & 0xff) + 0x100, 16).substring(1);
        }
        return result;
    }
}

package Guillou_Quisquater;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Created by admin on 04.12.2015.
 */
public class SmartCard {
    private String name;


    //Secret data
    private BigInteger p;
    private BigInteger q;
    private BigInteger b;
    public SecureRandom random;

    //Public data
    private BigInteger j;
    private String jString;
    private BigInteger J;
    private BigInteger n;
    private BigInteger v;

    //In-process generating data
    private BigInteger r;
    private BigInteger T;

    private BigInteger d;
    private BigInteger D;

    public void setD(BigInteger d) {
       this.d = d;
    }


    //Constructor for Peggy
    public SmartCard(String name) {
        this.name = name;
        this.random = new SecureRandom();
        generateParams();
    }

    private void generateParams() {
        p = randomNumber(true, 40);
        q = randomNumber(true, 40);
        n = p.multiply(q);
        j = new BigInteger(toSHA1(name.getBytes()));
        jString = byteArrayToHexString(toSHA1(name.getBytes()));
        v = randomNumber(false, 10);
        generateSecret();
        generateOpen();
    }

    private void generateOpen() {
        r = randomNumber(false, n.subtract(BigInteger.ONE).bitLength());
        T = r.modPow(v, n);
        System.out.println("T = " + T);
    }

    private void generateSecret() {
        b = j.modPow(v.negate(), n);
    }

    public SmartCard() {
    }

    public BigInteger getV() {

        return v;
    }

    public BigInteger randomNumber(boolean prime, int size) {
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

    public BigInteger getJ() {
        return j;
    }

    public BigInteger getN() {

        return n;
    }

    public void calculateD(){
        D = r.multiply(b.modPow(d, n)).mod(n);
        System.out.println("D = " + D);

    }
}

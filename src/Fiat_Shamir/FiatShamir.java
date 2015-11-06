package Fiat_Shamir;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Created by admin on 30.10.2015.
 */
public class FiatShamir {
    //Открытые данные
    private int bitSize;
    private int k;
    private BigInteger t;
    private BigInteger b[];
    private byte[] message;
    private byte[] hash;
    private BigInteger[] signature = new BigInteger[2];

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
        generateSecretKey();
    }

    public static String byteArrayToHexString(byte[] b) {
        String result = "";
        for (byte aB : b) {
            result +=
                    Integer.toString((aB & 0xff) + 0x100, 16).substring(1);
        }
        return result;
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

    private void generateParameters() {
        p = randomNumber(true, bitSize);
        System.out.println("Generated p = " + p);
        q = randomNumber(true, bitSize);
        System.out.println("Generated q = " + q);
        n = p.multiply(q);
        System.out.println("Calculated n = " + n);
        r = randomNumber(false, bitSize);
        System.out.println("Generated r = " + r);
        u = r.modPow(new BigInteger(new byte[]{2}), n);
        System.out.println("Calculated u = " + u);
        generateSecretKey();
        generateOpenKey();
        generateDS();
        System.out.println("Signature: (" + signature[0] +
                ", " + signature[1] + ")");
    }

    private void generateDS() {
        BigInteger mult = BigInteger.ONE;
        for (int i = 0; i < hash.length; i++) {
            BigInteger temp = new BigInteger(new byte[]{hash[i]});
            mult = mult.multiply(a[i].modPow(temp, n));
        }
        t = r.multiply(mult);
        t = t.mod(n);
        signature[0] = new BigInteger(hash).abs();
        signature[1] = t;
    }

    private void generateSecret() {
    }

    private byte[] concat(byte[] a, byte[] b) {
        int aLen = a.length;
        int bLen = b.length;
        byte[] result = new byte[aLen + bLen];
        System.arraycopy(a, 0, result, 0, aLen);
        System.arraycopy(b, 0, result, aLen, bLen);
        return result;
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

    public int[] hexToBin(byte[] hex) {
//        int[] bits = new int[bitSize * 8 + 1];
        int[] bits = new int[160];
        for (int i = 0; i < 160; i++) {
            int sourceByte = 0xFF & (int) hex[i];//convert byte to unsigned int
            int mask = 0x80;
            for (int j = 0; j < 8; j++) {
                int maskResult = sourceByte & mask;  // Extract the single bit
                if (maskResult > 0) {
                    bits[8 * i + j] = 1;
                } else {
                    bits[8 * i + j] = 0;  // Unnecessary since array is initiated to zero but good documentation
                }
                mask = mask >> 1;
            }
        }
        System.out.print("Hash string in bits:   ");
        for (int i = 0; i < bitSize; i++) {
            System.out.print(bits[i]);
        }
        return bits;
    }

    private void generateSecretKey() {
        byte[] concat = concat(message, u.toByteArray());
        hash = toSHA1(concat);
        int[] bits = hexToBin(hash);
        System.out.println();
//        System.out.println("Hash = " + Arrays.toString(hash));
        System.out.println("Current secret key = ");
        a = new BigInteger[hash.length];
        for (int i = 0; i < hash.length; i++) {
            BigInteger s_temp = randomNumber(false, bitSize);
            while (!(s_temp.gcd(n).equals(BigInteger.ONE)))
                s_temp = randomNumber(false, bitSize);
            a[i] = s_temp;
            System.out.print(a[i]);
        }
        System.out.println("\n------------------");
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
        b = new BigInteger[hash.length];
        for (int i = 0; i < hash.length; i++) {
            b[i] = inverse(a[i], n).modPow(new BigInteger(String.valueOf(2)), n);
        }
    }
}


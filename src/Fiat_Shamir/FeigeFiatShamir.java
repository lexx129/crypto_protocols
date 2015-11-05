package Fiat_Shamir;

import java.math.BigInteger;
import java.security.SecureRandom;

public class FeigeFiatShamir {

    /**
     * PUBLIC PARAMETERS
     */
    private int bitSize;
    private int k, t;

    /**
     * PRIVATE PARAMETERS
     */
    private BigInteger p, q;

    /**
     * DATA KNOWN BY THE VERIFIER
     */
    private BigInteger n, v[];

    /**
     * DATA KNOWN BY THE PROVER
     */
    private BigInteger s[];

    private SecureRandom rand;

    private BigInteger x, r, a[];

    /**
     * Constructor generating parameters n,v,s for the system
     *
     * @param bitSize: size in bits for p and q, for 1024 security use 512
     * @param k:       security parameter for size of arrays
     * @param t:       number of iterations
     */
    public FeigeFiatShamir(int bitSize, int k, int t) {
        this.bitSize = bitSize;
        this.k = k;
        this.t = t;
        this.rand = new SecureRandom();
        generateParameters();
    }

    /**
     * Constructor for the prover
     *
     * @param n: modulo of the system
     * @param v: public part to verify.
     */
    public FeigeFiatShamir(BigInteger n, BigInteger v[]) {
        this.n = n;
        this.bitSize = n.bitLength();
        this.rand = new SecureRandom();
        this.v = v;
        this.k = v.length;
        this.s = null;
    }

    /**
     * Constructor for the verifier
     *
     * @param n: modulo of the system
     * @param v: public part to verify.
     * @param s: secret corresponding to v
     */
    public FeigeFiatShamir(BigInteger n, BigInteger v[], BigInteger s[]) {
        this.n = n;
        this.bitSize = n.bitLength();
        this.v = v;
        this.s = s;
        this.k = v.length;
        this.rand = new SecureRandom();
    }

    /**
     * Generates p,q,n,v and s
     */
    public void generateParameters() {
        p = randomNumber(true, bitSize);
        q = randomNumber(true, bitSize);
        n = p.multiply(q);
        generateSecret();
    }

    /**
     * generates v and s
     */
    private void generateSecret() {
        s = new BigInteger[k];
        v = new BigInteger[k];
        for (int i = 0; i < k; i++) {
            do {
                s[i] = randomNumber(false, bitSize * 2);
                v[i] = s[i].multiply(s[i]);
            } while (v[i].compareTo(n) <= 0);
            v[i] = v[i].mod(n);
        }
    }

    /**
     * The prover chooses a numbers s in {1,-1}, and a random r.
     *
     * @return sr^2 mod n
     */
    public BigInteger proverStep1() {
        BigInteger s = rand.nextBoolean() ? BigInteger.ONE : n.subtract(BigInteger.ONE);
        r = randomNumber(false, bitSize * 2);
        return s.multiply(r.multiply(r)).mod(n);
    }

    /**
     * The verifier chooses a random array {0,1}^k
     *
     * @return an array in {0,1}^k
     */
    public BigInteger[] verifierStep1(BigInteger x) {
        this.x = x;
        a = new BigInteger[k];
        for (int i = 0; i < a.length; i++)
            a[i] = rand.nextBoolean() ? BigInteger.ONE : BigInteger.ZERO;
        return a;
    }

    /**
     * Second step of the proof for the prover
     *
     * @param a: array of {0,1}
     * @return r*mult(si^ai) mod n
     */
    public BigInteger proverStep2(BigInteger a[]) {
        BigInteger y = r;
        for (int i = 0; i < a.length; i++)
            if (a[i].equals(BigInteger.ONE))
                y = y.multiply(s[i]).mod(n);
        return y;
    }

    /**
     * Tests if y^2 == +-x*mult(vi^ai)
     *
     * @param y: value computed by proverStep2
     * @return true if the test passed.
     */
    public boolean verifierStep2(BigInteger y) {
        BigInteger op = x;
        for (int i = 0; i < v.length; i++)
            if (a[i].equals(BigInteger.ONE))
                op = op.multiply(v[i]).mod(n);
        return y.multiply(y).mod(n).equals(op) || y.multiply(y).mod(n).equals(n.subtract(op));
    }

    /**
     * BigInteger random number generator
     *
     * @param prime true if the number must be prime, false otherwise
     * @param size: size in bits
     * @return random number with the criteria selected.
     */
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

    @Override
    public String toString() {
        StringBuffer str = new StringBuffer(1024 * 32);
        str.append("p: " + p + "\n");
        str.append("q: " + q + "\n");
        str.append("n: " + n + "\n");
        if (v != null) {
            str.append("v: ");
            for (int i = 0; i < v.length; i++)
                str.append(v[i] + " ");
            str.append("\n");
        }
        if (v != null) {
            str.append("s: ");
            for (int i = 0; i < s.length; i++)
                str.append(s[i] + " ");
            str.append("\n");
        }
        return str.toString();
    }

    public BigInteger[] getV() {
        return v;
    }

    public BigInteger[] getS() {
        return s;
    }

    public BigInteger getN() {
        return n;
    }

    public int getT() {
        return t;
    }

    public static void main(String[] args) {
        System.out.println("Generating parameters");
        FeigeFiatShamir ffs = new FeigeFiatShamir(8, 20, 12);
        System.out.println(ffs);
        FeigeFiatShamir prover = new FeigeFiatShamir(ffs.getN(), ffs.getV(), ffs.getS());
        FeigeFiatShamir verifier = new FeigeFiatShamir(ffs.getN(), ffs.getV());
        boolean result = true;
        for (int i = 0; i < ffs.getT(); i++) {
            BigInteger x = prover.proverStep1();
            BigInteger a[] = verifier.verifierStep1(x);
            BigInteger y = prover.proverStep2(a);
            result &= verifier.verifierStep2(y);
        }
        //Print OK if all rounds passed.
        System.out.println(result ? "OK" : "FAILED");
    }
}

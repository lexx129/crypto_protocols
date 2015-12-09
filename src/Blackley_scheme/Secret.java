package Blackley_scheme;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashMap;

public class Secret {
    private BigInteger secretInt; // íàøå ñåêðåòíîå çíà÷åíèå, êîòîðîå ìû ðàçäåëÿåì
    private int threshold; // Ïîðîãîâîå çíà÷åíèå
    private int usersAmount; // Êîëè÷åñòâî ÷åëîâåê, ìåæäó êîòîðûìè ðàçäåëÿåì
    private BigInteger p;
    private BigInteger[] bs; // Ñãåíåðèðîâàííûé ðÿä êîýôô. b
    private HashMap<Integer, BigInteger[]> as; // Ñãåíåðèðîâàííûå ËÍÇ ðÿäû êîýôô. a
    private BigInteger[] ds; // Ïîñ÷èòàííûå çíà÷åíèÿ d

    private SecureRandom rand;

    public BigInteger[] getBs() {
        return bs;
    }

    public HashMap<Integer, BigInteger[]> getAs() {
        return as;
    }

    public BigInteger[] getDs() {
        return ds;
    }

    public Secret(BigInteger secretInt, int usersAmount, int threshold) {
        this.secretInt = secretInt;
        this.usersAmount = usersAmount;
        this.threshold = threshold;
        this.rand = new SecureRandom();
        this.bs = new BigInteger[threshold];
        this.as = new HashMap<>(usersAmount, threshold);
        this.ds = new BigInteger[usersAmount];
    }

    public void generatePoint() {
        p = randomNumber(true, 70);
//        p = new BigInteger("13");
        System.out.println("**generated p = " + p);
        bs[0] = new BigInteger(String.valueOf(secretInt));
        for (int i = 1; i < threshold; i++) {
            bs[i] = randomNumber(false, p.bitLength()).mod(p);
        }
    }
//[12345678900987654321, 430801845012088571172, 441915124054896531693, 66708371685936962859, 449082275574752661551]
    public void secretDivide() {
        for (int i = 0; i < usersAmount; i++) {
            BigInteger[] curr = new BigInteger[threshold];
            for (int j = 0; j < curr.length; j++) {
                curr[j] = randomNumber(false, 60).mod(p);
            }
            if (i != 0) {
                if (isLinearIndependent(curr, i))
                    as.put(i, curr);
                else i--;
            } else as.put(i, curr);
            ds[i] = BigInteger.ZERO;
            for (int j = 0; j < threshold; j++) {
                ds[i] = (ds[i].add(as.get(i)[j].multiply(bs[j]))).mod(p);
            }
            ds[i] = ds[i].negate();
        }
    }

    private boolean isLinearIndependent(BigInteger[] curr, int yetGenerated) {
        BigInteger divider;
        for (int i = 0; i < yetGenerated; i++) {
            BigInteger[] temp = as.get(i);
            if (curr[0].compareTo(temp[i]) >= 0) {
                if (curr[0].mod(temp[0]).equals(BigInteger.ZERO))
                    divider = curr[0].divide(temp[0]);
                else return true;
                for (int j = 1; j < curr.length; j++) {
                    if (!((curr[j].mod(temp[j]).equals(BigInteger.ZERO))
                            && (curr[j].divide(temp[j]).equals(divider))))
                        return true;
                }
            } else {
                if (temp[0].mod(curr[0]).equals(BigInteger.ZERO))
                    divider = temp[0].divide(curr[0]);
                else return true;
                for (int j = 1; j < temp.length; j++) {
                    if (!((temp[j].mod(curr[j]).equals(BigInteger.ZERO))
                            && (temp[j]).divide(curr[j]).equals(divider)))
                        return true;
                }
            }
        }
        return false;
    }

    public BigInteger[] gaussResolve (BigInteger[][] input){
        BigInteger[] x = new BigInteger[input.length];
        for (int i = 0; i < x.length; i++) {
            x[i] = input[i][input[i].length - 1];
        }
//        System.out.println("----\n" + Arrays.toString(x) + "-----\n");
//        for (int i = 1; i < input.length; i++) {
//            System.out.println(Arrays.toString(input[i]));
//        }
        BigInteger m;
        for (int k = 1; k < input.length; k++) {
            for (int j = k; j < input.length; j++) {
                m = input[j][k - 1].multiply(input[k - 1][k - 1].modInverse(p)).mod(p);
                for (int i = 0; i < input[j].length; i++) {
                    input[j][i] = input[j][i].subtract(m.multiply(input[k - 1][i])).mod(p);
                }
                x[j] = x[j].subtract(m.multiply(x[k - 1])).mod(p);
            }
        }
//        for (int i = 0; i < input.length; i++) {
//            System.out.println(Arrays.toString(input[i]));
//        }
        for (int i = input.length - 1; i >= 0; i--) {
            for (int j = i + 1; j < input.length; j++) {
                x[i] = x[i].subtract(input[i][j].multiply(x[j])).mod(p);
            }
                x[i] = x[i].multiply(input[i][i].modInverse(p)).mod(p);
        }
        for (int i = 0; i < x.length; i++) {
            x[i] = x[i].negate().mod(p);
        }
        return x;
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
}

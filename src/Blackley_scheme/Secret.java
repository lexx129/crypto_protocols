package Blackley_scheme;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;

public class Secret {
    private int secretInt; // наше секретное значение, которое мы разделяем
    private int threshold; // Пороговое значение
    private int usersAmount; // Количество человек, между которыми разделяем
    private BigInteger p;
    private BigInteger[] bs; // Сгенерированный ряд коэфф. b
    private HashMap<Integer, BigInteger[]> as; // Сгенерированные ЛНЗ ряды коэфф. a
    private BigInteger[] ds; // Посчитанные значения d

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

    public Secret(int secretInt, int usersAmount, int threshold) {
        this.secretInt = secretInt;
        this.usersAmount = usersAmount;
        this.threshold = threshold;
        this.rand = new SecureRandom();
        this.bs = new BigInteger[threshold];
        this.as = new HashMap<>(threshold);
        this.ds = new BigInteger[usersAmount];
    }

    public void generatePoint() {
        p = randomNumber(true, 40);
        System.out.println("**generated p = " + p);
        bs[0] = new BigInteger(String.valueOf(secretInt));
        for (int i = 1; i < threshold; i++) {
            bs[i] = randomNumber(false, p.bitLength()).mod(p);
        }
    }

    public void secretDivide() {
        for (int i = 0; i < usersAmount; i++) {
            BigInteger[] curr = new BigInteger[threshold];
            for (int j = 0; j < curr.length; j++) {
                curr[j] = randomNumber(false, 10).mod(p);
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
        BigInteger m;
        for (int k = 1; k < input.length; k++) {
            for (int j = k; j < input.length; j++) {
                m = input[j][k - 1].multiply(input[k - 1][k - 1].modInverse(p)).mod(p);
                for (int i = 0; i < input[j].length; i++) {
                    input[j][i] = (input[j][i].subtract(m.multiply(input[k - 1][i]))).mod(p);
                }
                x[j] = (x[j].subtract(m.multiply(x[k - 1]))).mod(p);
            }
        }
        for (int i = input.length; i >= 0; i--) {
            for (int j = i + 1; j < input.length; j++) {
                x[i] = (x[i].subtract(input[i][j].multiply(x[j]))).mod(p);
                x[i] = (x[i].multiply(input[i][i].modInverse(p))).mod(p);
            }
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

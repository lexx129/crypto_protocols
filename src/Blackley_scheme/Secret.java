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

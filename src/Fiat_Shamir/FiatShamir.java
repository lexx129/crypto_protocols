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
//    private int hashLength = 160;
    private int bitSize;
    private int k;
    private BigInteger t;
    private BigInteger b[];
    private byte[] message;
    private byte[] hash;
    private byte[] bits;
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
    public FiatShamir(byte[] message, BigInteger n, BigInteger b[],
                      BigInteger[] sign) {
        this.message = message;
        this.n = n;
//        this.bitSize = n.bitLength();
        this.rand = new SecureRandom();
        this.b = b;
        this.bits = sign[0].toByteArray();
        this.t = sign[1];

        byte[] temp = new byte[]{0};
        while (bits.length < 160) {
            bits = concat(temp, bits);
        }

        checkSign();
    }

    public static String byteArrayToHexString(byte[] b) {
        String result = "";
        for (byte aB : b) {
            result +=
                    Integer.toString((aB & 0xff) + 0x100, 16).substring(1);
        }
        return result;
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
                "(s), " + signature[1] + "(t))");
        System.out.println("***Для проверки подписи сохраните n, открытый ключ, а также саму подпись!***");
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

    private void generateSecretKey() {
        byte[] concat = concat(message, u.toByteArray());
        hash = toSHA1(concat);
        bits = hexToBin(hash);
//        System.out.println(Arrays.toString(bits));
        System.out.println();
//        System.out.println("Hash = " + Arrays.toString(hash));
        System.out.println("Your secret key: ");
//        System.out.println("Secret key is gonna be of " + bits.length + " numbers.");
        a = new BigInteger[bits.length];
        for (int i = 0; i < bits.length; i++) {
            BigInteger s_temp = randomNumber(false, bitSize);
            while (!(s_temp.gcd(n).equals(BigInteger.ONE)))
                s_temp = randomNumber(false, bitSize);
            a[i] = s_temp;
            System.out.print(a[i]);
        }
        System.out.println("\n------------------");
    }

    private void generateOpenKey() {
        b = new BigInteger[bits.length];
//        System.out.println("Secret key is gonna be of " + bits.length + " numbers.");
        System.out.println("Your open key is: ");
        for (int i = 0; i < bits.length; i++) {
            b[i] = a[i].modInverse(n).modPow(BigInteger.valueOf(2), n);
//            b[i] = inverse(a[i], n).modPow(new BigInteger(String.valueOf(2)), n);
            System.out.print(b[i] + ".");
        }
        System.out.println("\n------------------");
    }

    private void generateDS() {
        BigInteger mult = BigInteger.ONE;
        for (int i = 0; i < bits.length; i++) {
            BigInteger temp = new BigInteger(String.valueOf(bits[i]));
            mult = mult.multiply(a[i].modPow(temp, n));
        }
        t = r.multiply(mult);
        t = t.mod(n);
        signature[0] = new BigInteger(bits);
//        System.out.println(Arrays.toString(bits));
//        System.out.println(Arrays.toString(signature[0].toByteArray()));
        signature[1] = t;
    }

    private void checkSign() {
        BigInteger mult = BigInteger.ONE;
        for (int i = 0; i < bits.length; i++) {
            BigInteger temp = new BigInteger(String.valueOf(bits[i]));
            mult = mult.multiply(b[i].modPow(temp, n));
        }
        BigInteger w = t.multiply(t).multiply(mult).mod(n);
        byte[] concat = concat(message, w.toByteArray());
        hash = toSHA1(concat);
        byte[] newBits = hexToBin(hash);
        BigInteger newSign = new BigInteger(newBits);
        BigInteger oldSign = new BigInteger(bits);
//        System.out.println(Arrays.toString(newBits));
//        System.out.println(Arrays.toString(bits));
        if (newSign.equals(oldSign))
            System.out.println("Подпись верна.");
        else System.out.println("Подпись не принята.");
    }

    private byte[] concat(byte[] a, byte[] b) {
        int aLen = a.length;
        int bLen = b.length;
        byte[] result = new byte[aLen + bLen];
        System.arraycopy(a, 0, result, 0, aLen);
        System.arraycopy(b, 0, result, aLen, bLen);
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

    public byte[] hexToBin(byte[] hex) {
        byte[] res = new byte[160];
        //array of bits is of SHA1 hash string bit length
//        bits = new byte[160];
//        int counter = 0;
        for (int i = 0; i < 20; i++) {
            int sourceByte = 0xFF & (int) hex[i];//convert byte to unsigned int
            int mask = 0x80;
            for (int j = 0; j < 8; j++) {
                int maskResult = sourceByte & mask;  // Extract the single bit
                if (maskResult > 0) {
                    res[8 * i + j] = 1;
                } else {
                    res[8 * i + j] = 0;  // Unnecessary since array is initiated to zero but good documentation
                }
                mask = mask >> 1;
            }
        }
//        System.out.print("Hash string in bits:   ");
//        for (int i = 0; i < 160; i++) {
//            System.out.print(bits[i]);
//        }
        return res;
    }
}
//n
//1928814779
//openKey
//921072681.1382740919.134326356.184042062.1679842819.968844826.743478837.68269175.1901430723.930218668.1746997959.526817406.1500078090.195314619.262494223.805382760.549389237.1767725187.103441546.1148695947.290610941.319853733.1259099934.181486182.522528349.237148278.969458326.928666924.232502975.378764475.1801575970.1144990071.1507938629.1409505457.1119100976.592747725.419353263.203290957.1116605099.286795950.1384370830.1768259032.1857020581.1081499152.619228143.1807120747.1228294244.426847761.66128526.1344716467.711766163.527022212.1492542121.469202450.607616290.497080333.1348399261.987223107.268360497.383402125.1864594689.394221284.338158331.330496073.284019578.746394017.651322065.1329017784.1090201811.167784181.929546546.1813650121.1584889433.777610854.452925769.1598532127.106582917.1120843943.350128077.813383643.740456590.762956239.1467355517.1769543058.808907825.1582709640.357735602.605783810.849144763.1597755986.1049701450.1483338769.1288429456.857182792.60370398.1671583505.267637105.426630013.1234818421.745197772.1535051601.927781008.1431050306.794457591.733103754.1383265629.523382169.474942311.239717355.985646286.1428076879.556422844.90818982.1249262861.1027947361.89169490.369195486.592251671.987672177.1180168604.1076147581.1309654601.544556440.704299574.1811703076.1368790914.1672393884.1850960697.240282989.1782937989.1535923227.489079947.101618023.1881146352.551139328.1706958764.2397912.1662606083.133177266.743134918.924315079.1666792398.752293246.517805672.1581838451.1208938271.957990853.703574450.1125096630.262844575.1214745920.22583548.1314391872.1186720310.1590713611.1552049031.1120264946.1619371084.1210273992.556034279
//s
//81311975136318641314402609712165089885923552589828905572746519709274178906557041507278220203237773191053567177790507870682272611395185140130694344782560408046667664176603123635119884714305334249895058999709321218782922296368560996907170881328638532274412047547173645569576251367523006216059942651219303894804677939119212846799575560051904884650764286707881546217186387354068692500481(s), 974677880(t)
//t
// 974677880
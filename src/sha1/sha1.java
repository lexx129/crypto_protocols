package sha1;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Lexx on 04.09.2015.
 */
public class sha1 {
    int A, B, C, D, E, F;
    int[] H = {0x67452301, 0xEFCDAB89, 0x98BADCFE, 0x10325476, 0xC3D2E1F0};
    int temp;


    String hash(byte[] data) {
        byte[] padded = messagePad(data);
        int[] H = {0x67452301, 0xEFCDAB89, 0x98BADCFE, 0x10325476, 0xC3D2E1F0};
        int[] K = {0x5A827999, 0x6ED9EBA1, 0x8F1BBCDC, 0xCA62C1D6};

        if (padded.length % 64 != 0) {
            System.err.println("Разбитое на блоки сообщение имеет неправильную длину");
            System.exit(0);
        }
        int reqPas = padded.length / 64;
        byte[] work = new byte[64];
        for (int i = 0; i < reqPas; i++) {
            System.arraycopy(padded, 64 * i, work, 0, 64);
            blockProcess(work, H, K);

        }
        return intArrayToHex(H);
    }

    private byte[] messagePad(byte[] data) {
        int textLength = data.length;
        int tailLength = textLength % 64;
        int padLength = 0;
        if ((64 - tailLength >= 9)) {
            padLength = 64 - tailLength;
        } else {
            padLength = 128 - tailLength;
        }
        byte[] pad = new byte[padLength];
        pad[0] = (byte) 0x80;
        long bitLength = textLength * 8;
        for (int i = 0; i < 8; i++) {
            pad[pad.length - 1 - i] = (byte) ((bitLength >> (8 * i)) & 0x00000000000000FF);
        }
        byte[] result = new byte[textLength + padLength];
        System.arraycopy(data, 0, result, 0, textLength);
        System.arraycopy(pad, 0, result, textLength, pad.length);
        return result;
    }

    private void blockProcess(byte[] block, int H[], int[] K) {
        int[] W = new int[80];
        for (int i = 0; i < 16; i++) {
            int temp = 0;
            for (int j = 0; j < 4; j++) {
                temp = (block[i * 4 +j] & 0x000000FF) << (24 - j * 8);
                W[i] = W[i] | temp;
            }
        }
        for (int i = 16; i < 80; i++) {
            W[i] = rotateLeft(W[i - 3] ^ W[i - 8] ^ W[i - 14] ^ W[i - 16], 1);
        }

        A = H[0];
        B = H[1];
        C = H[2];
        D = H[3];
        E = H[4];

        for (int i = 0; i < 20; i++) {
            F = (B & C) | ((~B) & D);
            temp = rotateLeft(A, 5) + F + E + K[0] + W[i];
//            System.out.println(Integer.toHexString(K[0]));
            E = D;
            D = C;
            C = rotateLeft(B, 30);
            B = A;
            A = temp;
        }

        for (int i = 20; i < 40; i++) {
            F = B ^ C ^ D;
            temp = rotateLeft(A, 5) + F + E + K[1] + W[i];
//            System.out.println(Integer.toHexString(K[1]));
            E = D;
            D = C;
            C = rotateLeft(B, 30);
            B = A;
            A = temp;
        }

        for (int i = 40; i < 60; i++) {
            F = (B & C) | (B & D) | (C & D);
            temp = rotateLeft(A, 5) + F + E + K[2] + W[i];
            E = D;
            D = C;
            C = rotateLeft(B, 30);
            B = A;
            A = temp;
        }
        for (int j = 60; j < 80; j++) {
            F = B ^ C ^ D;
            temp = rotateLeft(A, 5) + F + E + K[3] + W[j];
            E = D;
            D = C;
            C = rotateLeft(B, 30);
            B = A;
            A = temp;
        }

        H[0] += A;
        H[1] += B;
        H[2] += C;
        H[3] += D;
        H[4] += E;

        int n;
        for (n = 0; n < 16; n++) {
//            System.out.println("W[" + n + "] = " + toHexString(ByteBuffer.allocateDirect(W[n])));
        }
//        System.out.println("H0:" + Integer.toHexString(H[0]));
//        System.out.println("H0:" + Integer.toHexString(H[1]));
//        System.out.println("H0:" + Integer.toHexString(H[2]));
//        System.out.println("H0:" + Integer.toHexString(H[3]));
//        System.out.println("H0:" + Integer.toHexString(H[4]));
    }

    static final String toHexString(final ByteBuffer bb) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bb.limit(); i += 4) {
            if (i % 4 == 0) {
                sb.append('\n');
            }
            sb.append(toHexString(ByteBuffer.allocateDirect(bb.getInt(i)))).append(' ');
        }
        sb.append('\n');
        return sb.toString();
    }

    final int rotateLeft(int value, int bits) {
        int q = (value << bits) | (value >>> (32 - bits));
        return q;
    }

    private String intArrayToHex(int[] data) {
        String output = "";
        String temp = "";
        int tempInt = 0;
        for (int i = 0; i < data.length; i++) {
            tempInt = data[i];
            temp = Integer.toHexString(tempInt);

            if (temp.length() == 1) {
                temp = "0000000" + temp;
            } else if (temp.length() == 2) {
                temp = "000000" + temp;
            } else if (temp.length() == 3) {
                temp = "00000" + temp;
            } else if (temp.length() == 4) {
                temp = "0000" + temp;
            } else if (temp.length() == 5) {
                temp = "000" + temp;
            } else if (temp.length() == 6) {
                temp = "00" + temp;
            } else if (temp.length() == 7) {
                temp = "0" + temp;
            }
            output = output + temp;
        }
        return output;
    }

    //    private static String getHash(File file) throws NoSuchAlgorithmException {
//        MessageDigest instance = MessageDigest.getInstance("SHA-1");
//        byte[] bytes = new byte[4096];
//        try {
//            DataInputStream dis = new DataInputStream(new FileInputStream(file));
//            while (true) {
//                int read = dis.read(bytes);
//                if (read == -1) break;
//                instance.update(bytes);
//            }
//        } catch (IOException e) {
//            System.err.println("Access to file " + file.getName() + " is denied :(");
//        }
//        return new BigInteger(1, instance.digest()).toString(16);
//    }
    private static String getHash(File file) throws NoSuchAlgorithmException, IOException {
        MessageDigest instance = MessageDigest.getInstance("SHA-1");
        byte[] data = new byte[1024];
        FileInputStream fis = new FileInputStream(file);
        int read = 0;
        while ((read = fis.read(data)) != -1) {
            instance.update(data, 0, read);
        }

        byte[] hashBytes = instance.digest();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < hashBytes.length; i++) {
            sb.append(Integer.toString((hashBytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        String fileHash = sb.toString();
        return fileHash;
    }


    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        sha1 instance = new sha1();
        System.out.println("File path: ");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String path = br.readLine();
        File file = new File(path);
        byte[] input = Files.readAllBytes(file.toPath());
        System.out.println("My hash: " + instance.hash(input));
//        System.out.println(sha1("My name is Roy"));
        System.out.println("True hash: " + getHash(file));
//        cc8f92b8d8a8d1dd0a31f1417b952edc184acf76
//        C:\SwSetup\SoftPaq\SP54421\autorun45.inf
    }

}

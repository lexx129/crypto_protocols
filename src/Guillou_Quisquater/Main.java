package Guillou_Quisquater;

import java.io.*;
import java.math.BigInteger;

/**
 * Created by admin on 04.12.2015.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(System.out));

        SmartCard peggy = new SmartCard("Denis Markin 27.06.1994");
        System.out.println("n = " + peggy.getN());
        System.out.println("v = " + peggy.getV());
        System.out.println("j = " + peggy.getJ());
        bw.write("SmartCard generated. Waiting for d..\n");
        bw.write("d = ");
        bw.flush();
        BigInteger d = new BigInteger(br.readLine());
//        bw.write("***d = " + d + "\n");

        bw.flush();
        peggy.setD(d);
        peggy.calculateD();
    }
}

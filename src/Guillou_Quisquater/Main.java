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

        SmartCard peggy = new SmartCard("Peggy");
        bw.write("SmartCard generated. Waiting for d..\n");
        bw.write("d = ");
        bw.flush();
        BigInteger d = new BigInteger(br.readLine().getBytes());
        bw.write("d = " + d);
        bw.flush();
        peggy.setD(d);
        peggy.calculateD();


    }
}

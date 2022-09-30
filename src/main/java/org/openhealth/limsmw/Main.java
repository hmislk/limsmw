/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.openhealth.limsmw;

import java.io.*;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

/**
 *
 * @author buddh
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Login frame = new Login();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);  // *** this will center your app ***
        frame.setVisible(true);
    }

    public static void listen() throws Exception {
        System.out.println("listen");
        Character ack = (char) 06;

        ServerSocket ss = new ServerSocket(3333);
        System.out.println("ss = " + ss);
        Socket s = ss.accept();
        System.out.println("s = " + s);
        DataInputStream din = new DataInputStream(s.getInputStream());
        System.out.println("din = " + din);
        DataOutputStream dout = new DataOutputStream(s.getOutputStream());
        System.out.println("dout = " + dout);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("br = " + br);
        String str = "";
        String str2 = ack.toString();
        while (!str.equals("stop")) {

            try {
                str = din.readUTF();
            } catch (Exception e) {
                str = e.getMessage();
            }

            System.out.println("client says: " + str);
            str2 = br.readLine();
            dout.writeUTF(str2);
            System.out.println("write");
            dout.flush();
            System.out.println("flush = " + str2);
        }
        din.close();
        s.close();
        ss.close();
    }
}

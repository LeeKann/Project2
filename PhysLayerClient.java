/*
*Eric Kannampuzha
*Project 2
*Class PhysLayerClient.java
*CS 380
*Nima
*/

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.zip.CRC32;
import java.util.Arrays;
import java.nio.ByteBuffer;

public final class PhysLayerClient {

    public static void main(String[] args) throws Exception {
        try {
            Socket socket = new Socket("18.221.102.182", 38002);
            System.out.println("Connected to server.");
            InputStream is = socket.getInputStream();
            DataInputStream dis = new DataInputStream(is);
            
            int[] preamble = new int[64];
            double baseline = 0.0;
            
            for(int i = 0; i < 64; i++) {
                preamble[i] = dis.readUnsignedByte();
                baseline += preamble[i];
            }
            
            baseline = baseline/64;
            
            System.out.println("Baseline established from preamble: " + baseline);
            
            int[] message = new int[32];

            int nrzi = 0;
            int last = 0;
            for(int i = 0; i < 32; i++) {
                StringBuilder decoded = new StringBuilder();

                for(int k = 0; k < 2; k++) {
                    StringBuilder sb = new StringBuilder();
                    for(int j = 0; j < 5; j++) {
                        int inc = dis.readUnsignedByte();
                        if( inc > baseline )
                            inc = 1;
                        else 
                            inc = 0;
                        if( inc != last )
                            nrzi = 1;
                        if( inc == last )
                            nrzi = 0;
                        last = inc;
                        //System.out.print(last);
                        sb.append(nrzi);
                        //System.out.print(nrzi);
                    }
                    //System.out.println(sb.toString());
                    switch(sb.toString()) {
                        case "11110": decoded.append("0");
                        break;
                        
                        case "01001": decoded.append("1");
                        break;
                        
                        case "10100": decoded.append("2");
                        break;
                        
                        case "10101": decoded.append("3");
                        break;
                        
                        case "01010": decoded.append("4");
                        break;
                        
                        case "01011": decoded.append("5");
                        break;
                        
                        case "01110": decoded.append("6");
                        break;
                        
                        case "01111": decoded.append("7");
                        break;
                        
                        case "10010": decoded.append("8");
                        break;
                        
                        case "10011": decoded.append("9");
                        break;
                        
                        case "10110": decoded.append("A");
                        break;
                        
                        case "10111": decoded.append("B");
                        break;
                        
                        case "11010": decoded.append("C");
                        break;
                        
                        case "11011": decoded.append("D");
                        break;
                        
                        case "11100": decoded.append("E");
                        break;
                        
                        case "11101": decoded.append("F");
                        break;
                    }
                    //System.out.println("Decoded: " + decoded.toString());
                }
                //System.out.println("Decoded: " + decoded.toString());
                message[i] = Integer.decode("0x" + decoded.toString());
            }
            
            StringBuilder print = new StringBuilder();
            for(int m : message) {
                print.append(String.format("%02X ", m));
            }
            System.out.println("Received bytes:\n[" + print.toString() + "]");

            OutputStream os = socket.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            
            for(int i = 0; i<32; i++) {
                dos.writeByte(message[i]);
            }
            
            if(dis.readByte() == 0x1)
                System.out.println("Response good.");
            else
                System.out.println("Response bad.");
                
            socket.close();
            System.out.println("Disconnected from server.");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}

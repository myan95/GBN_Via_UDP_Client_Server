 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package udp_client;

/**
 *
 * @author Owner
 */
import java.io.*;
import java.net.*;
import java.nio.*;
import java.util.Random;
import java.lang.Object;
import java.lang.Number;
import java.util.ArrayList;
import com.sun.xml.internal.ws.developer.Serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;

//-----------------------------------------------------------------------------------------------------------------------------
public class UDP_Client implements Serializable {

    /**
     * @param args the command line arguments
     */
    public static final int BUFFER_SIZE = 1024;
    public static final int PORT = 6789;
    public static final String HOSTNAME = "localhost";
    public static final String FILE_NAME = "apple.png";
    public static String packet, ack, str, msg;
    public static int n, i = 0, sequence = 1;
    public static int WINDOWS_SIZE = 6;
    public static int TIMER = 30;
    //base seq no of oldest unacked packet 
    public static int Base = 0;
    public static short LastSeq = 0;
    //last packet sent 
    public static int LastPacket = 0;
    // Maximum Segment Size - Quantity of data from the application layer in the segment
    public static final int MSS = 100;
    public static long FileSize;
    // Sequence number of the last packet sent (rcvbase)
    public static int lastSent = 0;
    // Sequence number of the last acked packet
    public static int waitingForAck = 0;
    public static boolean last = false;
    public static int PacketSequence =0; 
    //--------------------------------------------
    public static ArrayList<Packet> SentPackets = new ArrayList<Packet>();
    public static int LastAckSeq = 0;

    // public static 
//----------------------------------------------------------------------------------------------------------------------------------    
    public static void main(String[] args) throws SocketException, UnknownHostException, FileNotFoundException, IOException, ClassNotFoundException {

        // Create a socket
        DatagramSocket ClientSocket = new DatagramSocket();
        ClientSocket.setSoTimeout(1000);

        byte[] receiveData = new byte[BUFFER_SIZE];
        InetAddress IPAddress = InetAddress.getByName(HOSTNAME);
        File file = new File(FILE_NAME);
        if (!file.exists() || !file.isFile()) {
            System.out.println("File doesn\'t exist");
        } else {
            FileSize = file.length();
            System.out.println("File Size Equals   " + FileSize);
        }

        // reading file name 
        FileInputStream fstream = new FileInputStream(FILE_NAME);
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader bcr = new BufferedReader(new InputStreamReader(in));

        byte[] FNBytes = new byte[BUFFER_SIZE];
        FNBytes = FILE_NAME.getBytes();
        //sending file name via socket 
        DatagramPacket sendPacketFN = new DatagramPacket(FNBytes, FNBytes.length, IPAddress, PORT);
        ClientSocket.send(sendPacketFN);

        //----------------------------------------------------------\--------------------------------------
        //creating windows and sent vector 
        Packet[] PacketWindow = new Packet[WINDOWS_SIZE];
        //total number of sequence
        int lastSeq = (int) Math.ceil((double) FileSize / MSS);
        System.out.println("Last sequence   " + lastSeq);

        //-------------------------------------------------------------------------------------------------
        String Sentences;
        int Counter = 0;
        byte[] ReadData = new byte[MSS];
        short SequenceCounter = 0;
        int NextSeq = Base + WINDOWS_SIZE;
        while ((Counter = fstream.read(ReadData)) != -1) {
           
             PacketSequence = Base; 
           
            System.out.println("readdata   " + ReadData);
            System.out.println("counter " + Counter);
            Packet PacketObject = new Packet((short) 0, (short) Counter, PacketSequence, ReadData, false, false);
            byte[] ToSendBytes = Serializer.toBytes(PacketObject);
            System.out.println("TBS Bytes   " + ToSendBytes.length);
            System.out.println("TBS    " + ToSendBytes);
            DatagramPacket sendPacket = new DatagramPacket(ToSendBytes, ToSendBytes.length, IPAddress, PORT);
            
            ClientSocket.send(sendPacket);
            SentPackets.add(PacketObject);
            PacketSequence++;    
           
            
            for (int i = Base+1; i < NextSeq; i++) {
                
                System.out.println("****** for Loop Entered ******");
                if ((Counter = fstream.read(ReadData)) != -1) {
                    System.out.println("readdata   " + ReadData);
                    System.out.println("counter " + Counter);
                    if (Base == lastSeq) {
                        last = true;
                    }
                    PacketObject = new Packet((short) 0, (short) Counter,PacketSequence, ReadData, false, last);
                    ToSendBytes = Serializer.toBytes(PacketObject);
                    System.out.println("TBS Bytes   " + ToSendBytes.length);
                    System.out.println("TBS    " + ToSendBytes);
                    sendPacket = new DatagramPacket(ToSendBytes, ToSendBytes.length, IPAddress, PORT);
                    ClientSocket.send(sendPacket);
                    SentPackets.add(PacketObject);
                    PacketSequence++;

                }
            }

            boolean timedOut = true;
            LastAckSeq = Base;
            int k = 0;
            while (timedOut) {
                try {
                    while (k < 6) {
                        ClientSocket.setSoTimeout(TIMER);

                        System.out.println("*********inside try for ack ********* ");
                        DatagramPacket AckRecive = new DatagramPacket(receiveData, receiveData.length);
                        ClientSocket.receive(AckRecive);
                        String AckSequence = new String(AckRecive.getData(), 0, AckRecive.getLength());
                        
                        Base++;
                        System.out.println("Base inside loop    "+Base);
                        k++;
                    }
                    timedOut=false; 

                } catch (SocketTimeoutException exception) {
                    //resend 
                    System.out.println("******Time out resend packet of Base    ******" + Base);
                     /*for(int i = Base; i < PacketSequence ; i++) {
                          // Serialize the RDTPacket object
                    ToSendBytes = Serializer.toBytes(SentPackets.get(i));
                    sendPacket = new DatagramPacket(ToSendBytes, ToSendBytes.length, IPAddress, PORT);
                    ClientSocket.send(sendPacket);
                     Base++;
                     }*/
               

                }
            }
            NextSeq = Base + WINDOWS_SIZE;
            System.out.println("Next Seq   " + NextSeq);
        }


        /*
         boolean  timedOut = true;    
        

        
         while ((Sentences = bcr.readLine()) != null) {

        

         sequence = (sequence == 0) ? 1 : 0;
         msg = String.valueOf(sequence);
         Sentences = msg.concat(Sentences);
         System.out.println("after concatination \t" + Sentences);

         while (timedOut) {

         System.out.println("counetr " + counter);

         sendData = Sentences.getBytes();
         try {
         Random r = new Random();
         float chance = r.nextFloat();
         if (chance <= 0.90f) {

         DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, PORT);
         ClientSocket.send(sendPacket);
         } else {
         System.out.println("***packet lost wait till time out***");
         }

         DatagramPacket AckRecive = new DatagramPacket(receiveData, receiveData.length);
         ClientSocket.receive(AckRecive);
         String AckSequence = new String(AckRecive.getData(), 0, AckRecive.getLength());

         if (Integer.valueOf(AckSequence.substring(0, 1)) == sequence) {
         System.out.println("Ack  not corrupted wait to resend ");
         } else {
         System.out.println("Ack corrupted wait to resend ");
         }

         timedOut = false;

         } catch (SocketTimeoutException exception) {
         //resend 
         System.out.println("******Time out resend packet******");

         }
         counter++;
         }

         }
         */
        ClientSocket.close();

    }

}

/* serialization 
 //Packet PacketObject = new Packet((short)0,(short)Counter,LastPacket, ReadData, false,false);
             
 byte[] ToSendBytes = Serializer.toBytes(PacketObject);
 System.out.println("TBS Bytes   "+ToSendBytes.length);
 System.out.println("TBS    "+ToSendBytes);
             
             
 //Packet PacketObject = new Packet((short)0,(short)Counter,LastPacket, ReadData, false,false);
 /*  ByteArrayOutputStream b = new ByteArrayOutputStream();
 ObjectOutputStream o = new ObjectOutputStream(b);
 o.writeObject(PacketObject);
 byte[] ToSendBytes = b.toByteArray(); */
            //byte[] ToSendBytes = Serializer.toBytes(PacketObject);
//byte[] ToSendBytes = Serializer.toBytes(rdtPacketObject);
/*
 System.out.println("to bytes " + ToSendBytes);
 DatagramPacket sendPacket = new DatagramPacket(ToSendBytes, ToSendBytes.length, IPAddress, PORT);

 ClientSocket.send(sendPacket);

 System.out.println("Tosend bytes    " + ToSendBytes);
 System.out.println("To sendbyteslengh  " + ToSendBytes.length);
 //SentPackets.add(PacketObject);
 //SentPackets.add(rdtPacketObject);*/

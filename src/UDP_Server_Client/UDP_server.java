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
import java.util.*;
import java.io.BufferedWriter;
import static udp_client.UDP_Client.Base;
import static udp_client.UDP_Client.LastPacket;
import static udp_client.UDP_Client.PORT;
import static udp_client.UDP_Client.SentPackets;
import static udp_client.UDP_Client.last;

//----------------------------------------------------------------------------------------------
public class UDP_server {

    /**
     * @param args the command line arguments
     */
    private static final int BUFFER_SIZE = 1024;
    private static final int PORT = 6789;
    private static String FILE_NAME;
    private static int sequence = 0;
    private static int RecievedSequence = 0;
    private static String Ack = "0";
    public static final int MSS = 100;
    public static int Base = 0;
    public static int nextseq = 0;
    public static int waitingfor = 0;
    public static ArrayList<Packet> RecievedPackets = new ArrayList<Packet>();

    //private FileEvent fileEvent = null;
    // private static String packet,ack,data="";
    public static void main(String[] args) throws Exception, SocketException, IOException, ClassNotFoundException {

        // Create a server socket
        DatagramSocket serverSocket = new DatagramSocket(PORT);

        // Set up byte arrays for sending/receiving data
        byte[] receiveData = new byte[MSS + 120];
        byte[] dataForSend = new byte[BUFFER_SIZE];

        DatagramPacket ReceivedFN = new DatagramPacket(receiveData, receiveData.length);
        serverSocket.receive(ReceivedFN);
        String FN = new String(ReceivedFN.getData(), 0, ReceivedFN.getLength());
        System.out.println("FN" + FN);
        File file = new File("Reciedfile" + FN);
        System.out.println("file created ");
        int counter = 0;
        FileOutputStream fout = new FileOutputStream(file);
        boolean end = false;
        //-------------------------------------------------------------------------------------------------
        while (!end) {

            //receive and save image from client
            System.out.println("counter " + counter);
            counter++;
            ReceivedFN = new DatagramPacket(receiveData, receiveData.length);
            serverSocket.receive(ReceivedFN);
            byte[] data;
            Packet packet = (Packet) Serializer.toObject((data = ReceivedFN.getData()));
            System.out.println("Packet with sequence number " + packet.Getseqno() + " received (last: " + packet.IsLast() + " )");
            System.out.println("bytes " + data);
            int BytesSize = data.length;
            System.out.println("Bytes equals   " + BytesSize);
            System.out.println(" data \t" + data);
            int seq = packet.Getseqno();
            System.out.println("********seq****************>>>>>>>>>>  "+seq);
            System.out.println("waitingfor  >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> "+waitingfor);
            
            if (packet.Getseqno() == waitingfor && packet.IsLast()) {
                System.out.println("***** last packet ****** ");
                String AckValue = String.valueOf(packet.Getseqno());

                InetAddress IPAddress = ReceivedFN.getAddress();
                int port = ReceivedFN.getPort();
                byte[] Ackpacket = AckValue.getBytes();
                DatagramPacket Ack = new DatagramPacket(Ackpacket, Ackpacket.length, IPAddress, port);
                serverSocket.send(Ack);
                fout.write(packet.Getdata(), 0, packet.Getlen());
                waitingfor++;   
                end = true;
            } else if (packet.Getseqno() == waitingfor) {

                System.out.println("**** required packet *****");
                String AckValue = String.valueOf(packet.Getseqno());

                InetAddress IPAddress = ReceivedFN.getAddress();
                int port = ReceivedFN.getPort();
                byte[] Ackpacket = AckValue.getBytes();
                DatagramPacket Ack = new DatagramPacket(Ackpacket, Ackpacket.length, IPAddress, port);
                serverSocket.send(Ack);
                fout.write(packet.Getdata(), 0, packet.Getlen());
                waitingfor++;
            } else {
                System.out.println("Packet discarded (not in order)");
            }
            fout.flush();

        }

        /*
         while (true) {
         System.out.println("while true entered ");
         String NL = "\n";
            
         ReceivedFN = new DatagramPacket(receiveData, receiveData.length);
         serverSocket.receive(ReceivedFN);
         FN = new String(ReceivedFN.getData(), 0, ReceivedFN.getLength());
         Random random = new Random( );
             
         //int AckChance = random.nextInt( 100 );
         Random r = new Random();
         float chance = r.nextFloat();
            
         if(Integer.valueOf(FN.substring(0,1))== sequence){
         // if( ((AckChance % 2) == 0) ){
         if(chance <= 0.90f ){
            
         System.out.println("FROM CLIENT: " + FN);
         String AckValue = String.valueOf(sequence);
         System.out.println("if entered ");
         FN =FN.substring(1,FN.length());
         sequence=(sequence==0)?1:0;
         System.out.println("seuence "+sequence);
         byte[] LineBytes = FN.getBytes();
         fout.write(LineBytes, 0, LineBytes.length);
         String NewLine = "\n";
         byte[] gap = NewLine.getBytes();
         fout.write(gap);
                  
         fout.flush();
                 
         // Get packet's IP and port
         Random K = new Random();
         float Ackcorrution = K.nextFloat();
         if(chance <= 0.10f ){
         if(AckValue.equals("0"))
         AckValue="1";
         else 
         AckValue ="0";
              
         }
         InetAddress IPAddress = ReceivedFN.getAddress();
         int port = ReceivedFN.getPort();
         byte[] Ackpacket= AckValue.getBytes();
         DatagramPacket Ack = new DatagramPacket(Ackpacket,Ackpacket.length, IPAddress, port);
         serverSocket.send(Ack);

              
         } else {
         System.out.println( "Oops, packet with sequence number    "+ sequence+ "     was dropped");
         }
            
         }else {System.out.println("Wrong packet sequence *** doubled data ");
         System.out.println("sequence "+sequence);
         }

            
         }
         */
    }

}

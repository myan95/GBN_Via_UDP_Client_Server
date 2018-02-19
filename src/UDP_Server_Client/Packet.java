/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package udp_client;
import java.io.Serializable;
import java.lang.Object;
import java.lang.Number;
/**
 *
 * @author Myan
 */
public class Packet implements Serializable {
    short  cksum; /* Optional bonus part */
    short  len;
    int seqno;
    byte[] data ; 
    boolean acked;
    boolean last ; 
    
    public Packet (short  cksum , short  len, int seqno,byte[] data, boolean acked , boolean last){ 
        this.cksum = cksum ; 
        this.len = len;
        this.seqno = seqno;
        this.data= data;
        this.acked=acked;
        this.last =last;
     
    }
   public short Getcksum (){return cksum;}
   public void setcksum (short cksum){this.cksum = cksum; }
    
   public short Getlen (){return len;}
   public void setlen (short len){this.len = len; }
  
   public int Getseqno (){return seqno;}
   public void setseqno (short seqno){this.seqno = seqno; }
  
   public byte [] Getdata (){return data;}
   public void setcksum (byte[] data){this.data = data; }
  
   public boolean IsAcked (){return acked;}
   public void SetAcked (){ acked = true ;}
   
   public boolean IsLast (){return last;}
   public void SetLast (){ last = true ;}
   

}

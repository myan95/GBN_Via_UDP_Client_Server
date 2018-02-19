/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package udp_client;

/**
 *
 * @author Myan
 */
public class Ack {
    int Ackseqno;
    
    public Ack (int Ackseqno){ 
        this.Ackseqno = Ackseqno;
     
    }
    
   public int GetAckseqno (){return Ackseqno;}
   public void setAckseqno (short Ackno){this.Ackseqno = Ackno; }
    
    
}

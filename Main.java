import java.util.*;
import java.io.*;
import java.net.*;

/**---------- Main method -----------**/
public class Main{
	public static void main(String[] args){
		/*---------- First check the arguments -----------*/
		if(args.length != 2){
			System.out.println("usage: DatagramServer_port Datagram_IP");
			return;
		}
	
		try{
			int port = Integer.parseInt(args[0]);
			InetAddress host = InetAddress.getByName(args[1]);
			
			/*---------- Pass the arguments to the thread classes -----------*/
			SendAudio send = new SendAudio(port,host);
			ReceiveAudio receive = new ReceiveAudio(port,host);
			
			/*---------- Starting the thread classes -----------*/
			send.start();
			receive.start(); 
		}
		catch(Exception e) {
			System.out.println(e) ;
		}
	}
}
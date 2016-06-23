import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.SourceDataLine;
import java.io.*;
import java.net.*;

/*******---------- Receive audio class extended from thread -----------*******/
public class ReceiveAudio extends Thread{
	private static SourceDataLine sourceDataLine;
	
	private static InetAddress IP = null;
	private static int PORT = 0;
	
	/*---------- creating the constructor and pass arguments -----------*/
	public ReceiveAudio(int port, InetAddress host){
		this.IP = host;
		this.PORT = port;
	}
	
	/*---------- Run the thread object -----------*/
	public void run(){
		byte buffer[] = null;
		while(true){
			buffer = getAudio();
			toPlay(buffer);
		}
	}
	
	/*---------- Get the audio from the sender -----------*/
	public static byte[] getAudio(){
		byte[] tempBuffer = new byte[1024];
		try{
			DatagramSocket receiver_socket = new DatagramSocket(PORT);	//Create the socket
			DatagramPacket receive_packet = new DatagramPacket(tempBuffer, tempBuffer.length, IP, PORT);	//Create the packet
			
			receiver_socket.receive(receive_packet);		//Get the receive packet
			receiver_socket.close();		//Close the created socket
			
			return receive_packet.getData();	//Return got audio packet to "toPlay" class
		}
		catch (Exception e){
			System.out.println(e);
			System.exit(0);
			return null;
		}
		
	}
	
	/*---------- Format the audio which get from the sender -----------*/
	public static AudioFormat getAudioFormat(){
		float sampleRate = 16000.0F;
		int sampleSizeInBits = 16;
		int channels = 2;
		boolean signed = true;
		boolean bigEndian = true;
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
	}
	
	/*---------- Play the audio which get from the sender -----------*/
	public static void toPlay(byte audioPack[]){
		try{
			AudioFormat audioFormat = getAudioFormat();
			DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
			sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
			sourceDataLine.open(audioFormat);
			sourceDataLine.start();
			
			//Setting the maximum volume
			FloatControl control = (FloatControl)sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
			control.setValue(control.getMaximum());
			
			sourceDataLine.write(audioPack, 0, audioPack.length);
			sourceDataLine.drain();
			sourceDataLine.close();
		}
		catch (Exception e){
			System.out.println(e);
			System.exit(0);
			
		}
	}
}
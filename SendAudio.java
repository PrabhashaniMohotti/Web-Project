import java.io.IOException;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import java.io.*;
import java.net.*;

/*******---------- Sending audio class extended from thread -----------*******/
public class SendAudio extends Thread{
	private static TargetDataLine targetDataLine;
	private static AudioFormat audioFormat;
	
	private static InetAddress IP = null;
	private static int PORT = 0;
	
	/*---------- creating the constructor and pass arguments -----------*/
	public SendAudio(int port, InetAddress host){
		this.IP = host;
		this.PORT = port;
	}
	
	/*---------- Run the thread object -----------*/
	public void run(){
		/*---------- get the audio from sender -----------*/
		Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();    //get available mixers
		System.out.println("Available mixers:");
		Mixer mixer = null;
		for (int cnt = 0; cnt < mixerInfo.length; cnt++) {
			System.out.println(cnt + " " + mixerInfo[cnt].getName());
			mixer = AudioSystem.getMixer(mixerInfo[cnt]);

			Line.Info[] lineInfos = mixer.getTargetLineInfo();
			if (lineInfos.length >= 1 && lineInfos[0].getLineClass().equals(TargetDataLine.class)) {
				System.out.println(cnt + " Mic is supported!");
				break;
			}
		}
		
		byte Buffer[] = null;		//Frist initialized the audio byte array as null
		captureAudio(Buffer);		//Call the captureAudio class
	}

	
	/*---------- Capture the audio which get from the sender -----------*/
	public static void captureAudio(byte audioByte[]){
		try{
			audioFormat = getAudioFormat();
			DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
			targetDataLine = (TargetDataLine)AudioSystem.getLine(dataLineInfo);
			targetDataLine.open(audioFormat);
			targetDataLine.start();
			
			byte tempBuffer[] = new byte[1024];
			/*
			int n = 0;
			byte[] b = new byte[4];
			for (int i=0; i < 4; ++i){
				b[i] = (byte) (n & 0xFF);
				n = n >> 8;
			}*/
			
			while(true){
				targetDataLine.read(tempBuffer, 0, tempBuffer.length);		//capture sound
				sendAudio(tempBuffer);		// send the audio to the SendAudio class
			}
		}
		catch (Exception e){
			System.out.println(e);
			System.exit(0);
		}
	}
	
	/*---------- Get the capture audio and send to the receiver -----------*/
	public static void sendAudio(byte audioPack[]){
		try{
			DatagramSocket sender_socket = new DatagramSocket();	//create the socket
			DatagramPacket send_packet = new DatagramPacket(audioPack, audioPack.length, IP, PORT);	//create the audio packet
			sender_socket.send(send_packet);	//Send the packet
			sender_socket.close();		//Close the created socket
		}
		catch(Exception e){
			System.out.println(e);
			System.exit(0);
		}
	}
	
	//Get the captured audio and format it
	public static AudioFormat getAudioFormat(){
		float sampleRate = 16000.0F;
		int sampleSizeInBits = 16;
		int channels = 2;
		boolean signed = true;
		boolean bigEndian = true;
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
	}
}
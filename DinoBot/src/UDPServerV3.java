

import java.io.*;
import java.net.*;
import java.util.List;

import com.google.protobuf.CodedInputStream;

import ProtoBufProtos.BlenderDataProtos.BlenderData;
import ProtoBufProtos.BlenderDataProtos.Command;
import ProtoBufProtos.BlenderDataProtos.Header;
import ProtoBufProtos.EnvironmentProtos.EnvironmentObjects;
import ProtoBufProtos.EnvironmentProtos.Vegetation;
import ProtoBufProtos.EnvironmentProtos.Water;
import ProtoBufProtos.NewTargetsProtos.NewTargets;

public class UDPServerV3
{
    // Class Variables
	
	// Port Number
	int listenPort = 10000;
	int sendPort = 10001;
	
	// Header variables
	int localSeq;
	int remoteSeq;

    // LISTEN
    private DatagramSocket serverSocket;

    private byte[] receiveData;

    private DatagramPacket receivePacket;
    private byte[] receiveBytePacket;
    private SocketAddress address;

    // SEND
    private DatagramSocket clientSocket = new DatagramSocket();
    private byte[] sendData;
    private InetAddress IPAddress;
    
    // Round Trip Time
    private long[] rTT;

    
    public UDPServerV3() throws Exception {
    	
    	try {
    		
	        serverSocket = new DatagramSocket(listenPort);
	        receiveData = new byte[1024];
	
	        receivePacket = new DatagramPacket(receiveData, receiveData.length);
	
	        address = new InetSocketAddress(listenPort);
	        
	        /*
	         * CHANGE LINES BELOW FOR DISTRIBUTED SET UP 
	         */
	        
	        //IPAddress = InetAddress.getByName("192.168.2.5");
	        IPAddress = InetAddress.getByName("localhost");
	        
	        /*
	         * 
	         */
	        
	        serverSocket.setSoTimeout(1);
	        
	        localSeq = 0;
	        remoteSeq = 0;
	    	
	    	rTT = new long[2000];

    	}

    	catch (SocketException e) {

    		System.out.println(e);

    	}

    }
    
    // Sends message to Blender to start and receives as reply Environment object positions
    public EnvironmentObjects initialise() throws Exception {
    	
    	try {
    		// Tell Blender to start
    		BlenderData.Builder commands = BlenderData.newBuilder();
    		Command init = Command.newBuilder()
        				.setCommandType("JAVA-INITIALISE")
    					.build();
        		
        	commands.addCommand(init);
        	
        	BlenderData initCommand = commands.build();
        	
        	ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        	initCommand.writeDelimitedTo(outputStream);

    		byte c[] = outputStream.toByteArray( );
    		
    		DatagramPacket sendPacket = new DatagramPacket(c, c.length, IPAddress, sendPort);
    		clientSocket.send(sendPacket);

      		System.out.println("Sent Environment Object Initialiser");
      		
      		// wait 2 seconds
      	    Thread.sleep(2000);
      	    System.out.println("Waited for Blender");
      	    
      	    EnvironmentObjects.Builder objects = EnvironmentObjects.newBuilder();
      	    DatagramPacket p = new DatagramPacket(new byte[1024], 1024);
      	    
      	    while(recieve(p) == true) {
	      	    
	      	    EnvironmentObjects envObjects = EnvironmentObjects.parseFrom(CodedInputStream.newInstance(p.getData(), p.getOffset(), p.getLength()));
	      	    
	      	    for (Water water: envObjects.getWaterList()) {
	      	    	objects.addWater(water);
	      	    }
	
	      	    for (Vegetation veg: envObjects.getVegList()) {
	
	      	    	objects.addVeg(veg);
	      	    }
	      	    
	      	    System.out.println(envObjects.toString());
      	    }
			
      	    outputStream.close();
      	    
			return objects.build();
 
		}

		// Set Blocking Timeout
		catch (SocketTimeoutException e) {

			// Timeout is 1 millisecond 
			// No more packets
			System.out.println(e);
		}
    	
    	catch (UnknownHostException e) {

    		System.out.println(e);

    	}

    	catch (IOException e) {

    		System.out.println(e);

    	}
		return null;
    	
    }
    
    // Receives Data into parameter packet returning true, if none to receive returns false
    public boolean recieve(DatagramPacket p) throws Exception{
    	
    	try {
			serverSocket.receive(p);
			
			return true;
		} 
    	catch (SocketTimeoutException e) {

			return false;
		}
    	catch (IOException e) {
			return false;
		}
    }
    
    // Sends Protobufs efficiently
    // 10 Protobufs per UDP packet
    public void send(BlenderData commands) throws Exception {
    	
    	try {
    		
    		List<Command> commandList = commands.getCommandList();
    		ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
    		BlenderData.Builder commandChunks = BlenderData.newBuilder();
    		int counter = 0;
    		for(int x = 0; x < commandList.size(); x++) {
    			
    			commandChunks.addCommand(commandList.get(x));
    			
    			
    			counter = counter + 1;
    			
    			if(counter == 9) { // Send after 10 protobufs built
    				counter = 0;
    				
    				Header header = Header.newBuilder()
    	    				.setSequenceNum(localSeq)
    	    				.setAck(1)
    	    				.setBitField(1)
    						.build();
    				
    				commandChunks.setHeader(header);
    				
    				BlenderData readyForSendingChunks = commandChunks.build();
    				readyForSendingChunks.writeDelimitedTo(outputStream);
    	    		byte buffer[] = outputStream.toByteArray( );
    	    		
    	    		
    	    		DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length, IPAddress, sendPort);
    	    		  
    	    		clientSocket.send(sendPacket);
    	    		
    	    		//Keep record of packet
    	  			rTT[localSeq] = System.currentTimeMillis();
    	    		
    	    		localSeq++;
    	    		
    	    		if(localSeq > 1999) {
    	    			localSeq = 0;
    	    		}
    				//System.out.println("9 commands are " + buffer.length + "bytes");
    	    		commandChunks = BlenderData.newBuilder();
    	    		outputStream.reset();
    			}
    		
    		}
    		// Commands processed - send remaining
    		if(counter != 0) { // Still commands left to be sent
				counter = 0;
				
				Header header = Header.newBuilder()
	    				.setSequenceNum(localSeq)
	    				.setAck(1)
	    				.setBitField(1)
						.build();
				
				commandChunks.setHeader(header);
				
				BlenderData readyForSendingChunks = commandChunks.build();
				readyForSendingChunks.writeDelimitedTo(outputStream);
	    		byte buffer[] = outputStream.toByteArray( );
	    		
	    		
	    		DatagramPacket sendPacket = new DatagramPacket(buffer, buffer.length, IPAddress, sendPort);
	    		  
	    		clientSocket.send(sendPacket);
	    		
	    		//Keep record of packet
	  			rTT[localSeq] = System.currentTimeMillis();
	    		
	    		localSeq++;
	    		
	    		if(localSeq > 1999) {
	    			localSeq = 0;
	    		}
			}
    		
    		outputStream.close();
    		
		}

		// Set Blocking Timeout
		catch (SocketTimeoutException e) {
			System.out.println(e);
		}
    	catch (UnknownHostException e) {
    		System.out.println(e);
    	}
    	catch (IOException e) {
    		System.out.println(e);
    	}
    	
    }
    
	// Try and receive data packets
    public BlenderData listen() throws Exception {
    	
		try {
			BlenderData.Builder blendData = BlenderData.newBuilder();
		    DatagramPacket p = new DatagramPacket(new byte[1024], 1024);
		    int x = 0;
		    int listenLimit = 100;
		    
		    while(recieve(p) == true && x < listenLimit) {
	  	    
		    	BlenderData recievedData = BlenderData.parseFrom(CodedInputStream.newInstance(p.getData(), p.getOffset(), p.getLength()));
		    	
		  	    for (Command command: recievedData.getCommandList()) {
		  	    	
		  	    	blendData.addCommand(command);
		  	    }
		  	    
		  	    if(recievedData.hasHeader()) {
		  	    	
		  	    	Header header = recievedData.getHeader();
		  	    	//System.out.println(header.getAck());
		  	    	
		  	    	// TESTING ONLY
		  	    	/*
		  	    	if(header.getAck() > 1000) {
		    			//localSeq = 0;
		    			System.exit(0);
		    		}
		    		
		  	    	
		  	    	long packetSentTime = rTT[header.getAck()];
		  	    	long diff = (System.currentTimeMillis()) - packetSentTime;
		  	    	
		  	    	System.out.println("Recieved Packet Ack "+ header.getAck() + " rTT:" + diff);
		  	    	*/
		  	    	//System.out.println(diff);
		  	    }
		  	    
		  	    x = x + 1;
		  	    
	  	    //System.out.println(recievedData.toString());
		    }
		    
		    return blendData.build();
		    
		    
		}

		catch (SocketTimeoutException e) {

		}

		finally {

		}
	
    	return null;

    }
    
}

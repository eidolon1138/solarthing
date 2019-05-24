package me.retrodaredevil.iot;

import me.retrodaredevil.iot.packets.Packet;
import me.retrodaredevil.iot.packets.PacketCreator;
import me.retrodaredevil.iot.packets.PacketSaver;
import org.lightcouch.CouchDbException;
import org.lightcouch.DocumentConflictException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SolarReader implements Runnable{
	private static final long SAME_PACKET_COLLECTION_TIME = 250;

	private final int throttleFactor;
	private final InputStream in;
	private final PacketCreator creator;
	private final PacketSaver packetSaver;
	
	
	private final List<Packet> packetList = new ArrayList<>(); // a list that piles up SolarPackets and saves when needed // may be cleared
	private long lastFirstReceivedData = Long.MIN_VALUE; // the last time a packet was added to packetList
	private int packetCollectionCounter = -1;
	private final byte[] buffer = new byte[1024];

	/**
	 *
	 * @param in The InputStream to read directly from
	 * @param throttleFactor Will save every nth packet where n is this number
	 * @param packetCreator The packet creator that creates packets from bytes
	 * @param packetSaver The packet saver that saves a collection of packets at once
	 */
	public SolarReader(InputStream in, int throttleFactor, PacketCreator packetCreator, PacketSaver packetSaver) {
		this.in = in;
		this.throttleFactor = throttleFactor;
		this.creator = packetCreator;
		this.packetSaver = packetSaver;
	}

	@Override
	public void run() {
		try {
			// This implementation isn't perfect - we cannot detect EOF
			// stackoverflow: https://stackoverflow.com/q/53291868/5434860
			
			// ======= read bytes, append to packetList =======
			int len = 0;
			while (in.available() > 0 && (len = in.read(buffer)) > -1) {
				String s = new String(buffer, 0, len);
//					System.out.println("got: '" + s.replaceAll("\n", "\\\\n").replaceAll("\r", "\\\\r") + "'. len: " + len);
				Collection<Packet> newPackets = creator.add(s.toCharArray());
				
				long now = System.currentTimeMillis();
				if(lastFirstReceivedData + SAME_PACKET_COLLECTION_TIME < now) {
					lastFirstReceivedData = now; // set this to the first time we get bytes
				}
				packetList.addAll(newPackets);
			}
			
			// ======= Save data if needed =======
			long now = System.currentTimeMillis();
			if (len == -1 || lastFirstReceivedData + SAME_PACKET_COLLECTION_TIME < now) { // if there's no packets coming any time soon
				if(!packetList.isEmpty()) {
					packetCollectionCounter++;
					// because packetCollectionCounter starts at -1, after above if statement, it will be >= 0
					if(packetCollectionCounter % throttleFactor == 0) {
						System.out.println("saving above packet(s). packetList.size(): " + packetList.size());
						packetSaver.savePackets(packetList);
						packetList.clear();
					} else {
						System.out.println("Not saving above packet(s) because " +
								"throttleFactor: " + throttleFactor +
								" packetCollectionCounter: " + packetCollectionCounter);
						packetList.clear(); // don't save these packets - ignore them
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("We got an IOException which doesn't happen often. We are going to try again so hopefully this works.");
		} catch(DocumentConflictException ex){
			ex.printStackTrace();
			System.err.println("Error while saving something to couchdb. Continuing like nothing happened now. " +
					"Your throttle factor (--tf) may be too low.");
		} catch(CouchDbException ex){
			ex.printStackTrace();
			System.err.println("We got a CouchDbException probably meaning we couldn't reach the database.");
		}
	}
}
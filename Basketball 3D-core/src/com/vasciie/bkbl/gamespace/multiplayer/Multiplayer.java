package com.vasciie.bkbl.gamespace.multiplayer;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.vasciie.bkbl.gamespace.GameMap;

public class Multiplayer extends Listener {

	private static final int udpPort = 27960, tcpPort = 27960;
	Server server;
	Client client;
	
	GameMap map;
	
	ArrayList<ModelInstance> modelInstances;
	
	int team; //0 - unassigned, 1 - blue, 2 - red
	
	
	public Multiplayer(GameMap map) {
		this.map = map;
		
		modelInstances = new ArrayList<ModelInstance>();
		
		server = new Server(); //Create the server
		client = new Client(); //Create the client
	}
	
	public void create() throws Exception {
		server.getKryo().register(PacketMessage.class); //Register a packet class. We can only send objects as packets if they are registered!
		server.getKryo().register(ModelInstance.class);
		
		server.bind(tcpPort, udpPort); //Bind to a port
		
		server.start(); //Start the server
		
		server.addListener(this); //Add the listener
	}
	
	public void join(String ip) throws Exception {
		client.getKryo().register(PacketMessage.class);
		client.getKryo().register(ModelInstance.class);
		
		client.setName("tempClient");
		
		client.start(); //Start the client! The client MUST be started before connecting can take place
		//System.out.println(client.discoverHost(tcpPort, udpPort).getHostName());
		/*for(InetAddress a : client.discoverHosts(tcpPort, udpPort)) {
			System.out.println(a.getHostAddress());
		}*/
		
		client.connect(5000, ip, tcpPort, udpPort); //Connect to the server - wait 5000 ms before failing
		
		client.addListener(this); //Add a listener
	}
	
	public void disconnect() {
		client.stop();
		
		modelInstances.clear();
		//client = null;
	}
	
	public void quit() {
		server.stop();
		
		modelInstances.clear();
		//server = null;
	}
	
	public void setTeam(int team) {
		this.team = team;
	}
	
	public ArrayList<ModelInstance> getModelInstances(){
		return modelInstances;
	}
	
	public boolean isServer() {
		return server != null;
	}
	
	public boolean isMultiplayer() {
		return server != null || client != null;
	}
	
	@Override
	public void received(Connection c, Object o) {
		if(o instanceof ModelInstance) {
			modelInstances.add((ModelInstance) o);
		}
	}
	
}

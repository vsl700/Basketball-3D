package com.vasciie.bkbl.gamespace.multiplayer;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.vasciie.bkbl.gamespace.GameMap;
import com.vasciie.bkbl.gamespace.entities.Player;
import com.vasciie.bkbl.gamespace.tools.InputController;

import sun.misc.Queue;

public class Multiplayer extends Listener {

	private static final PacketMessage message = new PacketMessage();
	
	private static final int udpPort = 27960, tcpPort = 27960;
	Server server;
	Client client;
	
	GameMap map;
	
	//HashMap<InputController, Connection> inputConnection;
	HashMap<Connection, Player> connectionPlayer;
	
	int team; //0 - unassigned, 1 - blue, 2 - red
	
	boolean host, join;
	
	
	public Multiplayer(GameMap map) {
		this.map = map;
		
		server = new Server(); //Create the server
		client = new Client(); //Create the client
		
		//inputConnection = new HashMap<InputController, Connection>();
		connectionPlayer = new HashMap<Connection, Player>();
	}
	
	public void create() throws Exception {
		team = 1;
		
		server.getKryo().register(PacketMessage.class); //Register a packet class. We can only send objects as packets if they are registered!
		server.getKryo().register(float[].class);
		server.getKryo().register(Object[].class);
		server.getKryo().register(Matrix4.class);
		server.getKryo().register(Array.class);//For the transforms
		server.getKryo().register(InputController.class);
		
		server.bind(tcpPort, udpPort); //Bind to a port
		
		server.start(); //Start the server
		
		server.addListener(this); //Add the listener
		
		host = true;
	}
	
	public void join(String ip) throws Exception {
		client.getKryo().register(PacketMessage.class);
		client.getKryo().register(float[].class);
		client.getKryo().register(Object[].class);
		client.getKryo().register(Matrix4.class);
		client.getKryo().register(Array.class);//For the transforms
		client.getKryo().register(InputController.class);
		
		client.setName("tempClient");
		
		new Thread(client).start(); //Start the client! The client MUST be started before connecting can take place
		//System.out.println(client.discoverHost(tcpPort, udpPort).getHostName());
		/*for(InetAddress a : client.discoverHosts(tcpPort, udpPort)) {
			System.out.println(a.getHostAddress());
		}*/
		
		client.connect(999999999, ip, tcpPort, udpPort); //Connect to the server - wait 5000 ms before failing
		
		client.addListener(this); //Add a listener
		
		client.setKeepAliveTCP(1);
		
		join = true;
	}
	
	public void begin() {
		//When the host starts the game (sending chosen rules & challenges & available players, creating hashmaps)
		
		assignPlayer(team);
		assignPlayer(1);
		
		message.message = "teammates:" + map.getTeammates().size();
		sendToAllTCP(message);
		
		message.message = "opponents:" + map.getOpponents().size();
		sendToAllTCP(message);
		
		ArrayList<Player> tempPlayers = map.getAllPlayers();
		for(int i = 0; i < server.getConnections().length; i++) {
			Connection c = server.getConnections()[i];
			connectionPlayer.put(c, tempPlayers.get(i));
			
			tempPlayers.get(i).setABot(false);
			
			message.message = "mainPlayer:" + i;
			c.sendTCP(message);
		}
		
		
		
		
	}
	
	public void update() {
		if(isServer()) {
			ArrayList<Player> tempPlayers = map.getAllPlayers();
			for(int i = 0; i < tempPlayers.size(); i++) {
				message.message = "index:" + i;
				sendToAllTCP(message);
				
				sendToAllTCP(tempPlayers.get(i).getModelInstance().transform);
				
				sendToAllTCP(message);
				
				sendToAllTCP(tempPlayers.get(i).getNodesTransforms());
			}
		}else if(client.getTcpWriteBufferSize() < 8000){
			client.sendTCP(map.getInputs());
		}
	}
	
	private void sendToAllTCP(Object o) {
		for(Connection c : server.getConnections()) {
			if(c.getTcpWriteBufferSize() < 8000) {
				c.sendTCP(o);
			}
		}
	}
	
	public void disconnect() {
		client.stop();
		
		join = false;
		//client = null;
	}
	
	public void quit() {
		server.stop();
		
		connectionPlayer.clear();
		//inputConnection.clear();
		host = false;
		//server = null;
	}
	
	public void setTeam(int team) {
		this.team = team;
	}
	
	public void assignPlayer(int team) {
		if(team == 1)
			map.spawnPlayers(1, 0);
		else if(team == 2)
			map.spawnPlayers(0, 1);
	}
	
	public boolean isServer() {
		return host;
	}
	
	public boolean isMultiplayer() {
		return host || join;
	}
	
	/*@Override
	public void connected(Connection c) {
		assignPlayer(1);
	}*/
	
	private Queue<Integer> awaitingIndexes = new Queue<Integer>();
	private HashMap<Connection, InputController> awaitingInput;
	private boolean awaitingBall;
	@SuppressWarnings("unchecked")
	@Override
	public void received(Connection c, final Object o) {
		if(o instanceof PacketMessage) {
			final String message = ((PacketMessage) o).message;
			/*if(message.equals("shareInputs")) {
				c.sendTCP(map.getInputs());
			}else */if(message.contains("delta:")) {
				map.controlPlayer(awaitingInput.remove(c), connectionPlayer.get(c), Float.parseFloat(message.substring(message.indexOf(':') + 1)));
				awaitingInput = null;
			}else if(message.contains("mainPlayer:")) {
				Gdx.app.postRunnable(new Runnable() {

					@Override
					public void run() {
						map.setMainPlayer(map.getAllPlayers().get(Integer.parseInt(message.substring(message.indexOf(':') + 1))));
						
					}
					
				});
			}else if(message.contains("teammates:")) {
				Gdx.app.postRunnable(new Runnable() {

					@Override
					public void run() {
						map.spawnPlayers(Integer.parseInt(message.substring(message.indexOf(':') + 1)), 0);
						
					}
					
				});
			}else if(message.contains("opponents:")) {
				Gdx.app.postRunnable(new Runnable() {

					@Override
					public void run() {
						map.spawnPlayers(0, Integer.parseInt(message.substring(message.indexOf(':') + 1)));
						
						ArrayList<Player> tempPlayers = map.getAllPlayers();
						for(int i = 0; i < server.getConnections().length; i++) {
							Connection c = server.getConnections()[i];
							connectionPlayer.put(c, tempPlayers.get(i));
							
							tempPlayers.get(i).setABot(false);
						}
					}
					
				});
			}else if(message.contains("index:")) {
				awaitingIndexes.enqueue(Integer.parseInt(message.substring(message.indexOf(':') + 1)));
			}
		}else if(o instanceof InputController) {
			awaitingInput.put(c, (InputController) o);
		}else if(o instanceof Matrix4) {
			if(awaitingBall) {
				map.getBall().setWorldTransform((Matrix4) o);
				awaitingBall = false;
				return;
			}
			
			Runnable tempRunnable = new Runnable() {

				@Override
				public void run() {
					try {
						map.getAllPlayers().get(awaitingIndexes.dequeue()).setWorldTransform((Matrix4) o);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				}
				
			};
			//if(map.getAllPlayers().size() == 0) {
				Gdx.app.postRunnable(tempRunnable);
			//}else tempRunnable.run();
		}else if(o instanceof Array) {
			Runnable tempRunnable = new Runnable() {

				@Override
				public void run() {
					try {
						map.getAllPlayers().get(awaitingIndexes.dequeue()).setNodesTransforms((Array<Matrix4>) o);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
				}
				
			};
			
			//if(map.getAllPlayers().size() == 0) {
				Gdx.app.postRunnable(tempRunnable);
			//}else tempRunnable.run();
		}
	}
	
	@Override
	public void disconnected(final Connection c) {
		Gdx.app.postRunnable(new Runnable() {

			@Override
			public void run() {
				
				map.removePlayer(connectionPlayer.remove(c));
			}
			
		});
		
	}
	
}

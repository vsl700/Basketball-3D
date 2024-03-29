package com.vasciie.bkbl.gamespace.multiplayer;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.vasciie.bkbl.gamespace.GameMap;
import com.vasciie.bkbl.gamespace.entities.Player;
import com.vasciie.bkbl.gamespace.tools.InputController;

public class Multiplayer extends Listener {

	private static final PacketMessage message = new PacketMessage();
	private static final GameMessage gameMessage = new GameMessage();
	
	private static final int udpPort = 27960, tcpPort = 27960;
	Server server;
	Client client;
	
	GameMap map;
	
	//HashMap<InputController, Connection> inputConnection;
	HashMap<Connection, Player> connectionPlayer;
	
	int team; //0 - unassigned, 1 - blue, 2 - red
	
	boolean host, join;
	boolean gameReady;
	
	
	public Multiplayer(GameMap map) {
		this.map = map;
		
		server = new Server(); //Create the server
		server.getKryo().register(PacketMessage.class); //Register a packet class. We can only send objects as packets if they are registered!
		server.getKryo().register(float.class);
		server.getKryo().register(float[].class);
		server.getKryo().register(String[].class);
		server.getKryo().register(Object[].class);
		server.getKryo().register(Matrix4.class);
		server.getKryo().register(Array.class);//For the transforms
		server.getKryo().register(InputController.class);
		server.getKryo().register(GameMessage.class);
		server.getKryo().register(Color.class);
		
		client = new Client(); //Create the client
		client.getKryo().register(PacketMessage.class);
		client.getKryo().register(float.class);
		client.getKryo().register(float[].class);
		client.getKryo().register(String[].class);
		client.getKryo().register(Object[].class);
		client.getKryo().register(Matrix4.class);
		client.getKryo().register(Array.class);//For the transforms
		client.getKryo().register(InputController.class);
		client.getKryo().register(GameMessage.class);
		client.getKryo().register(Color.class);
		
		//inputConnection = new HashMap<InputController, Connection>();
		connectionPlayer = new HashMap<Connection, Player>();
	}
	
	public void create() throws Exception {
		team = 1;
		
		server.bind(tcpPort, udpPort); //Bind to a port
		
		server.start(); //Start the server
		
		server.addListener(this); //Add the listener
		
		host = true;
	}
	
	public void join(String ip) throws Exception {
		client.setName("tempClient");
		
		new Thread(client).start(); //Start the client! The client MUST be started before connecting can take place
		//System.out.println(client.discoverHost(tcpPort, udpPort).getHostName());
		/*for(InetAddress a : client.discoverHosts(tcpPort, udpPort)) {
			System.out.println(a.getHostAddress());
		}*/
		
		client.connect(5000, ip, tcpPort, udpPort); //Connect to the server - wait 5000 ms before failing
		
		client.addListener(this); //Add a listener
		
		//client.setKeepAliveTCP(1);
		
		join = true;
	}
	
	public void begin() {
		//When the host starts the game (sending chosen rules & challenges & available players, creating hashmaps)
		
		assignPlayer(team);
		
		for(int i = 0; i < server.getConnections().length; i++)
			assignPlayer(1);
		
		message.message = "teammates:" + map.getTeammates().size();
		sendToAllTCP(message, false);
		
		message.message = "opponents:" + map.getOpponents().size();
		sendToAllTCP(message, false);
		
		ArrayList<Player> tempPlayers = map.getAllPlayers();
		for(int i = 0; i < server.getConnections().length; i++) {
			Connection c = server.getConnections()[i];
			connectionPlayer.put(c, tempPlayers.get(i + 1));
			
			tempPlayers.get(i + 1).setABot(false);
			
			message.message = "mainPlayer:" + (i + 1);
			c.sendTCP(message);
		}
		
		
		message.message = "ready";
		sendToAllTCP(message, false);
		
	}
	
	public void updateClient(boolean controlPlayer) {
		/*if(isServer()) {
			sendTransforms();
		}else */
		/*if(!isServer() && !client.isConnected()) {
			map.getMessageListener().sendMessage("", "", null, null, false, new String[] {"main"});
			return;
		}*/
		
		if (controlPlayer && gameReady && client.getTcpWriteBufferSize() < 8000){
			
			if(Gdx.app.getType().equals(Application.ApplicationType.Android) && !map.isGameRunning())
				map.getInputs().updateRotation();
			
			message.message = "delta:" + Gdx.graphics.getDeltaTime();
			message.object = map.getInputs();
			client.sendTCP(message);
			
			map.updateInputs();
		}
		
		message.message = "shareData";
		message.object = null;
		client.sendTCP(message);
	}
	
	public void processInputs() {
		processingInputs = true;
		
		while(receivingInputs) {System.out.print("");};
		
		for(int i = awaitingInputs.size() - 1; i >= 0; i--) {
			map.controlPlayer(awaitingInputs.get(i), awaitingPlayers.get(i), awaitingInputDeltas.get(i));
			
			/*awaitingInputs.remove(i);
			awaitingPlayers.remove(i);
			awaitingInputDeltas.remove(i);*/
		}
		
		processingInputs = false;
	}
	
	public void sendMessage() {
		gameMessage.heading = map.getMessageListener().getMessageHeading();
		gameMessage.desc = map.getMessageListener().getMessageDesc();
		gameMessage.color = map.getMessageListener().getMessageColor();
		gameMessage.skippable = map.getMessageListener().isMessageSkippable();
		gameMessage.args = map.getMessageListener().getMessageArgs();
		sendToAllTCP(gameMessage, false);
		
		gameMessage.reset();
	}
	
	public void receivedMessage() {
		message.message = "received";
		message.object = null;
		
		if(isServer())
			sendToAllTCP(message, false);
		else client.sendTCP(message);
	}
	
	private void sendGameData(Connection c) {
		message.message = "timer:" + map.getTimer();
		message.object = null;
		c.sendTCP(message);
		
		sendTransforms(c);
	}
	
	private void sendTransforms(Connection c) {
		if(c.getTcpWriteBufferSize() >= 8000)
			return;
		
		ArrayList<Player> tempPlayers = map.getAllPlayers();
		for(int i = 0; i < tempPlayers.size(); i++) {
			message.message = "index:" + i;
			message.object = tempPlayers.get(i).getModelInstance().transform;
			c.sendTCP(message);
			
			message.object = tempPlayers.get(i).getNodesTransforms();
			c.sendTCP(message);
			
			message.message = "ball";
			message.object = map.getBall().getModelInstance().transform;
			c.sendTCP(message);
		}
	}
	
	private void sendToAllTCP(Object o, boolean waitTCP) {
		for(Connection c : server.getConnections()) {
			if(waitTCP && c.getTcpWriteBufferSize() < 8000 || !waitTCP) {
				c.sendTCP(o);
			}
		}
	}
	
	public void disconnect() {
		client.stop();
		
		join = false;
		gameReady = false;
		
		message.message = null;
		message.object = null;
		
		//client = null;
	}
	
	public void onNewGame() {
		if(!host && !join)
			return;
		
		message.message = "newGame";
		message.object = null;
		sendToAllTCP(message, false);
		
		//begin();
	}
	
	public void onGameOver() {
		message.message = "gameOver";
		message.object = null;
		sendToAllTCP(message, false);
	}
	
	public void quit() {
		gameMessage.args = new String[] {"main"};
		sendToAllTCP(gameMessage, false);
		
		server.stop();
		
		connectionPlayer.clear();
		//inputConnection.clear();
		host = false;
		
		message.message = null;
		message.object = null;
		
		awaitingInputs.clear();
		awaitingInputDeltas.clear();
		awaitingPlayers.clear();
		
		processingInputs = receivingInputs = false;
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
	
	//private Queue<Integer> awaitingIndexes = new Queue<Integer>();
	//private final HashMap<Connection, InputController> awaitingInput = new HashMap<Connection, InputController>();
	private final ArrayList<InputController> awaitingInputs = new ArrayList<InputController>();
	private final ArrayList<Float> awaitingInputDeltas = new ArrayList<Float>();
	private final ArrayList<Player> awaitingPlayers = new ArrayList<Player>();
	private boolean processingInputs, receivingInputs;
	@SuppressWarnings("unchecked")
	@Override
	public void received(final Connection c, final Object o) {
		if(o instanceof PacketMessage) {
			final PacketMessage packet = (PacketMessage) o;
			final String message = packet.message;
			/*if(message.equals("shareInputs")) {
				c.sendTCP(map.getInputs());
			}else */if(message.equals("ready")) {
				gameReady = true;
			}else if(message.equals("shareData")) {
				sendGameData(c);
			}else if(message.contains("timer:")) {
				map.setTimer(Float.parseFloat(message.substring(message.indexOf(':') + 1)));
			}else if(message.equals("newGame")) {
				Gdx.app.postRunnable(new Runnable() {

					@Override
					public void run() {
						map.clear();
						
					}
					
				});
			}else if(message.equals("gameOver")) {
				map.getMessageListener().sendMessage("", "", new Color(), null, true, new String[] {"gameOver"});
			}else if(message.equals("received")) {
				map.getMessageListener().sendMessage("", "", new Color(), null, true);
				
				if(isServer())
					sendToAllTCP(packet, false);
			}else if(message.contains("delta:")) {
				while(processingInputs) {System.out.print("");}
				
				receivingInputs = true;
				
				int tempIndex = awaitingPlayers.indexOf(connectionPlayer.get(c));
				if(tempIndex != -1) {
					awaitingInputs.remove(tempIndex);
					awaitingPlayers.remove(tempIndex);
					awaitingInputDeltas.remove(tempIndex);
				}
				
				awaitingInputs.add((InputController) packet.object);
				awaitingInputDeltas.add(Float.parseFloat(message.substring(message.indexOf(':') + 1)));
				awaitingPlayers.add(connectionPlayer.get(c));
				
				receivingInputs = false;
			}else if(message.contains("mainPlayer:")) {
				Gdx.app.postRunnable(new Runnable() {

					@Override
					public void run() {
						//System.out.println(Integer.parseInt(message.substring(message.indexOf(':') + 1)));
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
				int index = Integer.parseInt(message.substring(message.indexOf(':') + 1));
				
				if(packet.object instanceof Matrix4) {
					map.getAllPlayers().get(index).setWorldTransform((Matrix4) packet.object);
				}else if(packet.object instanceof Array) {
					map.getAllPlayers().get(index).setNodesTransforms((Array<Matrix4>) packet.object);
				}
			}else if(message.equals("ball")) {
				if(packet.object instanceof Matrix4) {
					map.getBall().setWorldTransform((Matrix4) packet.object);
				}
			}
		}else if(o instanceof GameMessage) {
			GameMessage message = (GameMessage) o;
			map.getMessageListener().sendMessage(message.heading, message.desc, message.color, map, message.skippable, message.args);
		}/*else if(o instanceof Matrix4) {
			Runnable tempRunnable = new Runnable() {

				@Override
				public void run() {
					final int index;
					try {
						index = awaitingIndexes.dequeue();
					} catch (InterruptedException e1) {
						e1.printStackTrace();
						return;
					}
					if(index == -1) {
						map.getBall().setWorldTransform((Matrix4) o);
						return;
					}
					
					map.getAllPlayers().get(index).setWorldTransform((Matrix4) o);
					
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
		}*/
	}
	
	@Override
	public void disconnected(final Connection c) {
		Gdx.app.postRunnable(new Runnable() {

			@Override
			public void run() {
				
				map.removePlayer(connectionPlayer.remove(c));
				
				//TODO If the host disconnects, then there should be a gameover screen (or maybe setting someone else as a host)!
			}
			
		});
		
	}
	
}

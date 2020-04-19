package com.vasciie.bkbl.gamespace.entities;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.vasciie.bkbl.gamespace.GameMap;
import com.vasciie.bkbl.gamespace.entities.players.Opponent;
import com.vasciie.bkbl.gamespace.entities.players.Teammate;

public enum EntityType {
	
	BALL(Ball.class, "ball", 1),
	TEAMMATE(Teammate.class, "team", 30),
	OPPONENT(Opponent.class, "opp", 30);
	
	@SuppressWarnings("rawtypes")
	private Class loaderClass;
	private String id;
	private float mass;
	
	private EntityType(@SuppressWarnings("rawtypes") Class loaderClass, String id, float mass) {
		this.loaderClass = loaderClass;
		this.id = id;
		this.mass = mass;
	}

	@SuppressWarnings("rawtypes")
	public Class getLoaderClass() {
		return loaderClass;
	}

	public String getId() {
		return id;
	}
	
	public float getMass() {
		return mass;
	}
	
	public static Player createPlayer(String id, GameMap map, Vector3 pos){
		EntityType type = entityTypes.get(id); 
		try {
			@SuppressWarnings("unchecked")
			Player player = (Player) ClassReflection.newInstance(type.loaderClass);
			player.create(type, map, pos);
			return player;
		} catch (ReflectionException e) {
			Gdx.app.error("Entity Loader", "Could not load entity of type " + type.id);
			return null;
		}
	}
	
	public static Entity createEntity(String id, GameMap map, Vector3 pos){
		EntityType type = entityTypes.get(id);
		try {
			@SuppressWarnings("unchecked")
			Entity entity = (Entity) ClassReflection.newInstance(type.loaderClass);
			entity.create(type, map, pos);
			return entity;
		} catch (ReflectionException e) {
			Gdx.app.error("Entity Loader", "Could not load entity of type " + type.id);
			return null;
		}
	}

	private static HashMap<String, EntityType> entityTypes;

	static{
		entityTypes = new HashMap<String, EntityType>();
		for(EntityType type : EntityType.values())
			entityTypes.put(type.id, type);

	}
	
}

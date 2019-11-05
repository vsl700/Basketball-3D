package com.gamesbg.bkbl.gamespace.objects;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.gamesbg.bkbl.gamespace.GameMap;
import com.gamesbg.bkbl.gamespace.objects.baskets.*;

public enum ObjectType {

	TERRAIN(Terrain.class, "terrain"),
	HOMEBASKET(Home.class, "home"),
	AWAYBASKET(Away.class, "away");
	
	@SuppressWarnings("rawtypes")
	private Class loaderClass;
	private String id;
	
	private ObjectType(@SuppressWarnings("rawtypes") Class loaderClass, String id) {
		this.loaderClass = loaderClass;
		this.id = id;
	}
	
	@SuppressWarnings("rawtypes")
	public Class getLoaderClass() {
		return loaderClass;
	}

	public String getId() {
		return id;
	}
	
	public static GameObject createGameObject(String id, GameMap map, float x, float y, float z){
		ObjectType type = objectTypes.get(id); 
		try {
			@SuppressWarnings("unchecked")
			GameObject entity = ClassReflection.newInstance(type.loaderClass);
			entity.create(type, map, x, y, z);
			return entity;
		} catch (ReflectionException e) {
			// TODO Auto-generated catch block
			Gdx.app.error("Entity Loader", "Could not load entity of type " + type.id);
			return null;
		}
	}

	private static HashMap<String, ObjectType> objectTypes;

	static{
		objectTypes = new HashMap<String, ObjectType>();
		for(ObjectType type : ObjectType.values())
			objectTypes.put(type.id, type);

	}
	
}

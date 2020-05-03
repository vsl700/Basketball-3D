package com.vasciie.bkbl.gamespace.objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.vasciie.bkbl.gamespace.GameMap;
import com.vasciie.bkbl.gamespace.objects.baskets.Away;
import com.vasciie.bkbl.gamespace.objects.baskets.Home;

import java.util.HashMap;

public enum ObjectType {

	CAMERA(Camera.class, "cam"),
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
		GameObject entity = null;
		try {
			entity = (GameObject) ClassReflection.newInstance(type.loaderClass);
		}catch(ReflectionException e){
			Gdx.app.error("Entity Loader", "Could not load entity of type " + type.id);
			return null;
		}


		entity.create(type, map, x, y, z);
		return entity;
	}

	public static Basket createBasket(String id, GameMap map, float x, float y, float z){
		ObjectType type = objectTypes.get(id);

		Basket basket = null;
		try {
			basket = (Basket) ClassReflection.newInstance(type.loaderClass);
		}catch(ReflectionException e){
			Gdx.app.error("Entity Loader", "Could not load entity of type " + type.id);
			return null;
		}


		basket.create(type, map, x, y, z);
		return basket;
	}

	private static HashMap<String, ObjectType> objectTypes;

	static{
		objectTypes = new HashMap<String, ObjectType>();
		for(ObjectType type : ObjectType.values())
			objectTypes.put(type.id, type);

	}
	
}

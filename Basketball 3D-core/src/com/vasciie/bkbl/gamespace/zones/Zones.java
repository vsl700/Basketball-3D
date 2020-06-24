package com.vasciie.bkbl.gamespace.zones;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.vasciie.bkbl.gamespace.GameMap;
import com.vasciie.bkbl.gamespace.tools.GameTools;

/**
 * Contains all the zones in the game (midcourt, basket zone, etc.)
 * 
 * @author studi
 *
 */
public class Zones {

	Zone[] zones;
	
	boolean firstRendered;
	
	
	public Zones() {
		zones = new Zone[] {
				
		};
	}
	
	public boolean isInZone(String id, Vector3 pos) {
		for(Zone zone : zones) {
			if(zone.getId().equals(id))
				return zone.checkZone(GameTools.toVector2(pos));
		}
		
		return false;
	}
	
	public void render(ModelBatch mBatch, Environment e) {
		if(!firstRendered) {
			for(Zone zone : zones) {
				zone.createModels();
				zone.render(mBatch, e);
			}
			
			firstRendered = true;
		}else {
			for(Zone zone : zones)
				zone.render(mBatch, e);
		}
		
	}
	
	public void dispose() {
		for(Zone zone : zones)
			zone.dispose();
	}
	
	
	public static abstract class Zone {
		
		String id;
		Model model;
		ModelInstance modelInstance;
		
		
		public Zone(String id) {
			this.id = id;
		}
		
		public abstract void createModels();
		
		public abstract void createTexture();
		
		public abstract boolean checkZone(Vector2 pos);
		
		public void render(ModelBatch mBatch, Environment e) {
			mBatch.render(modelInstance, e);
		}
		
		public abstract boolean isZoneActive(GameMap map);
		
		public void dispose() {
			model.dispose();
		}
		
		public String getId() {
			return id;
		}
	}
}

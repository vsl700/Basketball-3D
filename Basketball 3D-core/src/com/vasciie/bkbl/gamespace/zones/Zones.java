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

	GameMap map;
	
	Zone[] zones;
	
	boolean firstRendered;
	
	
	public Zones(GameMap map) {
		Vector2[] tempTeamFree = new Vector2[] {new Vector2(3.6292338f, 15.944867f), new Vector2(-3.6292338f, 15.944867f), new Vector2(-5.6740437f, 28.619236f), new Vector2(5.6740437f, 28.619236f)};
		Vector2[] tempOppFree = new Vector2[tempTeamFree.length];
		
		for(int i = 0; i < tempOppFree.length; i++) {
			tempOppFree[i] = tempTeamFree[i].cpy().scl(1, -1);
		}
		
		Vector2[] tempTeamThree = new Vector2[] {new Vector2(1.583448f, 11.885692f), new Vector2(-1.583448f, 11.885692f), new Vector2(-4.1226497f, 12.503972f), new Vector2(-5.787121f, 13.309863f),
				new Vector2(-6.946278f, 14.224272f), new Vector2(-8.111437f, 15.451321f), new Vector2(-9.015614f, 16.829515f), new Vector2(-9.639332f, 18.239443f), new Vector2(-10.24686f, 21.927698f),
				/*new Vector2(-6.3040233f, 13.501618f), new Vector2(-6.5534277f, 13.672493f), new Vector2(-6.87086f, 13.9120035f), new Vector2(-7.0578227f, 14.158667f), 
				new Vector2(-7.218311f, 14.416079f), new Vector2(-7.4736457f, 14.621986f), new Vector2(-7.719531f, 14.799216f), new Vector2(-7.868765f, 14.963927f), new Vector2(-8.043922f, 15.1564455f),
				new Vector2(-8.208387f, 15.429901f), new Vector2(-8.375041f, 15.660322f), new Vector2(-8.538396f, 15.867058f), new Vector2(-8.712505f, 16.1703f), new Vector2(-9.536805f, 17.554167f),
				new Vector2(-10.208837f, 19.707685f),*/ new Vector2(-10.299844f, 28.70149f)};
		
		/*for(int i = 0; i < tempTeamThree.length - 1; i++) { //We apply a little bit correction
			if(i < 2) {
				tempTeamThree[i].sub(0, 1.5f);
				continue;
			}
			
			if(i > tempTeamThree.length / 2 - 1) {
				if(i < tempTeamThree.length - 2)
					tempTeamThree[i].sub(1, 0);
				else if(i == tempTeamThree.length - 2) 
					tempTeamThree[i].sub(2f, 0);
				else if(i >= tempTeamThree.length - 4)
					tempTeamThree[i].add(2.5f, 0);
			}else if(i > 1) {
				tempTeamThree[i].sub(0, 0.2f);
			}
			
			tempTeamThree[i].sub(1.1f, 1.8f);
		}*/
		
		Vector2[] realTeamThree = new Vector2[tempTeamThree.length * 2 - 2]; //We miss the first item
		
		int index;
		for(int i = 0; (index = i) < tempTeamThree.length; i++) {
			realTeamThree[i] = tempTeamThree[i];
		}
		
		int tempIndex = index;
		for(int i = index; (index = i) < realTeamThree.length; i++) {
			realTeamThree[i] = tempTeamThree[tempTeamThree.length - (i - tempIndex) - 1].cpy().scl(-1, 1);
		}
		
		Vector2[] realOppThree = new Vector2[realTeamThree.length];
		
		for(int i = 0; i < realOppThree.length; i++) {
			realOppThree[i] = realTeamThree[i].cpy().scl(1, -1);
		}
		
		Vector2[] blueZone = new Vector2[] {new Vector2(-13.763321f, 0.75f), new Vector2(-13.763321f, 28.86293f), new Vector2(13.763321f, 28.86293f), new Vector2(13.763321f, 0.75f)};
		
		Vector2[] redZone = new Vector2[blueZone.length];
		for(int i = 0; i < redZone.length; i++) {
			redZone[i] = blueZone[i].cpy().scl(1, -1);
		}
		
		zones = new Zone[] {
				new CircleZone("center", map, new Vector2(), 4.31f) {

					@Override
					public void createModels() {
						
						
					}

					@Override
					public void createTexture() {
						
						
					}

					@Override
					public boolean isZoneActive() {
						
						return true;
					}
					
				},
				
				new PolygonZone("free-throw-team", map, tempTeamFree) {

					@Override
					public void createModels() {
						
					}

					@Override
					public void createTexture() {
						
					}

					@Override
					public boolean isZoneActive() {
						
						return true;
					}
					
				}, 
				
				new PolygonZone("free-throw-opp", map, tempOppFree) {

					@Override
					public void createModels() {
						
					}

					@Override
					public void createTexture() {
						
					}

					@Override
					public boolean isZoneActive() {
						
						return true;
					}
					
				},
				
				new PolygonZone("three-point-team", map, realTeamThree) {

					@Override
					public void createModels() {
						
						
					}

					@Override
					public void createTexture() {
						
						
					}

					@Override
					public boolean isZoneActive() {
						
						return false;
					}
					
				},
				
				new PolygonZone("three-point-opp", map, realOppThree) {

					@Override
					public void createModels() {
						
						
					}

					@Override
					public void createTexture() {
						
						
					}

					@Override
					public boolean isZoneActive() {
						
						return false;
					}
					
				},
				
				new PolygonZone("blue-zone", map, blueZone) {

					@Override
					public void createModels() {
						
						
					}

					@Override
					public void createTexture() {
						
						
					}

					@Override
					public boolean isZoneActive() {
						
						return false;
					}
					
				},
				
				new PolygonZone("red-zone", map, redZone) {

					@Override
					public void createModels() {
						
						
					}

					@Override
					public void createTexture() {
						
						
					}

					@Override
					public boolean isZoneActive() {
						
						return false;
					}
					
				}
		};
		
		this.map = map;
	}
	
	public Zone getZone(String id) {
		for(Zone zone : zones)
			if(id.equals(zone.getId()))
				return zone;
		
		return null;
	}
	
	private static final Vector2 tempVec = new Vector2(), tempVec2 = new Vector2();
	public boolean isInZone(String id, Vector3 pos) {
		for(Zone zone : zones) {
			if(zone.getId().equals(id))
				return zone.checkZone(GameTools.toVector2(pos, tempVec));
		}
		
		return false;
	}
	
	public boolean isInZone(String id, Vector3 pos, Vector3 dimensions) {
		for(Zone zone : zones) {
			if(zone.getId().equals(id))
				return zone.checkZone(GameTools.toVector2(pos, tempVec), GameTools.toVector2(pos, tempVec2));
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
		
		GameMap map;
		
		String id;
		Model model;
		ModelInstance modelInstance;
		
		
		public Zone(String id, GameMap map) {
			this.map = map;
			this.id = id;
		}
		
		public abstract void createModels();
		
		public abstract void createTexture();
		
		protected abstract boolean checkZone(Vector2 pos);
		
		protected abstract boolean checkZone(Vector2 pos, Vector2 dimensions);
		
		private static final Vector2 tempVec1 = new Vector2(), tempVec2 = new Vector2();
		public boolean checkZone(Vector3 pos) {
			return checkZone(GameTools.toVector2(pos, tempVec1));
		}
		
		public boolean checkZone(Vector3 pos, Vector3 dimensions) {
			Vector2 tempDimensions = GameTools.toVector2(dimensions, tempVec2);
			
			tempDimensions.scl(0.5f);
			
			return checkZone(GameTools.toVector2(pos, tempVec1), tempDimensions);
		}
		
		public void render(ModelBatch mBatch, Environment e) {
			if(isZoneActive())
				mBatch.render(modelInstance, e);
		}
		
		public abstract boolean isZoneActive();
		
		public void dispose() {
			model.dispose();
		}
		
		public String getId() {
			return id;
		}
		
		public abstract Vector2[] getPositions();
	}
}

package com.vasciie.bkbl.gamespace.zones;

import com.badlogic.gdx.math.Vector2;
import com.vasciie.bkbl.gamespace.GameMap;
import com.vasciie.bkbl.gamespace.zones.Zones.Zone;

public abstract class CircleZone extends Zone {

	Vector2 pos;
	float radius;
	
	public CircleZone(String id, GameMap map, Vector2 pos, float radius) {
		super(id, map);
		
		this.pos = pos;
		this.radius = radius;
	}

	@Override
	public boolean checkZone(Vector2 pos) {
		
		return pos.dst(this.pos) <= radius;
	}
	
	@Override
	public Vector2[] getPositions() {
		return new Vector2[] {pos};
	}

}

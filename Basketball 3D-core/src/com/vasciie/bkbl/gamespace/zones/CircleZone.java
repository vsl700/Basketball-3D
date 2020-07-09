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
	public boolean checkZone(Vector2 pos, Vector2 dimensions) {
		Vector2 comb1 = pos.cpy().add(dimensions.x, 0);
		Vector2 comb2 = pos.cpy().sub(dimensions.x, 0);
		Vector2 comb3 = pos.cpy().add(0, dimensions.y);
		Vector2 comb4 = pos.cpy().sub(0, dimensions.y);
		
		return comb1.dst(this.pos) <= radius || comb2.dst(this.pos) <= radius || comb3.dst(this.pos) <= radius || comb4.dst(this.pos) <= radius;
	}
	
	@Override
	public Vector2[] getPositions() {
		return new Vector2[] {pos};
	}

}

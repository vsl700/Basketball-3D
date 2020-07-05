package com.vasciie.bkbl.gamespace.zones;

import com.badlogic.gdx.math.Vector2;
import com.vasciie.bkbl.gamespace.GameMap;
import com.vasciie.bkbl.gamespace.zones.Zones.Zone;

public abstract class PolygonZone extends Zone {

	Vector2[] positions;
	
	public PolygonZone(String id, GameMap map, Vector2[] positions) {
		super(id, map);
		this.positions = positions;
	}
	
	@Override
	public boolean checkZone(Vector2 pos) {
		for(int i = 0; i < positions.length; i++) {
			int indexStart, indexEnd;
			if(i == 0) {
				indexStart = positions.length - 1;
				indexEnd = i + 1;
			}else if(i == positions.length - 1) {
				indexStart = i - 1;
				indexEnd = 0;
			}else {
				indexStart = i - 1;
				indexEnd = i + 1;
			}
			
			if(!checkPoint(pos, positions[i], positions[indexStart], positions[indexEnd]))
				return false;
		}
		
		return true;

	}
	
	private boolean checkPoint(Vector2 entityPos, Vector2 mainPos, Vector2 posCheck1, Vector2 posCheck2) {
		Vector2 mainToCheck1 = posCheck1.cpy().sub(mainPos).nor();
		Vector2 mainToCheck2 = posCheck2.cpy().sub(mainPos).nor();
		Vector2 mainToEntity = entityPos.cpy().sub(mainPos).nor();
		
		float tempDst = mainToCheck1.dst2(mainToCheck2);
		
		return mainToCheck1.dst2(mainToEntity) <= tempDst && mainToCheck2.dst2(mainToEntity) <= tempDst;
	}
	
	@Override
	public Vector2[] getPositions() {
		return positions;
	}

}

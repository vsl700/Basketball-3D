package com.vasciie.bkbl.gamespace.objects;

import com.badlogic.gdx.math.Vector3;

public class Camera extends GameObject {

	private final Vector3 position = new Vector3();
	private final Vector3 direction = new Vector3();
	
	@Override
	protected void createModels() {
		

	}

	@Override
	protected void createCollisionShapes() {
		
	}

	@Override
	protected void manuallyRecalcCollisions() {
		

	}

	@Override
	protected void manuallySetObjects() {
		

	}

	@Override
	protected void manuallySetBodies() {
		

	}
	
	public void setPosition(Vector3 position) {
		this.position.set(position);
	}
	
	public void setDirection(Vector3 direction) {
		this.direction.set(direction);
	}
	
	public Vector3 getDirection() {
		return direction;
	}

	@Override
	public Vector3 getPosition() {
		return position;
	}
	
	public void updateCamera() {
		Vector3 tempPosition = position.cpy();
		float scl = 3.5f, scl2 = 4.25f;
		if(position.x < 0)
			position.x -= getWidth() * scl;
		else position.x += getWidth() * scl;
		
		//if(position.y < 0)
			position.y -= getDepth() * scl2;
		//else position.y += getDepth() * scl;
		
		if(position.z < 0)
			position.z -= getDepth() * scl;
		else position.z += getDepth() * scl;
		
		boolean[] crosses = new boolean[] {getPosition().x > map.getTerrain().getWidth() / 2, getPosition().x < -map.getTerrain().getWidth() / 2, getPosition().z > map.getTerrain().getDepth() / 2, getPosition().z < -map.getTerrain().getDepth() / 2, getPosition().y < 0};
		
		for (int i = 0; i < crosses.length; i++) {
			if (crosses[i]) {
				Vector3 offset;
				if (i == 0)
					offset = new Vector3(1, 0, 0);
				else if (i == 1)
					offset = new Vector3(-1, 0, 0);
				else if (i == 2)
					offset = new Vector3(0, 0, 1);
				else if(i == 3)
					offset = new Vector3(0, 0, -1);
				else
					offset = new Vector3(0, -1, 0);

				Vector3 positiveOff = new Vector3(Math.abs(offset.x), Math.abs(offset.y), Math.abs(offset.z));

				Vector3 tempPos = position.cpy().scl(positiveOff);
				Vector3 tempVec = new Vector3(map.getTerrain().getWidth() / 2, 0, map.getTerrain().getDepth() / 2).scl(offset);

				Vector3 dir = direction.cpy().scl(tempPos.dst(tempVec));

				tempPosition.add(dir);
			}

			
		}
		position.set(tempPosition);
	}

	@Override
	public float getWidth() {
		
		return 0.5f;
	}

	@Override
	public float getHeight() {
		
		return 0.5f;
	}

	@Override
	public float getDepth() {
		
		return 0.5f;
	}

}

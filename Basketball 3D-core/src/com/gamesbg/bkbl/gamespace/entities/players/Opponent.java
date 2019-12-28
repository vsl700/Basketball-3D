package com.gamesbg.bkbl.gamespace.entities.players;

import com.badlogic.gdx.graphics.Color;
import com.gamesbg.bkbl.gamespace.entities.Player;
import com.gamesbg.bkbl.gamespace.objects.GameObject;

public class Opponent extends Player {

	@Override
	protected Color getPlayerColor() {
		// TODO Auto-generated method stub
		return Color.RED;
	}

	@Override
	public GameObject getTargetBasket() {
		// TODO Auto-generated method stub
		return map.getHomeBasket();
	}
	
	

}

package com.gamesbg.bkbl.gamespace.entities.players;

import com.badlogic.gdx.graphics.Color;
import com.gamesbg.bkbl.gamespace.entities.Player;
import com.gamesbg.bkbl.gamespace.objects.GameObject;

public class Teammate extends Player {

	@Override
	protected Color getPlayerColor() {
		// TODO Auto-generated method stub
		return Color.BLUE;
	}

	@Override
	public GameObject getTargetBasket() {
		// TODO Auto-generated method stub
		return map.getAwayBasket();
	}

}

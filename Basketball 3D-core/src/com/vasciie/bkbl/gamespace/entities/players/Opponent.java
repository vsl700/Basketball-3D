package com.vasciie.bkbl.gamespace.entities.players;

import com.badlogic.gdx.graphics.Color;
import com.vasciie.bkbl.gamespace.entities.Player;
import com.vasciie.bkbl.gamespace.objects.Basket;
import com.vasciie.bkbl.gamespace.zones.Zones.Zone;

public class Opponent extends Player {

	@Override
	protected Color getPlayerColor() {
		return Color.RED;
	}

	@Override
	public Basket getTargetBasket() {
		return map.getHomeBasket();
	}

	@Override
	public boolean isInAwayZone() {
		
		return getPosition().z > 0;
	}

	@Override
	public Zone getHomeBasketZone() {
		
		return map.getZones().getZone("free-throw-opp");
	}

	@Override
	public Zone getHomeThreePointZone() {
		
		return map.getZones().getZone("three-point-opp");
	}

	@Override
	public Zone getAwayBasketZone() {
		
		return map.getZones().getZone("free-throw-team");
	}

	@Override
	public Zone getAwayThreePointZone() {
		
		return map.getZones().getZone("three-point-team");
	}
	

}

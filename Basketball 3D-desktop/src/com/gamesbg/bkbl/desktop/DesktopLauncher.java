package com.gamesbg.bkbl.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.gamesbg.bkbl.MyGdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new MyGdxGame(), config);
		
		//config.useGL30 = false;
		config.width = MyGdxGame.WIDTH;
		config.height = MyGdxGame.HEIGHT;
		config.title = "Basketball 3D";
		config.vSyncEnabled = false;
		config.samples = 4;
		config.addIcon("application/icons/bkbl.png", FileType.Internal);
		//LwjglApplicationConfiguration.disableAudio = true;
		//config.resizable = false;
		config.foregroundFPS = 120;
		config.backgroundFPS = 20;
	}
}

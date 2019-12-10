package com.gamesbg.bkbl.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.gamesbg.bkbl.MyGdxGame;

/**
 * Just want to note that <b>studi</b> and <b>User</b> are names of two different PC's, but only one person uses them. This project is
 * using the source-control system <b>GitHub</b> ({@link www.github.com})
 * 
 * @author studi
 *
 */
public class DesktopLauncher {
	public static void main (String[] arg) {
		final LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		new LwjglApplication(new MyGdxGame() {
			
			@Override
			public void setForegroundFps(int fps) {
				config.foregroundFPS = fps;
			}
			
			@Override
			public int getForegroundFps() {
				return config.foregroundFPS;
			}
			
		}, config);
		
		//config.useGL30 = false;
		config.width = MyGdxGame.WIDTH;
		config.height = MyGdxGame.HEIGHT;
		config.title = "Basketball-3D";
		config.vSyncEnabled = false;
		//config.samples = 4;
		config.addIcon("application/icons/bkbl.png", FileType.Internal);
		//LwjglApplicationConfiguration.disableAudio = true;
		//config.resizable = false;
		config.foregroundFPS = 120;
		config.backgroundFPS = 20;
	}
}

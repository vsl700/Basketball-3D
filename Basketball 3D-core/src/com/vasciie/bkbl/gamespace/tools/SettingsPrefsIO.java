package com.vasciie.bkbl.gamespace.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public final class SettingsPrefsIO {

	static final Preferences prefs = Gdx.app.getPreferences("bkbl");
	
	
	public static void writeSettingBool(String name, boolean setting) {
		prefs.putBoolean(name, setting);
	}
	
	public static void writeSettingInt(String name, int setting) {
		prefs.putInteger(name, setting);
	}
	
	public static void flush() {
		prefs.flush();
	}
	
	public static boolean readSettingBool(String name) {
		return prefs.getBoolean(name);
	}
	
	public static int readSettingInteger(String name) {
		return prefs.getInteger(name);
	}
	
}

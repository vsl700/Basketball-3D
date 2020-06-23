package com.vasciie.bkbl.gamespace.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class SettingsPrefsIO {

	Preferences prefs;
	
	public SettingsPrefsIO() {
		prefs = Gdx.app.getPreferences("bkbl");
	}
	
	
	public void writeSettingBool(String name, boolean setting) {
		prefs.putBoolean(name, setting);
	}
	
	public void writeSettingInt(String name, int setting) {
		prefs.putInteger(name, setting);
	}
	
	public void flush() {
		prefs.flush();
	}
	
	public boolean readSettingBool(String name) {
		return prefs.getBoolean(name);
	}
	
	public int readSettingInteger(String name) {
		return prefs.getInteger(name);
	}
	
}

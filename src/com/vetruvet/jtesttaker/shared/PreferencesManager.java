package com.vetruvet.jtesttaker.shared;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Properties;

public class PreferencesManager {
	private File prefsFile = null;
	private Properties props = null;
	
	private PreferencesManager() { }
	private static PreferencesManager instance = null;
	public static PreferencesManager getInstance() {
		if (instance == null) instance = new PreferencesManager();
		return instance;
	}
	
	public void setPrefsFile(String path) {
		prefsFile = new File(path);
		props = new Properties();
		try {
			props.load(new FileReader(prefsFile));
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}
	
	public File getPrefsFile() {
		return prefsFile;
	}
	
	public void commit() {
		if (props == null || prefsFile == null) return;
		try {
			props.store(new FileWriter(prefsFile), "JTestTaker Preferences");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String[] getRecentFiles() {
		String fileStr = props.getProperty("recentFiles", null);
		if (fileStr == null) return new String[0];
		return fileStr.split("\\|");
	}
	
	public void setRecentFiles(String[] files) {
		String fileStr = "";
		for (String file : files) fileStr += file + "|";
		props.setProperty("recentFiles", fileStr);
	}
	
	public File getLastDir() {
		return new File(props.getProperty("lastDir", "."));
	}
	
	public void setLastDir(File dir) {
		props.setProperty("lastDir", dir.getAbsolutePath());
	}
}

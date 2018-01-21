package com.team766.lib;

import interfaces.ConfigFileReader;

public class ConfigFile {
	
	public static int[] getLeftMotor(){
		return ConfigFileReader.getInstance().getPorts("leftDrive");
	}
	
	public static int[] getRightMotor(){
		return ConfigFileReader.getInstance().getPorts("rightDrive");
	}
	
	public static int[] getLeftEncoder(){
		return ConfigFileReader.getInstance().getPorts("leftEncoder");
	}
	
	public static int[] getRightEncoder(){
		return ConfigFileReader.getInstance().getPorts("rightEncoder");
	}

}

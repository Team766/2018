package com.team766.robot;

import com.team766.lib.ConfigFile;

public class HardwareProvider {
	
	private static HardwareProvider instance;
	
	public static HardwareProvider getInstance(){
		if(instance == null){
			instance = new HardwareProvider();
		}
		return instance;
	}

}

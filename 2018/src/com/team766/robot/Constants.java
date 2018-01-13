package com.team766.robot;

import lib.ConstantsFileReader;
import lib.RobotValues;

public class Constants extends RobotValues{
	
	public static int getAutonMode(){
		return AutonMode;
	}
	
	public static void setAutonMode(int autonMode){
		AutonMode = autonMode;
	}

	public static final String[] AUTONS = new String[]{"None", "Switch", "Scale", "Exchange", "CrossLine", "DriveByTime"};

}

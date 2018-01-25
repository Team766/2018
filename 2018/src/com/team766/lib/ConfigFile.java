package com.team766.lib;

import interfaces.ConfigFileReader;

public class ConfigFile {
	
	//Drive
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
	
	//gripper
	public static int getGripperA(){
		return ConfigFileReader.getInstance().getPort("gripperA");
	}
	
	public static int getGripperB(){
		return ConfigFileReader.getInstance().getPort("gripperB");
	}
	
	/* does not have a motor for now
	public static int getGripperMotorA(){
		return ConfigFileReader.getInstance().getPort("gripperMotorA");
	}
	
	public static int getGripperMotorB(){
		return ConfigFileReader.getInstance().getPort("gripperMotorB");
	}
	*/
	
	//Joysticks
	public static int getLeftJoy(){
		return ConfigFileReader.getInstance().getPort("leftJoy");
	}
	
	public static int getRightJoy(){
		return ConfigFileReader.getInstance().getPort("rightJoy");
	}
	
	public static int getButtonJoy() {
		return ConfigFileReader.getInstance().getPort("buttonJoy");
	}

}

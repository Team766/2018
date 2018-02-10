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
	public static int getGripper(){
		return ConfigFileReader.getInstance().getPort("gripper");
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

	public static int getRightShifter() {
		return ConfigFileReader.getInstance().getPort("rightShifter");
	}
	
	public static int getLeftShifter() {
		return ConfigFileReader.getInstance().getPort("leftShifter");
	}
	
	/*
	//climber
	public static int getClimber() {
		return ConfigFileReader.getInstance().getPort("climber");
	}
	*/

}

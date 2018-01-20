package com.team766.robot;

import com.team766.lib.ConfigFile;

import interfaces.JoystickReader;
import interfaces.RobotProvider;
import interfaces.SpeedController;

public class HardwareProvider {
	
	private static HardwareProvider instance;
	
	public static HardwareProvider getInstance(){
		if(instance == null){
			instance = new HardwareProvider();
		}
		return instance;
	}
	
	//Drive
	public SpeedController getLeftDriveA(){
		return RobotProvider.instance.getMotor(ConfigFile.getLeftMotor()[0]);
	}
	public SpeedController getLeftDriveB(){
		return RobotProvider.instance.getMotor(ConfigFile.getLeftMotor()[1]);
	}
	public SpeedController getRightDriveA(){
		return RobotProvider.instance.getMotor(ConfigFile.getRightMotor()[0]);
	}
	public SpeedController getRightDriveB(){
		return RobotProvider.instance.getMotor(ConfigFile.getRightMotor()[1]);
	}
	
	//Joysticks
	public JoystickReader getLeftJoy() {
		return RobotProvider.instance.getJoystick(ConfigFile.getLeftJoy());
	}
	public JoystickReader getRightJoy() {
		return RobotProvider.instance.getJoystick(ConfigFile.getRightJoy());
	}
	public JoystickReader getButtonJoy() {
		return RobotProvider.instance.getJoystick(ConfigFile.getButtonJoy());
	}
	

}

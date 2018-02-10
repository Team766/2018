package com.team766.robot;

import com.team766.lib.ConfigFile;

import interfaces.EncoderReader;
import interfaces.JoystickReader;
import interfaces.RobotProvider;
import interfaces.SolenoidController;
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
	
	
//	public EncoderReader getLeftEncoder(){
//		return RobotProvider.instance.getEncoder(ConfigFile.getLeftEncoder()[0], ConfigFile.getLeftEncoder()[1]);
//	}
//	public EncoderReader getRightEncoder(){
//		return RobotProvider.instance.getEncoder(ConfigFile.getRightEncoder()[0], ConfigFile.getRightEncoder()[1]);
//	}

	
	//Gripper
	public SolenoidController getGripper(){
		return RobotProvider.instance.getSolenoid(ConfigFile.getGripper());
	}
	public SpeedController getGripperMotorA(){
		return RobotProvider.instance.getMotor(ConfigFile.getGripperMotorA());
	}
	public SpeedController getGripperMotorB(){
		return RobotProvider.instance.getMotor(ConfigFile.getGripperMotorB());
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
	
	
	//Arm
//	public SpeedController getLeftArmShoulder(){
//		return RobotProvider.instance.getMotor(ConfigFile.getArmShoulder()[0]);
//	}
	public SpeedController getRightArmShoulder(){
		return RobotProvider.instance.getMotor(ConfigFile.getArmShoulder()[1]);
	}
//	public SpeedController getLeftArmWrist(){
//		return RobotProvider.instance.getMotor(ConfigFile.getArmWrist()[0]);
//	}
	public SpeedController getRightArmWrist(){
		return RobotProvider.instance.getMotor(ConfigFile.getArmWrist()[1]);
	}
	public EncoderReader getShoulderEncoder(){
		return RobotProvider.instance.getEncoder(ConfigFile.getShoulderEncoder()[0], ConfigFile.getShoulderEncoder()[1]);
	}
	public EncoderReader getWristEncoder(){
		return RobotProvider.instance.getEncoder(ConfigFile.getWristEncoder()[0], ConfigFile.getWristEncoder()[1]);
	}
	
	//Climber
	/*
	public SpeedController getClimber() {
		return RobotProvider.instance.getMotor(ConfigFile.getClimber());
	}
	*/
}

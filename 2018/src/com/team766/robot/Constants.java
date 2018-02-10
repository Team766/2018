package com.team766.robot;

public class Constants {
	public static enum Autons {
		None,
		Switch,
		Scale,
		Exchange,
		CrossLine,
		DriveStraightTime,
		DriveSquareTime,
		DriveEncoderStraight
	};
	
	public static enum Drives {
		TankDrive,
		SingleStick
	};
	
	public static final double wheel_circumference = 4.0 * Math.PI; //inches
	
	public static final Drives driveType = Drives.SingleStick;
	
	public static final double leftAxisDeadband = 0.07;
	public static final double rightAxisDeadband = 0.07;
	
	public static final double driveThreshold = 1.0; //not tested
	public static final double gripperMotorSpeed = 0.4;
	public static final double climberSpeed = 1.0;
	
	//1 = right and -1 = left
	public static int scale_side;
	public static int switch_side;

	
	//shifters
	public static final boolean negateRightShifter = true;
	public static final boolean negateLeftShifter = false;

}

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
	public static final int counts_per_revolution = 1024;
	public static final int leftEncoderDirection = -1;
	public static final int rightEncoderDirection = 1;
	
	public static final int gyroDirection = 1; //turning right increases angle
	public static final double startAngle = 0.0;
	
	public static final Drives driveType = Drives.SingleStick;
	
	public static final double leftAxisDeadband = 0.07;
	public static final double rightAxisDeadband = 0.07;
	
	public static final double driveThreshold = 0.7;
	public static final double angleThreshold = 5.0; //not tested
	public static final double gripperMotorSpeed = 0.4;
	public static final double climberSpeed = 1.0;
	
	//1 = right and -1 = left
	public static int scale_side;
	public static int switch_side;

	
	//shifters
	public static final boolean negateRightShifter = true;
	public static final boolean negateLeftShifter = false;
	
	//PID - not tuned
	public static final double k_linearP = 0.1; //1/max linear vel
	public static final double k_linearI = 0.0;
	public static final double k_linearD = 0.3;
	public static final double k_linearThresh = 0.1;
	
	public static final double k_angularP = 0.1; //1/max angular vel
	public static final double k_angularI = 0.0;
	public static final double k_angularD = 0.0;
	public static final double k_angularThresh = 1.0;
	
}

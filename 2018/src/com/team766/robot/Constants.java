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
		ArmPID
	};
	
	public static enum Drives {
		TankDrive,
		SingleStick,
		CheesyDrive,
		Arm
	};
	
	public static final double wheel_circumference = 4.0 * Math.PI; //note: mule = 4 in ; revA = 6 in
	
	public static final Drives driveType = Drives.Arm;
	
	public static final double leftAxisDeadband = 0.07;
	public static final double rightAxisDeadband = 0.07;
	
	public static final double intakeMotorSpeed = 0.4;
	public static final double intakePowerLimit = 0.7;
	public static final double climberSpeed = 1.0;

	//1 = right and -1 = left
	public static int scale_side;
	public static int switch_side;
	
	//PID for arm shoulder
	public static final double k_shoulderUpP = 0.3;
	public static final double k_shoulderUpI = 0.0;
	public static final double k_shoulderUpD = 0.01;
	public static final double k_shoulderUpThresh = 20.0;
	
	public static final double k_shoulderBalanceP = 0.1;
	public static final double k_shoulderBalanceI = 0.0;
	public static final double k_shoulderBalanceD = 0.01;
	public static final double k_shoulderBalanceThresh = 5.0;
	
	public static final double armStartAngle = 0.0;
	public static final double armShoulderVertical = 4300; //delta value
	public static final double armWristLimit = 1000; //not tested - delta value
	public static final double shoulderUpPIDScale = 1.0 / (armShoulderVertical * k_shoulderUpP); 
	public static final double shoulderPowerLimit = 0.8;
	public static final double wristPowerLimit = 0.4;
}

package com.team766.robot;

public class Constants {
	/*
	 * Auton
	 */
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
	//1 = right and -1 = left
	public static int scale_side;
	public static int switch_side;
	
	
	/*
	 * Drive
	 */
	public static enum Drives {
		TankDrive,
		SingleStick,
		CheesyDrive,
		Arm
	};
	public static final Drives driveType = Drives.SingleStick;
	
	public static final double wheel_circumference = 4.0 * Math.PI; //note: mule = 4 in ; revA = 6 in
	
	
	/*
	 * Joysticks
	 */
	public static final double leftAxisDeadband = 0.07;
	public static final double rightAxisDeadband = 0.07;
	
	
	/*
	 * Intake
	 */
	public static final double intakeMotorSpeed = 0.4;
	public static final double intakePowerLimit = 0.7;
	
	
	/*
	 * Climber
	 */
	public static final double climberSpeed = 1.0;
	
	
	/*
	 * Arm
	 */
	//Shoulder
	
	//up
	public static final double armStartAngle = 0.0;
	public static final double armShoulderVertical = 3700; //delta value
	
	public static final double k_shoulderUpP = 0.3;
	public static final double k_shoulderUpI = 0.0;
	public static final double k_shoulderUpD = 0.00;
	public static final double k_shoulderUpThresh = 400.0;
	public static final double shoulderUpFeedForward = 0.4;
	
	public static final double shoulderUpPIDScale = 1.0 / (armShoulderVertical * k_shoulderUpP);
	public static final double shoulderUpPowerLimit = 0.8;
	
	//balance
	public static final double k_shoulderBalanceP = 1.0;
	public static final double k_shoulderBalanceI = 0.0;
	public static final double k_shoulderBalanceD = 0.0;
	public static final double k_shoulderBalanceThresh = 5.0;
	
	public static final double shoulderBalancePIDScale = 1.0 / (armShoulderVertical * k_shoulderUpP);
	public static final double shoulderBalancePowerLimit = 0.25;
	
	//Wrist
	public static final double wristPowerLimit = 0.4;
	public static final double armWristLimit = 1000; //not tested - delta value
}

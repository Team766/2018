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
		ArmPID,
		DriveSquare,
		DriveEncoder,
		DrivePID
	};
	//game data
	public static int scale_side; //1 = right and -1 = left
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
	public static final double drivePowerLimit = 0.4;
	
	//encoder
	public static final double wheel_circumference = 4.0 * Math.PI; //note: mule = 4 in ; revA = 6 in
	public static final int counts_per_revolution = 1024;
	public static final int leftEncoderDirection = -1;
	public static final int rightEncoderDirection = 1;
	
	//gyro
	public static final int gyroDirection = 1; //turning right increases angle
	public static final double startAngle = 0.0;
	
	//encoder drive
	public static final double driveEncoderThreshold = 0.3;
	public static final double driveAngleThreshold = 5.0; 
	
	//shifters
	public static final boolean negateRightShifter = false;
	public static final boolean negateLeftShifter = false;
	
	//PID
	public static final double k_linearP = 0.4; 
	public static final double k_linearI = 0.0;
	public static final double k_linearD = 0.02; //1/max linear vel
	public static final double k_linearThresh = 0.2;
	
	public static final double k_angularP = 0.1; //1/max angular vel
	public static final double k_angularI = 0.0;
	public static final double k_angularD = 0.005;
	public static final double k_angularThresh = 2.0;
	
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

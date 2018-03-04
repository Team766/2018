package com.team766.robot;

import lib.ConstantsFileReader;

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
		DrivePID,
		WristPID
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
	
	public static final double wheel_circumference = ConstantsFileReader.getInstance().get("wheel_diameter") * Math.PI; //note: mule = 4 in ; revA = 6 in diameter
	
	//gyro
	public static final double startAngle = 0.0;
	
	//encoder drive
	public static final double driveEncoderThreshold = 0.3;
	public static final double driveAngleThreshold = 5.0; 
	
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
	public static final double shoulderUpPIDScale = 1.0 / (ConstantsFileReader.getInstance().get("armShoulderVertical") * ConstantsFileReader.getInstance().get("k_shoulderUpP"));
	
	//Wrist
	public static final double armWristDown = 3900;
	public static final double armWristMiddle = 2600; //value needs to be tested
	public static final double armWristBack = 200; //still needs to be tested
	
	public static final double k_wristP = 0.1;
	public static final double k_wristI = 0;
	public static final double k_wristD = 0;
	public static final double k_wristThresh = 500.0;
	
	public static final double wristMiddlePIDScale = 1.0 / (armWristMiddle * k_wristP);
	public static final double wristBackPIDScale = 1.0 / (armWristBack * k_wristP);
	public static final double wristDownPIDScale = 1.0 / (armWristDown * k_wristP);
	
	public static final double wristPowerLimit = 0.86;
	public static final double armWristLimit = 1000; //not tested - delta value
	public static final double armWrisFeedForward = 0.5;
}

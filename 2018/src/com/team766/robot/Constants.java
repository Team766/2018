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
		WristPID,
		LeftToSwitch,
		RightToSwitch
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
	public static final double shoulderTimeLimit = 8000;
	//Wrist
	public static final double wristPowerLimit = 0.7;
	public static final double armWristLimit = 1000; //not tested - delta value
	
	/*
	 * To Switch
	 */
	//starting from sides
	public static final double switchStraight = 11.0; //distance to the switch if same side
	public static final double switchToMiddle = 3.0; //distance to the point preparing to turn
	public static final double switchHorizontal = 6.0; //horizontal distance for going to the switch at opposite direction
	public static final double switchToScore = 4.0; //distance to the switch for dropping cube after turning
	public static final double switchFirstTurnAngle = -90.0;
	public static final double switchSecondTurnAngle = 90.0;
	
	
}

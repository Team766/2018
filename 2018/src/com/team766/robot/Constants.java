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
	public static final double k_intakeP = 0.1;
	public static final double k_intakeI = 0.0008;
	public static final double k_intakeD = 0.0;
	public static final double k_intakeThresh = 0.001;
	
	public static final double armStartAngle = 0.0;
	public static final double armShoulderLimit = 4300; //delta value
	public static final double armWristLimit = 1000; //not tested - delta value
}

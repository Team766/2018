package com.team766.robot;

import lib.ConstantsFileReader;

public class Constants {
	
	public static final boolean enableCamera = false;
	public static final boolean mule = true;
	public static final boolean startSeq = false;
	
	/*
	 * Auton
	 */
	public static enum Autons {
		None,
		RightToScale,
		LeftToScale,
		CrossLine,
		DriveSquare,
		LeftToSwitch,
		RightToSwitch,
		MiddleToSwitch
	};
  
	//game data
	public static int scale_side; //1 = right and -1 = left
	public static int switch_side;
	
	public static final double field_width_half = 11.01;
	
	public static final double delta_center_lines = 0.42; //distance between robot middle and field middle when starting at "middle" position
	
	// side to scale
	public static final double side_scale_same_angle = -6.0; //degrees right
	public static final double side_scale_same_forward = 20.0; //24.0 if including the length of robot
	public static final double side_scale_same_forward_angle = 90.0; //left 96.0
	public static final double side_scale_same_forward_side = 1.0;
	
	public static final double side_scale_opposite_forward = 17.0; //17.0
	public static final double side_scale_opposite_forward_side = 14.0; //14.0
	public static final double side_scale_opposite_forward_side_forward = 7.0; //7.0
	
	/*
	 * Drive
	 */
	public static enum Drives {
		TankDrive,
		SingleStick,
		CheesyDrive,
		Arm
	};
	public static final Drives driveType = Drives.CheesyDrive; //(ConstantsFileReader.getInstance().get("driveType") == 0) ? Drives.TankDrive : Drives.CheesyDrive;
	public static final double drivePowerLimit = 1.0;
	
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
	public static final double intakePowerLimit = 1.0;
	
	/*
	 * Climber
	 */
	public static final double climberSpeed = 1.0;
	
	
	/*
	 * Arm
	 */
	//Shoulder
	public static final double shoulderUpPIDScale = 1.0; 
	public static final double shoulderTimeLimit = 8000;
	
	//Wrist
	public static final double wristPowerLimit = 0.7;
	
	
	/*
	 * To Switch
	 */
	public static final double switch_final_forward = 1.2;//1.2
	
	//starting from sides
	public static final double side_switch_straight = 10.45; //distance to the switch if same side 
	public static final double side_switch_straight_side = 3.4; //distance from side to the switch if same side
	public static final double side_switch_forward = 3.0; //distance to the point preparing to turn
	public static final double side_switch_forward_side = 15.0; //(actual value: 15 feet) horizontal distance for going to the switch at opposite direction
	public static final double side_switch_forward_side_forward = 4.45; //distance to the switch for dropping cube after turning
	public static final double switchFirstTurnAngle = -90.0;
	public static final double switchSecondTurnAngle = 90.0; //90.0
	
	//starting from middle
	public static final double middle_switch_forward = 3.0;
	public static final double middle_switch_forward_leftSide = 4.82;
	public static final double middle_switch_forward_rightSide = 4.50; //actual value: 6.66 feet
	public static final double middle_switch_forward_side_forward = 4.45; //4.45 calc value
	
	
}

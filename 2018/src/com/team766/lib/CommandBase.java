package com.team766.lib;

import interfaces.SubActor;

import com.team766.robot.Actors.Drive.Drive;
import com.team766.robot.Actors.Intake.Intake;
import com.team766.robot.Actors.Arm.Arm;
import com.team766.robot.Actors.Climber.Climber;

public abstract class CommandBase implements SubActor{
	
	public static Drive Drive;
	public static Intake Intake;
	public static Arm Arm;
	public static Climber Climber;
	
	public static void init(){
		Drive = new Drive();
		Intake = new Intake();
		Arm = new Arm();
		Climber = new Climber();
	}

}

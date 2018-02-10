package com.team766.lib;

import interfaces.SubActor;

import com.team766.robot.Actors.Drive.Drive;
import com.team766.robot.Actors.Gripper.Gripper;
import com.team766.robot.Actors.Arm.Arm;

public abstract class CommandBase implements SubActor{
	
	public static Drive Drive;
	public static Gripper Gripper;
	//public static Arm Arm;

	public static void init(){
		Drive = new Drive();
		Gripper = new Gripper();
		//Arm= new Arm();
	}

}

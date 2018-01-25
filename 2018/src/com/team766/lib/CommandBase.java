package com.team766.lib;

import interfaces.SubActor;

import com.team766.robot.Actors.Drive.Drive;
import com.team766.robot.Actors.Gripper.Gripper;

public abstract class CommandBase implements SubActor{
	
	public static Drive Drive;
	public static Gripper Gripper;

	public static void init(){
		Drive = new Drive();
		Gripper = new Gripper();
	}

}

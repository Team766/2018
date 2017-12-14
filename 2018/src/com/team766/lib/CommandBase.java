package com.team766.lib;

import interfaces.SubActor;

import com.team766.robot.Actors.Drive.Drive;

public abstract class CommandBase implements SubActor{
	
	public static Drive Drive;

	public static void init(){
		Drive = new Drive();
	}

}

package com.team766.lib;

import interfaces.SubActor;

import com.team766.robot.Actors.Drive.Drive;
import com.team766.robot.Actors.Intake.Intake;
import com.team766.robot.Actors.Shoulder.Shoulder;
import com.team766.robot.Actors.Climber.Climber;
import com.team766.robot.Actors.Wrist.Wrist;
import com.team766.robot.Constants;
import com.team766.robot.Actors.Camera.Camera;

public abstract class CommandBase implements SubActor{
	
	public static Drive Drive;
	public static Intake Intake;
	public static Shoulder Shoulder;
	public static Climber Climber;
	public static Wrist Wrist;
	public static Camera Camera;
	
	public static void init(){
		Drive = new Drive();
		Intake = new Intake();
		if(Constants.mule){
			Shoulder = new Shoulder();
			Climber = new Climber();
			Wrist = new Wrist();
		}
		if(Constants.enableCamera){
			Camera = new Camera();
		}
	}

}

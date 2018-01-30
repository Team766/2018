package com.team766.robot.Actors.Auton;

import lib.Actor;

import com.team766.lib.Messages.DriveTimeMessage;
import com.team766.lib.Messages.DriveUpdate;
import com.team766.robot.Constants;
import com.team766.robot.Constants.Autons;

public class AutonSelector extends Actor{

	public Constants.Autons autonMode;

	public AutonSelector (Constants.Autons mode){
		autonMode = mode;
	}

	@Override
	public void iterate() {
		switch(autonMode){
			case None:
				System.out.println("Auton: None");
				break;
			case Switch:
				System.out.println("Auton: Switch");
				break;
			case Scale:
				System.out.println("Auton: Scale");
				break;
			case CrossLine:
				System.out.println("Auton: CrossLine");
				break;
			case Exchange:
				System.out.println("Auton: Exchange");
				break;
			case DriveStraightTime:
				System.out.println("Auton: DriveStraightTime");
				sendMessage(new DriveTimeMessage(2.0, 0.2, false));
				break;
			case DriveSquareTime:
				System.out.println("Auton: DriveSquareTime");
				for(int i = 0; i < 3; i++){
					//straight 
					sendMessage(new DriveTimeMessage(2.0, 0.2, false));
					//turns left. to turn right: make power < 0
					sendMessage(new DriveTimeMessage(0.2, 0.1, true));
				}
				sendMessage(new DriveTimeMessage(2.0, 0.2, false));
				break;
		}
		done = true;
	}

	@Override
	public String toString() {
		return "Auton Selector";
	}
	
	public boolean isDone(){
		return done;
	}
}

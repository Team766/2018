package com.team766.robot.Actors.Auton;

import lib.Actor;

import com.team766.lib.Messages.DriveTimeMessage;
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
			case DriveByTime:
				System.out.println("Auton: DriveByTime");
				sendMessage(new DriveTimeMessage(2.0));
				break;
		}
		done = true;
	}

	@Override
	public String toString() {
		return "Auton Selector";
	}
}

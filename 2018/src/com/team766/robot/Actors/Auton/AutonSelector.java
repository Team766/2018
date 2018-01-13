package com.team766.robot.Actors.Auton;
import com.team766.robot.Actors.Drive.DriveStraightSpeed;
import com.team766.robot.Actors.Drive.DriveStraightTime;

import lib.Actor;

import com.team766.lib.Messages.DriveTimeMessage;
import com.team766.robot.Constants;
import com.team766.robot.Actors.Drive.*;

public class AutonSelector extends Actor{

	 private int autonMode; 
	 
	 public AutonSelector (int mode){
		 this.autonMode = mode;
	 }
	
	 public void init(){
		 acceptableMessages = new Class[]{DriveStraightTime.class, DriveStraightSpeed.class};
		 
	 }
	 
	@Override
	public void iterate() {
		switch(Constants.AUTON[autonMode]){
			case "None":
				System.out.println("Auton: None");
				break;
			case "Switch":
				System.out.println("Auton: Switch");
				break;
			case "Scale":
				System.out.println("Auton: Scale");
				break;
			case "CrossLine":
				System.out.println("Auton: CrossLine");
				break;
			case "Exchange":
				System.out.println("Auton: Exchange");
				break;
			case "DriveByTime":
				System.out.println("Auton: DriveByTime");
				sendMessage(new DriveTimeMessage(2.0));
				break;
		}
	}

	@Override
	public void run() {
		iterate();
	}

	@Override
	public String toString() {
		return "Auton Selector";
	}

	@Override
	public void step() {
		
	}

}

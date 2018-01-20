package com.team766.robot.Actors.Drive;

import com.team766.lib.CommandBase;
import com.team766.lib.Messages.DriveUpdate;

import lib.Message;

public class DriveUpdateCommand extends CommandBase{
	
	DriveUpdate command;
	boolean done;
	
	public DriveUpdateCommand(Message command){
		this.command = (DriveUpdate)command;
		done = false;
	}
	
	public void update() {
		switch(command.getMotor()){
			case leftDrive:
				Drive.setLeft(command.getPower());
				break;
			case rightDrive:
				Drive.setRight(command.getPower());
				break;
			default:
				System.out.println("Motor not recognized!");
				break;
		}
		
		done = true;
	}
	

	public boolean isDone() {
		return done;
	}

	public void stop() {
	}
}

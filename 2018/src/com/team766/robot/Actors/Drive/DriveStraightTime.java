package com.team766.robot.Actors.Drive;

import com.team766.lib.CommandBase;
import com.team766.lib.Messages.DriveTimeMessage;

import lib.Message;

public class DriveStraightTime extends CommandBase{
	
	private DriveTimeMessage message;
	private double startTime; 
	private boolean done;
	
	public DriveStraightTime(Message potato){
		
		this.message = (DriveTimeMessage) potato;
		
		startTime = (double) System.currentTimeMillis();
		
		done = false;
	}

	@Override
	public void update() {

		if ((double) System.currentTimeMillis() - startTime < message.getTime() * 1000.0){
			Drive.setDrive(1.0);
		}
		else{
			Drive.setDrive(0.0);
			done = true;
		}
			
		
	}

	@Override
	public void stop() {
		Drive.setDrive(0.0);
		
	}

	@Override
	public boolean isDone() {
		return done;
	}
	
	
}

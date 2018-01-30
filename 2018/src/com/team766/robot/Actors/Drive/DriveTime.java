package com.team766.robot.Actors.Drive;

import com.team766.lib.CommandBase;
import com.team766.lib.Messages.DriveTimeMessage;

import lib.Message;

public class DriveTime extends CommandBase{
	
	private DriveTimeMessage message;
	private double startTime; 
	private boolean done;
	private int turn;
	
	public DriveTime(Message potato){
		this.message = (DriveTimeMessage) potato;
		
		turn = message.getTurn() ? -1 : 1;
		
		startTime = (double) System.currentTimeMillis();
		done = false;
	}

	@Override
	public void update() {
		
		//will turn left if power is > 0
		
		if ((double) System.currentTimeMillis() - startTime < message.getTime() * 1000.0){
			Drive.setLeft(message.getPower() * turn);
			Drive.setRight(message.getPower());
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

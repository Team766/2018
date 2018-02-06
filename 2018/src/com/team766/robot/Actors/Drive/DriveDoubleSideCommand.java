package com.team766.robot.Actors.Drive;

import com.team766.lib.CommandBase;
import com.team766.lib.Messages.DriveDoubleSideUpdate;

import lib.Message;

public class DriveDoubleSideCommand extends CommandBase {

	DriveDoubleSideUpdate command;
	boolean done;
	
	public DriveDoubleSideCommand(Message command){
		this.command = (DriveDoubleSideUpdate)command;
		done = false;
	}

	@Override
	public void update() {
		Drive.setLeft(clamp(command.getLeftPower()));
		Drive.setRight(clamp(command.getRightPower()));
		done = true;
	}

	@Override
	public void stop() {
	}

	@Override
	public boolean isDone() {
		return done;
	}
	
	private double clamp(double power){
		power = Math.min(power, 1);
		power = Math.max(power, -1);
		return power;
	}

}

package com.team766.robot.Actors.Drive;

import com.team766.lib.CommandBase;
import com.team766.lib.Messages.DriveDoubleSideUpdate;
import com.team766.robot.Constants;

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
		Drive.setLeft(command.getLeftPower());
		Drive.setRight(command.getRightPower());
		done = true;
	}

	@Override
	public void stop() {
	}

	@Override
	public boolean isDone() {
		return done;
	}
}
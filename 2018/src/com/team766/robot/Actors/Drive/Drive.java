package com.team766.robot.Actors.Drive;

import com.team766.lib.Messages.DriveTimeMessage;
import com.team766.lib.Messages.DriveUpdate;
import com.team766.lib.Messages.Stop;
import com.team766.robot.HardwareProvider;
import com.team766.robot.Actors.Drive.DriveStraightTime;

import interfaces.SpeedController;
import interfaces.SubActor;
import lib.Actor;
import lib.Message;

public class Drive extends Actor{

	SpeedController leftDriveA = HardwareProvider.getInstance().getLeftDriveA();
	SpeedController leftDriveB = HardwareProvider.getInstance().getLeftDriveB();
	SpeedController rightDriveA = HardwareProvider.getInstance().getRightDriveA();
	SpeedController rightDriveB = HardwareProvider.getInstance().getRightDriveB();

	SubActor currentCommand;

	public void init() {
		acceptableMessages = new Class[]{Stop.class, DriveTimeMessage.class, DriveUpdate.class};
	}
	
	public void iterate() {
		while (newMessage()) {
			Message currentMessage = readMessage();
			if (currentMessage == null){
				continue;
			}
			stopCurrentCommand();
			if (currentMessage instanceof Stop) {
				currentCommand = new DriveStop();
			}
			if (currentMessage instanceof DriveTimeMessage){
				currentCommand = new DriveStraightTime(currentMessage);
			}
			if(currentMessage instanceof DriveUpdate){
				currentCommand = new DriveUpdateCommand(currentMessage);
			}
		}
		
		if (currentCommand != null) {
			currentCommand.update();
		}
	}

	public String toString() {
		return "Actor: Drive";
	}

	public void setRight(double power){
		rightDriveA.set(power);
		rightDriveB.set(power);
	}
	
	public void setLeft(double power){
		leftDriveA.set(-power);
		leftDriveB.set(-power);
	}
		
	public void setDrive(double power){
		setLeft(power);
		setRight(power);
	}
	
	private void stopCurrentCommand(){
		if(currentCommand != null){
			currentCommand.stop();
		}
		currentCommand = null;
	}
}
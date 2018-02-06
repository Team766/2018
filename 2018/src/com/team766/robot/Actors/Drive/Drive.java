package com.team766.robot.Actors.Drive;

import com.team766.lib.Messages.Done;
import com.team766.lib.Messages.DriveEncoderMessage;
import com.team766.lib.Messages.DriveTimeMessage;
import com.team766.lib.Messages.DriveUpdate;
import com.team766.lib.Messages.Stop;
import com.team766.robot.Constants;
import com.team766.robot.HardwareProvider;
import com.team766.robot.Actors.Drive.DriveTime;

import interfaces.EncoderReader;
import interfaces.SpeedController;
import interfaces.SubActor;
import lib.Actor;
import lib.ConstantsFileReader;
import lib.Message;

public class Drive extends Actor{

	SpeedController leftDriveA = HardwareProvider.getInstance().getLeftDriveA();
	SpeedController leftDriveB = HardwareProvider.getInstance().getLeftDriveB();
	SpeedController rightDriveA = HardwareProvider.getInstance().getRightDriveA();
	SpeedController rightDriveB = HardwareProvider.getInstance().getRightDriveB();
	
	EncoderReader leftEncoder = HardwareProvider.getInstance().getLeftEncoder();
	EncoderReader rightEncoder = HardwareProvider.getInstance().getRightEncoder();

	SubActor currentCommand;

	public void init() {
		acceptableMessages = new Class[]{Stop.class, DriveTimeMessage.class, DriveUpdate.class, DriveEncoderMessage.class};
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
				currentCommand = new DriveTime(currentMessage);
			}
			if (currentMessage instanceof DriveEncoderMessage){
				currentCommand = new DriveStraightEncoder(currentMessage);
			}
			if(currentMessage instanceof DriveUpdate){
				currentCommand = new DriveUpdateCommand(currentMessage);
			}
		}
		
		if (currentCommand != null) {
			currentCommand.update();
			if(currentCommand.isDone()){ 
				sendMessage(new Done());
				currentCommand = null;
			}
		}
		
		System.out.println("DBG: right encoder = " + rightEncoder.get() + "\t\t left = " + leftEncoder.get());
	}

	public String toString() {
		return "Actor: Drive";
	}

	public void setRight(double power){
		rightDriveA.set(-power);
		rightDriveB.set(-power);
	}
	
	public void setLeft(double power){
		leftDriveA.set(power);
		leftDriveB.set(power);
	}
		
	public void setDrive(double power){
		setLeft(power);
		setRight(power);
	}
	
	public double leftDistance(){
		//assume the counts_per_rev is 1000 for now
		return ConstantsFileReader.getInstance().get("LeftEncoderDirection") * (leftEncoder.getRaw() / 1000 * 4 * Math.PI);
	}
	
	public double rightDistance(){
		return ConstantsFileReader.getInstance().get("RightEncoderDirection") * (rightEncoder.getRaw() / 1000 * 4 * Math.PI);
	}
	
	public double AverageDistance(){
		return (leftDistance() + rightDistance())/2.0;
	}
	
	protected void resetEncoders(){
		leftEncoder.reset();
		rightEncoder.reset();
	}
	
	private void stopCurrentCommand(){
		if(currentCommand != null){
			currentCommand.stop();
		}
		currentCommand = null;
	}
	
}
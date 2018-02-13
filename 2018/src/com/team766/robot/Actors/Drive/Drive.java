package com.team766.robot.Actors.Drive;

import com.team766.lib.Messages.Done;
import com.team766.lib.Messages.DriveDoubleSideUpdate;
import com.team766.lib.Messages.DriveEncoderMessage;
import com.team766.lib.Messages.DriveTimeMessage;
import com.team766.lib.Messages.DriveUpdate;
import com.team766.lib.Messages.Stop;
import com.team766.robot.Constants;
import com.team766.robot.HardwareProvider;
import com.team766.robot.Actors.Drive.DriveTime;

import interfaces.EncoderReader;
import interfaces.GyroReader;
import interfaces.SpeedController;
import interfaces.SubActor;
import lib.Actor;
import lib.Message;

public class Drive extends Actor{

	SpeedController leftDriveA = HardwareProvider.getInstance().getLeftDriveA();
	SpeedController leftDriveB = HardwareProvider.getInstance().getLeftDriveB();
	SpeedController rightDriveA = HardwareProvider.getInstance().getRightDriveA();
	SpeedController rightDriveB = HardwareProvider.getInstance().getRightDriveB();
	
	EncoderReader leftEncoder = HardwareProvider.getInstance().getLeftEncoder();
	EncoderReader rightEncoder = HardwareProvider.getInstance().getRightEncoder();
	
	GyroReader gyro = HardwareProvider.getInstance().getGyro();

	SubActor currentCommand;
	private double gyroStart;

	public void init() {
		acceptableMessages = new Class[]{Stop.class, DriveTimeMessage.class, DriveUpdate.class, DriveEncoderMessage.class, DriveDoubleSideUpdate.class};
	
		gyroStart = gyro.getAngle();
	}
	
	public void iterate() {
		while (newMessage()) {
			Message currentMessage = readMessage();
			if (currentMessage == null){
				continue;
			}
			stopCurrentCommand();
			if (currentMessage instanceof Stop) {
				setDrive(0.0);
			}
			if (currentMessage instanceof DriveTimeMessage){
				currentCommand = new DriveTime(currentMessage);
			}
			if (currentMessage instanceof DriveEncoderMessage){
				currentCommand = new DriveDistance(currentMessage);
			}
			if(currentMessage instanceof DriveUpdate){
				currentCommand = new DriveUpdateCommand(currentMessage);
			}
			if(currentMessage instanceof DriveDoubleSideUpdate){
				currentCommand = new DriveDoubleSideCommand(currentMessage);
			}
		}
		
		if (currentCommand != null) {
			currentCommand.update();
			if(currentCommand.isDone()){ 
				sendMessage(new Done());
				currentCommand = null;
			}
		}
		
		//System.out.println("DBG: right encoder = " + rightEncoder.getRaw() + "\t\t left = " + leftEncoder.getRaw());
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
		return Constants.leftEncoderDirection * (leftEncoder.getRaw() / Constants.counts_per_revolution * 4.0/12.0 * Math.PI);
	}
	
	public double rightDistance(){
		return Constants.rightEncoderDirection * (rightEncoder.getRaw() / Constants.counts_per_revolution * 4.0/12.0 * Math.PI);
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
	
	//returns degree angle from 0 to 360 - not done yet
	public double getCalculatedAngle(){
		double angle = getGyroAngle();
		while(angle > 360){
			angle -= 360;
		}
		while(angle < -360){
			angle += 360;
		}
		return angle;
	}
	
	public double getGyroAngle(){
		return gyro.getAngle() - gyroStart;
	}
	
	
}
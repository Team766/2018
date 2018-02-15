package com.team766.robot.Actors.Drive;

import com.team766.lib.Messages.Done;
import com.team766.lib.Messages.DriveDoubleSideUpdate;
import com.team766.lib.Messages.DriveEncoderMessage;
import com.team766.lib.Messages.DriveTimeMessage;
import com.team766.lib.Messages.DriveUpdate;
import com.team766.lib.Messages.ShifterUpdate;
import com.team766.lib.Messages.Stop;
import com.team766.robot.Constants;
import com.team766.robot.HardwareProvider;
import com.team766.robot.Actors.Drive.DriveTime;

import interfaces.EncoderReader;
import interfaces.GyroReader;
import interfaces.SolenoidController;
import interfaces.SpeedController;
import interfaces.SubActor;
import lib.Actor;
import lib.Message;
import lib.PIDController;

public class Drive extends Actor{

	SpeedController leftDriveA = HardwareProvider.getInstance().getLeftDriveA();
	SpeedController leftDriveB = HardwareProvider.getInstance().getLeftDriveB();
	SpeedController rightDriveA = HardwareProvider.getInstance().getRightDriveA();
	SpeedController rightDriveB = HardwareProvider.getInstance().getRightDriveB();
	
	SolenoidController rightShifter = HardwareProvider.getInstance().getRightShifter();
	SolenoidController leftShifter = HardwareProvider.getInstance().getLeftShifter();

	EncoderReader leftEncoder = HardwareProvider.getInstance().getLeftEncoder();
	EncoderReader rightEncoder = HardwareProvider.getInstance().getRightEncoder();
	
	GyroReader gyro = HardwareProvider.getInstance().getGyro();
	
	PIDController distancePID = new PIDController(Constants.k_linearP, Constants.k_linearI, Constants.k_linearD, Constants.k_linearThresh);
	PIDController anglePID = new PIDController(Constants.k_angularP, Constants.k_angularI, Constants.k_angularD, Constants.k_angularThresh);

	SubActor currentCommand;
	private double gyroOffset;

	public void init() {
		acceptableMessages = new Class[]{Stop.class, DriveTimeMessage.class, DriveUpdate.class, DriveDoubleSideUpdate.class, DriveEncoderMessage.class, ShifterUpdate.class};
	
		gyroOffset = Constants.startAngle;
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
			if (currentMessage instanceof ShifterUpdate){
				currentCommand = null;
				ShifterUpdate shifterMessage = (ShifterUpdate) currentMessage;
				setLeftShifter(shifterMessage.getHighGear());
				setRightShifter(shifterMessage.getHighGear());
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
		System.out.println("Gyro: " + gyro.getAngle());
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

	public void setRightShifter(boolean setHighGear){
		rightShifter.set(setHighGear^Constants.negateRightShifter);
	}

	public boolean getRightShifter(){
		return rightShifter.get()^Constants.negateRightShifter;
	}

	public void setLeftShifter(boolean setHighGear){
		leftShifter.set(setHighGear^Constants.negateLeftShifter);
	}

	public boolean getLeftShifter(){
		return leftShifter.get()^Constants.negateLeftShifter;
	}


	private void stopCurrentCommand(){
		if(currentCommand != null){
			currentCommand.stop();
		}
		currentCommand = null;
	}
	
	//returns degree angle from -180 to 180
	public double getCalculatedAngle(){
		double angle = getGyroAngle();
		while(angle > 180){
			angle -= 360;
		}
		while(angle < -180){
			angle += 360;
		}
		return angle;
	}
	
	public void setGyroAngle(double angle){
		gyroOffset = gyro.getAngle() + angle;
	}
	
	public double getGyroAngle(){
		return gyro.getAngle() - gyroOffset;
	}
}
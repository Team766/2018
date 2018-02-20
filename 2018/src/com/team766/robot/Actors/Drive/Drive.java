package com.team766.robot.Actors.Drive;

import com.team766.lib.Messages.Done;
import com.team766.lib.Messages.DriveDoubleSideUpdate;
import com.team766.lib.Messages.DriveEncoderMessage;
import com.team766.lib.Messages.DrivePIDMessage;
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

	private SubActor currentCommand;
	private double gyroOffset;

	public void init() {
		acceptableMessages = new Class[]{Stop.class, DriveTimeMessage.class, DriveUpdate.class, DriveDoubleSideUpdate.class, DriveEncoderMessage.class, ShifterUpdate.class, DrivePIDMessage.class};
	
		gyroOffset = Constants.startAngle;
	}
	
	public void iterate() {
		while (newMessage()) {	
			Message currentMessage = readMessage();
			if (currentMessage == null){
				continue;
			}
			
			//System.out.println("DBG: got new message stopping current message!");
			
			if (currentMessage instanceof Stop) {
				stopCurrentCommand("got Stop message");
				setDrive(0.0);
			}
			else if (currentMessage instanceof DriveTimeMessage){
				swapCurrentCommand(new DriveTime(currentMessage), "got new drive time message");
			}
			else if (currentMessage instanceof DriveEncoderMessage){
				swapCurrentCommand(new DriveDistance(currentMessage), "got new drive encoder message");
			}
			else if(currentMessage instanceof DriveUpdate){
				swapCurrentCommand(new DriveUpdateCommand(currentMessage), "got new drive update message");
			}
			else if(currentMessage instanceof DriveDoubleSideUpdate){
				swapCurrentCommand(new DriveDoubleSideCommand(currentMessage), "got new drive double side message");
			}
			else if (currentMessage instanceof ShifterUpdate){
				//stopCurrentCommand();
				ShifterUpdate shifterMessage = (ShifterUpdate) currentMessage;
				setLeftShifter(shifterMessage.getHighGear());
				setRightShifter(shifterMessage.getHighGear());
			}
			else if (currentMessage instanceof DrivePIDMessage){
				swapCurrentCommand(new DrivePIDCommand(currentMessage), "got new drive PID message");
			}
		}
		
		if (currentCommand != null) {
			//System.out.println("DMDBG: Calling update");
			currentCommand.update();
			if(currentCommand.isDone()){ 
				sendMessage(new Done());
				currentCommand = null;
			}
		}
		
		//System.out.println("DBG: right encoder = " + rightEncoder.getRaw() + "\t\t left = " + leftEncoder.getRaw());
		//System.out.println("Gyro: " + gyro.getAngle());
	}

	public String toString() {
		return "Actor: Drive";
	}

	public void setRight(double power){
		rightDriveA.set(clamp(-power));
		rightDriveB.set(clamp(-power));
	}
	
	public void setLeft(double power){
		//System.out.println("left: " + power);
		leftDriveA.set(clamp(power));
		leftDriveB.set(clamp(power));
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
	
	public double averageDistance(){
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


	private void stopCurrentCommand(String reason){
		swapCurrentCommand(null, reason);
	}
	
	private void swapCurrentCommand(SubActor newCommand, String reason){
		//System.out.println("DMDBG: swapping currentCommand because: " + reason);
		if(currentCommand != null){
			currentCommand.stop();
		}
		currentCommand = newCommand;
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
	
	public double getAngularVel(){
		return gyro.getRate(); //deg/sec
	}
	
	private double clamp(double power){
		double clampedLimit = Math.max(Math.min(Constants.drivePowerLimit, 1), -1);
		
		power = Math.min(power, clampedLimit);
		power = Math.max(power, -clampedLimit);
		return power;
	}
	
	public void twist(double surge_v, double yaw_v){
		double leftPower = surge_v - yaw_v;
		double rightPower = surge_v + yaw_v;
		
		System.out.println("yaw = " + yaw_v);
		
		System.out.println("DBG: left = " + leftPower + " \t\tright = " + rightPower);
		
		setLeft(leftPower);
		setRight(rightPower);
	}
}
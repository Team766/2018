package com.team766.robot.Actors.Drive;

import com.team766.lib.Messages.Done;
import com.team766.lib.Messages.DriveDoubleSideUpdate;
import com.team766.lib.Messages.DriveEncoderMessage;
import com.team766.lib.Messages.DrivePIDMessage;
import com.team766.lib.Messages.DriveTimeMessage;
import com.team766.lib.Messages.DriveUpdate;
import com.team766.lib.Messages.EStop;
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
import lib.ConstantsFileReader;
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
	
	PIDController distancePID = new PIDController(ConstantsFileReader.getInstance().get("k_linearP"), ConstantsFileReader.getInstance().get("k_linearI"), ConstantsFileReader.getInstance().get("k_linearD"), ConstantsFileReader.getInstance().get("k_linearThresh"));
	PIDController anglePID = new PIDController(ConstantsFileReader.getInstance().get("k_angularP"), ConstantsFileReader.getInstance().get("k_angularI"), ConstantsFileReader.getInstance().get("k_angularD"), ConstantsFileReader.getInstance().get("k_angularThresh"));

	private SubActor currentCommand;
	private double gyroOffset;

	public void init() {
		acceptableMessages = new Class[]{EStop.class, Stop.class, DriveTimeMessage.class, DriveUpdate.class, DriveDoubleSideUpdate.class, DriveEncoderMessage.class, ShifterUpdate.class, DrivePIDMessage.class};
	
		gyroOffset = Constants.startAngle;
	}
	
	public void iterate() {
		while (newMessage()) {	
			Message currentMessage = readMessage();
			if (currentMessage == null){
				continue;
			}
			
			//System.out.println("DBG: got new message stopping current message!");
			
			if (currentMessage instanceof Stop || currentMessage instanceof EStop) {
				stopCurrentCommand("got stop message");
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
//			System.out.println("leftDrive power: " + leftDriveA.get());
//			System.out.println("rightDrive power: " + rightDriveA.get());
		
		if (currentCommand != null) {
			//System.out.println("DMDBG: Calling update");
			currentCommand.update();
			if(currentCommand.isDone()){ 
				sendMessage(new Done("Drive"));
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
		leftDriveA.set(clamp(power));
		leftDriveB.set(clamp(power));
	}
		
	public void setDrive(double power){
		setLeft(power);
		setRight(power);
	}
	
	public void setAutonLeft(double power){
		setLeft(autonClamp(power));
	}
	
	public void setAutonRight(double power){
		setRight(autonClamp(power));
	}
	
	public double leftDistance(){
		return ConstantsFileReader.getInstance().get("leftEncoderDirection") * (leftEncoder.getRaw() / ConstantsFileReader.getInstance().get("counts_per_revolution") * ConstantsFileReader.getInstance().get("wheel_diameter") / 12.0 * Math.PI);
	}
	
	public double rightDistance(){
		return ConstantsFileReader.getInstance().get("rightEncoderDirection") * (rightEncoder.getRaw() / ConstantsFileReader.getInstance().get("counts_per_revolution") * ConstantsFileReader.getInstance().get("wheel_diameter") / 12.0 * Math.PI);
	}
	
	public double averageDistance(){
		return (leftDistance() + rightDistance())/2.0;
	}
	
	protected void resetEncoders(){
		leftEncoder.reset();
		rightEncoder.reset();
	}

	public void setRightShifter(boolean setHighGear){
		rightShifter.set(ConstantsFileReader.getInstance().get("negateRightShifter") == 1 ? !setHighGear : setHighGear);
	}

	public boolean getRightShifter(){
		return ConstantsFileReader.getInstance().get("negateRightShifter") == 1 ? !rightShifter.get() : rightShifter.get();
	}

	public void setLeftShifter(boolean setHighGear){
		leftShifter.set(ConstantsFileReader.getInstance().get("negateLeftShifter") == 1 ? !setHighGear : setHighGear);
	}

	public boolean getLeftShifter(){
		return ConstantsFileReader.getInstance().get("negateLeftShifter") == 1 ? !leftShifter.get() : leftShifter.get();
	}


	private void stopCurrentCommand(String reason){
		swapCurrentCommand(null, reason);
	}
	
	private void swapCurrentCommand(SubActor newCommand, String reason){
		System.out.println("DMDBG: swapping currentCommand because: " + reason);
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
	
	private double autonClamp(double power){
		double clampedLimit = Math.max(Math.min(ConstantsFileReader.getInstance().get("autonDriveLimit"), 1), -1);
		
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
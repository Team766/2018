package com.team766.robot.Actors.Shoulder;

import com.team766.lib.Messages.Stop;
import com.team766.lib.Messages.WristPIDMessage;
import com.team766.lib.Messages.ArmStageMessage;
import com.team766.lib.Messages.Done;
import com.team766.lib.Messages.EStop;
import com.team766.lib.Messages.ShoulderPIDMessage;
import com.team766.lib.Messages.ShoulderSimpleMessage;
import com.team766.lib.Messages.ShoulderManualMessage;
import com.team766.robot.Constants;
import com.team766.robot.HardwareProvider;

import interfaces.CANSpeedController;
import interfaces.CANSpeedController.ControlMode;
import interfaces.DigitalInputReader;
import interfaces.SubActor;
import lib.Actor;
import lib.ConstantsFileReader;
import lib.Message;
import lib.PIDController;

public class Shoulder extends Actor {
	CANSpeedController leftShoulder = HardwareProvider.getInstance().getLeftArmShoulder();
	CANSpeedController rightShoulder = HardwareProvider.getInstance().getRightArmShoulder();
	
	DigitalInputReader limitSwitch = HardwareProvider.getInstance().getLimitSwitch();
	
	PIDController shoulderUpPID = new PIDController(ConstantsFileReader.getInstance().get("k_shoulderUpP"), ConstantsFileReader.getInstance().get("k_shoulderUpI"), ConstantsFileReader.getInstance().get("k_shoulderUpD"), ConstantsFileReader.getInstance().get("k_shoulderUpThresh"));
		
	private boolean commandFinished;

	private double shoulderSetPoint;
	
	Message currentMessage;
	private SubActor currentCommand;
	
	public void init(){
		acceptableMessages = new Class[]{ArmStageMessage.class, EStop.class, Stop.class, ShoulderManualMessage.class, ShoulderPIDMessage.class, ShoulderSimpleMessage.class};
		setShoulderEncoders((int)ConstantsFileReader.getInstance().get("shoulderStartValue"));
	}
	
	public String toString() {
		return "Actor: Shoulder";
	}

	public void iterate() {
		while(newMessage()){
			if(currentCommand != null)
				currentCommand.stop();
			
			commandFinished = false;
			
			currentMessage = readMessage();
			if(currentMessage == null)
				return;
			
			else if(currentMessage instanceof ShoulderSimpleMessage){
				ShoulderSimpleMessage mess = (ShoulderSimpleMessage) currentMessage;
				setLeftShoulder(mess.getLeft());
				setRightShoulder(mess.getRight());
			}
			else if(currentMessage instanceof Stop){
				currentCommand = null;
				setShoulder(0.0);
			}
			else if(currentMessage instanceof EStop){
				System.out.println("eStop: holding shoulder");
				currentCommand = new ShoulderPIDCommand(new ShoulderPIDMessage(3)); //stay at current encoder value
			}
			else if(currentMessage instanceof ShoulderManualMessage){
				System.out.println("shoulder manual message");
				double currPos = this.getAveShoulderEncoder();
				double shoulderPower = ConstantsFileReader.getInstance().get("shoulderManualPower");
				double ff = ConstantsFileReader.getInstance().get("shoulderUpFeedForward") * Math.cos(this.getShoulderAngleRad(currPos));
				ShoulderManualMessage armMessage = (ShoulderManualMessage)currentMessage;
				currentCommand = null;
				if(armMessage.getShoulderDirection() == 0 && currPos < ConstantsFileReader.getInstance().get("armShoulderVerticle")){
					System.out.println("********************shoulder moving up*********************");
					setShoulder(shoulderPower + ff);
				}
				else if(armMessage.getShoulderDirection() == 1 && currPos > 0){
					System.out.println("--------------------shoulder moving down----------------------");
					setShoulder(-shoulderPower + ff);
				}	
				else
					currentCommand = new ShoulderPIDCommand(new ShoulderPIDMessage(3)); //hold	
			}
			else if(currentMessage instanceof ArmStageMessage){
				currentCommand = new ArmStageCommand(currentMessage);
			}
			else if(currentMessage instanceof ShoulderPIDMessage){
				currentCommand = new ShoulderPIDCommand(currentMessage);
				System.out.println("receiving message");
			}
			else if(currentMessage instanceof Done){
				currentCommand.stop();
			}
		}

		if (currentCommand != null) {
			currentCommand.update();
			
			if(currentCommand.isDone()){
				sendMessage(new Done("Shoulder"));
			}	
		}
		if (!getLimitSwitch()){
			setShoulderEncoders(0);
		}
		
		System.out.println("shoulder encoder: " + getAveShoulderEncoder() + "\t\tshoulder angle: " + getShoulderAngle());
	}
	
	//mule: one shoulder motor is cross wired so they spin the same way, no need to negate one side, theoreticlly negate right?
	public void setLeftShoulder(double power){
		leftShoulder.set(ControlMode.PercentOutput, clamp(power, ConstantsFileReader.getInstance().get("shoulderUpPowerLimit")));
	}
	
	//PercentOutput is the mode for setting speed
	public void setRightShoulder(double power){
		rightShoulder.set(ControlMode.PercentOutput, clamp(power, ConstantsFileReader.getInstance().get("shoulderUpPowerLimit")));
	}
	
	public void setShoulder(double power){ //for going up and regular
		setLeftShoulder(power);
		setRightShoulder(power);
		System.out.println("shoulder up power: " + clamp(power, ConstantsFileReader.getInstance().get("shoulderUpPowerLimit")));
	}
	
	public void setShoulderDown(double power){
		double clampedPower = clamp(power, ConstantsFileReader.getInstance().get("shoulderDownPowerLimit"));
		setLeftShoulder(clampedPower);
		setRightShoulder(clampedPower);
		System.out.println("shoulder down power: " + clampedPower);
	}
	
	public void setShoulderBalance(double power){
		double clamped_power = clamp(power, ConstantsFileReader.getInstance().get("shoulderBalancePowerLimit"));
		setLeftShoulder(clamped_power);
		setRightShoulder(clamped_power);
		System.out.println("shoulder timeout power: " + clamped_power);
	}
	
	public double getLeftShoulderEncoder(){
		return leftShoulder.getSensorPosition();
	}
	/*
	public double getRightShoulderEncoder(){
		return rightShoulder.getSensorPosition();
	}
	*/
	
	public double getAveShoulderEncoder(){
		return -1 * getLeftShoulderEncoder();
	}
	
	public double getShoulderAngle(){
		//assuming the reduction value is 1 for now...
		return (getAveShoulderEncoder() * (360d/(ConstantsFileReader.getInstance().get("counts_per_revolution") * 1)) + ConstantsFileReader.getInstance().get("armStartAngle"));
	}
	
	public double getShoulderSetPoint(){
		return shoulderSetPoint;
	}
	
	public void setShoulderSetPoint(double setPoint){
		this.shoulderSetPoint = setPoint;
	}
	
	public double getHeight(){
		return 2 * 36 * (Math.sin(getShoulderAngle()));
	}
	
	public double getAngleFromHeight(double height){
		return Math.asin(height / (2 * 36));
	}
	
	public void setShoulderEncoders(int position){
		leftShoulder.setPosition(position);
		//rightShoulder.setPosition(position);
	}
	
	public void setEncoders(int position){
		setShoulderEncoders(position);
	}
	
	public double clamp(double value, double limit){
		limit = Math.abs(limit);
		return Math.max(Math.min(value, limit), -limit);
	}
	
	public double getShoulderAngleRad(double encoder){
		return 0.5 * Math.PI * getAveShoulderEncoder() / ConstantsFileReader.getInstance().get("armShoulderVertical");
	}
	
	public boolean getLimitSwitch(){
		return limitSwitch.get();
	}
	
}

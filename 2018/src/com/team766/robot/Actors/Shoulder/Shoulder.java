package com.team766.robot.Actors.Shoulder;

import com.team766.lib.Messages.Stop;
import com.team766.lib.Messages.WristPIDMessage;
import com.team766.lib.Messages.ArmSimpleMessage;
import com.team766.lib.Messages.ArmStageMessage;
import com.team766.lib.Messages.Done;
import com.team766.lib.Messages.ShoulderPIDMessage;
import com.team766.robot.Constants;
import com.team766.robot.HardwareProvider;

import interfaces.CANSpeedController;
import interfaces.CANSpeedController.ControlMode;
import interfaces.SubActor;
import lib.Actor;
import lib.ConstantsFileReader;
import lib.Message;
import lib.PIDController;

public class Shoulder extends Actor {
	CANSpeedController leftShoulder = HardwareProvider.getInstance().getLeftArmShoulder();
	CANSpeedController rightShoulder = HardwareProvider.getInstance().getRightArmShoulder();
	
	PIDController shoulderUpPID = new PIDController(ConstantsFileReader.getInstance().get("k_shoulderUpP"), ConstantsFileReader.getInstance().get("k_shoulderUpI"), ConstantsFileReader.getInstance().get("k_shoulderUpD"), ConstantsFileReader.getInstance().get("k_shoulderUpThresh"));
		
	private boolean commandFinished;

	private double shoulderSetPoint;
	
	Message currentMessage;
	private SubActor currentCommand;
	
	public void init(){
		acceptableMessages = new Class[]{ArmSimpleMessage.class, ArmStageMessage.class, Stop.class, ShoulderPIDMessage.class};
		setShoulderEncoders(0);
	}
	
	public String toString() {
		return "Actor: Shoulder";
	}

	public void iterate() {
		if(newMessage()){
			if(currentCommand != null)
				currentCommand.stop();
			
			commandFinished = false;
			
			currentMessage = readMessage();
			if(currentMessage == null)
				return;
			
			else if(currentMessage instanceof Stop){
				System.out.println("stopping shoulder");
				currentCommand = null;
				setShoulder(0.0);
			}
			else if(currentMessage instanceof ArmSimpleMessage){
				System.out.println("got arm simple message");
				currentCommand = null;
				ArmSimpleMessage armMessage = (ArmSimpleMessage)currentMessage;
				if(getAveShoulderEncoder() < ConstantsFileReader.getInstance().get("armShoulderVertical")){
					//no need to negate shoulder on mule see note
					setShoulder(armMessage.getShoulderSpeed());
				}else{
					leftShoulder.stopMotor();
					rightShoulder.stopMotor();				
				}
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
				sendMessage(new Done());
			}	
		}
	}
	
	//mule: one shoulder motor is cross wired so they spin the same way, no need to negate one side
	public void setLeftShoulder(double power){
		leftShoulder.set(ControlMode.PercentOutput, clamp(power, ConstantsFileReader.getInstance().get("shoulderUpPowerLimit")));
	}
	
	//PercentOutput is the mode for setting speed
	public void setRightShoulder(double power){
		rightShoulder.set(ControlMode.PercentOutput, clamp(power, ConstantsFileReader.getInstance().get("shoulderUpPowerLimit")));
	}
	
	public void setShoulder(double power){
		setLeftShoulder(power);
		setRightShoulder(power);
		System.out.println("shoulder 1: " + clamp(power, ConstantsFileReader.getInstance().get("shoulderUpPowerLimit")));
	}
	
	public void setShoulderBalance(double power){
		double clamped_power = clamp(power, ConstantsFileReader.getInstance().get("shoulderBalancePowerLimit"));
		setLeftShoulder(clamped_power);
		setRightShoulder(clamped_power);
		//System.out.println("shoudler 2: " + clamped_power);
	}
	
	public double getLeftShoulderEncoder(){
		return leftShoulder.getSensorPosition();
	}
	
	public double getRightShoulderEncoder(){
		return rightShoulder.getSensorPosition();
	}
	
	public double getAveShoulderEncoder(){
		return 0.5 * (Math.abs(getLeftShoulderEncoder()) + Math.abs(getRightShoulderEncoder()));
	}
	
	public double getShoulderAngle(){
		//assuming the reduction value is 1 for now...
		return (getAveShoulderEncoder() * (360d/(1024.0 * 1)) + ConstantsFileReader.getInstance().get("armStartAngle"));
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
		rightShoulder.setPosition(position);
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
	
}

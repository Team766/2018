package com.team766.robot.Actors.Arm;

import com.team766.lib.Messages.Stop;
import com.team766.lib.Messages.ArmSimpleMessage;
import com.team766.lib.Messages.ArmStageMessage;
import com.team766.robot.Constants;
import com.team766.robot.HardwareProvider;

import interfaces.CANSpeedController;
import interfaces.CANSpeedController.ControlMode;
import interfaces.SubActor;
import lib.Actor;
import lib.Message;
import lib.PIDController;

public class Arm extends Actor {
	CANSpeedController leftShoulder = HardwareProvider.getInstance().getLeftArmShoulder();
	CANSpeedController rightShoulder = HardwareProvider.getInstance().getRightArmShoulder();
	CANSpeedController leftWrist = HardwareProvider.getInstance().getLeftArmWrist();
	CANSpeedController rightWrist = HardwareProvider.getInstance().getRightArmWrist();
	
	PIDController armPID = new PIDController(Constants.k_intakeP, Constants.k_intakeI, Constants.k_intakeD, Constants.k_intakeThresh);
	
	private boolean commandFinished;

	private double shoulderSetPoint;
	private double shoulderLimit = 0.4;
	private double wristLimit = 0.4;
	
	Message currentMessage;
	private SubActor currentCommand;
	
	public void init(){
		acceptableMessages = new Class[]{ArmSimpleMessage.class, ArmStageMessage.class, Stop.class};
	}
	
	public String toString() {
		return "Actor: Arm";
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
				currentCommand = null;
				setShoulder(0.0);
				setWrist(0.0);
			}
			else if(currentMessage instanceof ArmSimpleMessage){
				currentCommand = null;
				ArmSimpleMessage armMessage = (ArmSimpleMessage)currentMessage;
				if(getAveShoulderEncoder() < Constants.armShoulderLimit){
					//joystick forward = up
					setLeftShoulder(-armMessage.getShoulderSpeed());
					setRightShoulder(armMessage.getShoulderSpeed());
				}else{
					leftShoulder.stopMotor();
					rightShoulder.stopMotor();				
				}
				/*
				if(getAveShoulderEncoder() < Constants.armWristLimit){
					//joystick forward = up?
					setLeftWrist(-armMessage.getWristSpeed());
					setRightWrist(armMessage.getWristSpeed());
				}else{
					leftWrist.stopMotor();
					rightWrist.stopMotor();				
				}
				*/
			}
			else if(currentMessage instanceof ArmStageMessage){
				currentCommand = new ArmStageCommand(currentMessage);
			}
		}

		if (currentCommand != null) {
			currentCommand.update();
		}
		//System.out.println("left shoulder = " + getLeftShoulderEncoder() + "\t\t right shoulder = " + getRightShoulderEncoder());
		System.out.println("\tleft wrist = " + getLeftWristEncoder() + "\t\t right wrist = " + getRightWristEncoder());
	}
	
	public void setLeftShoulder(double power){
		leftShoulder.set(ControlMode.PercentOutput, clamp(power, shoulderLimit));
		System.out.println("set left shoulder: " + clamp(power, shoulderLimit));
	}
	
	//PercentOutput is the mode for setting speed
	public void setRightShoulder(double power){
		rightShoulder.set(ControlMode.PercentOutput, clamp(-power, shoulderLimit));
		System.out.println("set right shoulder: " + clamp(-power, shoulderLimit));
	}
	
	public void setShoulder(double power){
		setLeftShoulder(power);
		setRightShoulder(power);
		System.out.println("arm power: " + power);
	}
	
	public void setLeftWrist(double power){
		leftWrist.set(ControlMode.PercentOutput, clamp(power, wristLimit));
	}
	
	public void setRightWrist(double power){
		rightWrist.set(ControlMode.PercentOutput, clamp(-power, wristLimit));
	}
	
	public void setWrist(double power){
		setLeftWrist(power);
		setRightWrist(power);
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
	
	public double getLeftWristEncoder(){
		return leftWrist.getSensorPosition();
	}
	
	public double getRightWristEncoder(){
		return rightWrist.getSensorPosition();
	}
	
	public double getAveWristEncoder(){
		return 0.5 * (getLeftWristEncoder() + getRightWristEncoder());
	}
	
	public double getShoulderAngle(){
		//assuming the reduction value is 1 for now...
		return (getAveShoulderEncoder() * (360d/(1024.0 * 1)) + Constants.armStartAngle);
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
	//cannot reset encoders right now
//	public void resetEncoders(){
//		
//	}
	
	public double clamp(double value, double limit){
		limit = Math.abs(limit);
		return Math.max(Math.min(value, limit), -limit);
	}

}

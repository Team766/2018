package com.team766.robot.Actors.Arm;

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

public class Arm extends Actor {
	CANSpeedController leftShoulder = HardwareProvider.getInstance().getLeftArmShoulder();
	CANSpeedController rightShoulder = HardwareProvider.getInstance().getRightArmShoulder();
	CANSpeedController leftWrist = HardwareProvider.getInstance().getLeftArmWrist();
	CANSpeedController rightWrist = HardwareProvider.getInstance().getRightArmWrist();
	
	PIDController shoulderUpPID = new PIDController(ConstantsFileReader.getInstance().get("k_shoulderUpP"), ConstantsFileReader.getInstance().get("k_shoulderUpI"), ConstantsFileReader.getInstance().get("k_shoulderUpD"), ConstantsFileReader.getInstance().get("k_shoulderUpThresh"));
	
	PIDController wristPID = new PIDController(Constants.k_wristP, Constants.k_wristI, Constants.k_wristD, Constants.k_wristThresh);
	
	private boolean commandFinished;

	private double shoulderSetPoint;
	
	Message currentMessage;
	private SubActor currentCommand;
	
	public void init(){
		acceptableMessages = new Class[]{ArmSimpleMessage.class, ArmStageMessage.class, Stop.class, ShoulderPIDMessage.class, WristPIDMessage.class};
		setWristEncoders(0);
		setShoulderEncoders(0);
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
				System.out.println("stopping shoulder");
				currentCommand = null;
				setShoulder(0.0);
				setWrist(0.0);
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
				
				if(getAveWristEncoder() < ConstantsFileReader.getInstance().get("armWristLimit")){
					setWrist(armMessage.getWristSpeed());
				}else{
					leftWrist.stopMotor();
					rightWrist.stopMotor();			
				}
				
			}
			else if(currentMessage instanceof ArmStageMessage){
				currentCommand = new ArmStageCommand(currentMessage);
			}
			else if(currentMessage instanceof ShoulderPIDMessage){
				currentCommand = new ShoulderPIDCommand(currentMessage);
			}
			else if(currentMessage instanceof WristPIDMessage){
				currentCommand = new WristPIDCommand(currentMessage);
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

		//System.out.println("Arm wrist encoder value: " + getAveWristEncoder());
		//System.out.println("left wrist: " + getLeftWristEncoder());
		//System.out.println("right wrist: " + getRightWristEncoder());		
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
		System.out.println("shoudler 2: " + clamped_power);
	}
	
	public void setLeftWrist(double power){
		leftWrist.set(ControlMode.PercentOutput, clamp(power, Constants.armWristLimit));
	}
	
	public void setRightWrist(double power){
		rightWrist.set(ControlMode.PercentOutput, clamp(-power, Constants.armWristLimit));
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
		return 0.5 * (Math.abs(getLeftWristEncoder()) + Math.abs(getRightWristEncoder()));
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
	
	public void setWristEncoders(int position){
		leftWrist.setPosition(position);
		rightWrist.setPosition(position);
	}
	
	public void setEncoders(int position){
		setShoulderEncoders(position);
		setWristEncoders(position);
	}
	
	public double clamp(double value, double limit){
		limit = Math.abs(limit);
		return Math.max(Math.min(value, limit), -limit);
	}
	
	public double getShoulderAngleRad(double encoder){
		return 0.5 * Math.PI * getAveShoulderEncoder() / ConstantsFileReader.getInstance().get("armShoulderVertical");
	}
	
	public double getWristAngleRad(double encoder){
		return 0.5 * Math.PI * getAveWristEncoder() / Constants.armWristMiddle;
	}

}

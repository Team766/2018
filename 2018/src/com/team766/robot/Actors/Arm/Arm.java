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
import lib.Message;
import lib.PIDController;

public class Arm extends Actor {
	CANSpeedController leftShoulder = HardwareProvider.getInstance().getLeftArmShoulder();
	CANSpeedController rightShoulder = HardwareProvider.getInstance().getRightArmShoulder();
	CANSpeedController leftWrist = HardwareProvider.getInstance().getLeftArmWrist();
	CANSpeedController rightWrist = HardwareProvider.getInstance().getRightArmWrist();
	
	PIDController shoulderUpPID = new PIDController(Constants.k_shoulderUpP, Constants.k_shoulderUpI, Constants.k_shoulderUpD, Constants.k_shoulderUpThresh);
	PIDController shoulderBalancePID = new PIDController(Constants.k_shoulderBalanceP, Constants.k_shoulderBalanceI, Constants.k_shoulderBalanceD, Constants.k_shoulderBalanceThresh);
	
	PIDController wristPID = new PIDController(Constants.k_wristP, Constants.k_wristI, Constants.k_wristD, Constants.k_wristThresh);
	
	private boolean commandFinished;

	private double shoulderSetPoint;
	
	Message currentMessage;
	private SubActor currentCommand;
	
	public void init(){
		acceptableMessages = new Class[]{ArmSimpleMessage.class, ArmStageMessage.class, Stop.class, ShoulderPIDMessage.class, WristPIDMessage.class};
		
		setWristEncoders(0);
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
				if(getAveShoulderEncoder() < Constants.armShoulderVertical){
					//no need to negate shoulder on mule see note
					setShoulder(armMessage.getShoulderSpeed());
				}else{
					leftShoulder.stopMotor();
					rightShoulder.stopMotor();				
				}
				
				if(getAveWristEncoder() < Constants.armWristLimit){
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
		}

		if (currentCommand != null) {
			currentCommand.update();
			if(currentCommand.isDone()){
				sendMessage(new Done());
			}
		}
		
		System.out.println("Arm wrist encoder value: " + getAveWristEncoder());
		//System.out.println("left wrist: " + getLeftWristEncoder());
		//System.out.println("right wrist: " + getRightWristEncoder());
		
	}
	
	//mule: one shoulder motor is cross wired so they spin the same way, no need to negate one side
	public void setLeftShoulder(double power){
		leftShoulder.set(ControlMode.PercentOutput, clamp(power, Constants.shoulderUpPowerLimit));
	}
	
	//PercentOutput is the mode for setting speed
	public void setRightShoulder(double power){
		rightShoulder.set(ControlMode.PercentOutput, clamp(power, Constants.shoulderUpPowerLimit));
	}
	
	public void setShoulder(double power){
		setLeftShoulder(power);
		setRightShoulder(power);
	}
	
	public void setShoulderBalance(double power){
		setLeftShoulder(clamp(power, Constants.shoulderBalancePowerLimit));
		setRightShoulder(clamp(power, Constants.shoulderBalancePowerLimit));
	}
	
	public void setLeftWrist(double power){
		leftWrist.set(ControlMode.PercentOutput, clamp(power, Constants.wristPowerLimit));
	}
	
	public void setRightWrist(double power){
		rightWrist.set(ControlMode.PercentOutput, clamp(-power, Constants.wristPowerLimit));
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
		return 0.5 * Math.PI * getAveShoulderEncoder() / Constants.armShoulderVertical;
	}

}

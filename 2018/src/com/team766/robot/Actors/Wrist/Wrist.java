package com.team766.robot.Actors.Wrist;

import com.team766.lib.Messages.Done;
import com.team766.lib.Messages.Stop;
import com.team766.lib.Messages.WristPIDMessage;
import com.team766.robot.Constants;
import com.team766.robot.HardwareProvider;
import com.team766.robot.Actors.Wrist.WristPIDCommand;
import interfaces.CANSpeedController;
import interfaces.SubActor;
import interfaces.CANSpeedController.ControlMode;
import lib.Actor;
import lib.ConstantsFileReader;
import lib.Message;
import lib.PIDController;

public class Wrist extends Actor {
	
	CANSpeedController leftWrist = HardwareProvider.getInstance().getLeftArmWrist();
	CANSpeedController rightWrist = HardwareProvider.getInstance().getRightArmWrist();
	
	PIDController wristPID = new PIDController(ConstantsFileReader.getInstance().get("k_wristP"), ConstantsFileReader.getInstance().get("k_wristI"), ConstantsFileReader.getInstance().get("k_wristD"), ConstantsFileReader.getInstance().get("k_wristThresh"));
	
	private boolean commandFinished;

	private double shoulderSetPoint;
	
	Message currentMessage;
	private SubActor currentCommand;
	
	public Wrist() {
		acceptableMessages = new Class[]{Stop.class, WristPIDMessage.class};
		setWristEncoders(0);
	}

	@Override
	public String toString() {
		return "Actor: Wrist";
	}

	@Override
	public void iterate() {
		if(newMessage()){
			if(currentCommand != null)
				currentCommand.stop();
			
			commandFinished = false;
			
			currentMessage = readMessage();
			if(currentMessage == null)
				return;
			
			else if(currentMessage instanceof Stop){
				System.out.println("stopping wrist");
				currentCommand = null;
				setWrist(0.0);
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
	
	public double getLeftWristEncoder(){
		return leftWrist.getSensorPosition();
	}
	
	public double getRightWristEncoder(){
		return rightWrist.getSensorPosition();
	}
	
	public double getAveWristEncoder(){
		return 0.5 * (Math.abs(getLeftWristEncoder()) + Math.abs(getRightWristEncoder()));
	}
	
	public void setWristEncoders(int position){
		leftWrist.setPosition(position);
		rightWrist.setPosition(position);
	}
	
	public void setEncoders(int position){
		setWristEncoders(position);
	}
	
	public double clamp(double value, double limit){
		limit = Math.abs(limit);
		return Math.max(Math.min(value, limit), -limit);
	}
	
	public double getWristAngleRad(double encoder){
		return 0.5 * Math.PI * getAveWristEncoder() / ConstantsFileReader.getInstance().get("armWristMiddle");
	}

	
}


package com.team766.robot.Actors.Wrist;

import com.team766.lib.Messages.Done;
import com.team766.lib.Messages.EStop;
import com.team766.lib.Messages.ShoulderPIDMessage;
import com.team766.lib.Messages.ShoulderManualMessage;
import com.team766.lib.Messages.Stop;
import com.team766.lib.Messages.WristPIDMessage;
import com.team766.lib.Messages.WristSimpleMessage;
import com.team766.robot.Constants;
import com.team766.robot.HardwareProvider;
import com.team766.robot.Actors.Shoulder.ShoulderPIDCommand;
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
		acceptableMessages = new Class[]{EStop.class, Stop.class, WristPIDMessage.class, WristSimpleMessage.class};
		setWristEncoders((int)ConstantsFileReader.getInstance().get("wristStartValue"));
	}

	@Override
	public String toString() {
		return "Actor: Wrist";
	}

	@Override
	public void iterate() {
		while(newMessage()){
			if(currentCommand != null)
				currentCommand.stop();
			
			commandFinished = false;
			
			currentMessage = readMessage();
			if(currentMessage == null)
				return;
			else if(currentMessage instanceof Stop){
				currentCommand = null;
				setWrist(0.0);
			}
			else if(currentMessage instanceof EStop){
				System.out.println("holding wrist movement");
				currentCommand = new WristPIDCommand(new WristPIDMessage(3));
			}
			else if(currentMessage instanceof WristSimpleMessage){
				System.out.println("WristSimpleMessage");
				double currPos = this.getAveWristEncoder();
				double wristPower = ConstantsFileReader.getInstance().get("wristManualPower");
				double ff = ConstantsFileReader.getInstance().get("armWristFeedForward") * Math.cos(this.getWristAngleRad(currPos));
				WristSimpleMessage wristMessage = (WristSimpleMessage)currentMessage;
				currentCommand = null;
				
				if(wristMessage.getWristDirection() == 0 && currPos < ConstantsFileReader.getInstance().get("armWristBack"))
					setWrist(wristPower + ff);
				else if(wristMessage.getWristDirection() == 1 && currPos > 50)
					setWrist(-wristPower + ff);
				else
					currentCommand = new WristPIDCommand(new WristPIDMessage(3)); //hold	
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
				sendMessage(new Done("Wrist"));
			}
			
		}

		System.out.println("left wrist encoder: " + getLeftWristEncoder() + " wrist angle: " + getWristAngleRad(getLeftWristEncoder()));
		
	}
	
	public void setLeftWrist(double power){
		leftWrist.set(ControlMode.PercentOutput, clamp(power, ConstantsFileReader.getInstance().get("wristPowerLimit")));
	}
	
	public void setRightWrist(double power){
		rightWrist.set(ControlMode.PercentOutput, clamp(-power, ConstantsFileReader.getInstance().get("wristPowerLimit")));
	}
	
	public void setWrist(double power){
		setLeftWrist(power);
		setRightWrist(power);
	}
	
	public double getLeftWristEncoder(){
		return leftWrist.getSensorPosition();
	}
	
//	public double getRightWristEncoder(){
//		return rightWrist.getSensorPosition();
//	}
	
	public double getAveWristEncoder(){
		return getLeftWristEncoder();
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


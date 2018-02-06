package com.team766.robot.Actors.Arm;

import com.team766.lib.Messages.ArmSimpleMessage;
import com.team766.robot.HardwareProvider;

import interfaces.EncoderReader;
import interfaces.SpeedController;
import interfaces.SubActor;
import lib.Actor;
import lib.Message;

public class Arm extends Actor {
	
	private boolean commandFinished;
	
	Message currentMessage;
	SubActor currentCommand;

	SpeedController leftArmShoulder = HardwareProvider.getInstance().getLeftArmShoulder();
	SpeedController rightArmShoulder = HardwareProvider.getInstance().getRightArmShoulder();
	SpeedController leftArmWrist = HardwareProvider.getInstance().getLeftArmWrist();
	SpeedController rightArmWrist = HardwareProvider.getInstance().getRightArmWrist();
	
	EncoderReader shoulderEncoder = HardwareProvider.getInstance().getShoulderEncoder();
	EncoderReader wristEncoder = HardwareProvider.getInstance().getWristEncoder();
	
	public Arm() {		
	}
	
	public void init(){
		acceptableMessages = new Class[]{ArmSimpleMessage.class};
	}
	
	public String toString() {
		return "Actor: Arm";
	}

	public void iterate() {

	}
	
	public void setLeftArmShoulder(double speed){
		leftArmShoulder.set(speed);
	}
	
	public void setRightArmShoulder(double speed){
		rightArmShoulder.set(-speed);
	}
	
	//setting both motors for arm shoulder 
	public void setArmShoulder(double speed){
		setLeftArmShoulder(speed);
		setRightArmShoulder(speed);
	}
	
	public void setLeftArmWrist(double speed){
		leftArmWrist.set(speed);
	}
	
	public void setRightArmWrist(double speed){
		rightArmWrist.set(-speed);
	}
	
	//setting both motors for arm wrist
	public void setArmWrist(double speed){
		setLeftArmWrist(speed);
		setRightArmWrist(speed);
	}
	

}

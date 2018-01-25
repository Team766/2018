package com.team766.robot.Actors.Gripper;

import com.team766.lib.Messages.GripperUpdate;
import com.team766.lib.Messages.Stop;
import com.team766.robot.HardwareProvider;

import interfaces.SolenoidController;
import interfaces.SpeedController;
import interfaces.SubActor;
import lib.Actor;
import lib.Message;

public class Gripper extends Actor{
	
	SolenoidController leftGripper = HardwareProvider.getInstance().getGripperA();
	SolenoidController rightGripper = HardwareProvider.getInstance().getGripperB();
	
	//SpeedController leftGripperMotor = HardwareProvider.getInstance().getGripperMotorA();
	//SpeedController rightGripperMotor = HardwareProvider.getInstance().getGripperMotorB();
	
	private boolean commandFinished;
	
	Message currentMessage;
	SubActor currentCommand;
	
	
	public void init(){
		acceptableMessages = new Class[]{GripperUpdate.class, Stop.class};
	}
	
	public void iterate() {
		if(newMessage()){
			if(currentCommand != null)
				currentCommand.stop();
			
			commandFinished = false;
			
			currentMessage = readMessage();
			if(currentMessage == null)
				return;
		
			if(currentMessage instanceof GripperUpdate){
				currentCommand = null;
				GripperUpdate gripperMessage = (GripperUpdate)currentMessage;
				setGripper(gripperMessage.getGrab());
			}
		}
	}
	
	public String toString() {
		return "Actor:\tGripper";
	}
	
	protected boolean getGripper1(){
		return leftGripper.get();
	}
	
	protected boolean getGripper2(){
		return rightGripper.get();
	}
	
	public void setGripper1(boolean grab){
		leftGripper.set(grab);
	}
	
	public void setGripper2(boolean grab){
		rightGripper.set(grab);
	}
	
	public void setGripper(boolean grab){
		setGripper1(grab);
		setGripper2(grab);
	}
	
	/*
	public void setLeftGripperMotor(double speed){
		leftGripperMotor.set(speed);
	}
	
	public void setRightGripperMotor(double speed){
		rightGripperMotor.set(speed);
	}
	
	public void setGripperMotor(double speed){
		setLeftGripperMotor(speed);
		setRightGripperMotor(speed);
	}
	*/

}

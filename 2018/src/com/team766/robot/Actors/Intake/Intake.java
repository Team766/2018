package com.team766.robot.Actors.Intake;

import com.team766.lib.Messages.IntakeMotorUpdate;
import com.team766.lib.Messages.EStop;
import com.team766.lib.Messages.GripperUpdateMessage;
import com.team766.lib.Messages.Stop;
import com.team766.robot.Constants;
import com.team766.robot.HardwareProvider;

import interfaces.SolenoidController;
import interfaces.SpeedController;
import interfaces.SubActor;
import lib.Actor;
import lib.DoubleSolenoid;
import lib.DoubleSolenoid.State;
import lib.Message;

public class Intake extends Actor{
	
	//SolenoidController gripper = HardwareProvider.getInstance().getGripper();
	DoubleSolenoid gripper = new DoubleSolenoid(HardwareProvider.getInstance().getGripperA(), HardwareProvider.getInstance().getGripperB());
	
	SpeedController leftIntake = HardwareProvider.getInstance().getGripperMotorA();
	SpeedController rightIntake = HardwareProvider.getInstance().getGripperMotorB();
	
	private boolean commandFinished;
	
	Message currentMessage;
	SubActor currentCommand;
	
	
	public void init(){
		acceptableMessages = new Class[]{GripperUpdateMessage.class, IntakeMotorUpdate.class, EStop.class, Stop.class};
	}
	
	public void iterate() {
		while(newMessage()){
			if(currentCommand != null){
				System.out.println("got new intake message, stopping current command");
				currentCommand.stop();
			}
			commandFinished = false;
			
			currentMessage = readMessage();
			if(currentMessage == null)
				return;
		
			else if(currentMessage instanceof Stop || currentMessage instanceof EStop){
				currentCommand = null;
				setMotors(0.0);
			}
			else if(currentMessage instanceof GripperUpdateMessage){
				currentCommand = null;
				GripperUpdateMessage gripperMessage = (GripperUpdateMessage)currentMessage;
				setGripper(gripperMessage.getOpen());
				System.out.println("set: " + gripperMessage.getOpen() + "actual: " + getGripper());
			}
			else if(currentMessage instanceof IntakeMotorUpdate){
				currentCommand = null;
				IntakeMotorUpdate intakeMessage = (IntakeMotorUpdate)currentMessage;
				setMotors(intakeMessage.getSpeed());
				System.out.println("gripper motor speed: " + intakeMessage.getSpeed());
			}
			
		}
	}
	
	public String toString() {
		return "Actor:\tIntake";
	}
	
	protected boolean getGripper(){
		return gripper.get();
	}
	
	public void setGripper(boolean grab){
		if(grab){
			gripper.set(State.Forward);
		} else{
			gripper.set(State.Backward);
		}
	}
	
	public void setLeftMotor(double speed){
		leftIntake.set(speed);
	}
	
	public void setRightMotor(double speed){
		rightIntake.set(clamp(-speed, Constants.intakePowerLimit));
	}
	
	public void setMotors(double speed){
		setLeftMotor(speed);
		setRightMotor(speed);
	}
	
	public double clamp(double value, double limit){
		limit = Math.abs(limit);
		return Math.max(Math.min(value, limit), -limit);
	}

}

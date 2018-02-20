package com.team766.robot.Actors.Intake;

import com.team766.lib.Messages.IntakeMotorUpdate;
import com.team766.lib.Messages.GripperUpdate;
import com.team766.lib.Messages.Stop;
import com.team766.robot.Constants;
import com.team766.robot.HardwareProvider;

import interfaces.SolenoidController;
import interfaces.SpeedController;
import interfaces.SubActor;
import lib.Actor;
import lib.Message;

public class Intake extends Actor{
	
	SolenoidController gripper = HardwareProvider.getInstance().getGripper();
	
	SpeedController leftIntake = HardwareProvider.getInstance().getGripperMotorA();
	SpeedController rightIntake = HardwareProvider.getInstance().getGripperMotorB();
	
	private boolean commandFinished;
	
	Message currentMessage;
	SubActor currentCommand;
	
	
	public void init(){
		acceptableMessages = new Class[]{GripperUpdate.class, IntakeMotorUpdate.class, Stop.class};
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
				setMotors(0.0);
			}
			else if(currentMessage instanceof GripperUpdate){
				currentCommand = null;
				GripperUpdate gripperMessage = (GripperUpdate)currentMessage;
				setGripper(gripperMessage.getGrab());
				System.out.println("set: " + gripperMessage.getGrab() + "actual: " + getGripper());
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
		gripper.set(grab);
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

package com.team766.robot.Actors.Climb;

import com.team766.lib.Messages.ClimberUpdate;
import com.team766.robot.HardwareProvider;
import com.team766.robot.Constants;
import interfaces.SpeedController;
import interfaces.SubActor;
import lib.Actor;
import lib.ConstantsFileReader;
import lib.Message;

public class Climber extends Actor {
	
	private boolean commandFinished;
	//SpeedController climberMotor = HardwareProvider.getInstance().getClimber();
	
	Message currentMessage;
	SubActor currentCommand;

	public void init() {
		acceptableMessages = new Class[]{ClimberUpdate.class};
	}
	
	@Override
	public void iterate() {
//		if(newMessage()) {
//			if (currentCommand == null)
//				currentCommand.stop();
//			
//			commandFinished = false;
//			
//			currentMessage = readMessage();
//			if(currentMessage instanceof ClimberUpdate) {
//				ClimberUpdate climberMessage = (ClimberUpdate)currentMessage;
//				if(climberMessage.getClimb()){
//					setClimberMotor(Constants.climberSpeed);
//				}
//				else{
//					setClimberMotor(0.0);
//				}
//			}
//		}
//		
	}
	

	@Override
	public String toString() {
		return "Actor:\tClimber";
	}
	
//	protected double getClimberMotor() {
//		return climberMotor.get();
//	}
	
//	protected void setClimberMotor(double power){
//		climberMotor.set(power);
//	}


}

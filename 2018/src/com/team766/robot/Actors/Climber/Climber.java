package com.team766.robot.Actors.Climber;

import com.team766.lib.Messages.ClimberUpdate;
import com.team766.robot.HardwareProvider;
import com.team766.robot.Constants;
import interfaces.SpeedController;
import interfaces.SubActor;
import com.team766.lib.Messages.Stop;
import lib.Actor;
import lib.ConstantsFileReader;
import lib.Message;

public class Climber extends Actor {
	
	private boolean commandFinished;
	//SpeedController climberMotor = HardwareProvider.getInstance().getClimber();
	
	Message currentMessage;
	SubActor currentCommand;

	public void init() {
		acceptableMessages = new Class[]{ClimberUpdate.class, Stop.class};
	}
	
	@Override
	public void iterate() {
		if(newMessage()) {
//			if (currentCommand == null){
//				currentCommand.stop();
//			}
			
//			commandFinished = false;
			
//			currentMessage = readMessage();
			
//			if(currentMessage instanceof Stop){
//				setClimberMotor(0.0);
//			}
//			else if(currentMessage instanceof ClimberUpdate) {
//				setClimberMotor(Constants.climberSpeed);
//			}
		}
	}

	@Override
	public String toString() {
		return "Actor:\tClimber";
	}
//	
//	protected double getClimberMotor() {
//		return climberMotor.get();
//	}
//	
//	protected void setClimberMotor(double power){
//		climberMotor.set(power);
//	}


}

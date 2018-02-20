package com.team766.robot.Actors.Arm;

import com.team766.lib.CommandBase;
import com.team766.lib.Messages.ShoulderPIDMessage;
import com.team766.robot.Constants;

import lib.Message;

public class ShoulderPIDCommand extends CommandBase{
	/*
	 * This command moves the shoulder between the lowest resting point and vertical position
	 */
	private boolean done;
	private State currentState;
	private ShoulderPIDMessage message;
	
	private enum State{
		StayVertical,
		Up,
		Down,
		Stop
	}
	
	public ShoulderPIDCommand(Message m){
		message = (ShoulderPIDMessage) m;
		done = false;
		currentState = State.Stop;
		Arm.setShoulderEncoders(0);
		
		if(message.toVertical()){
			switchState(State.Up);
			Arm.shoulderUpPID.setSetpoint(Constants.armShoulderVertical);
			Arm.shoulderBalancePID.setSetpoint(Constants.armShoulderVertical);
		} else{
			switchState(State.Down);
		}
	}

	@Override
	public void update() {
		switch (currentState){
			case Up:
				Arm.shoulderUpPID.calculate(Arm.getAveShoulderEncoder(), false);
				//Arm.setShoulder(Constants.shoulderUpPIDScale * Arm.shoulderPID.getOutput());
				System.out.println("shoulder: " + Constants.shoulderUpPIDScale * Arm.shoulderUpPID.getOutput());
				if(Arm.shoulderUpPID.isDone()){
					switchState(State.StayVertical);
				}
				break;
			case StayVertical:
				Arm.shoulderBalancePID.calculate(Arm.getAveShoulderEncoder(), false);
				break;
			case Down:
				//pid calc with different p value so need to make second arm pid? -- can just let fall if adding those piston spring things 
				if(Arm.shoulderUpPID.isDone()){
					switchState(State.Stop);
				}
				break;
			case Stop:
				stop();
				done = true;
				break;
		}
		
	}

	@Override
	public void stop() {
		Arm.setShoulder(0.0);
	}

	@Override
	public boolean isDone() {
		stop();
		return done;
	}
	
	private void switchState(State state){
		currentState = state;
	}

}

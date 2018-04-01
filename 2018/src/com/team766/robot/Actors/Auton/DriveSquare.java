package com.team766.robot.Actors.Auton;

import com.team766.lib.Messages.DriveEncoderMessage;
import com.team766.lib.Messages.DrivePIDMessage;
import com.team766.lib.Messages.DriveTimeMessage;
import com.team766.robot.Actors.Auton.AutonSelector;
import com.team766.robot.Actors.Drive.DriveTime;

import interfaces.AutonMode;
import lib.Message;

/**
 * need to rewrite since switched to absolute heading
 *
 */
public class DriveSquare implements AutonMode{
	
	private enum State{
		Start,
		DriveStraight,
		Turn,
		Done
	}
	
	private int count;
	private boolean commandDone;
	
	private State currentState = State.Start;
	
	private AutonSelector parent;
	
	public DriveSquare(AutonSelector parent){
		this.parent = parent;
		count = 0;
		commandDone = false;
	}
	
	@Override
	public void iterate() {
		switch (currentState){
			case Start:
				//switchState(State.DriveStraight, new DriveTimeMessage(2.0, 0.5, false));
				switchState(State.DriveStraight, new DrivePIDMessage(3.0, 0.0, true));
				break;
			case DriveStraight:
				if(commandDone){
					//switchState(State.Turn, new DriveTimeMessage(1.0, 0.2, true));
					switchState(State.Turn, new DrivePIDMessage(0.0, 90.0, true));
				}
				break;
			case Turn:
				if(commandDone){
					if(count < 3){
						//switchState(State.DriveStraight, new DriveTimeMessage(2.0, 0.5, false));
						switchState(State.DriveStraight, new DrivePIDMessage(3.0, 90.0, true));
						count += 1;
					} else{
						setState(State.Done);
					}
				}
				break;
			case Done:
				parent.setDone(true);
				break;
		}
	}
	
	public State getState(){
		return currentState;
	}
	
	private void setState(State s){
		currentState = s;
	}
	
	@Override
	public void driveCommandDone(boolean done){
		commandDone = done;
	}
	
	private void switchState(State state, Message message){
		setState(state);
		parent.sendMessage(message);
		commandDone = false;
	}
	
	public String getTarget(){
		return "Drive";
	}

	@Override
	public void shoulderCommandDone(boolean done) {
	}

}

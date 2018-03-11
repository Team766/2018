package com.team766.robot.Actors.Auton;


import com.team766.lib.Messages.DrivePIDMessage;
import com.team766.lib.Messages.GripperUpdate;
import com.team766.lib.Messages.ShoulderPIDMessage;
import com.team766.lib.Messages.WristPIDMessage;
import com.team766.robot.Constants;

import interfaces.AutonMode;
import lib.Message;

public class SideToSwitch implements AutonMode{
	private boolean commandDone;
	private AutonSelector parent;
	private State currentState;
	private double[] straightDist;
	private double[] turnAngle;
	private int count;
	private boolean isOppositeSide;
	private int negateAngle;
	
	private enum State{
		Start,
		//GrabCube,
		DriveStraight,
		Turn,
		RaiseArm,
		DropCube,
		Done
	}
	public SideToSwitch(AutonSelector parent, boolean isStartingRight){
		this.parent = parent;
		negateAngle = isStartingRight? 1 : -1;
		commandDone = false;
		currentState = State.Start;
		count = 0;
		isOppositeSide = true;//Constants.switch_side == 1? true : false;
		straightDist = new double[]{Constants.switchToMiddle, Constants.switchHorizontal, Constants.switchToScore};
		turnAngle = new double[]{negateAngle * Constants.switchFirstTurnAngle, negateAngle * Constants.switchSecondTurnAngle, 0.0};
		
	}

	@Override
	public void iterate() {
		switch(currentState){
			case Start:
				//setState(State.GrabCube);
				//parent.sendMessage(new GripperUpdate(true));
				//parent.sendMessage(new WristPIDMessage(2));
				double dist = isOppositeSide? straightDist[count] : Constants.switchStraight;
				switchState(State.DriveStraight, new DrivePIDMessage(dist, 0));
				
				break;
//			case GrabCube:
//				System.out.println("grabbing the cube");
//				if(commandDone){
//					double dist = isRight? straightDist[count] : Constants.switchStraight;
//					switchState(State.DriveStraight, new DrivePIDMessage(dist, 0));
//				}
//				break;
			case DriveStraight:
				System.out.println("driving for " + straightDist[count] + " feet");
				if(commandDone){
					if(isOppositeSide){
						switchState(State.Turn, new DrivePIDMessage(0.0, turnAngle[count]));
					} else{
						//switchState(State.RaiseArm, new ShoulderPIDMessage(1));
						setState(State.Done);
					}
				}
				break;
			case Turn:
				System.out.println("turning for " + turnAngle[count] + " degrees");
				if(commandDone){
					if(count < 2){
						count += 1;
						switchState(State.DriveStraight, new DrivePIDMessage(straightDist[count], 0.0));
					} else{
						switchState(State.RaiseArm, new ShoulderPIDMessage(1));
						//setState(State.Done);
					}	
				}
				break;
			case RaiseArm:
				System.out.println("raising arms to the middle");
				if(commandDone)
					//switchState(State.DropCube, new GripperUpdate(false));
					setState(State.Done);
				break;
			case DropCube:
				System.out.println("dropping the cube");
				if(commandDone)
					setState(State.Done);
				break;
			case Done:
				parent.setDone(true);
				break;
		}
		
	}

	@Override
	public void commandDone(boolean done) {
		commandDone = done;
		
	}
	
	private void switchState(State state, Message message){
		currentState = state;
		parent.sendMessage(message);
		commandDone(false);
	}
	
	private void setState(State state){
		currentState = state;
		commandDone(false);
	}

}

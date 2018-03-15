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
		DriveStraight,
		Turn,
		DriveRaiseArm,
		DropCube,
		Done
	}
	public SideToSwitch(AutonSelector parent, boolean isStartingRight){
		this.parent = parent;
		negateAngle = isStartingRight? -1 : 1;
		commandDone = false;
		currentState = State.Start;
		count = 0;
		isOppositeSide = Constants.switch_side != negateAngle ;
		if(Constants.switch_side == 1){
			straightDist = new double[]{Constants.side_switch_forward, Constants.side_switch_forward_side, Constants.side_switch_forward_side_forward};
			turnAngle = new double[]{negateAngle * Constants.switchFirstTurnAngle, negateAngle * Constants.switchSecondTurnAngle, 0.0};
		} else{
			straightDist = new double[]{Constants.side_switch_straight, Constants.side_switch_straight_side};
			turnAngle = new double[]{negateAngle * Constants.switchFirstTurnAngle, 0.0};
		}
		
	}

	@Override
	public void iterate() {
		switch(currentState){
			case Start:
				double dist = isOppositeSide? straightDist[count] : Constants.side_switch_straight;
				switchState(State.DriveStraight, new DrivePIDMessage(dist, 0));
				break;
			case DriveStraight:
				System.out.println("driving for " + straightDist[count] + " feet");
				if(commandDone){
					switchState(State.Turn, new DrivePIDMessage(0.0, turnAngle[count]));
				} 
				break;
			case Turn:
				System.out.println("turning for " + turnAngle[count] + " degrees");
				if(commandDone){
					if((count < 2 && isOppositeSide) || (count < 1 && !isOppositeSide)){
						count += 1;
						switchState(State.DriveStraight, new DrivePIDMessage(straightDist[count], 0.0));
					} else{
						setState(State.DriveRaiseArm);
						parent.sendMessage(new DrivePIDMessage(Constants.switch_final_forward, 0.0));
						parent.sendMessage(new ShoulderPIDMessage(1));
					}	
				}
				break;
			case DriveRaiseArm:
				System.out.println("raising arms to the middle");
				if(commandDone)
					switchState(State.DropCube, new GripperUpdate(true));
					setState(State.Done);
				break;
			case DropCube:
				System.out.println("dropping the cube");
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

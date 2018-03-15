package com.team766.robot.Actors.Auton;

import com.team766.lib.Messages.DrivePIDMessage;
import com.team766.lib.Messages.GripperUpdate;
import com.team766.lib.Messages.ShoulderPIDMessage;
import com.team766.lib.Messages.WristPIDMessage;
import com.team766.robot.Constants;

import interfaces.AutonMode;

public class OppositeSideToScale implements AutonMode{
	
	private boolean commandDone;
	private AutonSelector parent;
	private State currentState;
	private int count, right;
	private double driveDist[];
	
	private enum State {
		Start, Drive, Turn, DropCube, Done
	}
	
	public OppositeSideToScale(AutonSelector parent, boolean startingRight){
		System.out.println("-------------------running opposite to scale-------------------");
		right = startingRight ? 1 : -1;
		this.parent = parent;
		count = 0;
		commandDone = false;
		currentState = State.Start;
		driveDist = new double[] {Constants.side_scale_opposite_forward_side, Constants.side_scale_opposite_forward_side_forward};
	}

	@Override
	public void iterate() {
		switch(currentState){
			case Start:
				parent.sendMessage(new DrivePIDMessage(Constants.side_scale_opposite_forward, 0.0));
				switchState(State.Drive);
				break;
			case Drive:
				if(commandDone){
					if(count < 2){
						int turnDirection = (((count % 2) == 0) ? 1 : -1) * right;
						parent.sendMessage(new DrivePIDMessage(0.0, turnDirection * 90.0));
						switchState(State.Turn);
					} else{
						switchState(State.DropCube);
					}
				}
				break;
			case Turn:
				if(commandDone){
					count ++;
					if(count < 3){
						System.out.println("count: " + count);
						parent.sendMessage(new DrivePIDMessage(driveDist[count - 1], 0.0));
						switchState(State.Drive);
						if(count == 1){
							System.out.println("-------------Raising shoulder and wrist--------------");
							//parent.sendMessage(new ShoulderPIDMessage(2));
							//parent.sendMessage(new WristPIDMessage(2));
						}
					}
				}
				break;
			case DropCube:
				System.out.println("inside drop cube case \t commandDone: " + commandDone);
				if(commandDone){
					System.out.println("sent message to drop cube");
					parent.sendMessage(new GripperUpdate(true));
					switchState(State.Done);
				}
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
	
	private void switchState(State s){ 
		currentState = s;
		commandDone = false;
	}

}

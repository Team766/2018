package com.team766.robot.Actors.Auton;

import lib.Actor;

import lib.Message;

import com.team766.lib.Messages.ShoulderPIDMessage;
import com.team766.lib.Messages.Done;
import com.team766.lib.Messages.GripperUpdate;
import com.team766.lib.Messages.Stop;
import com.team766.lib.Messages.WristPIDMessage;
import com.team766.robot.Constants;
import com.team766.robot.Constants.Autons;
import com.team766.robot.Actors.Auton.DriveStraightTime;
import com.team766.robot.Actors.Auton.DriveSquare;

import interfaces.AutonMode;
import interfaces.SubActor;

public class AutonSelector extends Actor{

	public Constants.Autons autonMode;
	public AutonMode currentMode;
	private State currentState;
	private int count;
	private boolean commandDone;
	
	SubActor currentCommand;
	
	private enum State{
		Start,
		FlipWrist,
		ChangeArmHeight,
		Intake,
		Path,
		Done
	}

	public AutonSelector (Constants.Autons mode){
		autonMode = mode;
		currentState = State.Start;
		count = 0;
		acceptableMessages = new Class[]{Done.class};
		commandDone = false;
		
		switch(autonMode){
		case None:
			System.out.println("Auton: None");
			break;
		case Switch:
			System.out.println("Auton: Switch");
			break;
		case LeftToScale:
			System.out.println("Auton: left side to scale");
			if(Constants.scale_side == 1){ //right
				currentMode = new OppositeSideToScale(this, false);
			}else{
				currentMode = new SameSideToScale(this, false);
			}
			break;
		case RightToScale:
			System.out.println("Auton: right side to scale");
			if(Constants.scale_side == 1){ //right
				currentMode = new SameSideToScale(this, true);
			}else{
				currentMode = new OppositeSideToScale(this, true);
			}
			break;
		case CrossLine:
			System.out.println("Auton: CrossLine");
			currentMode = new CrossLine(this);
			break;
		case Exchange:
			System.out.println("Auton: Exchange");
			break;
		case DriveSquare:
			System.out.println("Auton: DriveSquare");
			currentMode = new DriveSquare(this);
			break;
		/*
		case DriveStraightTime:
			System.out.println("Auton: DriveStraightTime");
			currentMode = new DriveStraightTime(this);
			break;
		case DriveEncoder:
			System.out.println("Auton: DriveEncoder");
			currentMode = new DriveEncoder(this);
			break;
		case DrivePID:
			System.out.println("Auton: DrivePID");
			currentMode = new DrivePID(this);
			break;
		case ArmPID:
			System.out.println("Auton: ArmPID");
			currentMode = new ArmPID(this);
			break;
		case WristPID:
			System.out.println("Auton: WristPID");
			currentMode = new WristPID(this);
			break;
		*/
		case LeftToSwitch:
			System.out.println("Auton: LeftToSwitch");
			currentMode = new SideToSwitch(this, false);
			break;
		case RightToSwitch:
			System.out.println("Auton: RightToSwitch");
			currentMode = new SideToSwitch(this, true);
      break;
		case MiddleToSwitch:
			System.out.println("Auton: MiddleToSwitch");
			currentMode = new MiddleToSwitch(this);
      break;
		}
	}

	@Override
	public void iterate() {
		startSequence(currentMode);
	}
	
	private void stopCurrentCommand(){
		if(currentCommand != null){
			System.out.println("got new auton command, stopping current command");
			currentCommand.stop();
		}
		currentCommand = null;
	}

	@Override
	public String toString() {
		return "Actor: Auton Selector";
	}
	
	@Override
	public boolean isDone(){
		return done;
	}
	
	public void setDone(boolean done){
		this.done = done;
	}
	
	public void startSequence(AutonMode auton){
		switch(currentState){
			case Start:
				System.out.println("starting case for auton");
				this.sendMessage(new GripperUpdate(true));
				System.out.println("lifting arm");
				setState(State.ChangeArmHeight);
				this.sendMessage(new ShoulderPIDMessage(2));
				break;
			case FlipWrist:
				System.out.println("flipping the wrist");
				if(commandDone){
					if(count == 0){
						count ++;
						setState(State.ChangeArmHeight);
						this.sendMessage(new ShoulderPIDMessage(0));
						System.out.println("lowering arm");
					}
					else{
						setState(State.Path);
					}
				}
				break;
			case ChangeArmHeight:
				System.out.println("Changing Arm Height");
				if(commandDone){
					if(count == 0){
						setState(State.FlipWrist);
						this.sendMessage(new WristPIDMessage(2));
					} else{
						setState(State.Intake);
						this.sendMessage(new GripperUpdate(false));
					} 
				}
				break;
			case Intake:
				System.out.println("Intakaing the cube");
				setState(State.FlipWrist);
				this.sendMessage(new WristPIDMessage(1));
				break;
			case Path:
				System.out.println("Auton Path");
				auton.iterate();
				while (newMessage()) {
					Message currentMessage = readMessage();
					if (currentMessage == null){
						continue;
					}
					stopCurrentCommand();
					if(currentMessage instanceof Done){
						System.out.println("setting command to done ");
						auton.commandDone(true);
					}
				}
				if (currentCommand != null) {
					currentCommand.update();
				}
				break;
				
		}
	}
	
	
	private void setState(State state){
		currentState = state;
		commandDone = false;
	}

}

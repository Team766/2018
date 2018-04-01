package com.team766.robot.Actors.Auton;

import lib.Actor;

import lib.Message;

import com.team766.lib.Messages.ShoulderPIDMessage;
import com.team766.lib.Messages.Done;
import com.team766.lib.Messages.GripperUpdateMessage;
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
	private boolean shoulderCommandDone, wristCommandDone, startSeq;
	
	SubActor currentCommand;
	
	private enum State{
		Start,
		ArmUp,
		LowerShoulder,
		AutonPath
	}

	public AutonSelector (Constants.Autons mode){
		autonMode = mode;
		currentState = State.Start;
		count = 0;
		acceptableMessages = new Class[]{Done.class};
		shoulderCommandDone = false;
		wristCommandDone = false;
		startSeq = Constants.startSeq;
		
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
		case DriveSquare:
			System.out.println("Auton: DriveSquare");
			currentMode = new DriveSquare(this);
			break;
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
		}
	}
	
	@Override
	public void init(){
	}

	@Override
	public void iterate() {
		if(startSeq){
			startSequence(currentMode);
		} else{
			currentMode.iterate();
		}
		
		while (newMessage()) {
			Message currentMessage = readMessage();
			if (currentMessage == null){
				continue;
			}
			stopCurrentCommand();
			if(currentMessage instanceof Done){
				Done doneMessage = (Done) currentMessage;
				if(doneMessage.getSender() == "Drive"){
					System.out.println("setting drive command to done");
					currentMode.driveCommandDone(true);
				} 
				if(doneMessage.getSender() == "Shoulder"){
					System.out.println("shoulder command is done");
					shoulderCommandDone = true;
					currentMode.shoulderCommandDone(true);
				}
				else if(doneMessage.getSender() == "Wrist"){
					System.out.println("wrist command is done");
					wristCommandDone = true;
				}
			}
		}
		if (currentCommand != null) {
			System.out.println("Auton Selector has a current command....it shouldn't");
			currentCommand.update();
		}
		
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
				System.out.println("starting case for auton: sent message to close gripper, raise intake, lift shoulder");
				sendMessage(new WristPIDMessage(2)); //intake position
				sendMessage(new ShoulderPIDMessage(1)); //arm up to middle
				sendMessage(new GripperUpdateMessage(false)); //close
				setState(State.ArmUp);
				break;
			case ArmUp:
				System.out.println("raising arm and wrist");
				if(wristCommandDone && shoulderCommandDone){
					sendMessage(new ShoulderPIDMessage(0));
					setState(State.LowerShoulder);
				}
				break;
			case LowerShoulder:
				System.out.println("lowering shoulder!");
				if(shoulderCommandDone){
					setState(State.AutonPath);
				}
				break;
			case AutonPath:
				System.out.println("Auton Path");
				auton.iterate();
				break;
				/*
			case EndDrive:
				break;
			case End:
				System.out.println("ending auton position");
				if(commandDone)
					sendMessage(new ShoulderPIDMessage(0));
				break;
				*/		
		}
	}
	
	private void setState(State state){
		currentState = state;
		
		wristCommandDone = false;
		shoulderCommandDone = false;
	}

}

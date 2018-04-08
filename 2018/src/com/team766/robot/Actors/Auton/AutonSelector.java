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
	private boolean armCommandDone, startSeq;
	
	SubActor currentCommand;
	
	private enum State{
		Start,
		FlipWrist,
		ChangeArmHeight,
		Intake,
		Path,
		EndDrive,
		End
	}

	public AutonSelector (Constants.Autons mode){
		autonMode = mode;
		currentState = State.Start;
		count = 0;
		acceptableMessages = new Class[]{Done.class};
		armCommandDone = false;
		startSeq = true;
		
		switch(autonMode){
		case None:
			System.out.println("Auton: None");
			startSeq = false;
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
			startSeq = false;
			break;
		case DriveSquare:
			System.out.println("Auton: DriveSquare");
			currentMode = new DriveSquare(this);
			break;
		case LeftToSwitch:
			System.out.println("Auton: LeftToSwitch");
			currentMode = new SideToSwitch(this, false);
			startSeq = false;
			break;
		case RightToSwitch:
			System.out.println("Auton: RightToSwitch");
			currentMode = new SideToSwitch(this, true);
			startSeq = false;
			break;
		case MiddleToSwitch:
			System.out.println("Auton: MiddleToSwitch");
			currentMode = new MiddleToSwitch(this);
			startSeq = false;
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
		sendMessage(new WristPIDMessage(2)); //intake position
		sendMessage(new GripperUpdateMessage(false)); //close
		sendMessage(new ShoulderPIDMessage(1)); //switch height
	}

	@Override
	public void iterate() {
		
		System.out.println("iterating in auton selector mode = " + currentMode.getTarget());
		
		currentMode.iterate();
		
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
				} else{
					System.out.println("arm command is done");
					armCommandDone = true;
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
	
	private void startSequence(AutonMode auton){
		switch(currentState){
			case Start:
				System.out.println("starting case for auton, sent message to open gripper and raise intake");
				sendMessage(new GripperUpdateMessage(true)); //open?
				sendMessage(new WristPIDMessage(0));
				setState(State.ChangeArmHeight);
				break;
			case FlipWrist:
				System.out.println("flipping the wrist");
				if(armCommandDone){
					if(count == 0){
						count ++;
						setState(State.ChangeArmHeight);
						sendMessage(new ShoulderPIDMessage(0));
						System.out.println("lowering arm");
					}
					else{
						setState(State.Path);
					}
				}
				break;
			case ChangeArmHeight:
				System.out.println("Changing Arm Height");
				if(armCommandDone){
					if(count == 0){
						setState(State.FlipWrist);
						sendMessage(new WristPIDMessage(2));
					} else{
						setState(State.Intake);
						sendMessage(new GripperUpdateMessage(false));
					} 
				}
				break;
			case Intake:
				System.out.println("Intakaing the cube");
				setState(State.FlipWrist);
				sendMessage(new WristPIDMessage(1));
				break;
			case Path:
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
		armCommandDone = false;
	}

}

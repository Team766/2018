package com.team766.robot.Actors.Auton;

import lib.Actor;
import lib.Message;

import com.team766.lib.Messages.ArmStageMessage;
import com.team766.lib.Messages.DriveTimeMessage;
import com.team766.lib.Messages.DriveUpdate;
import com.team766.lib.Messages.ShoulderPIDMessage;
import com.team766.lib.Messages.Done;
import com.team766.lib.Messages.Stop;
import com.team766.robot.Constants;
import com.team766.robot.Constants.Autons;
import com.team766.robot.Actors.Auton.DriveStraightTime;
import com.team766.robot.Actors.Auton.DriveSquare;

import interfaces.AutonMode;
import interfaces.SubActor;

public class AutonSelector extends Actor{

	public Constants.Autons autonMode;
	public AutonMode currentMode;
	
	SubActor currentCommand;

	public AutonSelector (Constants.Autons mode){
		autonMode = mode;
		
		acceptableMessages = new Class[]{Done.class};
		
		switch(autonMode){
		case None:
			System.out.println("Auton: None");
			break;
		case Switch:
			System.out.println("Auton: Switch");
			break;
		case Scale:
			System.out.println("Auton: Scale");
			currentMode = new Scale(this);
			break;
		case CrossLine:
			System.out.println("Auton: CrossLine");
			break;
		case Exchange:
			System.out.println("Auton: Exchange");
			break;
		case DriveStraightTime:
			System.out.println("Auton: DriveStraightTime");
			currentMode = new DriveStraightTime(this);
			break;
		case DriveSquare:
			System.out.println("Auton: DriveSquare");
			currentMode = new DriveSquare(this);
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
		}
	}

	@Override
	public void iterate() {
		currentMode.iterate();
		
		while (newMessage()) {
			Message currentMessage = readMessage();
			if (currentMessage == null){
				continue;
			}
			stopCurrentCommand();
			if(currentMessage instanceof Done){
				System.out.println("setting command to done ");
				currentMode.commandDone(true);
			}
		}
		if (currentCommand != null) {
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
}

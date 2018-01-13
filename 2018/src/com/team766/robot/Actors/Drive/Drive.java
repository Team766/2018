package src.com.team766.robot.Actors.Drive;

import com.team766.lib.Messages.DriveTimeMessage;
import com.team766.robot.HardwareProvider;
import com.team766.robot.Actors.Drive.DriveStraightTime;

import interfaces.SpeedController;
import interfaces.SubActor;
import lib.Actor;
import lib.Message;

public class Drive extends Actor{

	SpeedController leftDriveA = HardwareProvider.getInstance().getLeftDriveA();
	SpeedController leftDriveB = HardwareProvider.getInstance().getLeftDriveB();
	SpeedController rightDriveA = HardwareProvider.getInstance().getRightDriveA();
	SpeedController rightDriveB = HardwareProvider.getInstance().getRightDriveB();

	private boolean commandFinished;
	Message currentMessage;
	
	SubActor currentCommand;
	
	
	
	
	public void init() {
		acceptableMessages = new Class[]{DriveTimeMessage.class};
		commandFinished = false;
	}
	
	
	public void run() {
		while(enabled){	
			iterate();
			sleep();
		}
		
		//Stop all processes
		stopCurrentCommand();
	}
	
	public void iterate() {
		if (newMessage()){
			stopCurrentCommand();
			commandFinished = false; 
			currentMessage = readMessage();
			if (currentMessage == null){
				return;
			}
			if (currentMessage instanceof DriveTimeMessage){
				currentCommand = new DriveStraightTime(currentMessage);
			}
		}
		
	}

	public String toString() {
		return null;
	}

	public void step() {
		
	}

	public void setRight(double power){
		rightDriveA.set(power);
		rightDriveB.set(power);
	}
	
	public void setLeft(double power){
		leftDriveA.set(power);
		leftDriveB.set(power);
	}
		
	public void setDrive(double power){
		setLeft(power);
		setRight(power);
	}
	
	private void stopCurrentCommand(){
		if(currentCommand != null){
			currentCommand.stop();
		}
		currentCommand = null;
		commandFinished = true;
	}
		
		
}
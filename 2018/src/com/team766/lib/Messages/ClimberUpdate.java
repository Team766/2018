package com.team766.lib.Messages;

import lib.Message;

public class ClimberUpdate implements Message {
	//true is forward, false is backward
	private boolean climb;
	
	public ClimberUpdate(boolean climb) {
		this.climb = climb;
	}
	
	public boolean getClimb() {
		return climb;
	}
	
	public String toString() {
		return "Message:\tUpdate Climber";
	}
}

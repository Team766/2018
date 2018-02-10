package com.team766.lib.Messages;

import lib.Message;

public class ShifterUpdate implements Message {

	boolean isHighGear;
	
	public ShifterUpdate(boolean isHighGear){
		this.isHighGear = isHighGear;
		System.out.println("Shifter gets the message.");
	}
	
	public boolean getHighGear(){
		return isHighGear;

	}
	
}

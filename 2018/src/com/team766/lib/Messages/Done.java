package com.team766.lib.Messages;

import lib.Message;

public class Done implements Message{
	
	private String actor;
	
	public Done(String actor){
		this.actor = actor;
	}
	
	public String getSender(){
		return actor;
	}

}

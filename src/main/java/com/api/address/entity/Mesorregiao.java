package com.api.address.entity;

import com.google.gson.annotations.SerializedName;

public class Mesorregiao {
	private static final long serialVersionUID = 1L;
	
    @SerializedName("UF") 
    private UF uF;
    
	// Getters and Setters

	public UF getuF() {
		return uF;
	}

	public void setuF(UF uF) {
		this.uF = uF;
	}
}

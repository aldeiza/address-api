package com.api.address.entity;

public class Microrregiao {
	@Override
	public String toString() {
		return "Microrregiao [mesorregiao=" + mesorregiao + "]";
	}

	private static final long serialVersionUID = 1L;

	private Mesorregiao mesorregiao;

	// Getters and Setters

	public Mesorregiao getMesorregiao() {
		return mesorregiao;
	}

	public void setMesorregiao(Mesorregiao mesorregiao) {
		this.mesorregiao = mesorregiao;
	}
}

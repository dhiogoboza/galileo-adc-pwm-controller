/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.ufrn.sd.project2;

/**
 *
 * @author dhiogoboza
 */
public class SocData {
	private boolean on;
	private int ldrValue;
	private int pwmValue;

	public SocData(int ldrValue, int pwmValue) {
		this.ldrValue = ldrValue;
		this.pwmValue = pwmValue;
	}
	
	public int getLdrValue() {
		return ldrValue;
	}

	public void setLdrValue(int ldrValue) {
		this.ldrValue = ldrValue;
	}

	public int getPwmValue() {
		return pwmValue;
	}

	public void setPwmValue(int pwmValue) {
		this.pwmValue = pwmValue;
	}

	public boolean isOn() {
		return on;
	}

	public void setOn(boolean on) {
		this.on = on;
	}
	
}

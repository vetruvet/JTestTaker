package com.vetruvet.jtesttaker.shared.questions;

import java.awt.event.ActionListener;

import javax.swing.JPanel;

public class ShortTextQuestion extends Question {
	private static final long serialVersionUID = 4428422656882151511L;
	
	private boolean ambig = false;
	private String regexMatch = null;
	
	public String getRegex() {
		return regexMatch;
	}
	
	public void setAmbiguous(boolean ambiguous) {
		this.ambig = ambiguous;
	}
	
	public void setRegex(String regex) {
		this.regexMatch = regex;
	}
	
	@Override
	public boolean isAmbiguous() {
		return ambig;
	}

	@Override
	public boolean isCorrect() {
		if (isAmbiguous()) return true;
		// TODO Auto-generated method stub
		return false; 
	}

	@Override
	protected void render() {
		// TODO Auto-generated method stub
	}

	@Override
	public String getBodyString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void parseBodyString(String body) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected ActionListener renderTypeOptionsPanel(JPanel container) {
		// TODO Auto-generated method stub
		return null;
	}
}

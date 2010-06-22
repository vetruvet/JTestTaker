package com.vetruvet.jtesttaker.shared.questions;

import java.awt.event.ActionListener;

import javax.swing.JPanel;

public class LongTextQuestion extends Question {
	private static final long serialVersionUID = -1123892918135408192L;
	
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

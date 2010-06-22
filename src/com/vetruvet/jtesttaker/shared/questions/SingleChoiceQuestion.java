package com.vetruvet.jtesttaker.shared.questions;

import java.awt.event.ActionListener;

import javax.swing.JPanel;

public class SingleChoiceQuestion extends Question {
	private static final long serialVersionUID = -2029332786875748222L;
	
	private String[] choices = null;
	private int correctChoice = -1;
	
	public String[] getChoices() {
		return choices;
	}
	
	public String getChoice(int index) {
		return choices[index];
	}
	
	public int getCorrectChoice() {
		return correctChoice;
	}
	
	public void setChoices(String[] choices) {
		this.choices = choices;
	}
	
	public void setChoice(int index, String choice) {
		this.choices[index] = choice;
	}
	
	public void setCorrectChoice(int choice) {
		correctChoice = choice;
	}
	
	@Override
	public boolean isAmbiguous() {
		return false;
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

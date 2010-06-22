package com.vetruvet.jtesttaker.shared.questions;

import java.awt.event.ActionListener;

import javax.swing.JPanel;

public class MultipleChoiceQuestion extends Question {
	private static final long serialVersionUID = -7029071380083022959L;

	private String[] choices = null;
	private int[] correctChoices = null;
	
	public String[] getChoices() {
		return choices;
	}
	
	public String getChoice(int index) {
		return choices[index];
	}
	
	public int[] getCorrectChoices() {
		return correctChoices;
	}
	
	public void setChoices(String[] choices) {
		this.choices = choices;
	}
	
	public void setChoice(int index, String choice) {
		this.choices[index] = choice;
	}
	
	public void setCorrectChoice(int[] choices) {
		correctChoices = choices;
	}

	@Override
	public boolean isAmbiguous() {
		return false;
	}

	@Override
	public boolean isCorrect() {
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

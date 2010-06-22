package com.vetruvet.jtesttaker.shared.questions;

import java.awt.event.ActionListener;

import javax.swing.JPanel;

public class MatchingQuestion extends Question {
	private static final long serialVersionUID = 5458808351019393526L;
	
	private String[] leftChoices = null;
	private String[] rightChoices = null;
	private int[][] correctMatches = null;
	
	public String[] getLeftChoices() {
		return leftChoices;
	}
	
	public String getLeftChoice(int index) {
		return leftChoices[index];
	}
	
	public String[] getRightChoices() {
		return rightChoices;
	}
	
	public String getRightChoice(int index) {
		return rightChoices[index];
	}
	
	public int[][] getCorectMatches() {
		return correctMatches;
	}
	
	public void setLeftChoices(String[] choices) {
		leftChoices = choices;
	}
	
	public void setLeftChoice(int index, String choice) {
		leftChoices[index] = choice;
	}
	
	public void setRightChoices(String[] choices) {
		rightChoices = choices;
	}
	
	public void setRightChoice(int index, String choice) {
		rightChoices[index] = choice;
	}
	
	public void setCorrectMatches(int[][] matches) {
		correctMatches = matches;
	}

	@Override
	public boolean isAmbiguous() {
		// TODO Auto-generated method stub
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

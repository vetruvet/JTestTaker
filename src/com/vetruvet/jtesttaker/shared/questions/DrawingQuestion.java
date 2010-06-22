package com.vetruvet.jtesttaker.shared.questions;

import java.awt.event.ActionListener;

import javax.swing.JPanel;

public class DrawingQuestion extends Question {
	private static final long serialVersionUID = -2278092948192119658L;

	@Override
	public boolean isAmbiguous() {
		return true;
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

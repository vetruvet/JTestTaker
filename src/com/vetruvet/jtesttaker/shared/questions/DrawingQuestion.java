package com.vetruvet.jtesttaker.shared.questions;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class DrawingQuestion extends Question {
	private static final long serialVersionUID = -2278092948192119658L;
	
	private int areaWidth = 500;
	private int areaHeight = 500;
	
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
		// TODO Render question (on client)
	}

	@Override
	public String getBodyString() {
		return (areaWidth + "x" + areaHeight);
	}

	@Override
	public void parseBodyString(String body) {
		int whSep = body.indexOf("x");
		if (whSep != -1) {
			areaWidth = Integer.parseInt(body.substring(0, whSep));
			areaHeight = Integer.parseInt(body.substring(whSep + 1));
		}
	}

	@Override
	protected ActionListener renderTypeOptionsPanel(JPanel container) {
		container.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Drawing Options"));
		container.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.weightx = c.weighty = 0.0;
		c.gridheight = c.gridwidth = 1;
		c.insets = new Insets(3, 3, 2, 3);
		c.anchor = GridBagConstraints.LINE_START;
		
		c.gridy = 0;
		JLabel areaWidthLbl = new JLabel("Drawing Area Width");
		areaWidthLbl.setToolTipText("Enter the width of the drawing area (in pixels)");
		container.add(areaWidthLbl, c);
		
		c.gridy = 1;
		JLabel areaHeightLbl = new JLabel("Drawing Area Height");
		areaHeightLbl.setToolTipText("Enter the height of the drawing area (in pixels)");
		container.add(areaHeightLbl, c);
		
		c.gridx = 1;
		c.weightx = c.weighty = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		
		c.gridy = 0;
		final JTextField areaWidthFld = new JTextField(areaWidth + "");
		container.add(areaWidthFld, c);
		
		c.gridy = 1;
		final JTextField areaHeightFld = new JTextField(areaHeight + "");
		container.add(areaHeightFld, c);
		
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				areaWidth = Integer.parseInt(areaWidthFld.getText());
				areaHeight = Integer.parseInt(areaHeightFld.getText());
			}
		};
	}
}

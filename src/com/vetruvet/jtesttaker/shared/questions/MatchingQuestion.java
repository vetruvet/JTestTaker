package com.vetruvet.jtesttaker.shared.questions;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class MatchingQuestion extends Question {
	private static final long serialVersionUID = 5458808351019393526L;
	
	private String[] leftChoices = null;
	private String[] rightChoices = null;
	private int[][] correctMatches = null;
	private int numberStyle = 0;
	
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
		return false;
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
		String out = "";
		for (int q = 0; q < leftChoices.length; q++) {
			out += leftChoices[q];
			out += "\n";
		}
		out += "\n";
		for (int q = 0; q < rightChoices.length; q++) {
			out += rightChoices[q];
			out += "\n";
		}
		for (int q = 0; q < correctMatches.length; q++) {
			out += correctMatches[q][0];
			out += "-";
			out += correctMatches[q][1];
			out += ",";
		}
		out += "\n";
		out += numberStyle;
		return out;
	}

	@Override
	public void parseBodyString(String body) {
		String[] bodyParts = body.split("\n");
		
		int q;
		ArrayList<String> choices = new ArrayList<String>();
		for (q = 0; q < bodyParts.length - 2; q++) {
			if (bodyParts[q].isEmpty()) {
				leftChoices = choices.toArray(new String[0]);
				choices.clear();
			}
			else choices.add(bodyParts[q]);
		}
		rightChoices = choices.toArray(new String[0]);
		
		String[] matchesParts = bodyParts[q++].split(",");
		if (matchesParts[0].isEmpty()) matchesParts = new String[0];
		correctMatches = new int[matchesParts.length][2];
		for (int w = 0; w < matchesParts.length; w++) {
			if (matchesParts[w].isEmpty()) continue;
			String[] matchParts = matchesParts[w].split("-");
			if (matchParts.length != 2) continue;
			
			correctMatches[w][0] = matchParts[0].isEmpty() ? 0 : Integer.parseInt(matchParts[0]);
			correctMatches[w][1] = matchParts[1].isEmpty() ? 0 : Integer.parseInt(matchParts[1]);
		}
		
		numberStyle = Integer.parseInt(bodyParts[q++]);
	}

	@Override
	protected ActionListener renderTypeOptionsPanel(JPanel container) {
		container.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Multiple-Choice Options"));
		container.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.weightx = c.weighty = 0.0;
		c.gridheight = c.gridwidth = 1;
		c.insets = new Insets(3, 3, 2, 3);
		c.anchor = GridBagConstraints.LINE_START;
		
		c.gridy = 0;
		JLabel numStyleLbl = new JLabel("Numbering Style");
		numStyleLbl.setToolTipText("Select the numbering style for options");
		container.add(numStyleLbl, c);
		
		c.gridy = 1;
		JLabel leftLbl = new JLabel("Items on the left");
		leftLbl.setToolTipText("List the items on the left side, one on every line");
		container.add(leftLbl, c);
		
		c.gridy = 2;
		JLabel rightLbl = new JLabel("Items on the right");
		rightLbl.setToolTipText("List the items on the right side, one on every line");
		container.add(rightLbl, c);
		
		c.gridy = 3;
		JLabel answerLbl = new JLabel("Correct Answer(s)");
		answerLbl.setToolTipText("<html>Enter the correct combinations of items<br />" +
				"Each combination should be of the form Left-Right<br />" +
				"Separate combinations with commas.");
		container.add(answerLbl, c);
		
		c.gridx = 1;
		c.weightx = c.weighty = 1.0;
		
		c.gridy = 0;
		final JComboBox numStyleBox = new JComboBox(NUMBER_STYLES);
		numStyleBox.setSelectedIndex(numberStyle);
		container.add(numStyleBox, c);
		
		c.gridy = 1;
		c.fill = GridBagConstraints.BOTH;
		final JTextArea leftArea = new JTextArea(5, 15);
		if (leftChoices != null) {
			for (int q = 0; q < leftChoices.length; q++) {
				leftArea.setText(leftArea.getText() + leftChoices[q] + 
						(q < leftChoices.length - 1 ? "\n" : ""));
			}
		}
		container.add(leftArea, c);
		
		c.gridy = 2;
		final JTextArea rightArea = new JTextArea(5, 15);
		if (leftChoices != null) {
			for (int q = 0; q < rightChoices.length; q++) {
				rightArea.setText(rightArea.getText() + rightChoices[q] + 
						(q < rightChoices.length - 1 ? "\n" : ""));
			}
		}
		container.add(rightArea, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		
		c.gridy = 3;
		final JTextField matchesFld = new JTextField();
		if (correctMatches != null) {
			for (int q = 0; q < correctMatches.length; q++) {
				matchesFld.setText(matchesFld.getText() + (correctMatches[q][0] + 1) + 
						"-" + (correctMatches[q][1] + 1) +
						(q < correctMatches.length - 1 ? "," : ""));
			}
		}
		container.add(matchesFld, c);
		
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				numberStyle = numStyleBox.getSelectedIndex();
				leftChoices = leftArea.getText().split("\n");
				rightChoices = rightArea.getText().split("\n");
				
				String[] matchesParts = matchesFld.getText().split(",");
				if (matchesParts[0].isEmpty()) matchesParts = new String[0];
				correctMatches = new int[matchesParts.length][2];
				for (int q = 0; q < matchesParts.length; q++) {
					if (matchesParts[q].isEmpty()) continue;
					String[] matchParts = matchesParts[q].split("-");
					if (matchParts.length != 2) continue;
					
					correctMatches[q][0] = matchParts[0].isEmpty() ? 0 : Integer.parseInt(matchParts[0]) - 1;
					correctMatches[q][1] = matchParts[1].isEmpty() ? 0 : Integer.parseInt(matchParts[1]) - 1;
				}
			}
		};
	}
}

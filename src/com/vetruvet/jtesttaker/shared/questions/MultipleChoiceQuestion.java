package com.vetruvet.jtesttaker.shared.questions;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class MultipleChoiceQuestion extends Question {
	private static final long serialVersionUID = -7029071380083022959L;
	
	private static final String[] NUM_ANS_STYLES = new String[] {
		"[None]", "All that apply", "Up to X", "No more than X", "May choose more than 1"
	};
	
	private String[] choices = null;
	private int[] correctChoices = null;
	private int numberStyle = 0;
	private int nAnsStyle = 0;
	
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
		// TODO Render question (on client)
	}

	@Override
	public String getBodyString() {
		String out = "";
		for (int q = 0; q < choices.length; q++) {
			out += choices[q];
			out += "\n";
		}
		for (int q = 0; q < correctChoices.length; q++) {
			out += correctChoices[q];
			out += ",";
		}
		out += "\n";
		out += numberStyle;
		out += "\n";
		out += nAnsStyle;
		return out;
	}

	@Override
	public void parseBodyString(String body) {
		String[] bodyParts = body.split("\n");
		
		int q;
		choices = new String[bodyParts.length - 3];
		for (q = 0; q < choices.length; q++) {
			choices[q] = bodyParts[q];
		}
		
		String[] correctParts = bodyParts[q++].split(",");
		if (correctParts[0].isEmpty()) correctParts = new String[0];
		correctChoices = new int[correctParts.length];
		for (int w = 0; w < correctParts.length; w++) correctChoices[w] = Integer.parseInt(correctParts[w]);
		
		numberStyle = Integer.parseInt(bodyParts[q++]);
		nAnsStyle = Integer.parseInt(bodyParts[q++]);
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
		JLabel answersLbl = new JLabel("Possible Answers");
		answersLbl.setToolTipText("List the possible answers, one on every line");
		container.add(answersLbl, c);
		
		c.gridy = 2;
		JLabel answerLbl = new JLabel("Correct Answer(s)");
		answerLbl.setToolTipText("<html>Enter the number(s) of the correct answer(s)<br />" +
				"Separate with commas if more than one correct answer.</html>");
		container.add(answerLbl, c);
		
		c.gridy = 3;
		JLabel nAnsStyleLbl = new JLabel("Select how many?");
		nAnsStyleLbl.setToolTipText("This is be displayed as 'Select all that apply' or 'Select up to 2'");
		container.add(nAnsStyleLbl, c);
		
		c.gridx = 1;
		c.weightx = c.weighty = 1.0;
		
		c.gridy = 0;
		final JComboBox numStyleBox = new JComboBox(NUMBER_STYLES);
		numStyleBox.setSelectedIndex(numberStyle);
		container.add(numStyleBox, c);
		
		c.gridy = 1;
		c.fill = GridBagConstraints.BOTH;
		final JTextArea answersArea = new JTextArea(5, 15);
		if (choices != null) {
			for (int q = 0; q < choices.length; q++) {
				answersArea.setText(answersArea.getText() + choices[q] + 
						(q < choices.length - 1 ? "\n" : ""));
			}
		}
		container.add(answersArea, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		
		c.gridy = 2;
		final JTextField answerFld = new JTextField();
		if (correctChoices != null) {
			for (int q = 0; q < correctChoices.length; q++) {
				answerFld.setText(answerFld.getText() + (correctChoices[q] + 1) + 
						(q < correctChoices.length - 1 ? "," : ""));
			}
		}
		container.add(answerFld, c);
		
		c.gridy = 3;
		c.fill = GridBagConstraints.NONE;
		final JComboBox nAnsStyleBox = new JComboBox(NUM_ANS_STYLES);
		nAnsStyleBox.setEnabled(false);
		nAnsStyleBox.setSelectedIndex(nAnsStyle);
		container.add(nAnsStyleBox, c);
		
		answerFld.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e) {
				nAnsStyleBox.setEnabled(answerFld.getText().split(",").length > 1);
			}
			@Override
			public void insertUpdate(DocumentEvent e) {
				nAnsStyleBox.setEnabled(answerFld.getText().split(",").length > 1);
			}
			@Override
			public void changedUpdate(DocumentEvent e) { }
		});
		
		return new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				nAnsStyle = nAnsStyleBox.getSelectedIndex();
				numberStyle = numStyleBox.getSelectedIndex();
				choices = answersArea.getText().split("\n");
				
				String[] answerParts = answerFld.getText().split(",");
				if (answerParts[0].isEmpty()) answerParts = new String[0];
				correctChoices = new int[answerParts.length];
				for (int q = 0; q < answerParts.length; q++) 
					correctChoices[q] = answerParts[q].isEmpty() ? 0 : Integer.parseInt(answerParts[q]) - 1;
			}
		};
	}
}

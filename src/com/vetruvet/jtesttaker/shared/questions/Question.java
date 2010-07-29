package com.vetruvet.jtesttaker.shared.questions;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public abstract class Question extends JPanel {
	private static final long serialVersionUID = -2903143552848826449L;
	
	protected static final String[] NUMBER_STYLES = new String[] {
		"[None]", "A B C", "1 2 3", 
		"a b c", "I II III", "i ii ii"
	};
	
	private ArrayList<String> attachIDs = new ArrayList<String>();
	
	private String id = null;
	private String title = null;
	private String qText = null;

	private int time = 0;
	
	public Question(String id, String title, String qText) {
		this.id = id;
		this.title = title;
		this.qText = qText;
	}
	
	public Question(String id, String title, String qText, int time) {
		this(id, title, qText);
		this.time = time;
	}
	
	public Question() { }
	
	public String getID() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getQuestionText() {
		return qText;
	}

	public int getAllowedTime() {
		return time;
	}
	
	public String getAttachID(int index) {
		return attachIDs.get(index);
	}
	
	public String[] getAttachments() {
		return attachIDs.toArray(new String[0]);
	}
	
	public void addAttachment(String id) {
		attachIDs.add(id);
	}
	
	public void insertAttachment(String id, int index) {
		attachIDs.add(index, id);
	}
	
	public void removeAttachment(String id) {
		attachIDs.remove(id);
	}
	
	public void removeAttachment(int index) {
		attachIDs.remove(index);
	}
	
	public void setID(String id) {
		this.id = id;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setQuestionText(String text) {
		this.qText = text;
	}

	public void setAllowedTime(int time) {
		this.time = time;
	}
	
	@Override
	public String toString() {
		return (id + " (" + title + ")");
	}
	
	public void showOptionsDialog(Component parent) {
		final JDialog optsDlg = new JDialog((Frame) null, id == null ? "New Question" : "Editing Question", true);
		
		JPanel optionsRoot = new JPanel(new BorderLayout(3, 3));
		
		JPanel commonRoot = new JPanel(new GridBagLayout());
		commonRoot.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Common Options"));
		optionsRoot.add(commonRoot, BorderLayout.PAGE_START);
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.weightx = c.weighty = 0.0;
		c.gridheight = c.gridwidth = 1;
		c.insets = new Insets(3, 3, 2, 3);
		c.anchor = GridBagConstraints.LINE_START;
		
		c.gridy = 0;
		JLabel idLbl = new JLabel("Question ID");
		idLbl.setToolTipText("Uniquely Identifies the question in the test.");
		commonRoot.add(idLbl, c);
		
		c.gridy = 1;
		JLabel titleLbl = new JLabel("Question Title");
		titleLbl.setToolTipText("A Name for the question");
		commonRoot.add(titleLbl, c);
		
		c.gridy = 2;
		JLabel queryLbl = new JLabel("Question");
		titleLbl.setToolTipText("The question being asked");
		commonRoot.add(queryLbl, c);
		
		c.gridx = 1;
		c.weightx = c.weighty = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		
		c.gridy = 0;
		final JTextField idFld = new JTextField(id == null ? "new_question" : id, 15);
		commonRoot.add(idFld, c);
		
		c.gridy = 1;
		final JTextField titleFld = new JTextField(title == null ? "New Question" : title, 15);
		commonRoot.add(titleFld, c);
		
		c.gridy = 2;
		final JTextArea queryFld = new JTextArea(qText == null ? "" : qText, 5, 15);
		commonRoot.add(queryFld, c);
		
		JPanel typeOptsRoot = new JPanel();
		optionsRoot.add(typeOptsRoot, BorderLayout.CENTER);
		
		ActionListener typeApply = renderTypeOptionsPanel(typeOptsRoot);
		ActionListener commonApply = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setID(idFld.getText());
				setTitle(titleFld.getText());
				setQuestionText(queryFld.getText());
			}
		};
		
		JPanel butPanel = new JPanel(new FlowLayout());
		optionsRoot.add(butPanel, BorderLayout.PAGE_END);
		
		JButton okButton = new JButton("OK");
		okButton.setDefaultCapable(true);
		okButton.addActionListener(typeApply);
		okButton.addActionListener(commonApply);
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				optsDlg.setVisible(false);
				optsDlg.dispose();
			}
		});
		optsDlg.getRootPane().setDefaultButton(okButton);
		butPanel.add(okButton);
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				optsDlg.setVisible(false);
				optsDlg.dispose();
			}
		});
		butPanel.add(cancelButton);
		
		JButton applyButton = new JButton("Apply");
		applyButton.addActionListener(typeApply);
		applyButton.addActionListener(commonApply);
		butPanel.add(applyButton);
		
		optsDlg.setContentPane(optionsRoot);
		optsDlg.pack();
		optsDlg.setResizable(true);
		optsDlg.setLocationRelativeTo(parent);
		optsDlg.setVisible(true);
	}
	protected abstract ActionListener renderTypeOptionsPanel(JPanel container);
	
	protected abstract void render();
	public abstract String getBodyString();
	public abstract void parseBodyString(String body);
	
	public abstract boolean isCorrect();
	public abstract boolean isAmbiguous();
}
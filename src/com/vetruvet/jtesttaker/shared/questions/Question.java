package com.vetruvet.jtesttaker.shared.questions;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
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

import com.vetruvet.jtesttaker.shared.attachments.Attachment;

public abstract class Question extends JPanel {
	private static final long serialVersionUID = -2903143552848826449L;
	
	private ArrayList<Attachment> attachments = new ArrayList<Attachment>();
	
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
	
	public int addAttachment(Attachment att) {
		attachments.add(att);
		return attachments.size() - 1;
	}
	
	public Attachment[] getAttachments() {
		return attachments.toArray(new Attachment[0]);
	}
	
	public Attachment getAttachment(int index) {
		return attachments.get(index);
	}
	
	public Attachment getAttachment(String id) {
		for (Attachment att : attachments) {
			if (att.getID().equals(id)) return att;
		}
		return null;
	}
	
	public void removeAttachment(int index) {
		attachments.set(index, null);
	}
	
	public void removeAttachment(String id) {
		for (int q = 0; q < attachments.size(); q++) {
			if (attachments.get(q).getID().equals(id)) {
				attachments.set(q, null);
				break;
			}
		}
	}
	
	public void showOptionsDialog(Component parent) {
		final JDialog optsDlg = new JDialog();
		optsDlg.setModal(true);
		
		JPanel optionsRoot = new JPanel(new BorderLayout());
		
		JPanel commonRoot = new JPanel(new GridBagLayout());
		commonRoot.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(), "Common Options"));
		optionsRoot.add(commonRoot, BorderLayout.PAGE_START);
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
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
		c.anchor = GridBagConstraints.CENTER;
		
		c.gridy = 0;
		final JTextField idFld = new JTextField(15);
		commonRoot.add(idFld, c);
		
		c.gridy = 1;
		final JTextField titleFld = new JTextField(15);
		commonRoot.add(titleFld, c);
		
		c.gridy = 2;
		final JTextArea queryFld = new JTextArea(5, 15);
		commonRoot.add(queryFld, c);
		
		JPanel typeOptsRoot = new JPanel();
		optionsRoot.add(typeOptsRoot, BorderLayout.CENTER);
		
		ActionListener typeApply = renderTypeOptionsPanel(typeOptsRoot);
		ActionListener commonApply = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Save settings from inputs
			}
		};
		
		JPanel butPanel = new JPanel(new FlowLayout());
		optionsRoot.add(butPanel, BorderLayout.PAGE_END);
		
		JButton okButton = new JButton("OK");
		okButton.addActionListener(typeApply);
		okButton.addActionListener(commonApply);
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				optsDlg.setVisible(false);
				optsDlg.dispose();
			}
		});
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
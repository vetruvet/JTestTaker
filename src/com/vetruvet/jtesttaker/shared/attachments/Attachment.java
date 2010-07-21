package com.vetruvet.jtesttaker.shared.attachments;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public abstract class Attachment extends JPanel {
	private static final long serialVersionUID = 3069786813115183683L;
	
	private String id = null;
	private String title = null;
	private URL url = null;
	
	public Attachment(String id, String title) {
		this.id = id;
		this.title = title;
	}
	
	public Attachment(String id, String title, URL url) {
		this(id, title);
		this.url = url;
	}
	
	public Attachment() { }
	
	public String getID() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public URL getURL() {
		return url;
	}
	
	public void setID(String id) {
		this.id = id;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setURL(URL url) {
		this.url = url;
	}
	
	public void showOptionsDialog(Component parent) {
		final JDialog optsDlg = new JDialog((Frame) null, id == null ? "New Attachment" : "Editing Attachment", true);
		
		JPanel optionsRoot = new JPanel(new BorderLayout(3, 3));
		
		JPanel commonRoot = new JPanel(new GridBagLayout());
		optionsRoot.add(commonRoot, BorderLayout.PAGE_START);
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.weightx = c.weighty = 0.0;
		c.gridheight = c.gridwidth = 1;
		c.insets = new Insets(3, 3, 2, 3);
		c.anchor = GridBagConstraints.LINE_START;
		
		c.gridy = 0;
		JLabel idLbl = new JLabel("Attachment ID");
		idLbl.setToolTipText("Uniquely Identifies the attachment in the test.");
		commonRoot.add(idLbl, c);
		
		c.gridy = 1;
		JLabel titleLbl = new JLabel("Attachment Title");
		titleLbl.setToolTipText("A Name for the question");
		commonRoot.add(titleLbl, c);
		
		c.gridx = 1;
		c.weightx = c.weighty = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		
		c.gridy = 0;
		final JTextField idFld = new JTextField(id == null ? "new_attach" : id, 15);
		commonRoot.add(idFld, c);
		
		c.gridy = 1;
		final JTextField titleFld = new JTextField(title == null ? "New Attachment" : title, 15);
		commonRoot.add(titleFld, c);
		
		JPanel typeOptsRoot = new JPanel();
		optionsRoot.add(typeOptsRoot, BorderLayout.CENTER);
		
		ActionListener commonApply = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setID(idFld.getText());
				setTitle(titleFld.getText());
			}
		};
		
		JPanel butPanel = new JPanel(new FlowLayout());
		optionsRoot.add(butPanel, BorderLayout.PAGE_END);
		
		JButton okButton = new JButton("OK");
		okButton.setDefaultCapable(true);
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
		applyButton.addActionListener(commonApply);
		butPanel.add(applyButton);
		
		optsDlg.setContentPane(optionsRoot);
		optsDlg.pack();
		optsDlg.setResizable(true);
		optsDlg.setLocationRelativeTo(parent);
		optsDlg.setVisible(true);
	}
	
	protected abstract void render();
}

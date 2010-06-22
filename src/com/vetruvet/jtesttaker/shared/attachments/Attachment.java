package com.vetruvet.jtesttaker.shared.attachments;

import javax.swing.JPanel;

public abstract class Attachment extends JPanel {
	private static final long serialVersionUID = 3069786813115183683L;
	
	private String id = null;
	private String title = null;
	
	public Attachment(String id, String title) {
		this.id = id;
		this.title = title;
	}
	
	public Attachment() { }
	
	public String getID() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setID(String id) {
		this.id = id;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	protected abstract void render();
}

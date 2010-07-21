package com.vetruvet.jtesttaker.shared;

import java.awt.Component;
import java.io.File;
import java.util.HashMap;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.vetruvet.jtesttaker.shared.attachments.Attachment;
import com.vetruvet.jtesttaker.shared.attachments.AudioAttachment;
import com.vetruvet.jtesttaker.shared.attachments.ImageAttachment;
import com.vetruvet.jtesttaker.shared.attachments.PDFAttachment;
import com.vetruvet.jtesttaker.shared.attachments.TableAttachment;
import com.vetruvet.jtesttaker.shared.attachments.VideoAttachment;

public class FileChooserManager {
	private static final PreferencesManager PM = PreferencesManager.getInstance();
	
	private static final HashMap<FileType, FileNameExtensionFilter> filters = new HashMap<FileType, FileNameExtensionFilter>();
	static {
		filters.put(FileType.TEST, new FileNameExtensionFilter("JTestSuite Test (*.jtt)", "jtt"));
		filters.put(FileType.IMAGE, new FileNameExtensionFilter("Image Files", "jpg", "png", "gif", "bmp"));
		filters.put(FileType.AUDIO, new FileNameExtensionFilter("Audio Files", "mp3", "wav", "wma", "aac"));
		filters.put(FileType.VIDEO, new FileNameExtensionFilter("Video Files", "avi", "mp4", "mpg", "mov"));
		filters.put(FileType.TABLE, new FileNameExtensionFilter("Tabular Data", "xls", "xlsx", "csv", "ods"));
		filters.put(FileType.PDF, new FileNameExtensionFilter("PDF Files", "pdf"));
	}
	
	public static File showSaveDlg(Component parent, FileType type) {
		JFileChooser fc = new JFileChooser();
		
		fc.setCurrentDirectory(PM.getLastDir());
		fc.setMultiSelectionEnabled(false);
		fc.setDialogTitle("Save Current Test");
		fc.setAcceptAllFileFilterUsed(type != FileType.TEST);
		fc.setFileFilter(filters.get(type));
		
		if (fc.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
			File savFile;
			if (fc.getSelectedFile().getAbsolutePath().endsWith(".jtt")) savFile = fc.getSelectedFile();
			else savFile = new File(fc.getSelectedFile().getAbsolutePath() + ".jtt");
			
			if (savFile.exists()) {
				int resp = JOptionPane.showConfirmDialog(parent, 
						"File already exists!\nDo you want to overwrite it?",
						"Overwrite file?", JOptionPane.YES_NO_OPTION);
				if (resp == JOptionPane.NO_OPTION) return null;
			}
			
			PM.setLastDir(fc.getCurrentDirectory());
			return savFile;
		}
		return null;
	}
	
	public static File showOpenDlg(Component parent, FileType type) {
		JFileChooser fc = new JFileChooser();
		
		fc.setCurrentDirectory(PM.getLastDir());
		fc.setMultiSelectionEnabled(false);
		fc.setDialogTitle("Open Existing Test File");
		fc.setAcceptAllFileFilterUsed(type != FileType.TEST);
		fc.setFileFilter(filters.get(type));
		
		if (fc.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
			PM.setLastDir(fc.getCurrentDirectory());
			return fc.getSelectedFile();
		}
		return null;
	}
	
	public static FileType getTypeForAttachment(Attachment att) {
		if (att instanceof ImageAttachment) return FileType.IMAGE;
		if (att instanceof AudioAttachment) return FileType.AUDIO;
		if (att instanceof VideoAttachment) return FileType.VIDEO;
		if (att instanceof TableAttachment) return FileType.TABLE;
		if (att instanceof PDFAttachment) return FileType.PDF;
		return null;
	}

	public static enum FileType {
		TEST, IMAGE, AUDIO, VIDEO, TABLE, PDF
	}
}

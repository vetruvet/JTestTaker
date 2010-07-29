package com.vetruvet.jtesttaker.shared.images;

import java.net.URL;

import javax.swing.ImageIcon;

import com.vetruvet.jtesttaker.shared.attachments.Attachment;
import com.vetruvet.jtesttaker.shared.attachments.AudioAttachment;
import com.vetruvet.jtesttaker.shared.attachments.ImageAttachment;
import com.vetruvet.jtesttaker.shared.attachments.PDFAttachment;
import com.vetruvet.jtesttaker.shared.attachments.TableAttachment;
import com.vetruvet.jtesttaker.shared.attachments.VideoAttachment;

public class ImageManager {
	public static ImageIcon getImage(String baseName, String size) {
		URL imgURL = ImageManager.class.getResource(baseName + "_" + size + ".png");
		if (imgURL == null) imgURL = ImageManager.class.getResource(baseName + "_" + size + ".gif");
		if (imgURL == null) return null;
		return new ImageIcon(imgURL);
	}
	
	public static ImageIcon getAttachIcon(Attachment att, String size) {
		if (att instanceof ImageAttachment) return getImage("image_att", size);
		if (att instanceof AudioAttachment) return getImage("audio_att", size);
		if (att instanceof VideoAttachment) return getImage("video_att", size);
		if (att instanceof TableAttachment) return getImage("table_att", size);
		if (att instanceof PDFAttachment) return getImage("pdf_att", size);
		return null;
	}
}

package com.vetruvet.jtesttaker.shared;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.vetruvet.jtesttaker.shared.attachments.Attachment;
import com.vetruvet.jtesttaker.shared.questions.Question;

public class Test {
	private ArrayList<Question> questions = new ArrayList<Question>();
	private ArrayList<Attachment> attachments = new ArrayList<Attachment>();
	
	private String preText = null;
	private String postText = null;
	private String title = null;
	
	private File saveFile = null;
	
	public static Test readFromFile(String file) {
		return readFromFile(new File(file));
	}
	
	public static Test readFromFile(File file) {
		if (!file.exists()) return null;
		
		ZipFile zip = null;
		try {
			Test test = new Test();
			test.setSaveFile(file);
			
			zip = new ZipFile(file);
			ZipEntry testEntry = zip.getEntry("test.xml");
			if (testEntry == null) return null;
			
			Document testDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
					zip.getInputStream(testEntry));
			testDoc.getDocumentElement().normalize();
			
			NamedNodeMap testAttrs = testDoc.getElementsByTagName("test").item(0).getAttributes();
			Node nameNode = testAttrs.getNamedItem("name");
			String testName = null;
			if (nameNode != null) testName = nameNode.getNodeValue();
			if (testName != null) test.setTitle(testName);
			
			NodeList questionList = testDoc.getElementsByTagName("question");
			for (int q = 0; q < questionList.getLength(); q++) {
				Node questionNode = questionList.item(q);
				NamedNodeMap attrs = questionNode.getAttributes();

				String type = null;
				Node typeNode = attrs.getNamedItem("type");
				if (typeNode != null) {
					type = typeNode.getNodeValue();
				}
				if (type == null) continue;
				Question quest = (Question) Class.forName(type).newInstance();
				
				String qid = null;
				Node qidNode = attrs.getNamedItem("qid");
				if (qidNode != null) qid = qidNode.getNodeValue();
				if (qid != null) quest.setID(qid);
				
				String time = null;
				Node timeNode = attrs.getNamedItem("time");
				if (timeNode != null) time = timeNode.getNodeValue();
				if (time != null) {
					try {
						int timeVal = Integer.parseInt(time);
						quest.setAllowedTime(timeVal);
					} catch (NumberFormatException e) { }
				}
				
				NodeList childList = questionNode.getChildNodes();
				for (int w = 0; w < childList.getLength(); w++) {
					Node childNode = childList.item(w);
					String childName = childNode.getNodeName();
					if (childName.equalsIgnoreCase("attach")) {
						Node attIDs = childNode.getAttributes().getNamedItem("ids");
						String ids = null;
						if (attIDs != null) ids = attIDs.getNodeValue();
						if (ids != null) {
							for (String id : ids.split(" ")) quest.addAttachment(id);
						}
					}
					else if (childName.equalsIgnoreCase("title")) {
						quest.setTitle(childNode.getTextContent());
					}
					else if (childName.equalsIgnoreCase("query")) {
						quest.setQuestionText(childNode.getTextContent());
					}
					else if (childName.equalsIgnoreCase("body")) {
						quest.parseBodyString(childNode.getTextContent());
					}
				}
				
				test.questions.add(quest);
			}
			
			NodeList attachList = testDoc.getElementsByTagName("attachment");
			for (int q = 0; q < attachList.getLength(); q++) {
				Node attachNode = attachList.item(q);
				NamedNodeMap attAttrs = attachNode.getAttributes();
				
				String type = null;
				Node attTypeNode = attAttrs.getNamedItem("type");
				if (attTypeNode != null) {
					type = attTypeNode.getNodeValue();
				}
				if (type == null) continue;
				Attachment att = (Attachment) Class.forName(type).newInstance();
				
				String aid = null;
				Node aidNode = attAttrs.getNamedItem("aid");
				if (aidNode != null) {
					aid = aidNode.getNodeValue();
				}
				if (aid != null) {
					att.setID(aid);
					att.setURL(new URL("jar:file:" + file.getCanonicalPath() + "!/attachments/" + aid));
				}
				
				String attTitle = null;
				Node titleNode = attAttrs.getNamedItem("title");
				if (titleNode != null) {
					attTitle = titleNode.getNodeValue();
				}
				if (attTitle != null) att.setTitle(attTitle);
				
				test.attachments.add(att);
			}
			
			Node preTextNode = testDoc.getElementsByTagName("pretext").item(0);
			if (preTextNode != null) test.setPreText(preTextNode.getTextContent());
			
			Node postTextNode = testDoc.getElementsByTagName("posttext").item(0);
			if (postTextNode != null) test.setPostText(postTextNode.getTextContent());
			
			return test;
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			if (zip != null) {
				try { zip.close(); } catch (IOException e) { }
			}
		}
		return null;
	}

	public Question getQuestion(int index) {
		return questions.get(index);
	}
	
	public Question getQuestion(String id) {
		for (Question q : questions) {
			if (q.getID().equals(id)) return q;
		}
		return null;
	}
	
	public ArrayList<Question> getQuestions() {
		return questions;
	}
	
	public int getNQuestions() {
		return questions.size();
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
	
	public ArrayList<Attachment> getAttachments() {
		return attachments;
	}
	
	public int getNAttachments() {
		return attachments.size();
	}
	
	public String getPreText() {
		return preText;
	}
	
	public String getPostText() {
		return postText;
	}
	
	public String getTitle() {
		return title;
	}
	
	public File getSaveFile() {
		return saveFile;
	}
	
	public boolean isIDUsed(String id) {
		for (Question quest : questions) {
			if (quest.getID().equals(id)) return true;
		}
		for (Attachment att : attachments) {
			if (att.getID().equals(id)) return true;
		}
		return false;
	}
	
	public void addQuestion(Question question) {
		questions.add(question);
	}

	public void addAttachment(Attachment att, URL srcUrl) {
		att.setURL(srcUrl);
		attachments.add(att);
	}
	
	public void setQuestions(ArrayList<Question> questions) {
		this.questions = questions;
	}
	
	public void setQuestion(int index, Question question) {
		questions.set(index, question);
	}
	
	public void setAttachments(ArrayList<Attachment> attachments) {
		this.attachments = attachments;
	}
	
	public void setAttachment(int index, Attachment att) {
		attachments.set(index, att);
	}
	
	public void removeQuestion(int index) {
		questions.remove(index);
	}
	
	public void removeQuestion(String id) {
		for (Iterator<Question> it = questions.iterator(); it.hasNext(); ) {
			if (it.next().getID().equals(id)) it.remove();
		}
	}
	
	public void removeAttachment(int index) {
		removeAttachFromQuestions(attachments.remove(index).getID());
	}
	
	public void removeAttachment(String id) {
		for (Iterator<Attachment> it = attachments.iterator(); it.hasNext(); ) {
			if (it.next().getID().equals(id)) it.remove();
		}
		removeAttachFromQuestions(id);
	}
	
	public void removeAttachFromQuestions(String id) {
		for (Question quest : questions) quest.removeAttachment(id);
	}
	
	public void setPreText(String text) {
		preText = text;
	}
	
	public void setPostText(String text) {
		postText = text;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setSaveFile(File file) {
		saveFile = file;
	}
	
	public void writeToFile(String file) {
		writeToFile(new File(file));
	}
	
	public void writeToFile(File file) {
		setSaveFile(file);
		
		ZipOutputStream zipOut = null;
		ZipFile tempIn = null;
		File tempFile = null;
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			
			Element testRoot = doc.createElement("test");
			if (title != null) testRoot.setAttribute("name", title);
			doc.appendChild(testRoot);
			
			if (preText != null && !preText.isEmpty()) {
				Element preTextElem = doc.createElement("pretext");
				preTextElem.appendChild(doc.createTextNode(preText));
				testRoot.appendChild(preTextElem);
			}
			
			if (postText != null && !postText.isEmpty()) {
				Element postTextElem = doc.createElement("posttext");
				postTextElem.appendChild(doc.createTextNode(postText));
				testRoot.appendChild(postTextElem);
			}
			
			for (Question question : questions) {
				Element questElem = doc.createElement("question");
				questElem.setAttribute("qid", question.getID());
				questElem.setAttribute("type", question.getClass().getName());
				if (question.getAllowedTime() > 0) 
					questElem.setAttribute("time", question.getAllowedTime() + "");
				testRoot.appendChild(questElem);
				
				Element titleElem = doc.createElement("title");
				titleElem.appendChild(doc.createTextNode(question.getTitle()));
				questElem.appendChild(titleElem);
				
				Element queryElem = doc.createElement("query");
				queryElem.appendChild(doc.createTextNode(question.getQuestionText()));
				questElem.appendChild(queryElem);
				
				Element bodyElem = doc.createElement("body");
				bodyElem.appendChild(doc.createTextNode(question.getBodyString()));
				questElem.appendChild(bodyElem);
				
				String attIDs = "";
				for (String att : question.getAttachments()) attIDs += att + " ";
				if (!attIDs.isEmpty()) {
					Element attElem = doc.createElement("attach");
					attElem.setAttribute("ids", attIDs.substring(0, attIDs.length() - 1));
					questElem.appendChild(attElem);
				}
			}
			
			for (Attachment att : attachments) {
				Element attElem = doc.createElement("attachment");
				attElem.setAttribute("aid", att.getID());
				attElem.setAttribute("title", att.getTitle());
				attElem.setAttribute("type", att.getClass().getName());
				testRoot.appendChild(attElem);
			}
			
			Transformer trans = TransformerFactory.newInstance().newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");
			
			if (saveFile != null) {
				tempFile = new File(saveFile.getAbsolutePath() + System.currentTimeMillis() + ".tmp~");
				saveFile.renameTo(tempFile);
			}
			
			zipOut = new ZipOutputStream(new FileOutputStream(file));
			
			zipOut.putNextEntry(new ZipEntry("test.xml"));
			
			DOMSource src = new DOMSource(doc);
			StreamResult res = new StreamResult(zipOut);
			trans.transform(src, res);
			
			zipOut.closeEntry();
			
			if (tempFile != null) tempIn = new ZipFile(tempFile);
			
			for (Attachment att : attachments) {
				ZipEntry attEntry = new ZipEntry("attachments/" + att.getID());
				
				InputStream attIn = !att.getURL().toExternalForm().startsWith("jar:file:") ? 
						att.getURL().openStream() : (tempIn == null ? null :
							tempIn.getInputStream(tempIn.getEntry("attachments/" + att.getID())));
				
				if (attIn == null) continue;
				zipOut.putNextEntry(attEntry);
				
				int nRead = -1;
				byte[] buf = new byte[4096];
				while ((nRead = attIn.read(buf)) != -1) {
					zipOut.write(buf, 0, nRead);
				}
				attIn.close();
				
				zipOut.closeEntry();
				att.setURL(new URL("jar:file:" + file.getCanonicalPath() + "!/attachments/" + att.getID()));
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerFactoryConfigurationError e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (tempIn != null) try { tempIn.close(); } catch (IOException e) { }
			if (zipOut != null) try { zipOut.close(); } catch (IOException e) { }
			if (tempFile != null && tempFile.exists() && !tempFile.delete()) tempFile.deleteOnExit();
		}
	}
	
	public void showOptionsDialog(Component parent) {
		final JDialog optsDlg = new JDialog((Frame) null, "Editing Test", true);
		
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
		JLabel titleLbl = new JLabel("Test Title");
		titleLbl.setToolTipText("A Name for the test");
		commonRoot.add(titleLbl, c);
		
		c.gridy = 1;
		JLabel preTextLbl = new JLabel("Introductory Text");
		preTextLbl.setToolTipText("<html>This text will be shown prior to the first question.<br />" +
				"Basic HTML is allowed.");
		commonRoot.add(preTextLbl, c);
		
		c.gridy = 2;
		JLabel postTextLbl = new JLabel("Concluding Text");
		postTextLbl.setToolTipText("<html>This text will be shown after the last question.<br />" +
				"Basic HTML is allowed.");
		commonRoot.add(postTextLbl, c);
		
		c.gridy = 3;
		JLabel dateStartLbl = new JLabel("Start date/time");
		dateStartLbl.setEnabled(false);
		dateStartLbl.setToolTipText("The test cannot be started before this date/time.");
		commonRoot.add(dateStartLbl, c);
		
		c.gridy = 4;
		JLabel dateEndLbl = new JLabel("End date/time");
		dateEndLbl.setEnabled(false);
		dateEndLbl.setToolTipText("The test cannot be started after this date/time.");
		commonRoot.add(dateEndLbl, c);
		
		c.gridx = 1;
		c.weightx = 1.0;
		c.weighty = 0.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		
		c.gridy = 0;
		final JTextField titleFld = new JTextField(title == null ? "New Test" : title, 15);
		commonRoot.add(titleFld, c);
		
		c.gridy = 1;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		final JTextArea preTextFld = new JTextArea(preText == null ? "" : preText, 5, 15);
		commonRoot.add(preTextFld, c);
		
		c.gridy = 2;
		final JTextArea postTextFld = new JTextArea(postText == null ? "" : postText, 5, 15);
		commonRoot.add(postTextFld, c);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weighty = 0.0;
		
		c.gridy = 3;
		final JTextField dateStartFld = new JTextField("");
		dateStartFld.setEnabled(false);
		commonRoot.add(dateStartFld, c);
		
		c.gridy = 4;
		final JTextField dateEndFld = new JTextField("");
		dateEndFld.setEnabled(false);
		commonRoot.add(dateEndFld, c);
		
		JPanel typeOptsRoot = new JPanel();
		optionsRoot.add(typeOptsRoot, BorderLayout.CENTER);
		
		ActionListener commonApply = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setTitle(titleFld.getText());
				setPreText(preTextFld.getText());
				setPostText(postTextFld.getText());
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
}

package com.vetruvet.jtesttaker.shared;

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
		
		try {
			Test test = new Test();
			test.setSaveFile(file);
			
			ZipFile zip = new ZipFile(file);
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
				
				test.addQuestion(quest);
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
		attachments.remove(index);
	}
	
	public void removeAttachment(String id) {
		for (Iterator<Attachment> it = attachments.iterator(); it.hasNext(); ) {
			if (it.next().getID().equals(id)) it.remove();
		}
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
					attElem.setAttribute("ids", attIDs.substring(1, attIDs.length() - 1));
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
			
			File tempFile = null;
			if (saveFile != null) {
				tempFile = File.createTempFile(saveFile.getName(), null);
				tempFile.delete();
				saveFile.renameTo(tempFile);
			}
			
			ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(file));
			
			zipOut.putNextEntry(new ZipEntry("test.xml"));
			
			DOMSource src = new DOMSource(doc);
			StreamResult res = new StreamResult(zipOut);
			trans.transform(src, res);
			
			zipOut.closeEntry();
			
			ZipFile tempIn = (tempFile == null) ? null : new ZipFile(tempFile);
			
			for (Attachment att : attachments) {
				ZipEntry attEntry = new ZipEntry("attachments/" + att.getID());
				InputStream attIn = att.getURL().toExternalForm().startsWith("jar:file:") ? 
						(tempIn != null ? tempIn.getInputStream(attEntry) : null) :
							att.getURL().openStream();
				
				if (attIn == null) continue;
				zipOut.putNextEntry(attEntry);
				
				int nRead = -1;
				byte[] buf = new byte[4096];
				while ((nRead = attIn.read(buf)) != -1) {
					zipOut.write(buf, 0, nRead);
				}
				attIn.close();
				
				zipOut.closeEntry();
			}
			
			if (tempFile != null) tempFile.delete();
			
			zipOut.close();
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
		}
	}
}

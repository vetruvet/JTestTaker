package com.vetruvet.jtesttaker.shared;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
import com.vetruvet.jtesttaker.shared.attachments.AudioAttachment;
import com.vetruvet.jtesttaker.shared.attachments.ImageAttachment;
import com.vetruvet.jtesttaker.shared.attachments.PDFAttachment;
import com.vetruvet.jtesttaker.shared.attachments.TableAttachment;
import com.vetruvet.jtesttaker.shared.attachments.VideoAttachment;
import com.vetruvet.jtesttaker.shared.questions.DrawingQuestion;
import com.vetruvet.jtesttaker.shared.questions.LongTextQuestion;
import com.vetruvet.jtesttaker.shared.questions.MatchingQuestion;
import com.vetruvet.jtesttaker.shared.questions.MultipleChoiceQuestion;
import com.vetruvet.jtesttaker.shared.questions.Question;
import com.vetruvet.jtesttaker.shared.questions.ShortTextQuestion;

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
			
			ZipFile zip = new ZipFile(file);
			ZipEntry testEntry = zip.getEntry("test.xml");
			if (testEntry == null) return null;
			
			Document testDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
					zip.getInputStream(testEntry));
			testDoc.getDocumentElement().normalize();
			
			NodeList questionList = testDoc.getElementsByTagName("question");
			for (int q = 0; q < questionList.getLength(); q++) {
				Node questionNode = questionList.item(q);
				Question quest = null;
				
				NamedNodeMap attrs = questionNode.getAttributes();

				String type = null;
				Node typeNode = attrs.getNamedItem("type");
				if (typeNode != null) {
					type = typeNode.getNodeValue();
				}
				if (type == null) continue;
				if (type.equals(MultipleChoiceQuestion.class.getName())) {
					quest = new MultipleChoiceQuestion();
				}
				else if (type.equals(MatchingQuestion.class.getName())) {
					quest = new MatchingQuestion();
				}
				else if (type.equals(ShortTextQuestion.class.getName())) {
					quest = new ShortTextQuestion();
				}
				else if (type.equals(LongTextQuestion.class.getName())) {
					quest = new LongTextQuestion();
				}
				else if (type.equals(DrawingQuestion.class.getName())) {
					quest = new DrawingQuestion();
				}
				else continue;
				
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
				Attachment att = null;
				
				NamedNodeMap attAttrs = attachNode.getAttributes();
				
				String attType = null;
				Node attTypeNode = attAttrs.getNamedItem("type");
				if (attTypeNode != null) {
					attType = attTypeNode.getNodeValue();
				}
				if (attType == null) continue;
				if (attType.equals(ImageAttachment.class.getName())) {
					att = new ImageAttachment();
				}
				else if (attType.equals(AudioAttachment.class.getName())) {
					att = new AudioAttachment();
				}
				else if (attType.equals(VideoAttachment.class.getName())) {
					att = new VideoAttachment();
				}
				else if (attType.equals(PDFAttachment.class.getName())) {
					att = new PDFAttachment();
				}
				else if (attType.equals(TableAttachment.class.getName())) {
					att = new TableAttachment();
				}
				else continue;
				
				String aid = null;
				Node aidNode = attAttrs.getNamedItem("aid");
				if (aidNode != null) {
					aid = aidNode.getNodeValue();
				}
				if (aid != null) att.setID(aid);
				
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
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}
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

	public void addAttachment(Attachment att) {
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
			
			ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(file));
			
			zipOut.putNextEntry(new ZipEntry("test.xml"));
			
			DOMSource src = new DOMSource(doc);
			StreamResult res = new StreamResult(zipOut);
			trans.transform(src, res);
			
			zipOut.closeEntry();
			
			//TODO write attachments to ZIP
			
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

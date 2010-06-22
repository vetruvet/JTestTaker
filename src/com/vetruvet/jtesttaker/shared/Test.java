package com.vetruvet.jtesttaker.shared;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
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
import com.vetruvet.jtesttaker.shared.questions.SingleChoiceQuestion;

public class Test {
	private ArrayList<Question> questions = new ArrayList<Question>();
	private String preText = null;
	private String postText = null;
	private String title = null;
	
	public static Test readFromFile(String file) {
		return readFromFile(new File(file));
	}
	
	public static Test readFromFile(File file) {
		if (!file.exists()) return null;
		
		try {
			Test test = new Test();
			
			Document testDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
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
				if (type.equals(SingleChoiceQuestion.class.getName())) {
					quest = new SingleChoiceQuestion();
				}
				else if (type.equals(MultipleChoiceQuestion.class.getName())) {
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
				if (qidNode != null) {
					qid = qidNode.getNodeValue();
				}
				if (qid != null) quest.setID(qid);
				
				String time = null;
				Node timeNode = attrs.getNamedItem("time");
				if (timeNode != null) {
					time = timeNode.getNodeValue();
				}
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
					if (childName.equalsIgnoreCase("attachment")) {
						Attachment att = null;
						
						NamedNodeMap attAttrs = childNode.getAttributes();
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
			}
			
			Node preTextNode = testDoc.getElementsByTagName("pretext").item(0);
			test.setPreText(preTextNode.getTextContent());
			
			Node postTextNode = testDoc.getElementsByTagName("posttext").item(0);
			test.setPostText(postTextNode.getTextContent());
			
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
	
	public ArrayList<Question> getQuestions() {
		return questions;
	}
	
	public Question getQuestion(int index) {
		return questions.get(index);
	}
	
	public void setQuestions(ArrayList<Question> questions) {
		this.questions = questions;
	}
	
	public void setQuestion(int index, Question question) {
		questions.set(index, question);
	}
	
	public void addQuestion(Question question) {
		questions.add(question);
	}
	
	public void removeQuestion(int index) {
		questions.remove(index);
	}
	
	public int getNQuestions() {
		return questions.size();
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
	
	public void writeToFile(String file) {
		writeToFile(new File(file));
	}
	
	public void writeToFile(File file) {
		BufferedWriter out = null;
		try {
			out = new BufferedWriter(new FileWriter(file));
			
			out.write("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
			out.newLine();
			
			out.write("<test");
			if (title != null && !title.isEmpty()) out.write(" name=\"" + title + "\"");
			out.write(">");
			out.newLine();
			
			if (preText != null && !preText.isEmpty()) {
				out.write("<pretext>");
				out.newLine();
				out.write(preText);
				out.newLine();
				out.write("</pretext>");
			}
			
			if (postText != null && !postText.isEmpty()) {
				out.write("<posttext>");
				out.newLine();
				out.write(postText);
				out.newLine();
				out.write("</posttext>");
			}
			
			for (Question question : questions) {
				out.write("<question qid=\"" + question.getID() + 
						"\" type=\"" + question.getClass().getName() + "\"");
				int qTime = question.getAllowedTime();
				if (qTime > 0) out.write(" time=\"" + qTime + "\"");
				out.write(">");
				out.write("<title>" + question.getTitle() + "</title>");
				out.write("<query>" + question.getQuestionText() + "</query>");
				out.write("<body>" + question.getBodyString() + "</query>");
				
				for (Attachment att : question.getAttachments()) {
					out.write("<attachment aid=\"" + att.getID() + "\" " +
							"name=\"" + att.getTitle() + "\" " +
							"type=\"" + att.getClass().getName() + "\">");
					//TODO write attachment data (Base64 for small data or URI for large data)
					out.write("</attachment>");
				}
				
				out.write("</question>");
				out.newLine();
			}
			out.write("</test>");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) { }
			}
		}
	}
}

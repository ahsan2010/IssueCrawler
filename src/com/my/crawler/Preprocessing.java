package com.my.crawler;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.lang3.tuple.Pair;
import org.tartarus.snowball.ext.englishStemmer;


import jregex.Matcher;
import jregex.Pattern;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.OutputDocument;
import net.htmlparser.jericho.Renderer;
import net.htmlparser.jericho.Segment;
import net.htmlparser.jericho.Source;
import net.htmlparser.jericho.StartTag;

public class Preprocessing {

	public static Map<String, ArrayList<String>> answerByPostId = new HashMap<String, ArrayList<String>>();
	public static Map<String, Integer> userAnswer = new HashMap<String, Integer>();
	public static int maxiAccAnswer = 0;

	private static Pattern myPattern4 = new Pattern("at (.)*?\\([A-Z][a-z0-9A-Z]*.java:[0-9]*");
	private static Pattern myPattern3 = new Pattern("[A-Z][a-zA-Z]*Exception");
	private static Pattern myPattern2 = new Pattern("(V|D|I|W|E|F|INFO|DEBUG|WARN|FATAL|VERBOSE|ERROR)\\/(.)*?\\(");
	private static Pattern myPattern1 = new Pattern("(Exception:|Error:)(.)*?\\([A-Z][a-z0-9A-Z]*\\.java:[0-9]*\\)");

	private static final Set<String> DISALLOWED_HTML_TAGS = new HashSet<String>(Arrays.asList(HTMLElementName.LINK));
	private static final Set<String> DISALLOWED_CODE_HTML_TAGS = new HashSet<String>(
			Arrays.asList(HTMLElementName.PRE, HTMLElementName.CODE, HTMLElementName.A, HTMLElementName.LINK));

	private static final Set<String> LINK_TAGS = new HashSet<String>(
			Arrays.asList(HTMLElementName.A, HTMLElementName.LINK));

	private static final Set<String> CODE_TAGS = new HashSet<String>(
			Arrays.asList(HTMLElementName.CODE, HTMLElementName.PRE));

	// HTMLElementName.PRE, HTMLElementName.CODE)

	public static Map<String, ArrayList<String>> getAnswerByPost() {
		if (answerByPostId == null) {
			loadData(Properties.tag);
		}

		return answerByPostId;
	}

	private static OutputDocument removeNotAllowedTags(Source source) {

		OutputDocument outputDocument = new OutputDocument(source);
		List<Element> elements = source.getAllElements();

		for (Element element : elements) {

			if (DISALLOWED_HTML_TAGS.contains(element.getName())) {
				outputDocument.remove(element);
			}

		}

		return outputDocument;
	}

	private static OutputDocument removeNotAllowedCodeTags(Source source) {

		OutputDocument outputDocument = new OutputDocument(source);
		List<Element> elements = source.getAllElements();

		for (Element element : elements) {

			if (DISALLOWED_CODE_HTML_TAGS.contains(element.getName())) {
				outputDocument.remove(element);
			}

		}

		return outputDocument;
	}

	private static OutputDocument onlyCodePart(Source source) {

		OutputDocument outputDocument = new OutputDocument(source);
		List<Element> elements = source.getAllElements();

		for (Element element : elements) {
			// System.out.println("Element Name " + element.getName());
			if (CODE_TAGS.contains(element.getName())) {
				continue;
			} else {
				outputDocument.remove(element);

			}

		}
		// System.out.println(outputDocument.toString());
		return outputDocument;
	}

	private static ArrayList<String> onlyLinkPart(Source source) {

		OutputDocument outputDocument = new OutputDocument(source);
		List<Element> elements = source.getAllElements();
		ArrayList<String> s = new ArrayList<String>();

		for (Element element : elements) {

			if (LINK_TAGS.contains(element.getName())) {
				// System.out.println("GOT");
				s.add(element.toString());

			}
		}
		// System.out.println(outputDocument.toString());
		return s;
	}

	public static String htmlRemove(String body) {
		String result = "";
		Source htmlSource = new Source(body);
		List<StartTag> sTag = new ArrayList<StartTag>();
		htmlSource.getAllStartTags();
		OutputDocument outputDocument = removeNotAllowedTags(htmlSource);
		Source source = new Source(outputDocument.toString());
		Segment htmlSeg = new Segment(source, 0, outputDocument.toString().length());

		Renderer htmlRend = new Renderer(htmlSeg);

		return htmlRend.toString();
	}

	public static ArrayList<String> htmlRemoveLink(String body) {

		ArrayList<String> s = new ArrayList<String>();

		Source htmlSource = new Source(body);
		List<StartTag> sTag = new ArrayList<StartTag>();
		htmlSource.getAllStartTags();
		s = onlyLinkPart(htmlSource);

		return s;
	}

	public static String htmlRemoveIgnoreCode(String body) {
		String result = "";
		Source htmlSource = new Source(body);
		List<StartTag> sTag = new ArrayList<StartTag>();
		htmlSource.getAllStartTags();
		OutputDocument outputDocument = removeNotAllowedCodeTags(htmlSource);
		Source source = new Source(outputDocument.toString());
		Segment htmlSeg = new Segment(source, 0, outputDocument.toString().length());

		Renderer htmlRend = new Renderer(htmlSeg);

		return htmlRend.toString();
	}

	private static boolean checkCode(String code) {
		boolean flag = false;

		Matcher m1 = myPattern1.matcher(code);
		Matcher m2 = myPattern2.matcher(code);
		Matcher m3 = myPattern3.matcher(code);
		Matcher m4 = myPattern4.matcher(code);

		if (m1.find()) {
			flag = true;

		} else if (m4.find()) {
			flag = true;
		} else if (m3.find()) {
			if (code.split("\\s+").length < 30) {
				flag = true;
			}
		}

		if (m2.find()) {
			flag = true;
		}

		return flag;
	}

	private static OutputDocument textTransform(Source source) {

		OutputDocument outputDocument = new OutputDocument(source);
		List<Element> elements = source.getAllElements();
		ArrayList<String> s = new ArrayList<String>();

		boolean prev = false;
		int index = 0;

		for (Element element : elements) {

			if (CODE_TAGS.contains(element.getName())) {
				// s.add(element.toString());
				// outputDocument.remove(element);
				boolean errorCode = checkCode(element.toString());
				if (errorCode) {
					outputDocument.replace(element, "E$$");
				} else {
					outputDocument.replace(element, "C$$");
				}

			} else {

			}
		}
		return outputDocument;
	}

	public static String htmlTextTransformation(String body) {
		ArrayList<String> s = new ArrayList<String>();

		String result = "";
		Source htmlSource = new Source(body);
		List<StartTag> sTag = new ArrayList<StartTag>();
		htmlSource.getAllStartTags();
		OutputDocument outputDocument = textTransform(htmlSource);

		Source source = new Source(outputDocument.toString());
		Segment htmlSeg = new Segment(source, 0, outputDocument.toString().length());

		Renderer htmlRend = new Renderer(htmlSeg);

		/*
		 * OutputDocument outputDocument = onlyCodePart(htmlSource); Source
		 * source = new Source(outputDocument.toString()); Segment htmlSeg = new
		 * Segment(source, 0, outputDocument.toString().length());
		 * 
		 * Renderer htmlRend = new Renderer(htmlSeg);
		 */

		return htmlRend.toString();
	}

	public static String htmlExtractCodePart(String body) {
		String result = "";
		Source htmlSource = new Source(body);
		List<StartTag> sTag = new ArrayList<StartTag>();
		htmlSource.getAllStartTags();
		OutputDocument outputDocument = onlyCodePart(htmlSource);
		Source source = new Source(outputDocument.toString());
		Segment htmlSeg = new Segment(source, 0, outputDocument.toString().length());

		Renderer htmlRend = new Renderer(htmlSeg);

		return htmlRend.toString();
	}

	public static boolean isNumeric(String str) {
		try {
			double d = Double.parseDouble(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public static Map<String, Integer> loadStopWord() {

		Map<String, Integer> stopWordList = new HashMap<String, Integer>();

		try {
			FileReader fl = new FileReader(Properties.stop_word_path);
			BufferedReader br = new BufferedReader(fl);

			String line;
			while ((line = br.readLine()) != null) {
				if (line.length() > 0) {
					line = line.trim().toLowerCase();
					// System.out.println(line);
					stopWordList.put(line, 1);
				}
			}

		} catch (Exception e) {

		}
		return stopWordList;
	}

	public static String activateStemmer(String data) {

		long start = System.currentTimeMillis();

		String your_steemed_String = "";
		Map<String, Integer> stopWords = loadStopWord();
		RemoveStopWord rm = new RemoveStopWord(stopWords);
		String result = "";
		result += rm.doRemove(data);

		String lang = "english";
		Class stemClass;
		try {
			stemClass = Class.forName("org.tartarus.snowball.ext." + lang + "Stemmer");
			// stemClass.
			// SnowballStemmer stemmer = (SnowballStemmer)
			// stemClass.newInstance();

			englishStemmer stemmer = new englishStemmer();

			String word[] = result.split("\\s+");
			String stemmedWord = "";
			for (String w : word) {
				stemmer.setCurrent(w);
				stemmer.stem();
				String st = stemmer.getCurrent().trim();
				if (st.length() > 2) {
					if (!isNumeric(st)) {
						stemmedWord += st + " ";
					}
				}
			}

			your_steemed_String += stemmedWord;

		} catch (Exception e) {
			e.printStackTrace();
		}

		long end = System.currentTimeMillis();
		// System.out.println("[Stemming takes: " + (end-start)+" ms]");
		// System.out.println();

		your_steemed_String = rm.doRemove(your_steemed_String);

		return your_steemed_String;
	}

	public static Map<String, Post> loadData(String api) {

		Map<String, Post> posts = new HashMap<String, Post>();
		String file_path = Properties.post_ser_file;

		long start = System.currentTimeMillis();

		FileInputStream input = null;
		
		try {

			input = new FileInputStream(file_path);
			System.out.println("FileReading...");

			ObjectInputStream in2 = new ObjectInputStream(input);
			System.out.println("Loading.....");

			posts = (Map<String, Post>) in2.readObject();
			System.out.println("Finishing.. Reading..");
			System.out.println("Total Posts: " + posts.size());

			for (Map.Entry<String, Post> mp : posts.entrySet()) {
				Post p = mp.getValue();
				if (!p.isQuestion) {

					if (!answerByPostId.containsKey(p.getParentPostId())) {
						ArrayList<String> list = new ArrayList<String>();
						list.add(p.getPostId());
						answerByPostId.put(p.getParentPostId(), list);
					} else {
						answerByPostId.get(p.getParentPostId()).add(p.getPostId());
					}

					// System.out.println(p.getPostId() +"
					// "+p.getAccAnswerId());
					if (!userAnswer.containsKey(p.getUserId())) {
						userAnswer.put(p.getUserId(), 0);
					}
					if (posts.get(p.getParentPostId()).getAccAnswerId() == null) {
						continue;
					}

					if (posts.get(p.getParentPostId()).getAccAnswerId().equals(p.getPostId())) {
						if (!userAnswer.containsKey(p.getUserId())) {
							userAnswer.put(p.getUserId(), 1);
						} else {
							userAnswer.put(p.getUserId(), userAnswer.get(p.getUserId()) + 1);
							maxiAccAnswer = Math.max(maxiAccAnswer, userAnswer.get(p.getUserId()));
						}
					}

				}
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				input.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		long end = System.currentTimeMillis();

		System.out.println("[Loading Posts takes: " + (end - start) + " ms]");
		System.out.println();

		return posts;

	}
	
	

	public static Set<String> readIssueId(String path) {
		Set<String> issueIds = new HashSet<String>();
		// String path =
		// "/home/amee/Documents/RecommendingPostAPIIssues/test_train/Final
		// Data/Total_Issue_Post_List";
		try {

			BufferedReader br = new BufferedReader(new FileReader(path));
			String line = "";
			while ((line = br.readLine()) != null) {
				if (line.trim().length() > 1) {
					issueIds.add(line.trim());
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return issueIds;
	}

	public void readXmlValue(StartElement startElement) {

		IssueThread t = new IssueThread();
		try {

			if (startElement.getName().getLocalPart().equals("bugid")) {

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static Map<String,IssueThread> readAndroidBug() {

		Map<String,IssueThread> androidThreads = new HashMap<String,IssueThread>();
		
		try {

			String fileName = "/home/amee/Documents/ICSE2018/ICSE2018/root/android_platform_bugs.xml";
			XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
			// xmlInputFactory.setProperty(XMLConstants.FEATURE_SECURE_PROCESSING,
			// false);
			xmlInputFactory.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, true);
			xmlInputFactory.setProperty(XMLInputFactory.IS_COALESCING, true);

			XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(new FileInputStream(fileName));
			int totalBugs = 0;
			IssueThread ithread = null;
			String dt="";
			while (xmlEventReader.hasNext()) {
				XMLEvent xmlEvent = xmlEventReader.nextEvent();
				if (xmlEvent.isStartElement()) {
					StartElement startElement = xmlEvent.asStartElement();
					if (startElement.getName().getLocalPart().equals("bug")) {
						ithread = new IssueThread();
					} else if (startElement.getName().getLocalPart().equals("bugid")) {
						xmlEvent = xmlEventReader.nextEvent();
						if(xmlEvent.isEndElement()) continue;
						ithread.setBugId(xmlEvent.asCharacters().getData());
						System.out.println(++totalBugs +" " + ithread.getBugId());
					} else if (startElement.getName().getLocalPart().equals("title")) {
						xmlEvent = xmlEventReader.nextEvent();
						if(xmlEvent.isEndElement()) continue;
						ithread.setTitle(xmlEvent.asCharacters().getData());
					} else if (startElement.getName().getLocalPart().equals("status")) {
						
						xmlEvent = xmlEventReader.nextEvent();
						if(xmlEvent.isEndElement()) continue;
						
						ithread.setStatus(xmlEvent.asCharacters().getData());
					} else if (startElement.getName().getLocalPart().equals("owner")) {
						xmlEvent = xmlEventReader.nextEvent();
						if(xmlEvent.isEndElement()) continue;
						ithread.setOwner(xmlEvent.asCharacters().getData());
					} else if (startElement.getName().getLocalPart().equals("type")) {
						xmlEvent = xmlEventReader.nextEvent();
						if(xmlEvent.isEndElement()) continue;
						ithread.setType(xmlEvent.asCharacters().getData());
					} else if (startElement.getName().getLocalPart().equals("component")) {
						xmlEvent = xmlEventReader.nextEvent();
						if(xmlEvent.isEndElement()) continue;
						ithread.setComponent(xmlEvent.asCharacters().getData());
					} else if (startElement.getName().getLocalPart().equals("star")) {
						xmlEvent = xmlEventReader.nextEvent();
						if(xmlEvent.isEndElement()) continue;
						ithread.setStar(xmlEvent.asCharacters().getData());
					} else if (startElement.getName().getLocalPart().equals("reportedBy")) {
						xmlEvent = xmlEventReader.nextEvent();
						if(xmlEvent.isEndElement()) continue;
						ithread.setReportedBy(xmlEvent.asCharacters().getData());
					} else if (startElement.getName().getLocalPart().equals("openedDate")) {
						xmlEvent = xmlEventReader.nextEvent();
						if(xmlEvent.isEndElement()) continue;
						ithread.setOpenDate(xmlEvent.asCharacters().getData());
					}else if (startElement.getName().getLocalPart().equals("closedOn")) {
						xmlEvent = xmlEventReader.nextEvent();
						if(xmlEvent.isEndElement()) continue;
						ithread.setCloseDate(xmlEvent.asCharacters().getData());
					}
					else if (startElement.getName().getLocalPart().equals("reportedBy")) {
						xmlEvent = xmlEventReader.nextEvent();
						if(xmlEvent.isEndElement()) continue;
						ithread.setReportedBy(xmlEvent.asCharacters().getData());
					}
					else if (startElement.getName().getLocalPart().equals("description")) {
						
						xmlEvent = xmlEventReader.nextEvent();
						
						ithread.setDescription(xmlEvent.asCharacters().getData());
					} 
					else if (startElement.getName().getLocalPart().equals("when")) {
						xmlEvent = xmlEventReader.nextEvent();
						if(xmlEvent.isEndElement()) continue;
						dt = convertDateFormat(xmlEvent.asCharacters().getData());
					}
					else if (startElement.getName().getLocalPart().equals("what")) {
						xmlEvent = xmlEventReader.nextEvent();
						if(xmlEvent.isEndElement()) continue;
						//Pair<String,String> p = new Pair<String,String>("a","b");
						
						ithread.getComments().add(Pair.of(dt,xmlEvent.asCharacters().getData()));
					}
					
				}
				if(xmlEvent.isEndElement()){
	                   EndElement endElement = xmlEvent.asEndElement();
	                   if(endElement.getName().getLocalPart().equals("bug")){
	                	   androidThreads.put(ithread.getBugId(),ithread);
	                   }
				}
			}
			System.out.println("Complete Reading..");
			System.out.println("Total Bugs: " + totalBugs);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return androidThreads;

	}
	
public static String convertDateFormat(String s){
		 
		
		String dt = "";
		
		if(s.trim().indexOf('T') > 0){
			s = s.replace('T',' ');
			dt = s.substring(0, s.indexOf("."));
			return dt;
		}
		if(s.trim().equals("null"))return "null";
		
		String temp[]=s.split(" ");
		
		if(temp[2].equals("Jan")){
			temp[2] = "01";
		}
		else if(temp[2].equals("Feb")){
			temp[2]="02";
		}
		else if(temp[2].equals("Mar")){
			temp[2]="03";
		}
		else if(temp[2].equals("Apr")){
			temp[2]="04";
		}
		else if(temp[2].equals("May")){
			temp[2]="05";
		}
		else if(temp[2].equals("Jun")){
			temp[2]="06";
		}
		else if(temp[2].equals("Jul")){
			temp[2]="07";
		}
		else if(temp[2].equals("Aug")){
			temp[2]="08";
		}
		else if(temp[2].equals("Sep")){
			temp[2]="09";
		}
		else if(temp[2].equals("Oct")){
			temp[2]="10";
		}
		else if(temp[2].equals("Nov")){
			temp[2]="11";
		}
		else if(temp[2].equals("Dec")){
			temp[2]="12";
		}
		dt = temp[3].trim()+"-"+temp[2]+"-"+temp[1]+" "+temp[4];
		return dt;		
		
	}

	public static Map<String,ArrayList<String>> readPostIssueTestData(){
		Map<String,ArrayList<String>> m = new HashMap<String,ArrayList<String>>();
		try{
			
			BufferedReader br = new BufferedReader(new FileReader(Properties.testSet));
			String line="";
			while((line = br.readLine()) != null){
				String ids[] = line.split("\\s+");
				System.out.println(line);
				if(!m.containsKey(ids[0].trim())){
					ArrayList<String> l = new ArrayList<String>();
					l.add(ids[1].trim());
					m.put(ids[0].trim(), l);
				}else{
					m.get(ids[0].trim()).add(ids[1].trim());
				}
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return m;
	}

}
package com.my.crawler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang3.tuple.Pair;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;

public class CrawlerThread implements Runnable{

	int start;
	int end;
    WebDriver driver = null;
	String threadName;
	final int INTERVAL = 5;
	static String emailAddress = "eemahasan2017@gmail.com";
	static String password = "megamind2010";
	static String logInAddress = "https://accounts.google.com/ServiceLoginAuth";

	public CrawlerThread(int start, int end,String threadName){
		this.start = start;
		this.end = end;
		this.driver = driver;
		this.threadName = threadName;
	}
	
	public static void logInGoogle(WebDriver driver ) {
		try {
			driver = new FirefoxDriver();
			driver.get(logInAddress);
			driver.findElement(By.name("identifier")).sendKeys(emailAddress);
			driver.findElement(By.xpath("//div[@id='identifierNext']")).click();
			Thread.sleep(4000);
			driver.findElement(By.name("password")).sendKeys(password);
			Thread.sleep(2000);
			driver.findElement(By.id("passwordNext")).click();
			Thread.sleep(1000);
			System.out.println("Successfully Logged In Google");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getDateFormat(String dt) {
		String formattedDate = "";
		try {
			//System.out.println("Date " + dt);

			dt = dt.replace(',', ' ');
			String temp[] = dt.split("\\s+");
			String month = temp[0].trim();
			String day = temp[1].trim();
			String year = temp[2].trim();
			String hour = temp[3].substring(0, 2).trim();
			String minute = temp[3].substring(3, 5).trim();
			String lb = temp[3].substring(5, 7).trim();
			if (lb.equals("PM")) {
				hour = Integer.toString((Integer.parseInt(hour) + 12));
			}

			formattedDate = year + "-" + month + "-" + day + " " + hour + ":" + minute + ":" + "00";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return formattedDate;
	}
	
	public IssueThread extractInfo(String id, WebDriver driver) {
		IssueThread th = null;
		try {

			th = new IssueThread();
			try{
				driver.get("http://code.google.com/p/android/issues/detail?id=" + id);
			}
			catch(Exception e){
				e.printStackTrace();
				return null;
			}
			
			/*try{
			WebDriverWait wait = new WebDriverWait(driver, 50);
			List<WebElement> elements = new ArrayList<WebElement>();
			elements.add(driver.findElement(By.className("bv2-event-note-container")));
			elements.add(driver.findElement(By.className("bv2-event-info")));
			elements.add(driver.findElement(By.className("bv2-event-user-id")));
			elements.add(driver.findElement(By.xpath(
							"//div[@class='bv2-issue-metadata-field-inner bv2-issue-metadata-field-type bv2-readonly']")));
			elements.add(driver.findElement(By.xpath(
							"//div[@class='bv2-issue-metadata-field-inner bv2-issue-metadata-field-status bv2-readonly']")));
			elements.add(driver.findElement(By.xpath(
							"//div[@class='bv2-issue-metadata-field-inner bv2-issue-metadata-field-priority bv2-readonly']")));
			
			
			wait.until(ExpectedConditions.visibilityOfAllElements(elements));
			
			}catch(Exception e){
				e.printStackTrace();
			}*/
			
			Thread.sleep(1000);
			List<WebElement> list = driver.findElements(By.xpath("//li[@class='bv2-event']"));
			th.setBugId(id);
			th.setTitle(driver.getTitle());
			
			ArrayList<Pair> comments = new ArrayList<Pair>();
			for (int i = 0; i < list.size(); i++) {

				WebElement wel = list.get(i);
				WebElement el = wel.findElement(By.className("bv2-event-note-container"));
				String dt = wel.findElement(By.className("bv2-event-info")).getText();
				String reporter = wel.findElement(By.className("bv2-event-user-id")).getText();

				if (i == 0) {
					th.setDescription(el.getText());
					th.setReportedBy(reporter.trim());	String logInAddress = "https://accounts.google.com/ServiceLoginAuth";

					th.setOpenDate(getDateFormat(dt.trim()), false);

				} else {
					Pair p = Pair.of(getDateFormat(dt), el.getText());
					comments.add(p);

				}

			}
			try {
				try {
					WebElement type = driver.findElement(By.xpath(
							"//div[@class='bv2-issue-metadata-field-inner bv2-issue-metadata-field-type bv2-readonly']"));
					th.setType(type.getText().split("\n")[1].trim());
				} catch (Exception e) {
					e.printStackTrace();
				}
				try{
					WebElement status = driver.findElement(By.xpath(
							"//div[@class='bv2-issue-metadata-field-inner bv2-issue-metadata-field-status bv2-readonly']"));
					th.setStatus(status.getText().split("\n")[1].trim());
				}catch(Exception e){
					e.printStackTrace();
				}
				try {
					WebElement priority = driver.findElement(By.xpath(
							"//div[@class='bv2-issue-metadata-field-inner bv2-issue-metadata-field-priority bv2-readonly']"));

					th.setPriority(priority.getText().split("\n")[1].trim());
				} catch (Exception e) {
					e.printStackTrace();
				}

		if (!comments.isEmpty())
					th.setComments(comments);
			} catch (Exception e) {
				System.out.println("Problem In Type Comments Prioriry");
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return th;
	}
	
	@Override
	public void run() {
		
		WebDriver driver = new FirefoxDriver();
		logInGoogle(driver);
		int total = 0;
		ArrayList<IssueThread> issueList = new ArrayList<IssueThread>();
		for(int i = start ; i <= end ; i ++ ){			
			total++;
			IssueThread th = extractInfo(Integer.toString(i),driver);
			if (th != null) {
				issueList.add(th);
				if (total % INTERVAL == 0) {
					writeXml(issueList, i);
					issueList.clear();
				}
			}
		}
		
	}
	public void writeXml(ArrayList<IssueThread> issueList, int pos) {
		try {
			String path = Properties.issueLocation+"/issue_"+pos+".xml";
			BufferedWriter bw = new BufferedWriter(new FileWriter(path));
			StringWriter stringWriter = new StringWriter();
			XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();
			XMLStreamWriter xMLStreamWriter = xMLOutputFactory.createXMLStreamWriter(stringWriter);

			xMLStreamWriter.writeStartDocument();
			xMLStreamWriter.writeStartElement(Properties.tag);
			for (IssueThread issue : issueList) {
				xMLStreamWriter.writeStartElement("bug");
				xMLStreamWriter.writeStartElement("bugid");
				xMLStreamWriter.writeCharacters(issue.getBugId());
				xMLStreamWriter.writeEndElement();

				xMLStreamWriter.writeStartElement("title");
				xMLStreamWriter.writeCharacters(issue.getTitle());
				xMLStreamWriter.writeEndElement();

				xMLStreamWriter.writeStartElement("status");
				xMLStreamWriter.writeCharacters(issue.getStatus());
				xMLStreamWriter.writeEndElement();

				xMLStreamWriter.writeStartElement("owner");
				xMLStreamWriter.writeCharacters(" ");
				xMLStreamWriter.writeEndElement();

				xMLStreamWriter.writeStartElement("type");
				xMLStreamWriter.writeCharacters(issue.getType());
				xMLStreamWriter.writeEndElement();

				xMLStreamWriter.writeStartElement("priority");
				xMLStreamWriter.writeCharacters(issue.getPriority());
				xMLStreamWriter.writeEndElement();

				xMLStreamWriter.writeStartElement("component");
				xMLStreamWriter.writeCharacters(" ");
				xMLStreamWriter.writeEndElement();

				xMLStreamWriter.writeStartElement("stars");
				xMLStreamWriter.writeCharacters(" ");
				xMLStreamWriter.writeEndElement();

				xMLStreamWriter.writeStartElement("reportedBy");
				xMLStreamWriter.writeCharacters(issue.getReportedBy());
				xMLStreamWriter.writeEndElement();

				xMLStreamWriter.writeStartElement("openedDate");
				xMLStreamWriter.writeCharacters(issue.getOpenDate());
				xMLStreamWriter.writeEndElement();

				xMLStreamWriter.writeStartElement("description");
				xMLStreamWriter.writeCharacters(issue.getDescription());
				xMLStreamWriter.writeEndElement();

				xMLStreamWriter.writeStartElement("closedOn");
				xMLStreamWriter.writeCharacters(" ");
				xMLStreamWriter.writeEndElement();

				for (Pair<String, String> c : issue.getComments()) {
					xMLStreamWriter.writeStartElement("comment");
					xMLStreamWriter.writeStartElement("when");
					xMLStreamWriter.writeCharacters(c.getLeft());
					xMLStreamWriter.writeEndElement();
					xMLStreamWriter.writeStartElement("what");
					xMLStreamWriter.writeCharacters(c.getRight());
					xMLStreamWriter.writeEndElement();
					xMLStreamWriter.writeEndElement();
				}

				xMLStreamWriter.writeEndElement();
			}
			xMLStreamWriter.writeEndElement();
			xMLStreamWriter.writeEndDocument();
			xMLStreamWriter.flush();
			xMLStreamWriter.close();

			String xmlString = stringWriter.getBuffer().toString();
			stringWriter.close();

			bw.write(xmlString);
			bw.newLine();
			bw.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main (String arg[]) throws Exception{
		System.setProperty("webdriver.gecko.driver", "/home/amee/Documents/Lib/geckodriver");
		
		int startIndex = 10000;
		int difference = 10;
		int NTHREDS=5;
		ExecutorService executor = Executors.newFixedThreadPool(NTHREDS);
		for(int i = 1 ; i <= 5;  i ++ ){
			 Runnable worker = new CrawlerThread(startIndex,startIndex+10,new String(startIndex+" " +(startIndex + difference)));
	            executor.execute(worker);
		}
		  executor.shutdown();
	        // Wait until all threads are finish
		 // executor.awa
	        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
	        System.out.println("Finished all threads");
	}

}

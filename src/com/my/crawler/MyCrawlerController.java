package com.my.crawler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang3.tuple.Pair;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.common.base.Predicate;

import junit.framework.Assert;

public class MyCrawlerController {
	// private static final Logger logger =
	// LoggerFactory.getLogger(MyCrawlerController.class);
	int bugFeature[] = { 0, 2, 3, 4, 5, 6, 7 };
	String featureLabel[] = { "status", "product", "classification", "component", "version", "hardware", "importance" };
	WebDriver driver = null;

	String logInAddress = "https://accounts.google.com/ServiceLoginAuth";
	String emailAddress = "eemahasan2017@gmail.com";
	String password = "megamind2010";
	final int INTERVAL = 100;
	int startId = 9100;
	int endId = 10000;

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

	public IssueThread extractInfo(String id) {
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
					th.setReportedBy(reporter.trim());
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

				}

				if (!comments.isEmpty())
					th.setComments(comments);
			} catch (Exception e) {
				System.out.println("Problem In Type Status Prioriry");
				e.printStackTrace();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return th;
	}

	public void logInGoogle() {
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

	public void androidCrawler() {

		try {
			int f = 1;
			System.setProperty("webdriver.gecko.driver", Properties.geckoDriverPath);
			logInGoogle();
			int total = 0;
			ArrayList<IssueThread> issueList = new ArrayList<IssueThread>();

			for (int i = startId; i <= endId; i++) {
				total++;

				IssueThread th = extractInfo(Integer.toString(i));
				if (th != null) {
					issueList.add(th);
					if (total % INTERVAL == 0) {
						writeXml(issueList, i);
						issueList.clear();
					}
				}

				
				// Thread.sleep(1000);
				System.out.println("Complete: " + i + "  T: " + total);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void basicTest() throws Exception {
		String url = "https://bugs.eclipse.org/bugs/show_bug.cgi?id=520001";

		Document doc = Jsoup.connect(url).get();

		// Elements info = doc.select("div#pnlResults");

		// Elements info = doc.select("pre.bz_comment_text");
		Elements info = doc.select("td#bz_show_bug_column_1");

		Element title = doc.getElementById("title");
		// System.out.println(title.size() +" " + title.get(0));
		System.out.println(doc.title());
		System.out.println(info.size());
		Element temp = info.get(0);
		Elements ets = temp.select("tr>td");
		System.out.println(ets.size());

		for (int i = 0; i < ets.size(); i++) {
			System.out.println(i + " " + ets.get(i).toString());
			System.out.println("******************");

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

	public void testUrl() {

		try {
			int ind = 510000;
			double averageTime = 0;
			int size = 10;
			for (int i = 0; i < size; i++) {
				long start = System.currentTimeMillis();

				String url = "https://bugs.eclipse.org/bugs/show_bug.cgi?id=" + ind;
				Document doc = Jsoup.connect(url).get();
				Elements info = doc.select("pre.bz_comment_text");
				Elements features = doc.select("td#bz_show_bug_column_1").select("tr>td");
				Elements reports = doc.select("td#bz_show_bug_column_2").select("tr>td");
				System.out.println("Bug: " + ind);
				System.out.println("Title: " + doc.title());

				IssueThread issue = new IssueThread();

				issue.setBugId(Integer.toString(ind));
				issue.setTitle(doc.title());
				issue.setStatus(Preprocessing.htmlRemove(features.get(0).toString()));
				issue.setType("bug");
				issue.setComponent(Preprocessing.htmlRemove(features.get(4).toString()));
				issue.setPriority(Preprocessing.htmlRemove(features.get(7).toString()));
				issue.setOpenDate(Preprocessing.htmlRemove(reports.get(0).toString()));
				issue.setCloseDate(Preprocessing.htmlRemove(reports.get(1).toString()));

				if (features.size() <= 0) {
					ind++;
					continue;
				}
				for (int k = 0; k < bugFeature.length; k++) {
					System.out.println(featureLabel[k] + " : "
							+ Preprocessing.htmlRemove(features.get(bugFeature[k]).toString()).trim());
				}
				long end = System.currentTimeMillis();

				averageTime += (end - start);
				System.out.println("[Loading Posts takes: " + (end - start) + " ms]");

				ind++;
				System.out.println("***********");
			}
			System.out.println("Total " + size + " Average Time: " + averageTime + " ms  " + averageTime / 1000 + " s");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public MyCrawlerController() {
		androidCrawler();
	}

	public static void main(String[] args) throws Exception {
		// new MyCrawlerController().basicTestAndroid();
		// new MyCrawlerController().testUrl();
		new MyCrawlerController();
		System.out.println("Finish..");

	}
}

package com.my.crawler;

import org.slf4j.Logger;

public class Properties {
	public static String root = "/home/amee/Documents/RecommendPost/IssueCrawler/resources";
	
	public static String post_xml_path = "/home/amee/Documents/StackOverflowDataDump/Posts.xml";
	public static String stop_word_path = root + "/stopList.csv";

	public static int finish_year = 2017;
	public static String tag = "android";
	public static String post_ser_file = root +"/"+ finish_year+ "_"+tag+".ser";
	public static String ldaModel=root + "/model.lda";
	public static String ldaStopWord = root +"/en.txt";
	public static String ldaCorpusPath = root + "/issue.mallet";
	public static String issuePath = " ";
	public static String issueIdMap = root+"/idList.txt";
	public static String soIssueLinks = root + "/SoInIssue.txt";
	public static String testSet = Properties.root + "/testSet.txt";
	public static String corpus = root+"/corpus.ser";
	
	public static int ldaNumIter = 200;
	public static int ldaNumThread = 5;
	public static int ldaNumTopic = 20;
	public static int minThresholdDay = 3;
	//public final static Logger logger = Logger.getLogger("ClintonTrumpNews Logger");
	
	public static String issueLocation = root+"/"+tag;
	
	public static int numberOfCrawlers = 7;
	public static int maximumCrawlingDepth = 2;
	public static String geckoDriverPath=root+"/geckodriver";
	
}

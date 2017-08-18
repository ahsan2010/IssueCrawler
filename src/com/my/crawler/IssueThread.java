package com.my.crawler;

import java.io.Serializable;
import java.util.ArrayList;

import org.apache.commons.lang3.tuple.Pair;


public class IssueThread implements Serializable{

	
	public String bugId;
	public String title;
	public String status;
	public String owner;
	public String star;
	public String type;
	public String component;
	public String priority;
	public String reportedBy;
	public String openDate;
	public String closeDate;
	public String description;
	
	
	public ArrayList<Pair>comments = new ArrayList<Pair>();

	
	public String convertDateFormat(String s){
		 
		
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
	

	public String getComponent() {
		return component;
	}


	public void setComponent(String component) {
		this.component = component;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}


	public String getBugId() {
		return bugId;
	}


	public void setBugId(String bugId) {
		this.bugId = bugId;
	}


	public String getStatus() {
		return status;
	}


	public void setStatus(String status) {
		this.status = status;
	}


	public String getOwner() {
		return owner;
	}


	public void setOwner(String owner) {
		this.owner = owner;
	}


	public String getStar() {
		return star;
	}


	public void setStar(String star) {
		this.star = star;
	}


	public String getPriority() {
		return priority;
	}


	public void setPriority(String priority) {
		this.priority = priority;
	}


	public String getReportedBy() {
		return reportedBy;
	}


	public void setReportedBy(String reportedBy) {
		this.reportedBy = reportedBy;
	}


	public String getOpenDate() {
		return openDate;
	}


	public void setOpenDate(String openDate) {
		this.openDate = convertDateFormat(openDate);
	}
	public void setOpenDate(String openDate,boolean flag) {
		this.openDate = openDate;
	}
	

	public String getCloseDate() {
		return closeDate;
	}


	public void setCloseDate(String closeDate) {
		this.closeDate = convertDateFormat(closeDate);
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public ArrayList<Pair> getComments() {
		return comments;
	}


	public void setComments(ArrayList<Pair> comments) {
		this.comments = comments;
	}
	
	public void showBugInfo(){
		
		System.out.println("ID: " + getBugId());
		System.out.println("Priority: " + getPriority());
		System.out.println("Star: " + getStar());
		System.out.println("Status: " + getStatus());
		System.out.println("Type: " + getType());
		System.out.println("Component: " + getComponent());
		System.out.println("OpenDate: " + getOpenDate());
		System.out.println("CloseDate: " + getCloseDate());
		System.out.println("Reported BY: " + getReportedBy());
		System.out.println("Title: " + getTitle());
		System.out.println("Body: " + getDescription());
		System.out.println("Comments: ");
		for(Pair s : comments){
			System.out.println("C: "+s.getRight());
		}
		System.out.println("-----------------------------------");
		
	}
	
	
	
	
	
}

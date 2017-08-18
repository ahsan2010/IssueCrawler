package com.my.crawler;

import java.io.Serializable;

public class Post implements Serializable{

	public String postId;
	public String userId;
	public String accAnswerId;
	public int commentsCount;
	public int upVoteCount;
	public int downVoteCount;
	public int favoriteCount;
	public String creationDate;
	public int answersCount;
	public String title;
	public String body;
	public String tags;
	public String parentPostId;
	public boolean isQuestion;	
	public double score;
	public int viewCount;
	
	
	public int getViewCount() {
		return viewCount;
	}
	public void setViewCount(String viewCount) {
		
		this.viewCount = viewCount == null ? 0 : Integer.parseInt(viewCount);
	}
	public double getScore() {
		return score;
	}
	public void setScore(double score) {
		this.score = score;
	}
	public String getParentPostId() {
		return parentPostId;
	}
	public void setParentPostId(String parentPostId) {
		this.parentPostId = parentPostId;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public String getTags() {
		return tags;
	}
	public void setTags(String tags) {
		this.tags = tags;
	}
	public String getPostId() {
		return postId;
	}
	public void setPostId(String postId) {
		this.postId = postId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	
	public String getAccAnswerId() {
		return accAnswerId;
	}
	public void setAccAnswerId(String accAnswerId) {
		if (accAnswerId != null)
			this.accAnswerId = accAnswerId;
		else
			this.accAnswerId = "-1";
	}
	
	public int getcommentsCount() {
		return commentsCount;
	}
	public void setcommentsCount(String commentsCount) {
		if(commentsCount == null){
			this.commentsCount = 0;
		}else{
			this.commentsCount = Integer.parseInt(commentsCount);
		}		
	}
	public int getUpVoteCount() {
		return upVoteCount;
	}
	public void setUpVoteCount(int upVoteCount) {
		this.upVoteCount = upVoteCount;
	}
	public void setUpVoteCount(String upVoteCount){
		if(upVoteCount == null){
			this.upVoteCount = 0;
		}else{
			this.upVoteCount = Integer.parseInt(upVoteCount);
		}	
	}
	public int getDownVoteCount() {
		return downVoteCount;
	}
	public void setDownVoteCount(int downVoteCount) {
		this.downVoteCount = downVoteCount;
	}
	public void setDownVoteCount(String downVoteCount){
		if(downVoteCount == null){
			this.downVoteCount = 0;
		}else{
			this.downVoteCount = Integer.parseInt(downVoteCount);
		}	
	}
	public int getFavoriteCount() {
		return favoriteCount;
	}
	public void setFavoriteCount(int favoriteCount) {
		this.favoriteCount = favoriteCount;
	}
	public void setFavoriteCount(String favoriteCount){
		if(favoriteCount == null){
			this.favoriteCount = 0;
		}else{
			this.favoriteCount = Integer.parseInt(favoriteCount);
		}	
	}
	public String getCreationDate() {
		
		return creationDate.substring(0, creationDate.length() - 4).replace('T',' ');
	}
	public void setCreationDate(String creationDate) {
		this.creationDate = creationDate;
	}
	public int getCommentsCount() {
		return commentsCount;
	}
	public void setCommentsCount(int commentsCount) {
		this.commentsCount = commentsCount;
	}
	public void setCommentsCount(String commentsCount){
		if(commentsCount == null){
			this.commentsCount = 0;
		}else{
			this.commentsCount = Integer.parseInt(commentsCount);
		}	
	}
	public int getAnswersCount() {
		return answersCount;
	}
	public void setAnswersCount(String answersCount){
		if(answersCount == null){
			this.answersCount = 0;
		}else{
			this.answersCount = Integer.parseInt(answersCount);
		}	
	}
	public void setAnswersCount(int answersCount) {
		this.answersCount = answersCount;
	}
	
	
	
}

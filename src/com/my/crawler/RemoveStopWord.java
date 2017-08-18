package com.my.crawler;

import java.util.HashMap;
import java.util.Map;


public class RemoveStopWord {

	String data;
	Map<String,Integer>stopList = new HashMap<String,Integer>();
	
	public RemoveStopWord(Map<String,Integer>stopList){
		
		this.stopList = stopList;
	}
	public String doRemove(String data){
		String afterRemove = "";
		
            data = data.replace('{', ' ');
            data = data.replace('}', ' ');
            data = data.replace('!', ' ');
            data = data.replace('@', ' ');
            data = data.replace('#', ' ');
            data = data.replace(')', ' ');
            data = data.replace('(', ' ');
            data = data.replace(':', ' ');
                
		if (data == null) return null;
		String[] words = data.split("\\s+");
		for (int i = 0; i < words.length; i++) {
		    
		    words[i] = words[i].replaceAll("[^\\w]", " ");
		    String tempWords[] = words[i].split("\\s+");
		    for (String st : tempWords){
		    	st = st.toLowerCase();
		    	if(!stopList.containsKey(st)){
		    		afterRemove+=st+" ";
		    	
		    	}
		    	
		    }
		    
		}
		
		//System.out.println(afterRemove);
		
		return afterRemove;
	}
}


package edu.illinois.cs.cogcomp.lorelei.cheaptrans;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.Map;

public class Embedding {
	private Map<String,String[]> embeddings = new LinkedHashMap<String,String[]>();
	public Embedding(String fname) throws IOException {
		FileInputStream input_stream = new FileInputStream(fname);
		InputStreamReader reader = new InputStreamReader(input_stream,"utf-8");
		BufferedReader br = new BufferedReader(reader);
		String line = null;
		while((line=br.readLine())!=null) {
			String[] sline = line.split("\t");
			String key = sline [0];
			String[] value = new String[sline.length-1];
			
			for (int i = 0; i<value.length;i++) {
				value[i]=sline[i+1];
			}
			
			this.embeddings.put(key, value);
		}
	}
	
	public int size() {
		return this.embeddings.size();
	}
	
	public String[] getCandidates(String key) {
		return this.embeddings.get(key);
	}
	
	public String getBestCandidate(String key) {
		return this.embeddings.get(key)[0];
	}
}

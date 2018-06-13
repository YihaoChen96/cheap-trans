package edu.illinois.cs.cogcomp.lorelei.cheaptrans;

import java.util.ArrayList;

public class XmlLexicon {
	private String entry_id;
	private String lemma;
	private String pos;
	private ArrayList<String> gloss;
	
	public XmlLexicon(String entry_id, String lemma, String pos, ArrayList<String> gloss) {
		this.entry_id = entry_id;
		this.lemma = lemma;
		this.pos = pos;
		this.gloss = gloss;
	}
	
	public String getEntryId() {
		return this.entry_id;
	}
	
	public String getLemma() {
		return this.lemma;
	}
	
	public String getPOS() {
		return this.pos;
	}
	
	public ArrayList<String> getGloss(){
		return this.gloss;
	}
	
	public String toPavlickLine() {
		String[] glosses = new String[this.gloss.size()];
		for(int i=0; i<this.gloss.size();i++) {
			glosses[i]= this.gloss.get(i);
		}
		String line = this.lemma+"\t"+String.join("\t",glosses);
		return line;
	}
	
}

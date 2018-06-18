package edu.illinois.cs.cogcomp.lorelei.cheaptrans;

public class TransMapping {
	public String original_word = "";
	public String translate_word = "";
	public int original_start = 0;
	public int original_end = 0;
	public int translate_start = 0;
	public int translate_end = 0;
	public int offset = 0;
	public TransMapping() {
		
	}
	public TransMapping(String o,String t, int os,int oe,int ts, int te, int off) {
		this.original_word=o;
		this.translate_word=t;
		this.original_start=os;
		this.original_end=oe;
		this.translate_start=ts;
		this.translate_end=te;
		this.offset= off;
	}
	public String toString() {
		String str="Original:"+ this.original_word + "\tOriginal Span:" + this.original_start+"-"+this.original_end+ "\tTranslate:"+ this.translate_word+"\tTranslate Span:"+ this.translate_start+"-"+this.translate_end+"\tOffset:"+this.offset;
		return str;
	}
	
}

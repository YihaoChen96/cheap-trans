package edu.illinois.cs.cogcomp.lorelei.cheaptrans;

public class Scores {

	public double score;
	public String str;

	public Scores(String str, double d) {
		this.score=d;
		this.str=str;

	}
	public String toString() {
		return str+": "+score;
	}


}

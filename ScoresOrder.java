package edu.illinois.cs.cogcomp.lorelei.cheaptrans;

import java.util.Comparator;

public class ScoresOrder implements Comparator<Scores>{
		ScoresOrder(){

		}
		public int compare(Scores o1, Scores o2) {

			if (o1.score==o2.score) return 0;
			else if (o1.score > o2.score) return 1;
			else return -1;

		}

}

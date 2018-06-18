package edu.illinois.cs.cogcomp.lorelei.cheaptrans;

import java.io.IOException;
import java.util.*;

public class Tester {
	public static void main (String[] args) throws IOException {
		Translate tl = new Translate("USEPAVLICK","lexicon","eng","fra");
		tl.loadDictionary();
		ArrayList<String> lines = new ArrayList<String>();
		lines.add("O	0	2	x	PRP	poster	x	x	0");
		tl.translateCONLL(lines);
		
		}
	}

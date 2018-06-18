package edu.illinois.cs.cogcomp.lorelei.cheaptrans;

import java.io.IOException;
import java.util.*;

public class Tester {
	public static String deleteTo(String str) {
		String[] temp = str.split("\\s+");
		if(temp[0].equals("to"))temp[0]="";
		return String.join(" ", temp);
	}
	public static void main (String[] args) throws IOException {
		System.out.println(Tester.deleteTo("to do"));
		
		}
	}

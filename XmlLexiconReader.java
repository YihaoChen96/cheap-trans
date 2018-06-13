package edu.illinois.cs.cogcomp.lorelei.cheaptrans;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlLexiconReader {
	private String inpath;
	private String outpath;
	public XmlLexiconReader() {
		
	}
	public XmlLexiconReader(String inpath,String outpath) {
		this.inpath = inpath;
		this.outpath = outpath;
	}
	public List<XmlLexicon> xmlParser(String inpath) throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document lexicon = db.parse(inpath);
		
		List<XmlLexicon> xml_lex = new ArrayList<XmlLexicon>();
		
		NodeList entries = lexicon.getElementsByTagName("ENTRY");

		for (int i = 0; i< entries.getLength();i++) {
			Element element = (Element)entries.item(i);
			String id = element.getAttribute("id");	
			String lemma = element.getElementsByTagName("LEMMA").item(0).getFirstChild().getNodeValue();
			String pos = element.getElementsByTagName("POS").item(0).getFirstChild().getNodeValue();
			NodeList gloss_lst = element.getElementsByTagName("GLOSS");
			ArrayList<String> glosses = new ArrayList<String>();
			
			for (int j=0;j<gloss_lst.getLength();j++) {
				String[] temp = gloss_lst.item(j).getFirstChild().getNodeValue().split(";|,");
				for (String s: temp) {
					glosses.add(s.trim());
				}
			}
			XmlLexicon xml_entry = new XmlLexicon(id, lemma, pos, glosses);
			xml_lex.add(xml_entry);
		}
		return xml_lex;
	}

	public void xmlToPavlick () throws SAXException, IOException, ParserConfigurationException {
		List<XmlLexicon> xml_lex = this.xmlParser(this.inpath);
		List<String> outlines = new ArrayList<String>();
		
		
		
		FileOutputStream output_stream = new FileOutputStream(this.outpath);
		OutputStreamWriter writer = new OutputStreamWriter(output_stream,"UTF-8");
		BufferedWriter bw = new BufferedWriter(writer);
		
		for (XmlLexicon xml_entry: xml_lex) {
			bw.write(xml_entry.toPavlickLine());
			bw.newLine();
		}
		bw.close();
	}
	
	
	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
		XmlLexiconReader xlr = new XmlLexiconReader(args[0],args[1]);
		xlr.xmlToPavlick();
	}
}

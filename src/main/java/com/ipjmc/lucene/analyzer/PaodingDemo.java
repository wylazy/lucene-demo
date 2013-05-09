package com.ipjmc.lucene.analyzer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.Scanner;

import net.paoding.analysis.analyzer.PaodingAnalyzer;
import net.paoding.analysis.analyzer.estimate.TryPaodingAnalyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Similarity;

public class PaodingDemo {

	private static final String text = "能够对未知的词汇进行合理解析";

	public static void main(String[] args) throws Exception {
		test2();
	}

	public static void test() throws Exception {
		PaodingAnalyzer analyzer = new PaodingAnalyzer();
		Scanner scanner = new Scanner(System.in);
		String words = scanner.next();
		
		TokenStream ts = analyzer.tokenStream("", new StringReader(words));
		ts.reset();

		TermAttribute termAtt = (TermAttribute) ts
				.addAttribute(TermAttribute.class);
		OffsetAttribute offAtt = (OffsetAttribute) ts
				.addAttribute(OffsetAttribute.class);

		while (ts.incrementToken()) {

			System.out.println(termAtt.term() + " " + offAtt.startOffset()
					+ " " + offAtt.endOffset());
		}

		System.out.println("Done");
		analyzer.close();
	}
	
	public static void test2() throws Exception {
		
		PaodingAnalyzer analyzer = new PaodingAnalyzer();
		TokenStream ts = analyzer.tokenStream("", new StringReader(text));

		CharTermAttribute ctAttr = ts.addAttribute(CharTermAttribute.class);
		PositionIncrementAttribute posAttr = ts.addAttribute(PositionIncrementAttribute.class);
		OffsetAttribute offsetAttr = ts.addAttribute(OffsetAttribute.class);
		TypeAttribute typeAttr = ts.addAttribute(TypeAttribute.class);
		
		int position = 0;
		while (ts.incrementToken()) {
			int increment = posAttr.getPositionIncrement();
			if (increment > 0) {
				position += increment;
				System.out.println("Position: " + position);
			}
			
			System.out.println("    [" + ctAttr.toString() + ": " + offsetAttr.startOffset() + " -> " + offsetAttr.endOffset() + " : " + typeAttr.type() + "]");
		}

		System.out.println("Done");
		analyzer.close();
	}

	public static void test3(String[] args) throws Exception {
		TryPaodingAnalyzer analyzer = new TryPaodingAnalyzer();
		analyzer.main(args);
	}

}

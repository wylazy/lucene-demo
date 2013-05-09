package com.ipjmc.lucene;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import com.ipjmc.lucene.collector.StatisticsCollector;

public class StaticsDemo {

	private static final Version DEFAULT_VERSION = Version.LUCENE_36;
	private static final String NAME = "names";
	
	private Directory dir;
	private IndexSearcher searcher;
	
	public void setup() throws CorruptIndexException, IOException {
		dir = new RAMDirectory();
		IndexWriter indexWriter = new IndexWriter(dir, new IndexWriterConfig(DEFAULT_VERSION, new WhitespaceAnalyzer(DEFAULT_VERSION)));
		
		for (int i = 0; i < 10086; i++) {
			addNames(indexWriter, "Wylazy");
		}
		
		for (int i = 0; i < 488; i++) {
			addNames(indexWriter, "Ipjmc");
		}
		
		addNames(indexWriter, "zzz");
		
		indexWriter.close();
		searcher = new IndexSearcher(IndexReader.open(dir));
	}
	
	private void addNames(IndexWriter indexWriter, String name) throws CorruptIndexException, IOException {
		Document doc = new Document();
		doc.add(new Field(NAME, name, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
		indexWriter.addDocument(doc);
	}
	
	public Map<String, Integer> statistics(String name) throws IOException {
		StatisticsCollector collector = new StatisticsCollector(NAME);
		
		Query query = new TermQuery(new Term(NAME, name));
		searcher.search(query, null, collector);
		
		for (Entry<String, Integer> entry : collector.getMap().entrySet()) {
			System.out.println(entry.getKey() + " => " + entry.getValue());
		}
		
		return collector.getMap();
	}
	
	public static void main(String [] args) throws Exception {
		StaticsDemo demo = new StaticsDemo();

		long time = System.currentTimeMillis();
		demo.setup();
		
		System.out.println("setup " + (System.currentTimeMillis() - time) + "ms");
		
		time = System.currentTimeMillis();
		demo.statistics("Wylazy");
		demo.statistics("Ipjmc");
		demo.statistics("zzz");
		
		System.out.println("statistics " + (System.currentTimeMillis() - time) + "ms");
		
	}
}

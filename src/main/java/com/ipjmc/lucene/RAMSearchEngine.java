package com.ipjmc.lucene;

import java.io.IOException;
import java.util.Arrays;
import net.paoding.analysis.analyzer.PaodingAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

/**
 * 用Lucene构建一个简单的搜索引擎示例，工作过程如下:
 * 1.原始内容->构建文档->创建索引
 * 2.查询关键字->构建Query->搜索
 * @author wylazy
 *
 */
public class RAMSearchEngine {

	private static Version DEFAULT_VERSION = Version.LUCENE_36;
	
	public static final String CONTENT = "contents";
	private static RAMSearchEngine searchEngine = new RAMSearchEngine();
	
	private Analyzer analyzer;
	private Directory indexDirectory;

	private RAMSearchEngine() {
		try {
			analyzer = new PaodingAnalyzer();
			indexDirectory = new RAMDirectory();
			//indexDirectory = FSDirectory.open(new File("/home/wylazy/index"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static RAMSearchEngine getInstance() {
		return searchEngine;
	}
	
	public void addIndex(String text) throws CorruptIndexException, LockObtainFailedException, IOException {
		IndexWriter indexWriter = new IndexWriter(indexDirectory, new IndexWriterConfig(DEFAULT_VERSION, analyzer));
		Document doc = new Document();
		doc.add(new Field(CONTENT, text, Store.YES, Index.ANALYZED, TermVector.WITH_POSITIONS_OFFSETS));
		indexWriter.addDocument(doc);
		indexWriter.close();
		System.out.println("Indexed success!");
	}
	
	public void search(String text) throws Exception {
		
		IndexSearcher searcher = new IndexSearcher(IndexReader.open(indexDirectory));
		
		QueryParser parser = new QueryParser(DEFAULT_VERSION, CONTENT, analyzer);
		Query query = parser.parse(text);
		System.out.println("##" +query.toString());
		
		TopDocs topDocs = searcher.search(query, 1);

		System.out.println("Found " + topDocs.totalHits + " hits.");
		for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
			Document doc = searcher.doc(scoreDoc.doc);
			System.out.println(Arrays.asList(doc.getValues(CONTENT)));
		}
		
		searcher.close();
	}
	public static void main(String [] args) {
		String s1 = "中国的通信产业正在迎来转型期，面临的风险与机会同时存在，在这个时候我们没有必要特别关注短期的业绩增长指标，更应该着眼长远拥抱未来。";
		RAMSearchEngine searchEngine = RAMSearchEngine.getInstance();
		try {
			searchEngine.addIndex(s1);
			searchEngine.search("通信产业转型");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

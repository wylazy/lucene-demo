package com.ipjmc.lucene;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldSelectorResult;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.search.FieldDoc;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopFieldDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import com.ipjmc.lucene.comparator.DistanceComparatorSource;

/**
 * 自定义排序搜索引擎，按照坐标排序
 * @author wylazy
 *
 */
public class PositionEngine {

	private static final Version DEFAULT_VERSION = Version.LUCENE_36;
	private static final String NAME = "names";
	private static final String POSITION = "position";
	private static final String TYPE = "type";
	private static final String RESTAURANT = "restaurant";
	
	private Directory dir;
	private IndexSearcher searcher;
	
	public void setup() throws CorruptIndexException, LockObtainFailedException, IOException {
		dir = new RAMDirectory();
		IndexWriter indexWriter = new IndexWriter(dir, new IndexWriterConfig(DEFAULT_VERSION, new WhitespaceAnalyzer(DEFAULT_VERSION)));
		addPoint(indexWriter, "Wylazy", 3, 3);
		addPoint(indexWriter, "Ipjmc", 2, 0);
		addPoint(indexWriter, "omt", 0, 100);
		addPoint(indexWriter, "zzz", 1, 1);
		addPoint(indexWriter, "Sgiz", 2, 2);
		
		indexWriter.close();
		
		searcher = new IndexSearcher(IndexReader.open(dir));
	}
	
	private void addPoint(IndexWriter indexWriter, String name, float x, float y) throws CorruptIndexException, IOException {
		Document doc = new Document();
		doc.add(new Field(NAME, name, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
		doc.add(new Field(POSITION, x + "," + y, Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
		doc.add(new Field(TYPE, RESTAURANT, Field.Store.NO, Field.Index.NOT_ANALYZED_NO_NORMS));
		indexWriter.addDocument(doc);
	}
	
	public List<String> seach(float x, float y) throws IOException {
		List<String> list = new LinkedList<String>();
		Sort sort = new Sort(new SortField(POSITION, new DistanceComparatorSource(x, y)));
		Query query = new TermQuery(new Term(TYPE, RESTAURANT));
		TopFieldDocs hits = searcher.search(query, null, 3, sort);
		
		for (ScoreDoc scoreDoc : hits.scoreDocs) {
			
			//获取匹配的用户名和距离
			list.add(searcher.doc(scoreDoc.doc).get(NAME) + ": " + ((FieldDoc)scoreDoc).fields[0]);
		}
		
		return list;
	}
	
	public static void main(String [] args) throws Exception {
		PositionEngine engine = new PositionEngine();
		engine.setup();
		System.out.println(engine.seach(0, 0));
	}
}

package com.ipjmc.lucene.collector;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.Scorer;

public class StatisticsCollector extends Collector {

	private Map<String, Integer> map;
	private String fieldName;
	private String [] values;
	
	public StatisticsCollector(String fieldName) {
		map = new HashMap<String, Integer>();
		this.fieldName = fieldName;
	}
	
	@Override
	public void setScorer(Scorer scorer) throws IOException {
		
	}

	@Override
	public void collect(int doc) throws IOException {
		Integer count = map.get(values[doc]);
		if (count == null) {
			count = 1;
		} else {
			count++;
		}
		
		map.put(values[doc], count);
	}

	@Override
	public void setNextReader(IndexReader reader, int docBase)
			throws IOException {
		values = FieldCache.DEFAULT.getStrings(reader, fieldName);
	}

	@Override
	public boolean acceptsDocsOutOfOrder() {
		return true;
	}

	
	public Map<String, Integer> getMap() {
		return this.map;
	}
}

package com.ipjmc.lucene.comparator;

import java.io.IOException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.FieldComparator;
import org.apache.lucene.search.FieldComparatorSource;


/**
 * 对坐标按距离(x,y)排序
 * @author wylazy
 *
 */
public class DistanceComparatorSource extends FieldComparatorSource {

	private static final long serialVersionUID = 1L;

	private float x;
	private float y;
	
	/**
	 * 设置坐标中心点
	 * @param x 横坐标
	 * @param y 纵坐标
	 */
	public DistanceComparatorSource(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public FieldComparator<?> newComparator(String fieldname, int numHits,
			int sortPos, boolean reversed) throws IOException {
		return new DistanceScoreLookupComparator(fieldname, numHits);
	}

	private class DistanceScoreLookupComparator extends FieldComparator<Float> {

		private String fieldName;
		private float [] values;
		private float bottom;
		private float [] xDoc, yDoc;
		
		public DistanceScoreLookupComparator(String fieldName, int numHits) {
			values = new float[numHits];
			this.fieldName = fieldName;
		}
		
		@Override
		public int compare(int slot1, int slot2) {
			if (values[slot1] < values[slot2]) {
				return -1;
			} else if (values[slot1] > values[slot2]) {
				return 1;
			}
			return 0;
		}

		/**
		 * 有限队列中最差的点
		 */
		@Override
		public void setBottom(int slot) {
			bottom = values[slot];
		}

		@Override
		public int compareBottom(int doc) throws IOException {
			float distance = getDistance(doc);
			if (bottom < distance) {
				return -1;
			} else if (bottom > distance) {
				return 1;
			}
			return 0;
		}

		@Override
		public void copy(int slot, int doc) throws IOException {
			values[slot] = getDistance(doc);
		}

		/**
		 * 初始化数据
		 * {@inheritDoc}
		 */
		@Override
		public void setNextReader(IndexReader reader, int docBase) throws IOException {
			
			String [] positions = FieldCache.DEFAULT.getStrings(reader, fieldName);
			xDoc = new float[positions.length];
			yDoc = new float[positions.length];
			
			int i = 0;
			for (String position : positions) {
				String [] p = position.split(",");
				xDoc[i] = Float.parseFloat(p[0]);
				yDoc[i] = Float.parseFloat(p[1]);
				++i;
			}
			
		}

		@Override
		public Float value(int slot) {
			return new Float(values[slot]);
		}
		
		/**
		 * 计算文档doc距离中心点的距离
		 * @param doc
		 * @return
		 */
		private float getDistance(int doc) {
			float dx = xDoc[doc] - x;
			float dy = yDoc[doc] - y;
			
			return (float) Math.sqrt(dx*dx + dy*dy);
		}
		
	}
}

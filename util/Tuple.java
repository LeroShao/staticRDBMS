package util;

import nio.TupleReader;

public class Tuple {

	public int[] cols;
	public TupleReader tpReader = null;

	public Tuple(int[] cols) {
		this.cols = cols;
	}
	
	public int get(int i) {
		return cols[i];
	}
	
	public int length() {
		return cols.length;
	}
	
	
	@Override
	public String toString() {
		if(this.length() == 0)
			return "";
		
		StringBuilder sb = new StringBuilder(String.valueOf(cols[0]));
		int i = 1;
		while(i < this.length()) {
			sb.append(",");
			sb.append(String.valueOf(cols[i++]));
		}
		return sb.toString();
	}

	/**
	 * hash code of the tuple
	 * @return the hash code of the string form
	 */
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	/**
	 * 	compare the tuple element by element
	 */
	@Override
	public boolean equals(Object obj) {
		Tuple tp = (Tuple) obj;
		int len1 = this.length();
		int len2 = tp.length();
		if(len1 != len2)
			return false;
		for(int i = 0; i < len1; i++) {
			if(tp.get(i) != this.get(i))
				return false;
		}
		
		return true;
	}
}

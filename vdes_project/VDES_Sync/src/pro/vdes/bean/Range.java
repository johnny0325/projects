/**
 * Range.java 2009-9-1 ÏÂÎç04:50:00
 */
package pro.vdes.bean;

/**
 * @author aiyan
 * @version 1.0
 *
 */
public class Range {
	private String table;
	private String type;
	private String neCodeField;
	private String rangeField;
	private String rangeFieldName;
	private float range;
	
	/**
	 * 
	 */
	public Range() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the neCodeField
	 */
	public String getNeCodeField() {
		return neCodeField;
	}

	/**
	 * @param neCodeField the neCodeField to set
	 */
	public void setNeCodeField(String neCodeField) {
		this.neCodeField = neCodeField;
	}

	/**
	 * @return the range
	 */
	public float getRange() {
		return range;
	}

	/**
	 * @param range the range to set
	 */
	public void setRange(float range) {
		this.range = range;
	}

	/**
	 * @return the rangeField
	 */
	public String getRangeField() {
		return rangeField;
	}

	/**
	 * @param rangeField the rangeField to set
	 */
	public void setRangeField(String rangeField) {
		this.rangeField = rangeField;
	}

	/**
	 * @return the rangeFieldName
	 */
	public String getRangeFieldName() {
		return rangeFieldName;
	}

	/**
	 * @param rangeFieldName the rangeFieldName to set
	 */
	public void setRangeFieldName(String rangeFieldName) {
		this.rangeFieldName = rangeFieldName;
	}

	/**
	 * @return the table
	 */
	public String getTable() {
		return table;
	}

	/**
	 * @param table the table to set
	 */
	public void setTable(String table) {
		this.table = table;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}


}

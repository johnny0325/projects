/**
 * LookStatus.java 2009-9-9 ÏÂÎç05:33:16
 */
package pro.vdes.service.monitor;

/**
 * @author aiyan
 * @version 1.0
 *
 */
public class Look {
	private int type;

	private String content;

	private String chineseDesc;

	/**
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	public String getChineseDesc() {
		return chineseDesc;
	}

	public void setChineseDesc(String chineseDesc) {
		this.chineseDesc = chineseDesc;
	}

}

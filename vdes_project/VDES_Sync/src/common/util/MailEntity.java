package common.util;

import java.util.LinkedHashMap;

public class MailEntity {
	public String subject;//标题
	public String content;//内容
	public LinkedHashMap toAddress;//收件人
	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}
	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}
	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}
	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}
	/**
	 * @return the toAddress
	 */
	public LinkedHashMap getToAddress() {
		return toAddress;
	}
	/**
	 * @param toAddress the toAddress to set
	 */
	public void setToAddress(LinkedHashMap toAddress) {
		this.toAddress = toAddress;
	}
	


}

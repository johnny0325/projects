package common.dom4j;

import java.util.Map;

import org.dom4j.Document;

public interface XmlSupport {
	public Document createDocument();
	public Map readDocument(String file)throws Exception;
	public void writerDocument() throws Exception;
	public void updateDocument() ;
	
}

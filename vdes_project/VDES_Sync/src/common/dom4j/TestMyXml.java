package common.dom4j;



import junit.framework.TestCase;

public class TestMyXml extends TestCase{
	public void testAA() throws Exception{
		XmlSupport xml = new XmlSupportImpl();
		xml.writerDocument();
		//xml.readDocument();
	}

}

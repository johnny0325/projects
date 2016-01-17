package common.dom4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

//<?xml version="1.0" encoding="UTF-8"?>
//<infoSystem>
//	<uploadFile>
//		<path>/hjtInfoUpload/</path>
//		<size>10240</size>
//		<type>zip,rar,png,jpg,gif</type>
//	</uploadFile>
//</infoSystem>
public class XmlSupportImpl implements XmlSupport {

	public Document createDocument() {
		// TODO Auto-generated method stub
		Document document = DocumentHelper.createDocument();
		Element rootElement = document.addElement("root");

		Element uploadFileElement = rootElement.addElement("uploadFile");

		Element path = uploadFileElement.addElement("path");
		path.setText("/hjtInfoUpload/");
		Element size = uploadFileElement.addElement("size");
		size.setText("10240");
		Element type = uploadFileElement.addElement("type");
		type.setText("zip,rar,png,jpg,gif");

		return document;
	}

	public void writerDocument() throws Exception {
		/**//*
			 * 格式化输出
			 */
		OutputFormat of = new OutputFormat("   ", true);
		/**//*
			 * 输出到屏幕
			 */
		// XMLWriter xmlWriter = new XMLWriter(System.out, of);
		/**//*
			 * 输出到流中
			 */
		XMLWriter xmlWriter = new XMLWriter(new FileWriter("hjt_info.xml"), of);
		System.out.println(new File("hjt_info.xml").getAbsolutePath());
		xmlWriter.write(this.createDocument());
		xmlWriter.close();

	}

	public Map  readDocument(String file) throws Exception{
		// TODO Auto-generated method stub
		Map map = new HashMap();
		SAXReader saxReader =  new SAXReader();
		

		/**
		 *  先通过 org.dom4j.io.SAXReader 读入整份XML文档
		 */
		Document document = saxReader.read(new FileInputStream(new File(file)));
		Element root = document.getRootElement();
		List list = root.elements();
		for(int i=0;i<list.size();i++){
			Element ele = (Element) list.get(i);
			map.put(ele.getName(), ele.getText());
			
			
		}
		return map;
		
		

	}

	public void updateDocument() {
		// TODO Auto-generated method stub

	}


}

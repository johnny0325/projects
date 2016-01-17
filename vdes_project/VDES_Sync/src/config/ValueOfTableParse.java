/**
 * ValueOfTableParse.java 2009-11-13 ÉÏÎç09:22:48
 */
package config;

import java.io.File;
import java.io.IOException;

import org.apache.xmlbeans.XmlException;

import pro.vdes.bean.WarmBean;
import sample3.xmlbean.WarmsDocument;
import sample3.xmlbean.WarmsDocument.Warms.Warm;

import common.util.ConfigureUtil;

/**
 * @author aiyan
 * @version 1.0
 *
 */
public class ValueOfTableParse {
	private WarmsDocument doc ;

	public ValueOfTableParse(String file) throws XmlException, IOException{
		String basePath = new ConfigureUtil().getValue("rangeconf");
		File xmlFile = new File(basePath + "/"+file);
		doc=WarmsDocument.Factory.parse(xmlFile);
	}
	
	public WarmBean[] getWarms(){
		WarmBean[] warmBean = null;
		if(doc!=null&&doc.getWarms()!=null&&doc.getWarms().getWarmArray()!=null){
			int len = doc.getWarms().getWarmArray().length;
			Warm[] w = doc.getWarms().getWarmArray();
			if(len>0){
				warmBean = new WarmBean[len];
				for(int i=0;i<len;i++){
					warmBean[i]= new WarmBean();
					warmBean[i].setSql(w[i].getSql());
					warmBean[i].setType(w[i].getType());
					warmBean[i].setNeCode(w[i].getNeCode());
					warmBean[i].setStatDate(w[i].getStatDate());
					warmBean[i].setIndicators(w[i].getIndicators());
					warmBean[i].setCount(w[i].getCount());
					warmBean[i].setField(w[i].getField());
					warmBean[i].setRange(w[i].getRange().floatValue());
					
				}
			}
			
		}
		return warmBean;
		
	}
	
}

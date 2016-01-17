/**
 * RangeParse.java 2009-7-9 ÏÂÎç05:27:09
 */
package config;

import java.io.File;
import java.io.IOException;

import org.apache.xmlbeans.XmlException;

import pro.vdes.bean.Range;
import sample2.xmlbean.RulesDocument;
import sample2.xmlbean.RulesDocument.Rules.Rule;

import common.util.ConfigureUtil;


/**
 * @author aiyan
 * @version 1.0
 * 
 */
public class RangeParse {
	
	private RulesDocument doc ;
	public RangeParse(String file) throws XmlException, IOException{
		String basePath = new ConfigureUtil().getValue("rangeconf");
		File xmlFile = new File(basePath + "/"+file);
		doc=RulesDocument.Factory.parse(xmlFile);
	}
	
	public Range[] getRange(){
		Range[] range = null;
		if(doc!=null&&doc.getRules()!=null&&doc.getRules().getRuleArray()!=null){
			int len = doc.getRules().getRuleArray().length;
			Rule[] r = doc.getRules().getRuleArray();
			if(len>0){
				range = new Range[len];
				for(int i=0;i<len;i++){
					range[i]= new Range();
					range[i].setTable(r[i].getTable());
					range[i].setType(r[i].getType());
					range[i].setNeCodeField(r[i].getNeCodeField());
					range[i].setRangeField(r[i].getRangeField());
					range[i].setRangeFieldName(r[i].getRangeFieldName());
					range[i].setRange(r[i].getRange().floatValue());
				}
			}
			
		}
		return range;
		
	}
	
}

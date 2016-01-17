package common.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.moc.idgenerator.api.IIDGenerator;
import com.moc.idgenerator.api.IdGeneratorService;

/**
 * 自定义生成ID
 */
public class IncrementGenerator { 
private static final Log log = LogFactory.getLog(IncrementGenerator.class); 
private static  IIDGenerator idgen = IdGeneratorService.getIdGenterator();

public static String generate() {
	return idgen.getNextId(); 
} 
public static void  main(String[] argv){
	log.info(generate());
	log.info(generate());
	
}

} 


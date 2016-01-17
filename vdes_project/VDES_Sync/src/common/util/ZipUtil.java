/**
 * RarUtil.java 2010-6-30 ����11:03:21
 */
package common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author aiyan
 * @version 1.0
 * 
 */
public class ZipUtil {

	 /**
    * ���Խ�ѹ������. ��d:\\download\\test.zip��ͬ��Ŀ¼��ѹ��d:\\temp\\zipoutĿ¼��.
    *
    * @throws Exception
    */
   public static void releaseZipToFile(String sourceZip, String outFileName)
                   throws IOException{
                 ZipFile zfile=new ZipFile(sourceZip);
                 System.out.println(zfile.getName());
                 Enumeration zList=zfile.entries();
                 ZipEntry ze=null;
                 byte[] buf=new byte[1024];
                 while(zList.hasMoreElements()){
                 //��ZipFile�еõ�һ��ZipEntry
                 ze=(ZipEntry)zList.nextElement();
                 if(ze.isDirectory()){
                 continue;
                 }
                 //��ZipEntryΪ�����õ�һ��InputStream����д��OutputStream��
                 OutputStream os=new BufferedOutputStream(new FileOutputStream(getRealFileName(outFileName, ze.getName())));
                 InputStream is=new BufferedInputStream(zfile.getInputStream(ze));
                 int readLen=0;
                 while ((readLen=is.read(buf, 0, 1024))!=-1) {
                 os.write(buf, 0, readLen);
                 }
                 is.close();
                 os.close();
                 System.out.println("Extracted: "+ze.getName());
                 }
                 zfile.close();

   }
   
   /**
    * ������Ŀ¼������һ�����·������Ӧ��ʵ���ļ���.
    *
    * @param baseDir
    *            ָ����Ŀ¼
    * @param absFileName
    *            ���·������������ZipEntry�е�name
    * @return java.io.File ʵ�ʵ��ļ�
    */
   private static File getRealFileName(String baseDir, String absFileName) {
           String[] dirs = absFileName.split("/");
           //System.out.println(dirs.length);
           File ret = new File(baseDir);
           //System.out.println(ret);
           if (dirs.length > 1) {
                   for (int i = 0; i < dirs.length - 1; i++) {
                           ret = new File(ret, dirs[i]);
                   }
           }
           if (!ret.exists()) {
                   ret.mkdirs();
           }
           ret = new File(ret, dirs[dirs.length - 1]);
           return ret;
   }

}

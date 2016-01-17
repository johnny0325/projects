/**
 * RarUtil.java 2010-6-30 上午11:03:21
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
    * 测试解压缩功能. 将d:\\download\\test.zip连同子目录解压到d:\\temp\\zipout目录下.
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
                 //从ZipFile中得到一个ZipEntry
                 ze=(ZipEntry)zList.nextElement();
                 if(ze.isDirectory()){
                 continue;
                 }
                 //以ZipEntry为参数得到一个InputStream，并写到OutputStream中
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
    * 给定根目录，返回一个相对路径所对应的实际文件名.
    *
    * @param baseDir
    *            指定根目录
    * @param absFileName
    *            相对路径名，来自于ZipEntry中的name
    * @return java.io.File 实际的文件
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

/**
 * RarUtil.java 2010-6-30 上午11:03:21
 */
package common.util;

/**
 * @author aiyan
 * @version 1.0
 * 
 */
public class RarUtil {
	private static String unrarCmd = "D:\\WinRAR\\UnRAR x ";

	/**
	 * 将1个RAR文件解压 rarFileName 需要解压的RAR文件(必须包含路径信息以及后缀) destDir 解压后的文件放置目录
	 */
	public static void unRARFile(String rarFileName, String destDir) {
		unrarCmd += rarFileName + " " + destDir;
		System.out.println(unrarCmd);
		try {
			Runtime rt = Runtime.getRuntime();
			rt.exec(unrarCmd);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}

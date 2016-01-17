/**
 * RarUtil.java 2010-6-30 ����11:03:21
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
	 * ��1��RAR�ļ���ѹ rarFileName ��Ҫ��ѹ��RAR�ļ�(�������·����Ϣ�Լ���׺) destDir ��ѹ����ļ�����Ŀ¼
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

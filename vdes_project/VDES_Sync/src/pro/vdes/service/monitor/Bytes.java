/**
 * fff.java 2009-9-8 ����04:43:03
 */
package pro.vdes.service.monitor;

/**
 * @author aiyan
 * @version 1.0
 *
 */
/**  
 * byte������.  
 */  
public class Bytes {   
    /**  
     * ����String.subString�Ժ��ִ���������⣨��һ��������Ϊһ���ֽ�)������� �������ֵ��ַ���ʱ�����������ֵ������£�  
     *   
     * @param src Ҫ��ȡ���ַ���  
     * @param start_idx ��ʼ���꣨����������)  
     * @param end_idx ��ֹ���꣨���������꣩  
     * @return  
     */  
    public static String substring(String src, int start_idx, int end_idx) {   
        byte[] b = src.getBytes();   
        String tgt = "";   
        for (int i = start_idx; i <= end_idx; i++) {   
            tgt += (char) b[i];   
        }   
        return tgt;   
    }   
} 


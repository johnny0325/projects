/**
 * aaa.java 2009-9-8 ����04:01:25
 */
package pro.vdes.service.monitor;

import java.io.File;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.management.ManagementFactory;

import org.apache.log4j.Logger;

import pro.vdes.task.TaskCheckReport;

import com.sun.management.OperatingSystemMXBean;

/**
 * @author aiyan
 * @version 1.0
 *
 */

	public class MonitorServiceImpl implements IMonitorService {   
		private static Logger log = Logger.getLogger(MonitorServiceImpl.class);
	    private static final int CPUTIME = 30;   
	    private static final int PERCENT = 100;   
	    private static final int FAULTLENGTH = 10;   
	    /**  
	     * ��õ�ǰ�ļ�ض���.  
	     *   
	     * @return ���ع���õļ�ض���  
	     * @throws Exception  
	     */  
	    public MonitorInfoBean getMonitorInfoBean() throws Exception {   
	        int kb = 1024;   
	        // ��ʹ���ڴ�   
	        long totalMemory = Runtime.getRuntime().totalMemory() / kb;   
	        // ʣ���ڴ�   
	        long freeMemory = Runtime.getRuntime().freeMemory() / kb;   
	        // ����ʹ���ڴ�   
	        long maxMemory = Runtime.getRuntime().maxMemory() / kb;   
	        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();   
	        // ����ϵͳ   
	        String osName = System.getProperty("os.name");   
	        // �ܵ������ڴ�   
	        long totalMemorySize = osmxb.getTotalPhysicalMemorySize() / kb;   
	        // ʣ��������ڴ�   
	        long freePhysicalMemorySize = osmxb.getFreePhysicalMemorySize() / kb;   
	        // ��ʹ�õ������ڴ�   
	        long usedMemory = (osmxb.getTotalPhysicalMemorySize() - osmxb.getFreePhysicalMemorySize()) / kb;   
	        // ����߳�����   
	        ThreadGroup parentThread;   
	        for (parentThread = Thread.currentThread().getThreadGroup(); parentThread.getParent() != null; parentThread = parentThread.getParent());   
	        int totalThread = parentThread.activeCount();   
	        double cpuRatio = 0;   
	        if (osName.toLowerCase().startsWith("windows")) {   
	            cpuRatio = this.getCpuRatioForWindows();   
	        }  
	        File[] listRoots = File.listRoots();
	        // ���췵�ض���   
	        
	        MonitorInfoBean infoBean = new MonitorInfoBean();   
	        infoBean.setListRoots(listRoots);
	        infoBean.setFreeMemory(freeMemory);   
	        infoBean.setFreePhysicalMemorySize(freePhysicalMemorySize);   
	        infoBean.setMaxMemory(maxMemory);   
	        infoBean.setOsName(osName);   
	        infoBean.setTotalMemory(totalMemory);   
	        infoBean.setTotalMemorySize(totalMemorySize);   
	        infoBean.setTotalThread(totalThread);   
	        infoBean.setUsedMemory(usedMemory);   
	        infoBean.setCpuRatio(cpuRatio);   
	        return infoBean;   
	    }       
	    /**  
	     * ���CPUʹ����.  
	     *   
	     * @return ����cpuʹ����  
	     */  
	    private double getCpuRatioForWindows() {   
	        try {   
	            String procCmd = System.getenv("windir") + "\\system32\\wbem\\wmic.exe process get Caption,CommandLine,KernelModeTime,ReadOperationCount,ThreadCount,UserModeTime,WriteOperationCount";   
	            // ȡ������Ϣ   
	            long[] c0 = readCpu(Runtime.getRuntime().exec(procCmd));   
	            Thread.sleep(CPUTIME);   
	            long[] c1 = readCpu(Runtime.getRuntime().exec(procCmd));   
	            if (c0 != null && c1 != null) {   
	                long idletime = c1[0] - c0[0];   
	                long busytime = c1[1] - c0[1];   
	                return Double.valueOf(PERCENT * (busytime) / (busytime + idletime)).doubleValue();   
	            } else {   
	                return 0.0;   
	            }   
	        } catch (Exception ex) {   
	            ex.printStackTrace();   
	            return 0.0;   
	        }   
	    } 
	    /**  
	     * ��ȡCPU��Ϣ.  
	     * @param proc  
	     * @return  
	     */  
	    private long[] readCpu(final Process proc) {   
	        long[] retn = new long[2];   
	        try {   
	            proc.getOutputStream().close();   
	            InputStreamReader ir = new InputStreamReader(proc.getInputStream());   
	            LineNumberReader input = new LineNumberReader(ir);   
	            String line = input.readLine();   
	            if (line == null || line.length() < FAULTLENGTH) {   
	                return null;   
	            }   
	            int capidx = line.indexOf("Caption");   
	            int cmdidx = line.indexOf("CommandLine");   
	            int rocidx = line.indexOf("ReadOperationCount");   
	            int umtidx = line.indexOf("UserModeTime");   
	            int kmtidx = line.indexOf("KernelModeTime");   
	            int wocidx = line.indexOf("WriteOperationCount");   
	            long idletime = 0;   
	            long kneltime = 0;   
	            long usertime = 0;   
	            while ((line = input.readLine()) != null) {   
	                if (line.length() < wocidx) {   
	                    continue;   
	                }   
	                // �ֶγ���˳��Caption,CommandLine,KernelModeTime,ReadOperationCount,   
	                // ThreadCount,UserModeTime,WriteOperation   
	                String caption = Bytes.substring(line, capidx, cmdidx - 1).trim();   
	                String cmd = Bytes.substring(line, cmdidx, kmtidx - 1).trim();   
	                if (cmd.indexOf("wmic.exe") >= 0) {   
	                    continue;   
	                }   
	                String s1 = Bytes.substring(line, kmtidx, rocidx - 1).trim();   
	                String s2 = Bytes.substring(line, umtidx, wocidx - 1).trim();   
	                if (caption.equals("System Idle Process") || caption.equals("System")) {   
	                    if (s1.length() > 0)   
	                        idletime += Long.valueOf(s1).longValue();   
	                    if (s2.length() > 0)   
	                        idletime += Long.valueOf(s2).longValue();   
	                    continue;   
	                }   
	                if (s1.length() > 0)   
	                    kneltime += Long.valueOf(s1).longValue();   
	                if (s2.length() > 0)   
	                    usertime += Long.valueOf(s2).longValue();   
	            }   
	            retn[0] = idletime;   
	            retn[1] = kneltime + usertime;   
	            return retn;   
	        } catch (Exception ex) {   
	            ex.printStackTrace();   
	        } finally {   
	            try {   
	                proc.getInputStream().close();   
	            } catch (Exception e) {   
	                log.error(e);   
	            }   
	        }   
	        return null;   
	    } 
	    
	    /**  
	     * ���Է���.  
	     *   
	     * @param args  
	     * @throws Exception  
	     */  
	    public static void main(String[] args) throws Exception {   
	        IMonitorService service = new MonitorServiceImpl();   
	        MonitorInfoBean monitorInfo = service.getMonitorInfoBean();   
	        System.out.println("cpuռ����=" + monitorInfo.getCpuRatio());   
	        System.out.println("��ʹ���ڴ�=" + monitorInfo.getTotalMemory());   
	        System.out.println("ʣ���ڴ�=" + monitorInfo.getFreeMemory());   
	        System.out.println("����ʹ���ڴ�=" + monitorInfo.getMaxMemory());   
	        System.out.println("����ϵͳ=" + monitorInfo.getOsName());   
	        System.out.println("�ܵ������ڴ�=" + monitorInfo.getTotalMemorySize() + "kb");   
	        System.out.println("ʣ��������ڴ�=" + monitorInfo.getFreeMemory() + "kb");   
	        System.out.println("��ʹ�õ������ڴ�=" + monitorInfo.getUsedMemory() + "kb");   
	        System.out.println("�߳�����=" + monitorInfo.getTotalThread() + "kb");   
	    }

	}

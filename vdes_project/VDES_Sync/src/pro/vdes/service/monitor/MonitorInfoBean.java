/**
 * MonitorInfoBean.java 2009-9-8 ����03:47:12
 */
package pro.vdes.service.monitor;

import java.io.File;

/**
 * @author aiyan
 * @version 1.0
 *
 */
public class MonitorInfoBean {  
	/** Ӳ������ */
	private File[] listRoots;
	
    /** ��ʹ���ڴ�. */  
    private long totalMemory;   
    /** ʣ���ڴ�. */  
    private long freeMemory;   
    /** ����ʹ���ڴ�. */  
    private long maxMemory;   
    /** ����ϵͳ. */  
    private String osName;   
    /** �ܵ������ڴ�. */  
    private long totalMemorySize;   
    /** ʣ��������ڴ�. */  
    private long freePhysicalMemorySize;   
    /** ��ʹ�õ������ڴ�. */  
    private long usedMemory;   
    /** �߳�����. */  
    private int totalThread;   
    /** cpuʹ����. */  
    private double cpuRatio;  
    
    
    
    /**
	 * @return the listRoots
	 */
	public File[] getListRoots() {
		return listRoots;
	}
	/**
	 * @param listRoots the listRoots to set
	 */
	public void setListRoots(File[] listRoots) {
		this.listRoots = listRoots;
	}
	public long getFreeMemory() {   
        return freeMemory;   
    }   
    public void setFreeMemory(long freeMemory) {   
        this.freeMemory = freeMemory;   
    }   
    public long getFreePhysicalMemorySize() {   
        return freePhysicalMemorySize;   
    }   
    public void setFreePhysicalMemorySize(long freePhysicalMemorySize) {   
        this.freePhysicalMemorySize = freePhysicalMemorySize;   
    }   
    public long getMaxMemory() {   
        return maxMemory;   
    }   
    public void setMaxMemory(long maxMemory) {   
        this.maxMemory = maxMemory;   
    }   
    public String getOsName() {   
        return osName;   
    }   
    public void setOsName(String osName) {   
        this.osName = osName;   
    }   
    public long getTotalMemory() {   
        return totalMemory;   
    }   
    public void setTotalMemory(long totalMemory) {   
        this.totalMemory = totalMemory;   
    }   
    public long getTotalMemorySize() {   
        return totalMemorySize;   
    }   
    public void setTotalMemorySize(long totalMemorySize) {   
        this.totalMemorySize = totalMemorySize;   
    }   
    public int getTotalThread() {   
        return totalThread;   
    }   
    public void setTotalThread(int totalThread) {   
        this.totalThread = totalThread;   
    }   
    public long getUsedMemory() {   
        return usedMemory;   
    }   
    public void setUsedMemory(long usedMemory) {   
        this.usedMemory = usedMemory;   
    }   
    public double getCpuRatio() {   
        return cpuRatio;   
    }   
    public void setCpuRatio(double cpuRatio) {   
        this.cpuRatio = cpuRatio;   
    }   
}


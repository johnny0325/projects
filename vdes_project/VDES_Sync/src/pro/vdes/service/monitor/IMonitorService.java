/**
 * IMonitorService.java 2009-9-8 下午03:48:46
 */
package pro.vdes.service.monitor;


/**
 * @author aiyan
 * @version 1.0
 *
 */
/**  
 * 获取系统信息的业务逻辑类接口.  
 */  
public interface IMonitorService {   
    /**  
     * 获得当前的监控对象.  
     * @return 返回构造好的监控对象  
     * @throws Exception  
     */  
    public MonitorInfoBean getMonitorInfoBean() throws Exception;   
}  
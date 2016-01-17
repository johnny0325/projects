/**
 * WarmDao.java 2009-4-27 下午05:04:13
 */
package common.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import test.pro.vdes.task.TaskFCityLogTest_thread;

import common.util.DBUtil;



/**
 * @author aiyan
 * @version 1.0
 *
 */
public class CommonDao {
	private static Logger log = Logger.getLogger(CommonDao.class);
	private Connection conn;
	private String defaultPool = "proxool.vdes";
	public CommonDao() throws Exception {	
        this.conn = DBUtil.getConnection(defaultPool);
    }   
	public CommonDao(String pool) throws Exception {	
        this.conn = DBUtil.getConnection(pool);
    } 
	public void begin() throws Exception{
        if(conn != null) {
            try {
                conn.setAutoCommit(false);
                System.out.println("conn start:"+conn);
            } catch (SQLException e) {
                throw new Exception("can not begin transaction", e);
            }
        } else {
            throw new Exception("connection not opened!");
        }
    }
    
    public void commit() throws Exception {
        try {
            if (conn != null && !conn.getAutoCommit()) {
                conn.commit();
                conn.setAutoCommit(true);
            } else {
                if (conn == null) {
                    throw new Exception("connection not opened!");
                } else {
                    throw new Exception("first begin then commit please!");
                }
            }
        } catch (SQLException e) {
            throw new Exception("can not commit transaction!", e);
        }
    }
    
    public void rollback() throws Exception {
        try {
            if (conn != null && !conn.getAutoCommit()) {
                conn.rollback();
                conn.setAutoCommit(true);
            } else {
                if (conn == null) {
                    throw new Exception("connection not opened!");
                } else {
                    throw new Exception("first begin then rollback please!");
                }
            }
        } catch (SQLException e) {
            throw new Exception("can not rollback transaction!", e);
        }
    }
    private List convert(ResultSet rs) throws Exception {

        // record list
        List retList = new ArrayList();

        try {
            ResultSetMetaData meta = rs.getMetaData();

            // column count
            int colCount = meta.getColumnCount();

            // each record
            while (rs.next()) {

                Map recordMap = new HashMap();

                // each column
                for (int i = 1; i <= colCount; i++) {
                    // column name
                    String name = meta.getColumnName(i);
                    // column value
                    Object value = rs.getObject(i);
                    // add column to record
                    recordMap.put(name, value);
                }
                // ad record to list
                retList.add(recordMap);
            }
        } catch (SQLException ex) {
            throw new Exception("can not convert result set to list of map", ex);
        }
        return retList;
    }
    private void apply(PreparedStatement pstmt, List params) throws Exception {
        try {
            // if params exist
            if (params != null && params.size() > 0) {
                // parameters iterator
                Iterator it = params.iterator();
                
                // parameter index
                int index = 1;
                while(it.hasNext()) {
                    
                    Object obj = it.next();
                    // if null set ""
                    if (obj == null) {
                        pstmt.setObject(index, "");
                    } else {
                        // else set object
                        pstmt.setObject(index, obj);
                    }
                    
                    //next index
                    index++;
                }
            }
        } catch (SQLException ex) {
            throw new Exception("can not apply parameter", ex);
        }
    }
    public List query(String sql, List params) throws Exception {
        List result = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sql);
            this.apply(pstmt, params);
            rs = pstmt.executeQuery();
            result = this.convert(rs);
        } catch (SQLException ex) {
            throw new Exception("can not execute query", ex);
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    // nothing
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    // nothing
                }
            }
        }

        return result;
    }
/*    特殊的查询方法（返回单值）
    有时候为了方便使用，我们需要返回单值的产寻方法，例如 select max(id) from table_a, select count(id) from table_b等。以下的代码使用了上述通用的查询方法，代码为：*/

    public Object queryOne(String sql, List params) throws Exception {
        List list = this.query(sql, params);
        
        if(list == null || list.size() == 0) {
            throw new Exception("data not exist");
        } else {
            Map record = (Map)list.get(0);
            if(record == null || record.size() == 0 ) {
                throw new Exception("data not exist");
            } else {
                return record.values().toArray()[0];
            }
        }
    }

/*    更新，删除，插入方法
    由于在JDBC中这三个方法都是用了一个execute完成，所以这里我们也使用一个方法来完成这些功能。代码为：*/

    public int execute(String sql, List params) throws Exception {
        int ret = 0;
        PreparedStatement pstmt = null;
        try {
            pstmt = conn.prepareStatement(sql);
            this.apply(pstmt, params);
            ret = pstmt.executeUpdate();
        }catch(SQLException ex) {
            throw new Exception("", ex);
        } finally {
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    // nothing.
                }
            }
        }
        
        return ret;
    }
/*    批处理方法（查询）
    有些时候为了便于操作，需要一次查询多条SQL语句，我们称之为批处理，实现参看以下方法，其中为了和query方法做区分，将参数和返回值都改为了数组形式。*/

    public List[] queryBatch(String[] sqlArray, List[] paramArray) throws Exception {
        List rets = new ArrayList();
        if(sqlArray.length != paramArray.length) {
            throw new Exception("sql size not equal parameter size");
        } else {
            for(int i = 0; i < sqlArray.length; i++) {
                String sql = sqlArray[i];
                List param = paramArray[i];
                List ret = this.query(sql, param);
                rets.add(ret);
            }
            return (List[])rets.toArray();
        }
    }
/*    批处理方法（更新）
    有些时候需要一次更新多条Sql语句，为了便于操作，添加了批处理更新操作，参看以下代码，为了和更新方法区分，将参数和返回值都改为了数组形式。*/
    public int[] executeBatch(String[] sqlArray, List[] paramArray) throws Exception {
        List rets = new ArrayList();
        if(sqlArray.length != paramArray.length) {
            throw new Exception("sql size not equal parameter size");
        } else {
            for(int i = 0; i < sqlArray.length; i++) {
                int ret = this.execute(sqlArray[i], paramArray[i]);
                rets.add(new Integer(ret));
            }
            
            int[] retArray = new int[rets.size()];
            for(int i = 0; i < retArray.length; i++) {
                retArray[i] = ((Integer)rets.get(i)).intValue();
            }
            
            return retArray;
        }
    }
/*    资源释放
    由于CommonDao有一个Connection的属性，且Connection属于稀缺资源，所以在CommonDao不需要在使用的时候需要显示的关闭Connection。代码如下：*/
    public void close() throws Exception{
        try {
            if (conn != null && conn.getAutoCommit()) {
            	System.out.println("conn close:"+conn);
                conn.close();
            } else {
                if(conn == null) {
                    throw new Exception("can not close null connection, first new then close");
                } else {
                    throw new Exception("transaction is running, rollbakc or commit befor close please.");
                }
            }
        } catch (SQLException ex) {
            throw new Exception("Can not close common dao");
        }
    }


}

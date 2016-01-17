package com.practise.my.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.practise.my.java.framework.freemarker.Column;
import com.practise.my.java.framework.freemarker.Table;

public class DatabaseMetaDataUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(DatabaseMetaDataUtil.class);
	
	private DatabaseMetaData dbMetaData = null;

	private Connection con = null;
	
	public DatabaseMetaDataUtil() {
		this.getDatabaseMetaData();
	}

	public void getDatabaseMetaData() {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			if (dbMetaData == null) {
				String url = "jdbc:oracle:thin:@10.20.4.100:1521/testdb";
				Properties props =new Properties();
				props.put("remarksReporting","true");//获取备注
				props.put("user","paydtest");
				props.put("password","paydtest");
				con = DriverManager.getConnection(url, props);
//				String user = "paydtest";
//				String password = "paydtest";
//				con = DriverManager.getConnection(url, user, password);
				dbMetaData = con.getMetaData();
				logger.info("获取数据库元信息成功.");
			}
		} catch (Exception e) {
			logger.info("获取数据库的元数据信息异常:"+e.getMessage());
		}
	}

	public void colseCon() {
		try {
			if (con != null) {
				con.close();
			}
			if (dbMetaData != null){
				dbMetaData = null;
			}
			logger.info("关闭数据库连接成功.");
		} catch (Exception e) {
			logger.info("关闭数据库连接异常:"+e.getMessage());
		}
	}
	
	public void printResultSet(ResultSet rs){
		try {
			ResultSetMetaData rsmd = rs.getMetaData();   
			int columnCount = rsmd.getColumnCount();   
			// 输出列名   
			for (int i=1; i<=columnCount; i++){   
			    logger.info(rsmd.getColumnName(i)+"(" + rsmd.getColumnTypeName(i) + "("+rsmd.getColumnDisplaySize(i)+"))");   
			}   
			// 输出数据   
			while (rs.next()){   
			    for (int i=1; i<=columnCount; i++){   
			        logger.info(rs.getString(i) + " ");   
			    }   
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}   
	}

	/**
	 * 打印数据库的一些相关信息
	 */
	public void printDataBaseInformations() {
		try {
			logger.info("URL:" + dbMetaData.getURL() + ";");
			logger.info("UserName:" + dbMetaData.getUserName() + ";");
			logger.info("isReadOnly:" + dbMetaData.isReadOnly() + ";");
			logger.info("DatabaseProductName:" + dbMetaData.getDatabaseProductName() + ";");
			logger.info("DatabaseProductVersion:" + dbMetaData.getDatabaseProductVersion() + ";");
			logger.info("DriverName:" + dbMetaData.getDriverName() + ";");
			logger.info("DriverVersion:" + dbMetaData.getDriverVersion());
			ResultSet rs = dbMetaData.getSchemas();//TABLE_SCHEM
			while(rs.next()){
				logger.info(rs.getString("TABLE_SCHEM"));
			}
//			printResultSet(rs);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获得该用户下面的所有表
	 */
	public List<Table> getAllTableList(String schemaName) {
		List<Table> tableNameList = new ArrayList<Table>();
		try {
			// table type. Typical types are "TABLE", "VIEW", "SYSTEM
			// TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS",
			// "SYNONYM".
			String[] types = { "TABLE" };
			ResultSet rs = dbMetaData.getTables(null, schemaName, "%", types);
			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME");
				String tableType = rs.getString("TABLE_TYPE");
				String remarks = rs.getString("REMARKS");// explanatory comment on the table
				logger.info(remarks + "-" +tableName );
				Table table = new Table();
				table.setTableName(tableName);
				table.setRemark(remarks==null?"":remarks);
				tableNameList.add(table);
			}
		} catch (Exception e) {
			logger.info("获取用户["+schemaName+"]数据库表信息异常:"+e.getMessage());
		}
		return tableNameList;
	}
	
	/**
	 * 获得该用户下面的所有表
	 */
	public List<Table> getTableInfo(String schemaName,String tableN) {
		List<Table> tableNameList = new ArrayList<Table>();
		try {
			// table type. Typical types are "TABLE", "VIEW", "SYSTEM
			// TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS",
			// "SYNONYM".
			String[] types = { "TABLE" };
			ResultSet rs = dbMetaData.getTables(null, schemaName, tableN, types);
			while (rs.next()) {
				String tableName = rs.getString("TABLE_NAME");
				String tableType = rs.getString("TABLE_TYPE");
				String remarks = rs.getString("REMARKS");// explanatory comment on the table
				logger.info(tableName + "-" + tableType + "-" + remarks);
				Table table = new Table();
				table.setTableName(tableName);
				table.setRemark(remarks==null?"":remarks);
				tableNameList.add(table);
			}
		} catch (Exception e) {
			logger.info("获取用户["+schemaName+"]数据库表["+tableN+"]信息异常:"+e.getMessage());
		}
		return tableNameList;
	}

	/**
	 * 获得该用户下面的所有视图
	 */
	public List<Table> getAllViewList(String schemaName) {
		List<Table> tableNameList = new ArrayList<Table>();
		try {
			String[] types = { "VIEW" };
			ResultSet rs = dbMetaData.getTables(null, schemaName, "%", types);
			while (rs.next()) {
				String viewName = rs.getString("TABLE_NAME");
				// table type. Typical types are "TABLE", "VIEW", "SYSTEM
				// TABLE", "GLOBAL TEMPORARY", "LOCAL TEMPORARY", "ALIAS",
				// "SYNONYM".
				String viewType = rs.getString("TABLE_TYPE");
				// explanatory comment on the table
				String remarks = rs.getString("REMARKS");
				logger.info(viewName + "-" + viewType + "-" + remarks);
				Table table = new Table();
				table.setTableName(viewName);
				table.setRemark(remarks==null?"":remarks);
				tableNameList.add(table);
			}
		} catch (SQLException e) {
			logger.info("获取用户["+schemaName+"]数据库视图信息异常:"+e.getMessage());
		}
		return tableNameList;
	}

	/**
	 * 获得数据库中所有方案名称
	 */
	public void printAllSchemas() {
		try {
			ResultSet rs = dbMetaData.getSchemas();
			while (rs.next()) {
				String tableSchem = rs.getString("TABLE_SCHEM");
				logger.info(tableSchem);
			}
		} catch (SQLException e) {
			logger.info("获取数据库方案信息异常:"+e.getMessage());
		}
	}

	/**
	 * 获得表或视图中的所有列信息
	 * 
	 * null-PAYDTEST-SUBJECT_DAY-WORKDATE-12-VARCHAR2-8-0-10-1-工作日期-null-0-016-1-YES-
     * null-PAYDTEST-SUBJECT_DAY-SUBJECTCODE-12-VARCHAR2-9-0-10-1-科目代码-null-0-018-2-YES-
     * null-PAYDTEST-SUBJECT_DAY-DEBITAMOUNT-3-NUMBER-15-2-10-1-昨日余额(借)-null-0-022-3-YES-
     * null-PAYDTEST-SUBJECT_DAY-CREDITAMOUNT-3-NUMBER-15-2-10-1-昨日余额(贷)-null-0-022-4-YES-
     * null-PAYDTEST-SUBJECT_DAY-DEBIT_NETAMOUNT-3-NUMBER-15-2-10-1-本日发生额(借)-null-0-022-5-YES-
     * null-PAYDTEST-SUBJECT_DAY-CREDIT_NETAMOUNT-3-NUMBER-15-2-10-1-本日发生额(贷)-null-0-022-6-YES-
     * null-PAYDTEST-SUBJECT_DAY-CURRDEBITAMOUNT-3-NUMBER-15-2-10-1-当日余额(借)-null-0-022-7-YES-
     * null-PAYDTEST-SUBJECT_DAY-CURRCREDITAMOUNT-3-NUMBER-15-2-10-1-当日余额(贷)-null-0-022-8-YES-
     * null-PAYDTEST-SUBJECT_DAY-ZY-12-VARCHAR2-60-0-10-1-摘要(关联商户)-null-0-0120-9-YES-
     * null-PAYDTEST-SUBJECT_DAY-TODAYDEBITAMOUNT-3-NUMBER-18-2-10-1-null-null-0-022-10-YES-
     * null-PAYDTEST-SUBJECT_DAY-TODAYCREDITAMOUNT-3-NUMBER-18-2-10-1-null-null-0-022-11-YES-
	 * 
	 */
	public List<Column> getTableColumns(String schemaName, String tableName) {
		List<Column> columnList = new ArrayList<Column>();
		try {
			ResultSet rs = dbMetaData.getColumns(null, schemaName, tableName, "%");
			while (rs.next()) {
				// table catalog (may be null)
				String tableCat = rs.getString("TABLE_CAT");
				// table schema (may be null)
				String tableSchemaName = rs.getString("TABLE_SCHEM");
				// table name
				String tableName_ = rs.getString("TABLE_NAME");
				// column name
				String columnName = rs.getString("COLUMN_NAME");
				// SQL type from java.sql.Types
				int dataType = rs.getInt("DATA_TYPE");
				// Data source dependent type name, for a UDT the type name is
				// fully qualified
				String dataTypeName = rs.getString("TYPE_NAME");
				// table schema (may be null)
				int columnSize = rs.getInt("COLUMN_SIZE");
				// the number of fractional digits. Null is returned for data
				// types where DECIMAL_DIGITS is not applicable.
				int decimalDigits = rs.getInt("DECIMAL_DIGITS");
				// Radix (typically either 10 or 2)
				int numPrecRadix = rs.getInt("NUM_PREC_RADIX");
				// is NULL allowed.
				int nullAble = rs.getInt("NULLABLE");
				// comment describing column (may be null)
				String remarks = rs.getString("REMARKS");
				// default value for the column, which should be interpreted as
				// a string when the value is enclosed in single quotes (may be
				// null)
				String columnDef = rs.getString("COLUMN_DEF");
				//
				int sqlDataType = rs.getInt("SQL_DATA_TYPE");
				//
				int sqlDatetimeSub = rs.getInt("SQL_DATETIME_SUB");
				// for char types the maximum number of bytes in the column
				int charOctetLength = rs.getInt("CHAR_OCTET_LENGTH");
				// index of column in table (starting at 1)
				int ordinalPosition = rs.getInt("ORDINAL_POSITION");
				// ISO rules are used to determine the nullability for a column.
				// YES --- if the parameter can include NULLs;
				// NO --- if the parameter cannot include NULLs
				// empty string --- if the nullability for the parameter is
				// unknown
				String isNullAble = rs.getString("IS_NULLABLE");
				// Indicates whether this column is auto incremented
				// YES --- if the column is auto incremented
				// NO --- if the column is not auto incremented
				// empty string --- if it cannot be determined whether the
				// column is auto incremented parameter is unknown
//				String isAutoincrement = rs.getString("IS_AUTOINCREMENT");//列名无效
				logger.info(tableCat + "-" + tableSchemaName + "-" + tableName_ + "-" + columnName + "-" + dataType + "-" + dataTypeName + "-" + columnSize + "-"
						+ decimalDigits + "-" + numPrecRadix + "-" + nullAble + "-" + remarks + "-" + columnDef + "-" + sqlDataType + "-" + sqlDatetimeSub + "-" + charOctetLength + "-"
						+ ordinalPosition + "-" + isNullAble + "-" );
				Column column = new Column();
				column.setCnName(paraseColumnCNName(columnName,remarks));
//				column.setCnName(remarks==null?"":remarks);
				column.setEnName(columnName);
				column.setNullAble(isNullAble);
				column.setType(dataTypeName);
				column.setComment(remarks==null?"":remarks);
				column.setColumnSize(columnSize);
				column.setDecimalDigits(decimalDigits);
				columnList.add(column);
			}
		} catch (Exception e) {
			logger.info("获取用户["+schemaName+"]数据库表["+tableName+"]列信息异常:"+e.getMessage());
		}
		return columnList;
	}
	
	/**
     * 匹配第一段连续的中文
     * @Date 2014-4-15下午3:12:35
     * @return void
     */
	String cnreg = "([\u4e00-\u9fa5]+)";
	Pattern cnPattern = Pattern.compile(cnreg);
	public String paraseColumnCNName(String columnName, String remark){
		String res = "";
		if(StringUtils.isEmpty(remark)) return res; 
		Matcher matcher = cnPattern.matcher(remark);
    	if(matcher.find()){
    		res = matcher.group(1);
    		if(!StringUtils.isEmpty(columnName)){
    			if("STATE".equals(columnName.trim())){//状态
	    			if(!res.contains("状态")){
	    				res = "状态";
	    			}
    			}else if(columnName.trim().endsWith("TYPE")){//类型
    				if(!res.contains("类") && !res.contains("别")){//类型,类别,行别
    					res = "类型";
    				}
    				
    			}
    		}
    	}
    	return res;
	}

	/**
	 * 获得一个表的索引信息
	 * 
	 * false-null-SUBJECT_DAY_UK1-1-1-SUBJECTCODE-null-425
     * false-null-SUBJECT_DAY_UK1-1-2-WORKDATE-null-425
     * false-null-SUBJECT_DAY_UK1-1-3-ZY-null-425
     * true-null-I_SUBJECT_DAY_WORKDATE-1-1-WORKDATE-null-25
	 */
	public List[] getIndexInfo(String schemaName, String tableName,String pk) {
		List<String> comIndexList = new ArrayList<String>();
		List<String> uniIndexList = new ArrayList<String>();
		StringBuffer comIndex = new StringBuffer();
		StringBuffer uniIndex = new StringBuffer();
		try {
			Map<String, SortedMap<String, String>> comIndexMaps = new HashMap<String, SortedMap<String, String>>();
//			SortedMap<String, String> comIndexMap = new TreeMap<String, String>();
			Map<String, SortedMap<String, String>> uniIndexMaps = new HashMap<String, SortedMap<String, String>>();
//			SortedMap<String, String> uniIndexMap = new TreeMap<String, String>();
			SortedMap<String, String> tmpMap;
			ResultSet rs = dbMetaData.getIndexInfo(null, schemaName, tableName, false, true);
			while (rs.next()) {
				// Can index values be non-unique. false when TYPE is
				// tableIndexStatistic
				boolean nonUnique = rs.getBoolean("NON_UNIQUE");
				// index catalog (may be null); null when TYPE is
				// tableIndexStatistic
				String indexQualifier = rs.getString("INDEX_QUALIFIER");
				// index name; null when TYPE is tableIndexStatistic
				String indexName = rs.getString("INDEX_NAME");
				// index type:
				// tableIndexStatistic - this identifies table statistics that
				// are returned in conjuction with a table's index descriptions
				// tableIndexClustered - this is a clustered index
				// tableIndexHashed - this is a hashed index
				// tableIndexOther - this is some other style of index
				short type = rs.getShort("TYPE");
				// column sequence number within index; zero when TYPE is
				// tableIndexStatistic
				short ordinalPosition = rs.getShort("ORDINAL_POSITION");
				// column name; null when TYPE is tableIndexStatistic
				String columnName = rs.getString("COLUMN_NAME");
				// column sort sequence, "A" => ascending, "D" => descending,
				// may be null if sort sequence is not supported; null when TYPE
				// is tableIndexStatistic
				String ascOrDesc = rs.getString("ASC_OR_DESC");
				// When TYPE is tableIndexStatistic, then this is the number of
				// rows in the table; otherwise, it is the number of unique
				// values in the index.
				int cardinality = rs.getInt("CARDINALITY");
				logger.info(nonUnique + "-" + indexQualifier + "-" + indexName + "-" + type + "-" + ordinalPosition + "-" + columnName + "-" + ascOrDesc + "-" + cardinality);
				if(!StringUtils.isEmpty(indexName)){
					if(!nonUnique){
						tmpMap = uniIndexMaps.get(indexName);
						if(tmpMap == null){
							tmpMap = new TreeMap<String, String>();
							uniIndexMaps.put(indexName, tmpMap);
						}
						tmpMap.put(String.valueOf(ordinalPosition), columnName);
//						uniIndex.append(indexName).append("(").append(columnName).append(")").append(Constants.RET);
					}else{
						tmpMap = comIndexMaps.get(indexName);
						if(tmpMap == null){
							tmpMap = new TreeMap<String, String>();
							comIndexMaps.put(indexName, tmpMap);
						}
						tmpMap.put(String.valueOf(ordinalPosition), columnName);
//						comIndex.append(indexName).append("(").append(columnName).append(")").append(Constants.RET);
					}
				}
			}
			if(uniIndexMaps.size() > 0){
				Iterator  iterator = uniIndexMaps.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry<String, SortedMap<String, String>> entry = (Map.Entry<String, SortedMap<String, String>>) iterator.next();
					uniIndex.append(entry.getKey()).append("(");
					Iterator  it = entry.getValue().entrySet().iterator();
					while(it.hasNext()){
						Map.Entry<String, String> e = (Map.Entry<String, String>)it.next();
						String columnName = e.getValue();
						if(it.hasNext()){
						    uniIndex.append(columnName).append(",");
						}else{
							uniIndex.append(columnName);
						}
					}
					uniIndex.append(")");
					if(!StringUtils.isEmpty(pk) && !uniIndex.toString().contains(pk)){ //剔除主键索引
					    uniIndexList.add(uniIndex.toString());
					}
					uniIndex = new StringBuffer();
				}
			}
			if(comIndexMaps.size() > 0){
				Iterator  iterator = comIndexMaps.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry<String, SortedMap<String, String>> entry = (Map.Entry<String, SortedMap<String, String>>) iterator.next();
					comIndex.append(entry.getKey()).append("(");
					Iterator  it = entry.getValue().entrySet().iterator();
					while(it.hasNext()){
						Map.Entry<String, String> e = (Map.Entry<String, String>)it.next();
						String columnName = e.getValue();
						if(it.hasNext()){
							comIndex.append(columnName).append(",");
						}else{
							comIndex.append(columnName);
						}
					}
					comIndex.append(")");
					comIndexList.add(comIndex.toString());
					comIndex = new StringBuffer();
				}
			}
		} catch (Exception e) {
			logger.info("获取用户["+schemaName+"]数据库表["+tableName+"]索引信息异常:"+e.getMessage());
		}
		return new List[]{comIndexList,uniIndexList};
	}

	/**
	 * 获得一个表的主键信息
	 * 
	 * DETAILNO-2-PK_PAYDETAIL_ID
     * SN-1-PK_PAYDETAIL_ID
	 */
	public String getAllPrimaryKeys(String schemaName, String tableName) {
		StringBuffer pk = new StringBuffer();
		String pkName = "";
		try {
			SortedMap<String, String> pkColumn = new TreeMap<String, String>();
			ResultSet rs = dbMetaData.getPrimaryKeys(null, schemaName, tableName);
			while (rs.next()) {
				// column name
				String columnName = rs.getString("COLUMN_NAME");
				// sequence number within primary key( a value of 1 represents
				// the first column of the primary key, a value of 2 would
				// represent the second column within the primary key).
				short keySeq = rs.getShort("KEY_SEQ");
				// primary key name (may be null)
				pkName = rs.getString("PK_NAME");
				pkColumn.put(String.valueOf(keySeq), columnName);
				logger.info(columnName + "-" + keySeq + "-" + pkName);
			}
			if(pkColumn.size() > 0){
//				pk.append(pkName).append("(");
				for (Map.Entry<String, String> entry : pkColumn.entrySet()) {
					String columnName = entry.getValue();
					pk.append(columnName).append(",");
				}
				pk.deleteCharAt(pk.length()-1);
//				pk.append(")");
			}
		} catch (Exception e) {
			logger.info("获取用户["+schemaName+"]数据库表["+tableName+"]主键信息异常:"+e.getMessage());
		}
		return pk.toString();
	}

	/**
	 * 获得一个表的外键信息(本表列作为其它表的外键)
	 */
	public List<String> getAllExportedKeys(String schemaName, String tableName) {
		List<String> fkList = new ArrayList<String>();
		StringBuffer fk = new StringBuffer();
		try {
			ResultSet rs = dbMetaData.getExportedKeys(null, schemaName, tableName);
			while (rs.next()) {
				// primary key table catalog (may be null)
				String pkTableCat = rs.getString("PKTABLE_CAT");
				// primary key table schema (may be null)
				String pkTableSchem = rs.getString("PKTABLE_SCHEM");
				// primary key table name
				String pkTableName = rs.getString("PKTABLE_NAME");
				// primary key column name
				String pkColumnName = rs.getString("PKCOLUMN_NAME");
				// foreign key table catalog (may be null) being exported (may
				// be null)
				String fkTableCat = rs.getString("FKTABLE_CAT");
				// foreign key table schema (may be null) being exported (may be
				// null)
				String fkTableSchem = rs.getString("FKTABLE_SCHEM");
				// foreign key table name being exported
				String fkTableName = rs.getString("FKTABLE_NAME");
				// foreign key column name being exported
				String fkColumnName = rs.getString("FKCOLUMN_NAME");
				// sequence number within foreign key( a value of 1 represents
				// the first column of the foreign key, a value of 2 would
				// represent the second column within the foreign key).
				short keySeq = rs.getShort("KEY_SEQ");
				// What happens to foreign key when primary is updated:
				// importedNoAction - do not allow update of primary key if it
				// has been imported
				// importedKeyCascade - change imported key to agree with
				// primary key update
				// importedKeySetNull - change imported key to NULL if its
				// primary key has been updated
				// importedKeySetDefault - change imported key to default values
				// if its primary key has been updated
				// importedKeyRestrict - same as importedKeyNoAction (for ODBC
				// 2.x compatibility)
				short updateRule = rs.getShort("UPDATE_RULE");

				// What happens to the foreign key when primary is deleted.
				// importedKeyNoAction - do not allow delete of primary key if
				// it has been imported
				// importedKeyCascade - delete rows that import a deleted key
				// importedKeySetNull - change imported key to NULL if its
				// primary key has been deleted
				// importedKeyRestrict - same as importedKeyNoAction (for ODBC
				// 2.x compatibility)
				// importedKeySetDefault - change imported key to default if its
				// primary key has been deleted
				short delRule = rs.getShort("DELETE_RULE");
				// foreign key name (may be null)
				String fkName = rs.getString("FK_NAME");
				// primary key name (may be null)
				String pkName = rs.getString("PK_NAME");
				// can the evaluation of foreign key constraints be deferred
				// until commit
				// importedKeyInitiallyDeferred - see SQL92 for definition
				// importedKeyInitiallyImmediate - see SQL92 for definition
				// importedKeyNotDeferrable - see SQL92 for definition
				short deferRability = rs.getShort("DEFERRABILITY");
				logger.info(pkTableCat + "-" + pkTableSchem + "-" + pkTableName + "-" + pkColumnName + "-" 
				          + fkTableCat + "-" + fkTableSchem + "-" + fkTableName + "-" + fkColumnName + "-" 
						  + keySeq + "-" + updateRule + "-" + delRule + "-" + fkName + "-" + pkName + "-" + deferRability);
				fk.append(fkName).append("(")
				.append(fkColumnName)
				.append(" --> ")
				.append(pkTableName).append(".").append(pkColumnName)
				.append(")");
				fkList.add(fk.toString());
				fk = new StringBuffer();
			}
		} catch (Exception e) {
			logger.info("获取用户["+schemaName+"]数据库表["+tableName+"]外键信息异常:"+e.getMessage());
		}
		return fkList;
	}
	
	/**
	 * 获得一个表的外键信息(引用其它表列为外键)
	 */
	public List<String> getAllImportedKeys(String schemaName, String tableName) {
		List<String> fkList = new ArrayList<String>();
		StringBuffer fk = new StringBuffer();
		try {
			ResultSet rs = dbMetaData.getImportedKeys(null, schemaName, tableName);
			while (rs.next()) {
				// primary key table catalog (may be null)
				String pkTableCat = rs.getString("PKTABLE_CAT");
				// primary key table schema (may be null)
				String pkTableSchem = rs.getString("PKTABLE_SCHEM");
				// primary key table name
				String pkTableName = rs.getString("PKTABLE_NAME");
				// primary key column name
				String pkColumnName = rs.getString("PKCOLUMN_NAME");
				// foreign key table catalog (may be null) being exported (may
				// be null)
				String fkTableCat = rs.getString("FKTABLE_CAT");
				// foreign key table schema (may be null) being exported (may be
				// null)
				String fkTableSchem = rs.getString("FKTABLE_SCHEM");
				// foreign key table name being exported
				String fkTableName = rs.getString("FKTABLE_NAME");
				// foreign key column name being exported
				String fkColumnName = rs.getString("FKCOLUMN_NAME");
				// sequence number within foreign key( a value of 1 represents
				// the first column of the foreign key, a value of 2 would
				// represent the second column within the foreign key).
				short keySeq = rs.getShort("KEY_SEQ");
				// What happens to foreign key when primary is updated:
				// importedNoAction - do not allow update of primary key if it
				// has been imported
				// importedKeyCascade - change imported key to agree with
				// primary key update
				// importedKeySetNull - change imported key to NULL if its
				// primary key has been updated
				// importedKeySetDefault - change imported key to default values
				// if its primary key has been updated
				// importedKeyRestrict - same as importedKeyNoAction (for ODBC
				// 2.x compatibility)
				short updateRule = rs.getShort("UPDATE_RULE");

				// What happens to the foreign key when primary is deleted.
				// importedKeyNoAction - do not allow delete of primary key if
				// it has been imported
				// importedKeyCascade - delete rows that import a deleted key
				// importedKeySetNull - change imported key to NULL if its
				// primary key has been deleted
				// importedKeyRestrict - same as importedKeyNoAction (for ODBC
				// 2.x compatibility)
				// importedKeySetDefault - change imported key to default if its
				// primary key has been deleted
				short delRule = rs.getShort("DELETE_RULE");
				// foreign key name (may be null)
				String fkName = rs.getString("FK_NAME");
				// primary key name (may be null)
				String pkName = rs.getString("PK_NAME");
				// can the evaluation of foreign key constraints be deferred
				// until commit
				// importedKeyInitiallyDeferred - see SQL92 for definition
				// importedKeyInitiallyImmediate - see SQL92 for definition
				// importedKeyNotDeferrable - see SQL92 for definition
				short deferRability = rs.getShort("DEFERRABILITY");
				logger.info(pkTableCat + "-" + pkTableSchem + "-" + pkTableName + "-" + pkColumnName + "-" 
				          + fkTableCat + "-" + fkTableSchem + "-" + fkTableName + "-" + fkColumnName + "-" 
						  + keySeq + "-" + updateRule + "-" + delRule + "-" + fkName + "-" + pkName + "-" + deferRability);
				fk.append(fkName).append("(")
				.append(fkColumnName)
				.append(" --> ")
				.append(pkTableName).append(".").append(pkColumnName)
				.append(")");
				fkList.add(fk.toString());
				fk = new StringBuffer();
			}
		} catch (Exception e) {
			logger.info("获取用户["+schemaName+"]数据库表["+tableName+"]外键(引用)信息异常:"+e.getMessage());
		}
		return fkList;
	}
	
}
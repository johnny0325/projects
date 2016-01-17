/**
 * DBVistor.java 2009-9-9 ÉÏÎç11:25:42
 */
package pro.vdes.service.monitor;

/**
 * @author aiyan
 * @version 1.0
 *
 */
public class DBVisitor implements Visitor{

	/* (non-Javadoc)
	 * @see pro.vdes.service.monitor.Visitor#visit()
	 */
	public String visit() {
		// TODO Auto-generated method stub
		String ret = "";
		String ret_DBVDESVisitor = new DBVDESVisitor().visit();
		String ret_DBPartnerVisitor = new DBPartnerVisitor().visit();
		ret += ";db_vdes:" + ret_DBVDESVisitor;
		ret += ";db_partner:" + ret_DBPartnerVisitor;
		// System.out.println(new DBICareVisitor().visit());
		return ret;

	}
	public static void main(String[] argv){
		System.out.println("----");
		System.out.println(new DBVisitor().visit());
		System.out.println("----");
	}

}

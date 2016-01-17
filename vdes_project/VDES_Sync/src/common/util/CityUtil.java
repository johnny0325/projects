package common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CityUtil {

	private static Map<String/* name */, City> nameMap = null;

	private static Map<String/* code */, City> codeMap = null;

	private static List<City> cities = null;

	private static void initNameMap() {
		nameMap = new HashMap<String, City>();
		nameMap.put(null, null);
		nameMap.put("省公司", City.PRIVINCE);

		for (City city : getCities()) {
			nameMap.put(city.getName(), city);
		}
	}

	private static void initCodeMap() {
		codeMap = new HashMap<String, City>();
		codeMap.put(null, null);
		codeMap.put("province", City.PRIVINCE);

		for (City city : getCities()) {
			codeMap.put(city.getCode(), city);
		}
	}

	public static City getCityByCode(String code) {
		if (codeMap == null) {
			initCodeMap();
		}
		return codeMap.get(code);
	}

	public static City getCityByName(String name) {
		if (nameMap == null) {
			initNameMap();
		}
		return nameMap.get(name);
	}

	public static String getName(String code) {
		City city = getCityByCode(code);
		if (city == null) {
			return null;
		}
		return city.getName();
	}

	public static String getCode(String name) {
		City city = getCityByName(name);
		if (city == null) {
			return null;
		}
		return city.getCode();
	}

	public static List<City> getCities() {
		if (cities == null) {
			cities = new ArrayList<City>();

			cities.add(City.GUANG_ZHOU);
			cities.add(City.SHEN_ZHEN);
			cities.add(City.DONG_GUAN);
			cities.add(City.FO_SHAN);
			cities.add(City.SHAN_TOU);

			cities.add(City.ZHU_HAI);
			cities.add(City.HUI_ZHOU);
			cities.add(City.ZHONG_SHAN);
			cities.add(City.JIANG_MEN);
			cities.add(City.SHAO_GUAN);

			cities.add(City.MEI_ZHOU);
			cities.add(City.SHAN_WEI);
			cities.add(City.YANG_JIANG);
			cities.add(City.ZHAN_JIANG);
			cities.add(City.MAO_MING);

			cities.add(City.ZHAO_QING);
			cities.add(City.QING_YUAN);
			cities.add(City.CHAO_ZHOU);
			cities.add(City.JIE_YANG);
			cities.add(City.YUN_FU);

			cities.add(City.HE_YUAN);

			cities.add(City.OTHER);
		}

		return cities;
	}

	public static final class City implements Comparable<City> {
		private String code = null;

		private String name = null;

		private String fullname = null;

		private int showOrder = 0;

		public City(String code, String name, String fullname, int showOrder) {
			this.code = code;
			this.name = name;
			this.fullname = fullname;
			this.showOrder = showOrder;
		}

		public String getCode() {
			return code;
		}

		public String getName() {
			return name;
		}

		public String getFullname() {
			return fullname;
		}

		public int getShowOrder() {
			return showOrder;
		}

		public static final City PRIVINCE = new City("province", "省公司", "省公司",
				0);

		public static final City GUANG_ZHOU = new City("gz", "广州", "广州市", 1);
		public static final City SHEN_ZHEN = new City("sz", "深圳", "深圳市", 2);
		public static final City DONG_GUAN = new City("dg", "东莞", "东莞市", 3);
		public static final City FO_SHAN = new City("fs", "佛山", "佛山市", 4);
		public static final City SHAN_TOU = new City("st", "汕头", "汕头市", 5);

		public static final City ZHU_HAI = new City("zh", "珠海", "珠海市", 6);
		public static final City HUI_ZHOU = new City("hz", "惠州", "惠州市", 7);
		public static final City ZHONG_SHAN = new City("zs", "中山", "中山市", 8);
		public static final City JIANG_MEN = new City("jm", "江门", "江门市", 9);
		public static final City SHAO_GUAN = new City("sg", "韶关", "韶关市", 10);

		public static final City MEI_ZHOU = new City("mz", "梅州", "梅州市", 11);
		public static final City SHAN_WEI = new City("sw", "汕尾", "汕尾市", 12);
		public static final City YANG_JIANG = new City("yj", "阳江", "阳江市", 13);
		public static final City ZHAN_JIANG = new City("zj", "湛江", "湛江市", 14);
		public static final City MAO_MING = new City("mm", "茂名", "茂名市", 15);

		public static final City ZHAO_QING = new City("zq", "肇庆", "肇庆市", 16);
		public static final City QING_YUAN = new City("qy", "清远", "清远市", 17);
		public static final City CHAO_ZHOU = new City("cz", "潮州", "潮州市", 18);
		public static final City JIE_YANG = new City("jy", "揭阳", "揭阳市", 19);
		public static final City YUN_FU = new City("yf", "云浮", "云浮市", 20);

		public static final City HE_YUAN = new City("hy", "河源", "河源市", 21);

		public static final City OTHER = new City("other", "其他", "其他地区", 22);

		public int compareTo(City city) {
			return showOrder - city.showOrder;
		}

	}
}

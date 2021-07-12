package cn.hawy.quick.core.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MapUtils {
	
	public static Map<String, Object> removeStrNull(Map<String, Object> map) {
		Set<String> set = map.keySet();
		Iterator<String> it = set.iterator();
		List<String> listKey = new ArrayList<String>();
		while (it.hasNext()) {
		  String str = it.next();
		  if(map.get(str)==null || "".equals(map.get(str))){
			  listKey.add(str) ;
		  }
		}
		for (String key : listKey) {
			map.remove(key);
		}
		return map;
	}

}

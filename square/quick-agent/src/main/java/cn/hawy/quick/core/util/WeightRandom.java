package cn.hawy.quick.core.util;

import java.util.List;
import java.util.TreeMap;

public class WeightRandom <K,V extends Number>{
	private TreeMap<Double, K> weightMap = new TreeMap<Double, K>();

	/*public WeightRandom(List<Pair<K, V>> list) {
        Preconditions.checkNotNull(list, "list can NOT be null!");
        for (Pair<K, V> pair : list) {
            double lastWeight = this.weightMap.size() == 0 ? 0 : this.weightMap.lastKey().doubleValue();//统一转为double
            this.weightMap.put(pair.getValue().doubleValue() + lastWeight, pair.getKey());//权重累加
        }
    }*/
}

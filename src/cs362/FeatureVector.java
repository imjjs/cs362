package cs362;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
public class FeatureVector implements Serializable {

    private Map<Integer, Double> _vec = new TreeMap<Integer, Double>();
	public void add(int index, double value) {
        this._vec.put(index, value);
	}
	
	public double get(int index) {
		return _vec.get(index);
	}
    public Map<Integer, Double> getMap(){return _vec;};
    public Set<Map.Entry<Integer, Double>> getEntrySet(){
        return _vec.entrySet();
    }
}

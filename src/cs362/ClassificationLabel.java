package cs362;

import java.io.Serializable;

public class ClassificationLabel extends Label implements Serializable {

    private int _label;
	public ClassificationLabel(int label) {
		this._label = label;
	}

	@Override
	public String toString() {
		return String.valueOf(_label);
	}

}

package cs362;

import java.io.Serializable;

public class RegressionLabel extends Label implements Serializable {

    double _label;
	public RegressionLabel(double label) {
		_label = label;
	}

	@Override
	public String toString() {
		return String.valueOf(_label);
	}

}

package neural;

import java.io.Serializable;

public class LinearActivationMethod implements ActivationMethod, Serializable {

	@Override
	public double activate(double weightedSum) {
		return weightedSum;
	}

	@Override
	public double derivative(double weightedSum) {
		return 1;
	}

	@Override
	public ActivationMethod copy() {
		return new LinearActivationMethod();
	}

}
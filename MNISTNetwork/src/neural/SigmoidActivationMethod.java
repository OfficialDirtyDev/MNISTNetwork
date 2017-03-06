package neural;

import java.io.Serializable;

public class SigmoidActivationMethod implements ActivationMethod, Serializable {

	@Override
	public double activate(double weightedSum) {
		return (1 / (1 + Math.exp(-weightedSum)));
	}

	@Override
	public double derivative(double weightedSum) {
		return weightedSum * (1.0 - weightedSum);
	}

	@Override
	public ActivationMethod copy() {
		return new SigmoidActivationMethod();
	}

}
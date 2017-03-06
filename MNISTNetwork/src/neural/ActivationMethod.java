package neural;

public interface ActivationMethod {
	double activate(double weightedSum);

	double derivative(double weightedSum);

	ActivationMethod copy();
}
package neural;

public class ThresholdActivationStrategy implements ActivationMethod {

    private double threshold;

    public ThresholdActivationStrategy(double threshold) {
        this.threshold = threshold;
        System.out.println("Initializing ThresholdActivationStrategy with " + threshold);
    }

    public double activate(double weightedSum) {
        return weightedSum > threshold ? 1 : 0;
    }

    public double derivative(double weightedSum) {
        return 0;
    }

    public ThresholdActivationStrategy copy() {
        return new ThresholdActivationStrategy(threshold);
    }
}

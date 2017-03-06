package neural;

public class TrainingSet {

    private double[] inputs;
    private double[] goodOutput;

    public TrainingSet(double[] inputs, double[] goodOutput) {
        this.inputs = inputs;
        this.goodOutput = goodOutput;
    }

    public double[] getInputs() {
        return inputs;
    }

    public double[] getGoodOutput() {
        return goodOutput;
    }
}

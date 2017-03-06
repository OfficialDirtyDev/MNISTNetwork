package neural;

public class TrainingData {

    private double[][] inputs;
    private double[][] outputs;

    public TrainingData(double[][] inputs, double[][] outputs) {
        this.inputs = inputs;
        this.outputs = outputs;
    }

    public double[][] getInputs() {
        return inputs;
    }

    public double[][] getOutputs() {
        return outputs;
    }
}

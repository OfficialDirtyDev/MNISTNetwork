package neural;

/**
 * Basic training class containing an array of example inputs and outputs
 * @author DirtyDev
 * Created by DirtyDev on 06.03.2017, 20:34:39
 *
 */
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

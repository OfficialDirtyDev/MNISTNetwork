package neural;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Neuron implements Serializable {

    private List<Synapse> inputs;
    private ActivationMethod activationMethod;
    private double output;
    private double derivative;
    private double weightedSum;
    private double error;

    public Neuron(ActivationMethod activationMethod) {
        inputs = new ArrayList<Synapse>();
        this.activationMethod = activationMethod;
        error = 0;
    }

    public void addInput(Synapse input) {
        inputs.add(input);
    }

    public List<Synapse> getInputs() {
        return this.inputs;
    }

    public double[] getWeights() {
        double[] weights = new double[inputs.size()];

        int i = 0;
        for(Synapse synapse : inputs) {
            weights[i] = synapse.getWeight();
            i++;
        }

        return weights;
    }

    private void calculateWeightedSum() {
        weightedSum = 0;
        for(Synapse synapse : inputs) {
            weightedSum += synapse.getWeight() * synapse.getSourceNeuron().getOutput();
        }
    }

    public void activate() {
        calculateWeightedSum();
        output = activationMethod.activate(weightedSum);
        derivative = activationMethod.derivative(output);
    }

    public double getOutput() {
        return this.output;
    }

    public void setOutput(double output) {
        this.output = output;
    }

    public double getDerivative() {
        return this.derivative;
    }

    public ActivationMethod getActivationMethod() {
        return activationMethod;
    }

    public double getError() {
        return error;
    }

    public void setError(double error) {
        this.error = error;
    }
}

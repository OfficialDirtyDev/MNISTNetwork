package neural;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jfree.data.xy.XYSeries;

public class Backpropagator {

	private Network neuralNetwork;
	private double learningRate;
	private double momentum;
	private double characteristicTime;
	private double currentEpoch;
	private boolean running = false;
	private Runnable afterTraining;

	public Backpropagator(Network neuralNetwork, double learningRate, double momentum, double characteristicTime) {
		this.neuralNetwork = neuralNetwork;
		this.learningRate = learningRate;
		this.momentum = momentum;
		this.characteristicTime = characteristicTime;
	}

	public double sum(List<Double> list) {
		double sum = 0;
		for (double i : list)
			sum = sum + i;
		return sum;
	}

	public void train(XYSeries errorSeries, XYSeries averageSeries, TrainingDataGenerator generator,
			double errorThreshold, int sequenceLength) {
		System.out.println("Starting training with errorThreshold = " + errorThreshold);
		int epoch = 0;
		running = true;
		if (errorSeries != null) {
			errorSeries.add(0, 1);
		}
		if (averageSeries != null) {
			averageSeries.add(0, 1);
		}
		double err = 1;
		double average = 1;
		ArrayList<Double> errors = new ArrayList<>();
		do {
			TrainingData trainingData = generator.getTrainingData();
			int dataLength = trainingData.getInputs().length;
			double error = backpropagate(trainingData.getInputs(), trainingData.getOutputs());
			errors.add(error);

			if (epoch >= sequenceLength) {
				average = (sum(errors.subList(errors.size() - sequenceLength, errors.size()))
						/ (sequenceLength * dataLength));
			}

			epoch++;

			if (errorSeries != null) {
				errorSeries.add(epoch, Math.min(error / dataLength, 1));
			}
			if (averageSeries != null) {
				averageSeries.add(epoch, Math.min(average, 1));
			}
			currentEpoch = epoch;
			err = error;
			System.out.println("Error for epoch " + epoch + ": " + error + ", Average: " + average);
		} while (average > errorThreshold && running);
		System.out.println("After backpropagation, last error = " + err + ", last average = " + average);
		if (this.afterTraining != null) {
			this.afterTraining.run();
			this.afterTraining = null;
		}
		running = false;
	}

	public void cancel(Runnable afterTraining) {
		cancel(afterTraining, false);
	}

	public void cancel(Runnable finished, boolean shutdown) {
		System.out.println("Cancelling training, shutdown " + shutdown);
		if(!running) {
			System.out.println("!running");
			this.neuralNetwork.persist();
			System.exit(0);
		}
		
		this.afterTraining = (() -> {
			System.out.println("Running afterTraining");
			finished.run();
			if (shutdown) {
				System.out.println("Finally exiting");
				System.exit(0);
			}
		});
		running = false;
		System.out.println("SETT");
	}

	public double backpropagate(double[][] inputs, double[][] expectedOutputs) {

		double error = 0;

		Map<Synapse, Double> synapseNeuronDeltaMap = new HashMap<Synapse, Double>();

		for (int i = 0; i < inputs.length; i++) {
			double[] input = inputs[i];
			double[] expectedOutput = expectedOutputs[i];
			List<Layer> layers = neuralNetwork.getLayers();

			neuralNetwork.setInputs(input);
			double[] output = neuralNetwork.getOutput();

			// First step of the backpropagation algorithm. Backpropagate errors
			// from the output layer all the way up
			// to the first hidden layer
			for (int j = layers.size() - 1; j > 0; j--) {
				Layer layer = layers.get(j);
				int start = layer.hasBias() ? 1 : 0;
				for (int k = start; k < layer.getNeurons().size(); k++) {
					Neuron neuron = layer.getNeurons().get(k);
					double neuronError = 0;
					if (layer.isOutputLayer()) {
						// the order of output and expected determines the sign
						// of the delta. if we have output - expected, we
						// subtract the delta
						// if we have expected - output we add the delta.
						neuronError = neuron.getDerivative() * (output[k - start] - expectedOutput[k - start]);
					} else {
						neuronError = neuron.getDerivative();

						double sum = 0;
						List<Neuron> downstreamNeurons = layer.getNextLayer().getNeurons();
						for (Neuron downstreamNeuron : downstreamNeurons) {

							int l = 0;
							boolean found = false;
							while (l < downstreamNeuron.getInputs().size() && !found) {
								Synapse synapse = downstreamNeuron.getInputs().get(l);

								if (synapse.getSourceNeuron() == neuron) {
									sum += (synapse.getWeight() * downstreamNeuron.getError());
									found = true;
								}

								l++;
							}
						}

						neuronError *= sum;
					}

					neuron.setError(neuronError);
				}
			}

			// Second step of the backpropagation algorithm. Using the errors
			// calculated above, update the weights of the
			// network
			for (int j = layers.size() - 1; j > 0; j--) {
				Layer layer = layers.get(j);

				for (Neuron neuron : layer.getNeurons()) {

					for (Synapse synapse : neuron.getInputs()) {

						double newLearningRate = characteristicTime > 0
								? learningRate / (1 + (currentEpoch / characteristicTime)) : learningRate;
						double delta = newLearningRate * neuron.getError() * synapse.getSourceNeuron().getOutput();

						if (synapseNeuronDeltaMap.get(synapse) != null) {
							double previousDelta = synapseNeuronDeltaMap.get(synapse);
							delta += momentum * previousDelta;
						}

						synapseNeuronDeltaMap.put(synapse, delta);
						synapse.setWeight(synapse.getWeight() - delta);
					}
				}
			}

			output = neuralNetwork.getOutput();
			error += error(output, expectedOutput);
		}

		return error;
	}

	public double error(double[] actual, double[] expected) {

		if (actual.length != expected.length) {
			throw new IllegalArgumentException("The lengths of the actual and expected value arrays must be equal");
		}

		double sum = 0;

		for (int i = 0; i < expected.length; i++) {
			sum += Math.pow(expected[i] - actual[i], 2);
		}

		return sum / 2;
	}
}
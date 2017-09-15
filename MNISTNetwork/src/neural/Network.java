package neural;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Network implements Serializable {

	private List<Layer> layers = new ArrayList<>();
	private Layer input;
	private Layer output;
	private String name;

	public Network(String name) {
		this.name = name;
	}

	public void addLayer(Layer layer) {
		layers.add(layer);

		if (layers.size() == 1) {
			input = layer;
		}

		if (layers.size() > 1) {
			Layer previousLayer = layers.get(layers.size() - 2);
			previousLayer.setNextLayer(layer);
		}

		output = layers.get(layers.size() - 1);
	}

	public void setInputs(double[] inputs) {
		if (input != null) {

			int biasCount = input.hasBias() ? 1 : 0;

			if (input.getNeurons().size() - biasCount != inputs.length) {
				throw new IllegalArgumentException(
						"The number of inputs must equal the number of neurons in the input layer");
			}

			else {
				List<Neuron> neurons = input.getNeurons();
				for (int i = biasCount; i < neurons.size(); i++) {
					neurons.get(i).setOutput(inputs[i - biasCount]);
				}
			}
		}
	}

	public double[] getOutput() {
		int start = output.hasBias() ? 1 : 0;
		double[] outputs = new double[output.getNeurons().size() - start];
		for (int i = 1; i < layers.size(); i++) {
			Layer layer = layers.get(i);
			layer.feedForward();
		}
		for (int i = start; i < output.getNeurons().size(); i++) {
			outputs[i - start] = output.getNeurons().get(i).getOutput();
		}
		return outputs;
	}

	public List<Layer> getLayers() {
		return layers;
	}

	public void persist() {
		String fileName = name.replaceAll(" ", "_") + "-" + new Date().getTime() + ".net";
		System.out.println("Writing trained neural network to file " + fileName);

		ObjectOutputStream objectOutputStream = null;

		try {
			objectOutputStream = new ObjectOutputStream(new FileOutputStream(fileName));
			objectOutputStream.writeObject(this);
		}

		catch (IOException e) {
			System.out.println("Could not write to file: " + fileName);
			e.printStackTrace();
		}

		finally {
			try {
				if (objectOutputStream != null) {
					objectOutputStream.flush();
					objectOutputStream.close();
				}
			}

			catch (IOException e) {
				System.out.println("Could not write to file: " + fileName);
				e.printStackTrace();
			}
		}
	}

	public Network copy() {
		Network copy = new Network(this.name);

		Layer previousLayer = null;
		for (Layer layer : layers) {

			Layer layerCopy;

			if (layer.hasBias()) {
				Neuron bias = layer.getNeurons().get(0);
				Neuron biasCopy = new Neuron(bias.getActivationMethod().copy());
				biasCopy.setOutput(bias.getOutput());
				layerCopy = new Layer(null, biasCopy);
			}

			else {
				layerCopy = new Layer();
			}

			layerCopy.setPreviousLayer(previousLayer);

			int biasCount = layerCopy.hasBias() ? 1 : 0;

			for (int i = biasCount; i < layer.getNeurons().size(); i++) {
				Neuron neuron = layer.getNeurons().get(i);

				Neuron neuronCopy = new Neuron(neuron.getActivationMethod().copy());
				neuronCopy.setOutput(neuron.getOutput());
				neuronCopy.setError(neuron.getError());

				if (neuron.getInputs().size() == 0) {
					layerCopy.addNeuron(neuronCopy);
				}

				else {
					double[] weights = neuron.getWeights();
					layerCopy.addNeuron(neuronCopy, weights);
				}
			}

			copy.addLayer(layerCopy);
			previousLayer = layerCopy;
		}

		return copy;
	}

	public void copyWeightsFrom(Network sourceNeuralNetwork) {
		if (layers.size() != sourceNeuralNetwork.layers.size()) {
			throw new IllegalArgumentException("Cannot copy weights. Number of layers do not match ("
					+ sourceNeuralNetwork.layers.size() + " in source versus " + layers.size() + " in destination)");
		}

		int i = 0;
		for (Layer sourceLayer : sourceNeuralNetwork.layers) {
			Layer destinationLayer = layers.get(i);

			if (destinationLayer.getNeurons().size() != sourceLayer.getNeurons().size()) {
				throw new IllegalArgumentException(
						"Number of neurons do not match in layer " + (i + 1) + "(" + sourceLayer.getNeurons().size()
								+ " in source versus " + destinationLayer.getNeurons().size() + " in destination)");
			}

			int j = 0;
			for (Neuron sourceNeuron : sourceLayer.getNeurons()) {
				Neuron destinationNeuron = destinationLayer.getNeurons().get(j);

				if (destinationNeuron.getInputs().size() != sourceNeuron.getInputs().size()) {
					throw new IllegalArgumentException("Number of inputs to neuron " + (j + 1) + " in layer " + (i + 1)
							+ " do not match (" + sourceNeuron.getInputs().size() + " in source versus "
							+ destinationNeuron.getInputs().size() + " in destination)");
				}

				int k = 0;
				for (Synapse sourceSynapse : sourceNeuron.getInputs()) {
					Synapse destinationSynapse = destinationNeuron.getInputs().get(k);

					destinationSynapse.setWeight(sourceSynapse.getWeight());
					k++;
				}

				j++;
			}

			i++;
		}
	}

	public static Network load() {
		System.out.println("Loading network...");
		File f = new File(new File("").getAbsolutePath());
		final FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				System.out.println("Checking " + name + ", returning " + name.endsWith(".net"));
				return name.endsWith(".net");
			}
		};
		File[] files = f.listFiles(filter);
		if (files == null) {

			return null;
		}
		File biggest = null;
		long biggestDate = -1L;
		for (File file : files) {
			String name = file.getName().replaceAll("\\D+", "");
			long date = Long.parseLong(name);
			if (date > biggestDate) {
				biggestDate = date;
				biggest = file;
			}
		}
		if (biggest == null || biggestDate == -1L) {
			return null;
		}
		Network network = null;
		try {
			FileInputStream fileIn = new FileInputStream(biggest.getName());
			ObjectInputStream in = new ObjectInputStream(fileIn);
			network = (Network) in.readObject();
			in.close();
			fileIn.close();
		} catch (IOException i) {
			i.printStackTrace();
			return null;
		} catch (ClassNotFoundException c) {
			System.out.println("Network class not found");
			c.printStackTrace();
			return null;
		}
		return network;
	}

}
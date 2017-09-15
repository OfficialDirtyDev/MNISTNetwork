package neural;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import data.ReadWriteFile;

public class DigitTrainingDataGenerator implements TrainingDataGenerator {

	private List<TrainingSet> loadedSets;
	private int setsLength;
	Random random = new Random();
	private final static int NUMBER_OF_DATA_PER_SET = 8;
	
	public DigitTrainingDataGenerator(String fileName) {
		this.loadedSets = new ArrayList<>(ReadWriteFile.readTrainingSets(fileName));
    	Collections.shuffle(loadedSets);
    	System.out.println(loadedSets.size());
    	this.setsLength = loadedSets.size();
	}
	
    public TrainingData getTrainingData() {
    	final int index = random.nextInt(setsLength - NUMBER_OF_DATA_PER_SET);
    	
    	final ArrayList<TrainingSet> sets = new ArrayList<>(this.loadedSets.subList(index, index + NUMBER_OF_DATA_PER_SET));
    	double[][] inputs = new double[sets.size()][sets.get(0).getInputs().length];
    	double[][] outputs = new double[sets.size()][sets.get(0).getInputs().length];
    	
    	for(int i = 0; i < NUMBER_OF_DATA_PER_SET; i++) {
    		inputs[i] = sets.get(i).getInputs();
    		outputs[i] = sets.get(i).getGoodOutput();
    	}
        return new TrainingData(inputs, outputs);
    }

}
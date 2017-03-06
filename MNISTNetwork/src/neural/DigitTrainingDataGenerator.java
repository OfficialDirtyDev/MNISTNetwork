package neural;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import data.ReadWriteFile;

public class DigitTrainingDataGenerator implements TrainingDataGenerator {

	private String fileName;
	
	public DigitTrainingDataGenerator(String fileName) {
		this.fileName = fileName;
	}
	
    public TrainingData getTrainingData() {
    	List<TrainingSet> sets = new ArrayList<>(ReadWriteFile.readTrainingSets(fileName));
    	Collections.shuffle(sets);
    	sets = new ArrayList<TrainingSet>(sets.subList(0, 10));
    	double[][] inputs = new double[sets.size()][sets.get(0).getInputs().length];
    	double[][] outputs = new double[sets.size()][sets.get(0).getInputs().length];
    	
    	for(int i = 0; i < sets.size(); i++) {
    		inputs[i] = sets.get(i).getInputs();
    		outputs[i] = sets.get(i).getGoodOutput();
    	}
        return new TrainingData(inputs, outputs);
    }

}
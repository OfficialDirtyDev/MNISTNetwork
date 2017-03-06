package neural;

/**
 * Basic class for a training data generator which will return an array of
 * inputs(double from 0 up to 2PI) and outputs(double from -1 up to 1)
 * @author DirtyDev
 * Created by DirtyDev on 06.03.2017, 20:31:55
 *
 */
public class SinusTrainingDataGenerator implements TrainingDataGenerator {

	@Override
	public TrainingData getTrainingData() {
		final double[][] inputs = new double[1][];
		final double[][] outputs = new double[1][];
		
		for(int i = 0; i < inputs.length; i++) {
			final double num = Math.random() * 2D * Math.PI;
			final double sin = Math.sin(Math.toRadians(num));
			inputs[i] = new double[]{num};
			outputs[i] = new double[]{sin};
		}
		
		return new TrainingData(inputs, outputs);
	}

}

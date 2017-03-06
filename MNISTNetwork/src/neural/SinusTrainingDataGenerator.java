package neural;

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

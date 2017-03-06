package data;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import gui.MainGui;
import neural.TrainingSet;

public class ReadWriteFile {

	private static ArrayList<TrainingSet> sets;
	private static String lastName;

	public static ArrayList<TrainingSet> readTrainingSets(String fileName) {
		if (sets != null && fileName.toLowerCase().equalsIgnoreCase(lastName.toLowerCase())) {
			return sets;
		}
		ArrayList<TrainingSet> trainingSets = new ArrayList<>();
		final File dir = new File(fileName);
		int read = 0;
		for (File num : dir.listFiles()) {
			final int number = Integer.parseInt(num.getName());
			System.out.println("Loading all " + number);
			for (File file : num.listFiles()) {
				read++;
				if (!file.getName().endsWith(".png")) {
					System.out.println("File " + file.getName() + " is not a png!");
					continue;
				}
				final double[] input = getPixels(file);
				final double[] output = new double[10];
				for (int i = 0; i < 10; i++) {
					if (i == number) {
						output[i] = 1D;
					} else {
						output[i] = 0D;
					}
				}
				trainingSets.add(new TrainingSet(input, output));
				MainGui.getInstance().getInfo().setText("Reading file " + read + " for number " + number);
			}
		}
		lastName = fileName;
		sets = trainingSets;
		return trainingSets;
	}

	public static double[] getPixels(File file) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(file);
		} catch (IOException e) {
			System.err.println("Error loading file " + file.getName());
			return null;
		}
		final double[] input = new double[img.getWidth() * img.getHeight()];
		int x = 0;
		for (int row = 0; row < img.getWidth(); row++) {
			for (int col = 0; col < img.getHeight(); col++) {
				final Color color = new Color(img.getRGB(row, col));
				final double avg = (color.getBlue() + color.getRed() + color.getGreen()) / 3D;
				final double f = Math.min(1, avg / 255D);
				input[x] = f;
				x++;
			}
		}
		return input;
	}

}
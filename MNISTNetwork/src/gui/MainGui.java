package gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

import data.ReadWriteFile;
import gui.components.DrawingPanel;
import gui.components.data.Section;
import neural.Backpropagator;
import neural.DigitTrainingDataGenerator;
import neural.Layer;
import neural.LinearActivationMethod;
import neural.Network;
import neural.Neuron;
import neural.SigmoidActivationMethod;
import neural.TrainingDataGenerator;

/**
 * Main class drawing the main frame
 */
public class MainGui extends JFrame {

	private final int RESOLUTION = 28;
	private final int INPUT = 784, HIDDEN = 400, OUTPUT = 10;
	private final int DRAWING_SIZE = 560;
	private final double ERROR_THRESHOLD = 0.05;
	private final int NUM_OF_SEQUENCE = 10;

	private JPanel mainPanel;
	private DrawingPanel drawingPanel;
	
	private JButton clearButton;
	private JButton transformButton;
	private JButton invertButton;
	private JButton trainNetworkButton;
	private JButton openButton;
	private JButton cancelButton;
	private JTextField trainingSetsAmount;
	private JTextField filePath;
	private JLabel label;
	private JTextArea outputTextArea;
	private JLabel info;
	private static MainGui instance;
	private final Network network;
	private Backpropagator back;
	private TrainingDataGenerator trainingDataGenerator;
	private ApplicationFrame chartFrame;

	public static void main(String[] args) {
		new MainGui();
	}

	public MainGui() {
		super("Jans Netzwerk");
		System.out.println("Starting network...");
		instance = this;
		this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setMainPanel();
		setLeftSide();
		setCenterArea();
		setRightSide();
		setOutputPanel();

		setOnClicks();

		setVisible(true);
		setSize(new Dimension(1900, 700));
		setLocationRelativeTo(null);

		createShutdownHooks();

		trainingDataGenerator = new DigitTrainingDataGenerator("E:\\NeuralNetworkReloaded\\training");
		//trainingDataGenerator = new SinusTrainingDataGenerator();

		final Network net = Network.load();
		if (net == null) {
			// Create the network
			network = new Network("Digits");

			// input layer
			Neuron inputBias = new Neuron(new LinearActivationMethod());
			inputBias.setOutput(1);
			Layer inputLayer = new Layer(null, inputBias);

			for (int i = 0; i < INPUT; i++) {
				final Neuron n = new Neuron(new LinearActivationMethod());
				n.setOutput(0D);
				inputLayer.addNeuron(n);
			}

			// hidden layer
			Neuron bias = new Neuron(new LinearActivationMethod());
			bias.setOutput(1);
			Layer hiddenLayer = new Layer(inputLayer, bias);

			for (int i = 0; i < HIDDEN; i++) {
				final Neuron n = new Neuron(new SigmoidActivationMethod());
				n.setOutput(0D);
				hiddenLayer.addNeuron(n);
			}

			// output layer
			Neuron outputBias = new Neuron(new LinearActivationMethod());
			outputBias.setOutput(1);
			Layer outputLayer = new Layer(hiddenLayer, outputBias);

			for (int i = 0; i < OUTPUT; i++) {
				final Neuron n = new Neuron(new SigmoidActivationMethod());
				n.setOutput(0D);
				outputLayer.addNeuron(n);
			}

			network.addLayer(inputLayer);
			network.addLayer(hiddenLayer);
			network.addLayer(outputLayer);

			System.out.println("Created network!");
			
		} else {
			System.out.println("Found existing network!");
			network = net;
		}

		back = new Backpropagator(network, 0.02, 0.9, 0);
		
	}

	private void createShutdownHooks() {
		this.addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
				System.out.println("Shutdown");
				back.cancel(() -> {
					System.out.println("Saving network...");
					network.persist();
				}, true);
		    }
		});
		
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			System.out.println("Shutdown");
			back.cancel(() -> {
				System.out.println("Saving network...");
				network.persist();
			}, true);
	    }));
	}

	private void setMainPanel() {
		mainPanel = new JPanel();
		mainPanel.setBackground(Color.LIGHT_GRAY);
		setContentPane(mainPanel);
	}

	private void setLeftSide() {
		JPanel panel = new JPanel();
		panel.setBackground(Color.LIGHT_GRAY);
		panel.setPreferredSize(new Dimension(DRAWING_SIZE, DRAWING_SIZE));

		drawingPanel = new DrawingPanel(DRAWING_SIZE, DRAWING_SIZE, RESOLUTION);

		panel.add(drawingPanel);

		mainPanel.add(panel);
	}

	private void setCenterArea() {
		JPanel centerPanel = new JPanel();
		centerPanel.setAlignmentY(0);
		centerPanel.setLayout(new GridBagLayout());
		centerPanel.setPreferredSize(new Dimension(1000, 600));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.anchor = GridBagConstraints.CENTER;

		trainNetworkButton = new JButton("Train X times:");
		trainingSetsAmount = new JFormattedTextField("2");
		trainingSetsAmount.setMaximumSize(new Dimension(100, 30));
		trainingSetsAmount.setPreferredSize(new Dimension(100, 30));
		centerPanel.add(trainNetworkButton, gbc);
		centerPanel.add(trainingSetsAmount, gbc);

		centerPanel.add(Box.createVerticalStrut(50));

		invertButton = new JButton("Invert");
		centerPanel.add(invertButton, gbc);

		centerPanel.add(Box.createVerticalStrut(50));

		transformButton = new JButton(">>");
		centerPanel.add(transformButton, gbc);

		centerPanel.add(Box.createVerticalStrut(50));

		cancelButton = new JButton("Cancel training");
		centerPanel.add(cancelButton, gbc);

		centerPanel.add(Box.createVerticalStrut(50));

		clearButton = new JButton("Clear");
		clearButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		centerPanel.add(clearButton, gbc);
		centerPanel.add(Box.createVerticalStrut(50));

		openButton = new JButton("Open file...");
		openButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		centerPanel.add(openButton, gbc);
		centerPanel.add(Box.createVerticalStrut(50));

		centerPanel.add(Box.createVerticalStrut(150));

		GridBagConstraints down = new GridBagConstraints();
		centerPanel.add(new JLabel("Training path:    "), down);

		filePath = new JFormattedTextField();
		filePath.setText(this.getCurrentFileName());
		filePath.setAlignmentX(Component.CENTER_ALIGNMENT);
		filePath.setMaximumSize(new Dimension(100, 30));
		filePath.setPreferredSize(new Dimension(400, 30));
		centerPanel.add(Box.createVerticalStrut(50));

		final GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1;
		info = new JLabel("");
		info.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));

		centerPanel.add(info, c);

		centerPanel.add(filePath);
		mainPanel.add(centerPanel);

	}

	private String getCurrentFileName() {
		return new File("").getAbsolutePath() + "\\training\\";
	}

	private void setRightSide() {
		label = new JLabel("");
		label.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 40));
		label.setBackground(Color.LIGHT_GRAY);
		label.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
		mainPanel.add(label);
	}

	private void setOutputPanel() {
		JPanel outputPanel = new JPanel();
		outputPanel.setPreferredSize(new Dimension(200, 430));

		outputTextArea = new JTextArea();
		outputTextArea.setPreferredSize(new Dimension(200, 430));
		outputPanel.add(outputTextArea);

		mainPanel.add(outputPanel);
		outputTextArea.setEditable(false);
	}

	private void setOnClicks() {
		clearButton.addActionListener(e -> drawingPanel.clear());

		transformButton.addActionListener(e -> {
			final double[] pixels = new double[drawingPanel.getPixels().size()];
			for (int i = 0; i < drawingPanel.getPixels().size(); i++) {
				pixels[i] = drawingPanel.getPixels().get(i);
			}
			handleInput(pixels);
		});

		cancelButton.addActionListener(e -> {
			if(back != null) {
				back.cancel(() -> {
					System.out.println("Saving network...");
					network.persist();
				});
			}
			if(chartFrame != null) {
				chartFrame.setVisible(false);
			}
		});

		invertButton.addActionListener(e -> {
			final double[] pixels = new double[drawingPanel.getPixels().size()];
			for (int i = 0; i < drawingPanel.getPixels().size(); i++) {
				pixels[i] = 1 - drawingPanel.getPixels().get(i);
			}
			drawingPanel.drawDigit(pixels);
		});

		drawingPanel.addMouseWheelListener(new MouseWheelListener() {

			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				final long x = Math.round(e.getPoint().getX() * RESOLUTION / DRAWING_SIZE);
				final long y = Math.round(e.getPoint().getY() * RESOLUTION / DRAWING_SIZE);
				final int index = (int) ((x * RESOLUTION) + y);
				final Section pix = drawingPanel.getSections().get(index);
				final double newBlack = Math.max(Math.min(1D, pix.getBlack() + (e.getWheelRotation() / 6D)), 0D);
				pix.setBlack(newBlack);
				drawingPanel.repaint();
			}
		});

		trainNetworkButton.addActionListener(e -> {
			System.out.println("Clicked training");
			new Thread(() -> {

				XYSeries averageSeries = new XYSeries("Error average of the last 5 epochs");
				XYSeries errorSeries = new XYSeries("Error per epoch");

				// create the datasets
				XYSeriesCollection averageSet = new XYSeriesCollection();
				XYSeriesCollection errorSet = new XYSeriesCollection();
				averageSet.addSeries(averageSeries);
				errorSet.addSeries(errorSeries);

				// construct the plot
				XYPlot plot = new XYPlot();
				plot.setDataset(0, averageSet);
				plot.setDataset(1, errorSet);

				// customize the plot with renderers and axis
				XYLineAndShapeRenderer firstRenderer = new XYLineAndShapeRenderer();
				firstRenderer.setSeriesShapesVisible(0, false);
				plot.setRenderer(0, firstRenderer);// use default fill
													// paint for first
													// series
				XYLineAndShapeRenderer splinerenderer = new XYLineAndShapeRenderer();
				splinerenderer.setSeriesShapesVisible(0, false);
				splinerenderer.setSeriesFillPaint(0, new Color(0F, 0F, 1F, 0.4F));
				plot.setRenderer(1, splinerenderer);

				plot.setRangeAxis(0, new NumberAxis("Average error of the last 5 epochs in %"));
				plot.setRangeAxis(1, new NumberAxis("Error in %"));
				plot.setDomainAxis(new NumberAxis("Epoch"));

				plot.mapDatasetToRangeAxis(0, 0);
				plot.mapDatasetToRangeAxis(1, 1);

				ValueMarker marker = new ValueMarker(ERROR_THRESHOLD);
				float dash[] = { 10.0f };
				marker.setStroke(
						new BasicStroke(3.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f));
				marker.setPaint(Color.GREEN);
				plot.addRangeMarker(marker);

				// generate the chart
				JFreeChart chart = new JFreeChart("MyPlot", getFont(), plot, true);
				chart.setBackgroundPaint(Color.WHITE);

				final ChartPanel chartPanel = new ChartPanel(chart);
				chartPanel.setMouseWheelEnabled(true);
				chartPanel.setMouseZoomable(true);

				ApplicationFrame f = new ApplicationFrame("Error Plotting");

				f.setLayout(new BorderLayout(0, 5));
				f.add(chartPanel, BorderLayout.CENTER);
				chartPanel.setMouseWheelEnabled(true);
				chartPanel.setHorizontalAxisTrace(true);
				chartPanel.setVerticalAxisTrace(true);

				JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));

				final JButton toggleAverage = new JButton(new AbstractAction("Toggle Average Series") {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (firstRenderer.getLinesVisible() == null || firstRenderer.getLinesVisible()) {
							firstRenderer.setLinesVisible(false);
						} else {
							firstRenderer.setLinesVisible(true);
						}
					}
				});

				final JButton toggleError = new JButton(new AbstractAction("Toggle Error Series") {
					@Override
					public void actionPerformed(ActionEvent e) {
						if (splinerenderer.getLinesVisible() == null || splinerenderer.getLinesVisible()) {
							splinerenderer.setLinesVisible(false);
						} else {
							splinerenderer.setLinesVisible(true);
						}
					}
				});

				panel.add(toggleError);
				panel.add(toggleAverage);
				f.add(panel, BorderLayout.SOUTH);
				f.pack();
				f.setLocationRelativeTo(null);
				f.setVisible(true);

				f.pack();
				f.setVisible(true);
				chartFrame = f;
				back.train(errorSeries, averageSeries, trainingDataGenerator, ERROR_THRESHOLD, NUM_OF_SEQUENCE);
			}).start();
		});

		openButton.addActionListener(e -> {
			final File file = new File(this.filePath.getText());
			if (!file.exists()) {
				JOptionPane.showMessageDialog(this, "Fehler: Der Pfad " + this.filePath.getText() + " existiert nicht.",
						"Error", JOptionPane.PLAIN_MESSAGE);
			}
			final double[] pixels = ReadWriteFile.getPixels(file);
			drawingPanel.drawDigit(pixels);
			handleInput(pixels);
		});

	}

	private void handleInput(double[] pixels) {
		network.setInputs(pixels);

		final double[] outputs = network.getOutput();
		int index = 0;
		for (int i = 0; i < outputs.length; i++) {
			if (outputs[i] >= outputs[index]) {
				index = i;
			}
		}

		updateTextArea(outputs);
		label.setText(String.valueOf(index));
		System.out.println("Selected image fits " + index + " most. ( " + ((int) (outputs[index] * 100)) + " % )");
	}

	private void updateTextArea(double[] outputs) {
		System.out.println("Got " + outputs.length + " outputs");
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.HALF_UP);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < outputs.length; i++) {
			sb.append(i);
			double value = outputs[i];

			sb.append("\t " + df.format((value * 100)) + "%");
			sb.append("\n");
		}
		outputTextArea.setText(sb.toString());
	}

	public JLabel getInfo() {
		return info;
	}

	public static MainGui getInstance() {
		return instance;
	}

	public DrawingPanel getDrawingPanel() {
		return drawingPanel;
	}

}

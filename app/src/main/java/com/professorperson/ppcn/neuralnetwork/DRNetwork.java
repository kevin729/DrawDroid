package com.professorperson.ppcn.neuralnetwork;

import android.graphics.Bitmap;

import com.professorperson.ppcn.utils.Utils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class DRNetwork {
	
	private NeuralNetwork brain;
	private double[][] images;
	private int index = 0;
	
	public DRNetwork(Utils.ActivationFunction af, int... layerSizes) {
		brain = new NeuralNetwork(af, layerSizes);
		images = new double[layerSizes[layerSizes.length-1]][layerSizes[0]];
	}
	
	public void feedForward(double[] pixels) {
		brain.feedForward(pixels);
	}
	
	public void feedForward(int[] pixels, int width, int height) {
		brain.feedForward(normalise(resizeImage(shrinkImage(pixels, width, height), (int)Math.sqrt(brain.getLayerSizes()[0]))));
		
		double result = 0;
		int neuronIndex = 0;
		for (int i = 0; i < brain.getOutputs().length; i++) {
			if (brain.getOutputs()[i] > result && i != 0) {
				result = brain.getOutputs()[i];
				neuronIndex = i;
			} else if (i == 0) {
				result = brain.getOutputs()[i];
				neuronIndex = i;
			}
			System.out.println(i + " " + brain.getOutputs()[i]);
		}
		
		System.out.println(neuronIndex);
	}

	public void addImage(double[] pixels) throws IOException {
		brain.setWeights(pixels, index);
		brain.feedForward(pixels);
		saveWeights();
		index++;
	}

	public void addImage(int[] pixels, int width, int height) throws IOException {
		int[] imagePixels = resizeImage(shrinkImage(pixels, width, height), (int)Math.sqrt(brain.getLayerSizes()[0]));
		images[index] = normalise(imagePixels);

		brain.setWeights(images[index], index);
		brain.feedForward(images[index]);
		saveWeights();
		index++;
	}

	private int[] resizeImage(Bitmap img, int newSize) {
		Bitmap newImage = Bitmap.createScaledBitmap(img, newSize, newSize, false);
		int[] pixels = new int[newSize * newSize];
		newImage.getPixels(pixels, 0, 0, 0, 0, newSize, newSize);
		return pixels;
	}

	private double[] normalise(int[] pixels) {
		double[] newPixels = new double[pixels.length];
		for(int i = 0; i < pixels.length; i++) {
			if(pixels[i] != -1) {
				newPixels[i] = 1;
			} else {
				newPixels[i] = -1;
			}
		}

		return newPixels;
	}

	private Bitmap shrinkImage(int[] pixels, int screenWidth, int screenHeight) {

		int minX = screenWidth, minY = screenHeight;
		int maxX = 0, maxY = 0;

		for (int y = 0; y < screenHeight; y++) {
			for (int x = 0; x < screenWidth; x++) {
				if (pixels[x+y*screenWidth] != -1) {
					if (minX > x) {
						minX = x;
					}
					if (maxX < x) {
						maxX = x;
					}

					if (minY > y) {
						minY = y;
					}
					if (maxY < y) {
						maxY = y;
					}
				}
			}
		}

		int width = maxX - minX;
		int height = maxY - minY;

		int[] newPixels = new int[width * height];

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				newPixels[x+y*width] = pixels[(x+minX)+(y+minY)*screenWidth];
			}
		}

		Bitmap img = Bitmap.createBitmap(newPixels, width, height, Bitmap.Config.RGB_565);
		return img;
	}

	public void saveWeights() throws IOException {
		brain.saveWeights();
	}

	public void loadWeights(String file) {
		Thread loadWeights = new Thread() {
			public void run() {
				try {
					brain.loadWeights(file);
				} catch (IOException e) {}
			}
		};

		loadWeights.start();
	}

	public void loadWeights(InputStream is) {
		Thread loadWeights = new Thread() {
			public void run() {
				try {
					brain.loadWeights(is);
				} catch (IOException e) {}
			}
		};

		loadWeights.start();
	}

	public double[] getOutputs() {
		return brain.getOutputs();
	}
}

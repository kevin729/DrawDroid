//package com.professorperson.ppcn.neuralnetwork;
//
//import com.professorperson.ppcn.pojos.Pattern;
//import com.professorperson.ppcn.utils.Utils;
//
//import java.awt.image.BufferedImage;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Random;
//
//public class ConvolutionalNeuralNetwork {
//    ArrayList<double[]> features = new ArrayList<>();
//    ArrayList<BufferedImage> filterImages = new ArrayList<>();
//    HashMap<Integer, double[]> filters = new HashMap<>();
//    private final int extractionWindowSize = 42;
//    private final int extractionStride = 42;
//    private final int convolutionWindowSize = 42;
//    private final int convolutionStride = 42;
//    private final int poolingWindowSize = 2;
//    private final int poolingStride = 2;
//    private NeuralNetwork brain;
//    private int filterPixelAmount = 0;
//    private int outputAmount = 0;
//    private final List<HashMap<String, double[]>> trainingSets = new ArrayList<>();
//
//    public ConvolutionalNeuralNetwork() {
//
//    }
//
//    /**
//     * @param _pixels pixels of target image
//     * @param width
//     * @param height
//     * @throws IOException
//     */
//    public void extractFeatures(int[] _pixels, int width, int height) throws IOException {
//        outputAmount++;
//
//        _pixels = Utils.resizeImage(_pixels, 128);
//        double[] pixels = Utils.normalise(_pixels);
//
//        //amount window needs to move
//        int xAmount = (int) Math.floor(width / extractionStride) - (extractionWindowSize - extractionStride);
//        int yAmount = (int) Math.floor(height / extractionStride) - (extractionWindowSize - extractionStride);
//
//        //moves window
//        for (int i = 0; i < xAmount * yAmount; i++) {
//            int x = (i % xAmount) * extractionStride;
//            int y = (int) Math.floor(i / xAmount) * extractionStride;
//
//            double[] featurePixels = new double[(int) Math.pow(extractionWindowSize, 2)];
//            int[] testPixels = new int[(int) Math.pow(extractionWindowSize, 2)];
//
//            //each pixel within extraction window
//            for (int j = 0; j < featurePixels.length; j++) {
//                int jX = j % extractionWindowSize + x;
//                int jY = (int) Math.floor(j / extractionWindowSize) + y;
//
//                featurePixels[j] = pixels[jX + jY * width];
//                testPixels[j] = (int) pixels[jX + jY * width];
//            }
//
//            //image for testing
//            BufferedImage feature = new BufferedImage(extractionWindowSize, extractionWindowSize, BufferedImage.TYPE_INT_RGB);
//            feature.setRGB(0, 0, extractionWindowSize, extractionWindowSize, testPixels, 0, extractionWindowSize);
//            //ImageIO.write(feature, "PNG", new File(i+"testImage.png"));
//
//
//            features.add(featurePixels);
//        }
//    }
//
//    /**
//     * convolution method on the image and passes filters through the neural network
//     *
//     * @param pixels Image pixels
//     * @param width  Image width
//     * @param height Image height
//     * @throws IOException
//     */
//
//    public Pattern feedForward(int[] pixels, int width, int height) throws IOException {
//
//        int xAmount = (int) Math.floor(width / convolutionStride) - (convolutionWindowSize - convolutionStride);
//        int yAmount = (int) Math.floor(height / convolutionStride) - (convolutionWindowSize - convolutionStride);
//
//        filterImages = new ArrayList<>();
//        process(pixels, width, height, xAmount, yAmount);
//
//        int inputAmount = filterPixelAmount * filters.size();
//        double[] inputs = new double[inputAmount];
//
//        //get pixels of all filters
//        for (int i = 0; i < filters.size(); i++) {
//            for (int j = 0; j < filters.get(i).length; j++) {
//                inputs[i * filterPixelAmount + j] = filters.get(i)[j];
//            }
//        }
//
//        brain.feedForward(inputs);
//
//        Pattern pattern = new Pattern();
//        int output = getOutputIndex();
//        switch (output) {
//
//            case 0:
//                pattern.setName("A");
//                break;
//            case 1:
//                pattern.setName("B");
//                break;
//            case 2:
//                pattern.setName("cross");
//                break;
//            case 3:
//                pattern.setName("plus");
//                break;
//            case 4:
//                pattern.setName("naut");
//                break;
//        }
//
//        return pattern;
//    }
//
//    /**
//     * Adds pixels width, height, and target training set
//     *
//     * @param pixels  Image pixels
//     * @param width   Image width
//     * @param height  Image height
//     * @param targets Target values in order of output neurons
//     */
//    public void addTrainingSet(int[] pixels, int width, int height, double... targets) throws IOException {
//        int xAmount = (int) Math.floor(width / convolutionStride) - (convolutionWindowSize - convolutionStride);
//        int yAmount = (int) Math.floor(height / convolutionStride) - (convolutionWindowSize - convolutionStride);
//
//        process(pixels, width, height, xAmount, yAmount);
//        filterImages = new ArrayList<>();
//
//        int inputAmount = filterPixelAmount * filters.size();
//        if (brain == null) brain = new NeuralNetwork(Utils.ActivationFunction.SIGMOID, inputAmount, outputAmount);
//
//        double[] inputs = new double[inputAmount];
//
//        for (int i = 0; i < filters.size(); i++) {
//            for (int j = 0; j < filters.get(i).length; j++) {
//                inputs[i * filterPixelAmount + j] = filters.get(i)[j];
//            }
//        }
//
//        HashMap<String, double[]> map = new HashMap();
//        map.put("inputs", inputs);
//        map.put("targets", targets);
//        trainingSets.add(map);
//        train(map);
//    }
//
//    public void addTrainingSet(int[] pixels, int width, int height, String pattern) throws IOException {
//        switch (pattern) {
//            case "A":
//                addTrainingSet(pixels, width, height, 1, 0, 0, 0, 0);
//                break;
//            case "B":
//                addTrainingSet(pixels, width, height, 0, 1, 0, 0, 0);
//                break;
//            case "Cross":
//                addTrainingSet(pixels, width, height, 0, 0, 1, 0, 0);
//                break;
//            case "Plus":
//                addTrainingSet(pixels, width, height, 0, 0, 0, 1, 0);
//                break;
//            case "Naut":
//                addTrainingSet(pixels, width, height, 0, 0, 0, 0, 1);
//                break;
//            default:
//                return;
//        }
//
//        train();
//    }
//
//    /**
//     * Trains the neural network
//     *
//     * @throws IOException
//     */
//    public void train() throws IOException {
//        System.out.println("Training...");
//
//        for (int i = 0; i < 1000000; i++) {
//            HashMap<String, double[]> map = trainingSets.get(new Random().nextInt(trainingSets.size()));
//            brain.backPropagation(map.get("inputs"), map.get("targets"));
//        }
//    }
//
//    public void train(HashMap<String, double[]> map) throws IOException {
//        System.out.println("Training...");
//
//        for (int i = 0; i < 100000; i++) {
//            brain.backPropagation(map.get("inputs"), map.get("targets"));
//        }
//    }
//
//    private void process(int[] pixels, int width, int height, int xAmount, int yAmount) throws IOException {
//        pixels = Utils.resizeImage(pixels, 128);
//        //pixels = Utils.resizeImage(Utils.shrinkImage(pixels, width, height), 128);
//
//        convolution(Utils.normalise(pixels), width, height);
//    }
//
//    /**
//     * Compares each feature by iterating the convolution window
//     *
//     * @param pixels Image pixels
//     * @param width  Image width
//     * @param height Image height
//     * @throws IOException
//     */
//    private void convolution(double[] pixels, int width, int height) throws IOException {
//        int xAmount = (int) Math.floor(width / convolutionStride) - (convolutionWindowSize - convolutionStride);
//        int yAmount = (int) Math.floor(height / convolutionStride) - (convolutionWindowSize - convolutionStride);
//
//        filterPixelAmount = xAmount * yAmount;
//        System.out.println(filterPixelAmount);
//
//        //Iterate through features
//        for (int f = 0; f < features.size(); f++) {
//            double[] featurePixels = features.get(f);
//
//            double[] filterPixels = new double[xAmount * yAmount];
//            int[] testPixels = new int[xAmount * yAmount];
//
//            //move convolution stride window
//            for (int i = 0; i < xAmount * yAmount; i++) {
//                int x = (i % xAmount) * convolutionStride;
//                int y = (int) Math.floor(i / xAmount) * convolutionStride;
//
//                double targetPixel = 0;
//                double pixel = 0;
//
//                //pixels within feature
//                for (int j = 0; j < featurePixels.length; j++) {
//                    int jX = j % convolutionWindowSize + x;
//                    int jY = (int) Math.floor(j / convolutionWindowSize) + y;
//
//                    pixel += featurePixels[j] * pixels[jX + jY * width];
//                    targetPixel += Math.pow(pixels[jX + jY * width], 2);
//                }
//
//                filterPixels[i] = Math.max((Math.min(pixel, targetPixel) / Math.max(pixel, targetPixel)), 0);
//                testPixels[i] = (int) Math.max((Math.min(pixel, targetPixel) * 255 / Math.max(pixel, targetPixel)), 0);
//            }
//
//            filters.put(f, filterPixels);
//
//            BufferedImage filter = new BufferedImage(xAmount, yAmount, BufferedImage.TYPE_INT_RGB);
//            filter.setRGB(0, 0, xAmount, yAmount, testPixels, 0, xAmount);
////			ImageIO.write(filter, "PNG", new File("filter"+f+".png"));
//            filterImages.add(filter);
//        }
//    }
//
//    public void pooling(int width, int height) throws IOException {
//        int xAmount = (int) Math.ceil(width / (double) poolingStride);
//        int yAmount = (int) Math.ceil(height / (double) poolingStride);
//        int filterSize = xAmount * yAmount;
//
//        filterPixelAmount = filterSize;
//
//        for (int f = 0; f < filters.size(); f++) {
//            double[] filter = new double[filterSize];
//            int[] test = new int[filterSize];
//            for (int i = 0; i < filterSize; i++) {
//
//                int x = (i % xAmount) * poolingStride;
//                int y = (int) Math.floor(i / xAmount) * poolingStride;
//
//                double pixel = -1;
//                for (int j = 0; j < Math.pow(poolingWindowSize, 2); j++) {
//                    int jX = j % poolingWindowSize + x;
//                    int jY = (int) Math.floor(j / poolingWindowSize + y);
//
//                    if (jX < width && jY < height) pixel = Math.max(pixel, filters.get(f)[jX + jY * width]);
//
//                }
//
//                filter[i] = pixel;
//                test[i] = (int) Math.round(pixel * 255);
//            }
//
//            BufferedImage feature = new BufferedImage(xAmount, yAmount, BufferedImage.TYPE_INT_RGB);
//            feature.setRGB(0, 0, xAmount, yAmount, test, 0, xAmount);
//
//            filters.put(f, filter);
//        }
//    }
//
//    public double[] getOutputs() {
//        return brain.getOutputs();
//    }
//
//    public int getOutputIndex() {
//        double[] outputs = getOutputs();
//        int index = 0;
//        double value = outputs[0];
//
//        for (int i = 1; i < outputs.length; i++) {
//            if (outputs[i] > value) {
//                index = i;
//                value = outputs[i];
//            }
//        }
//
//        return index;
//    }
//
//    public ArrayList<BufferedImage> getFilters() {
//        return filterImages;
//    }
//}

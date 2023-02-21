package com.professorperson.ppcn.neuralnetwork;

import com.professorperson.ppcn.utils.Utils;

public class Neuron {

    public double[] inputs;
    public double[] weights;
    private double weightedSum;
    private double fire = 0;
    private final Utils.ActivationFunction af;


    /**
     * Constructor for input neuron
     */
    public Neuron(Utils.ActivationFunction af) {
        this.af = af;
    }

    /**
     * Constructor for hidden and output neuron
     *
     * @param inputSize - amount of inputs and weights for the neuron
     */
    public Neuron(Utils.ActivationFunction af, int inputSize) {
        this.af = af;
        this.inputs = new double[inputSize];
        this.weights = new double[inputSize];
    }

    /**
     * Constructor for bias neuron
     *
     * @param output - output from neuron
     */
    public Neuron(Utils.ActivationFunction af, double output) {
        this.af = af;
        this.fire = output;
    }

    public void connect(double weight, Neuron neuron, int index) {
        this.inputs[index] = neuron.getFired();
        this.weights[index] = weight;

        this.weightedSum += this.inputs[index] * this.weights[index];
    }

    public void activationFunction() {
        switch (af) {
            case NONE:
                this.fire = this.weightedSum;
                break;
            case HEAVISDE:
                if (this.weightedSum <= 0) {
                    this.fire = 0;
                } else {
                    this.fire = 1;
                }
                break;
            case SIGNUM:
                if (this.weightedSum <= 0) {
                    this.fire = -1;
                } else {
                    this.fire = 1;
                }
                break;
            case SIGMOID:
                this.fire = Utils.sigmoid(this.weightedSum);
                break;
        }

        this.weightedSum = 0;
    }

    public double getFired() {
        return fire;
    }

    public void setFire(double Fire) {
        this.fire = Fire;
    }
}

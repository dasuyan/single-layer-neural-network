package Neural;

import java.util.Arrays;
import java.util.Random;

public class Perceptron {

    private final String language;
    private final double[] weights;

    public Perceptron(String language, double deviation){
        this.language = language;
        Random ra = new Random();
        weights = ra.doubles(27,0,1).toArray();
        weights[26] = deviation;
    }

    public void learn(double[] input, String textLanguage){
        double learningConstant = 0.1;
        double d = textLanguage.equals(language) ? 1 : 0;

        double y = calculateNet(input);
        for (int i = 0; i < input.length; i++) {
            weights[i] = weights[i] + learningConstant * (d - y) * input[i];
        }
    }

    double calculateNet(double[] input){
        double net = 0;
        for (int i = 0; i < weights.length; i++) {
            net += weights[i] * input[i];
        }
        return net >= 0 ? 1 : 0;
    }

    public String getLanguage() {
        return language;
    }

    @Override
    public String toString() {
        return "Perceptron{" + language +
                " weights=" + Arrays.toString(weights) +
                '}';
    }
}

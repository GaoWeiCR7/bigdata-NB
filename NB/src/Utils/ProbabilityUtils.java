package Utils;

import classification.NaiveBayesModel.Prediction;

import java.util.HashMap;

public class ProbabilityUtils {
    public static double calPriorProb(String className) {
        return Prediction.wordTotalNumForClass.get(className) / Prediction.wordTotalNum;
    }

    public static double calConProb(String word, String className){
        HashMap<String, Integer> count = Prediction.wordNumForClass.get(className);
        if (count.get(word) == null) {
            return Math.log(1 / (Prediction.wordTotalNumForClass.get(className) + Prediction.wordListSize));
        }else {
            return Math.log((count.get(word) + 1) / (Prediction.wordTotalNumForClass.get(className) + Prediction.wordListSize));
        }
    }
}

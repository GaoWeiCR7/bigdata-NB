package classification.NaiveBayesModel;

import Utils.ProbabilityUtils;

import java.util.*;

/**
 * 预测
 */
public class Prediction {
    /**
     * 单词总数 （允许重复）
     */
    public static double wordTotalNum = 0;

    /**
     * 单词表大小多个算一个
     */
    public static double wordListSize = 0;

    /**
     * 类名列表
     */
    public static List<String> classList = new LinkedList<>();

    /**
     * 类c下的单词总数
     */
    public static HashMap<String, Integer> wordTotalNumForClass = new HashMap<>();

    /**
     * 类c下的单词出现的次数
     */
    public static HashMap<String, HashMap<String, Integer>> wordNumForClass = new HashMap<>();

    /**
     * 先验概率
     */
    public static HashMap<String, Double> priorProbability = new HashMap<>();

    /**
     * 类条件概率
     */
    public static HashMap<String, Double> condProbability = new HashMap<>();

    /**
     * P(C）
     */
}


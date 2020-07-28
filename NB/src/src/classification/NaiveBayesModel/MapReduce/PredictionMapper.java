package classification.NaiveBayesModel.MapReduce;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import preprocess.TextTokenizer;

import java.io.IOException;
import java.util.*;

public class PredictionMapper extends Mapper<NullWritable, Text, Text, Text> {
    /**
     * 单词总数 （允许重复）
     */
    public double wordTotalNum = 0;

    /**
     * 单词表大小多个算一个
     */
    public double wordListSize = 0;
    /**
     * 类名列表
     */
    public List<String> classList = new LinkedList<>();
    /**
     * 类c下的单词总数
     */
    public HashMap<String, Integer> wordTotalNumForClass = new HashMap<>();
    /**
     * 类c下的单词出现的次数
     */
    public HashMap<String, HashMap<String, Integer>> wordNumForClass = new HashMap<>();
    /**
     * 先验概率
     */
    public HashMap<String, Double> priorProbability = new HashMap<>();

    /**
     * 类条件概率
     */
    public HashMap<String, Double> condProbability = new HashMap<>();

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
        String filestr = context.getProfileParams();
        String wordtotalfile = filestr.split("#")[0]+"/part-r-00000";
        String wordcountfile = filestr.split("#")[1]+"/part-r-00000";

        FSDataInputStream inStream1 = FileSystem.get(context.getConfiguration()).open(new Path(wordtotalfile));
        String line1;
        //类名\t单词数
        while(inStream1.available() > 0) {
            line1 = inStream1.readLine();
            String nclass = line1.split("\t")[0];
            Integer tempnum = Integer.parseInt(line1.split("\t")[1]);
            classList.add(nclass);
            wordTotalNumForClass.put(nclass,tempnum);
        }

        FSDataInputStream inStream2 = FileSystem.get(context.getConfiguration()).open(new Path(wordcountfile));
        String line2;
        String curclass = null;
        HashMap<String,Integer> wordnum = new HashMap<>();
        //类名 单词 数量
        while(inStream2.available()>0) {
            line2 = inStream2.readLine();
            String classword = line2.split("\t")[0];
            Integer tempnum = Integer.parseInt(line2.split("\t")[1]);
            String nclass = classword.split("#")[0];
            String tempword = classword.split("#")[1];
            if(curclass == null) {
                curclass = nclass;
            }
            if(!curclass.equals(nclass)) {
                wordNumForClass.put(curclass,wordnum);
                curclass = nclass;
                wordnum = new HashMap<>();
            }
            wordnum.put(tempword,tempnum);
        }
        wordNumForClass.put(curclass,wordnum);

        // 计算单词总数
        for (String className : wordTotalNumForClass.keySet()) {
            wordTotalNum += wordTotalNumForClass.get(className);
        }
        //计算先验概率
        for (String className : wordTotalNumForClass.keySet()){
            priorProbability.put(className, calPriorProb(className));
        }

        //计算每个类下的单词数
        Set<String> wordList = new HashSet<>();

        for (String className : wordNumForClass.keySet()) {
            HashMap<String, Integer> values = wordNumForClass.get(className);
            wordList.addAll(values.keySet());
        }
        wordListSize = wordList.size();

        //计算条件概率
        for (String className : wordNumForClass.keySet()) {
            HashMap<String, Integer> values = wordNumForClass.get(className);
            for (String word : values.keySet()) {
                condProbability.put(className + "#" + word, calConProb(word, className));
            }
        }
        /*for(int i = 0; i < classList.size(); ++i)
            System.out.println(classList.get(i));

        System.out.println(wordTotalNumForClass);
        System.out.println(wordNumForClass);*/
    }

    /**
     * 计算概率
     * 输出为：
     * 文件名  类名#概率
     * @param key
     * @param value
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void map(NullWritable key, Text value, Context context) throws IOException, InterruptedException {
        List<String> words = TextTokenizer.getInstance().tokenizeOneLine(value.toString());
        FileSplit fileSplit = (FileSplit)context.getInputSplit();
        Text text = new Text(fileSplit.getPath().getParent().getName()+"#"+fileSplit.getPath().getName());    //TODO:检查文件名

        for (String className : classList) {
            double pro = priorProbability.get(className);
            for (String word : words) {
                if (condProbability.get(className + "#" + word) != null) {
                    pro *= condProbability.get(className + "#" + word);
                }
            }
            context.write(text, new Text(className + "#" + pro));
        }
    }

    private double calPriorProb(String className) {
        return wordTotalNumForClass.get(className) / wordTotalNum;
    }

    private double calConProb(String word, String className){
        HashMap<String, Integer> count = wordNumForClass.get(className);
        if (count.get(word) == null) {
            return Math.log(1 / (wordTotalNumForClass.get(className) + wordListSize));
        }else {
            return Math.log((count.get(word) + 1) / (wordTotalNumForClass.get(className) + wordListSize));
        }
    }
}
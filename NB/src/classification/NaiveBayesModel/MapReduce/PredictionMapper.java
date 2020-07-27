package classification.NaiveBayesModel.MapReduce;

import Utils.FileUtils;
import Utils.ProbabilityUtils;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import preprocess.TextTokenizer;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static classification.NaiveBayesModel.Prediction.*;

public class PredictionMapper extends Mapper<Object, Text, Text, Text> {
    @Override
    protected void setup(Context context) throws IOException, InterruptedException {

        String filestr = context.getProfileParams();
        String wordtotalfile = filestr.split("#")[0]+"/part-r-00000";
        String wordcountfile = filestr.split("#")[1]+"/part-r-00000";

        FSDataInputStream inStream1 = FileSystem.get(context.getConfiguration()).open(new Path(wordtotalfile));
        String line1;
        while(inStream1.available() > 0)
        {
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
        while(inStream2.available()>0)
        {
            line2 = inStream2.readLine();
            String classword = line2.split("\t")[0];
            Integer tempnum = Integer.parseInt(line2.split("\t")[1]);
            String nclass = classword.split("#")[0];
            String tempword = classword.split("#")[1];
            if(curclass == null)
            {
                curclass = nclass;
            }
            if(curclass.equals(nclass) == false)
            {
                wordNumForClass.put(curclass,wordnum);
                curclass = nclass;
                wordnum = new HashMap<>();
            }
            wordnum.put(tempword,tempnum);
        }
        wordNumForClass.put(curclass,wordnum);


        //先计算先验概率 计算单词总数
        for (String className : wordTotalNumForClass.keySet()) {
            priorProbability.put(className, ProbabilityUtils.calPriorProb(className));
            wordTotalNum += wordTotalNumForClass.get(className);
            classList.add(className);
        }

        Set<String> wordList = new HashSet<>();

        //类条件概率
        for (String className : wordNumForClass.keySet()) {
            HashMap<String, Integer> values = wordNumForClass.get(className);
            for (String word : values.keySet()) {
                condProbability.put(className + "#" + word, ProbabilityUtils.calConProb(word, className));
                wordList.add(word);
            }
        }

        wordListSize = wordList.size();
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
    protected void map(Object key, Text value, Context context) throws IOException, InterruptedException {
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
}




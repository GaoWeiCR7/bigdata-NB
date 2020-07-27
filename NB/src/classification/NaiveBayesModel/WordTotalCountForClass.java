package classification.NaiveBayesModel;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import preprocess.TextTokenizer;


import java.io.IOException;
import java.util.List;

/**
 * 类名 单词总数
 */
public class WordTotalCountForClass {
    /**
     * 输入训练集
     * 输出：
     * 类名   1
     */
    public static class WordTotalCountMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
        private final Text text = new Text();

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            FileSplit fileSplit = (FileSplit) context.getInputSplit();
            Path path = fileSplit.getPath();

            text.set(path.getParent().getName());
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            List<String> values = TextTokenizer.getInstance().tokenizeOneLine(value.toString());
            int count = 0;
            for (String word : values) {
                count++;
            }
            context.write(text, new IntWritable(count));
        }
    }

    public static class PriorProbabilityReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        private final IntWritable intWritable = new IntWritable(1);

        /**
         * 输出：
         * 类名   单词数
         * @param key
         * @param values
         * @param context
         * @throws IOException
         * @throws InterruptedException
         */
        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable value : values) {
                sum += value.get();
            }
            intWritable.set(sum);
            Prediction.wordTotalNumForClass.put(key.toString(), sum);
            context.write(key, intWritable);
        }
    }
}

package classification.NaiveBayesModel;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import preprocess.TextTokenizer;

import java.io.IOException;
import java.util.List;

/**
 * 类名 单词 出现次数
 */
public class WordCountForClass {
    public static class WordCountForClassMapper extends Mapper<LongWritable, Text, Text, IntWritable> {
        private IntWritable intWritable = new IntWritable(1);

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            FileSplit fileSplit = (FileSplit)context.getInputSplit();
            String dirName = fileSplit.getPath().getParent().getName();

            String line = value.toString();

            List<String> words = TextTokenizer.getInstance().tokenizeOneLine(line);
            for (String word : words) {
                context.write(new Text(dirName + "#" + word), intWritable);
            }
        }
    }

    public static class WordCountForClassReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
        @Override
        protected void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable intWritable : values) {
                sum += intWritable.get();
            }
            context.write(key, new IntWritable(sum));
        }
    }
}

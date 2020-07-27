package classification.NaiveBayesModel.MapReduce;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class PredictionReducer extends Reducer<Text, Text, Text, Text> {
    /**
     * 找出概率最大的类
     * 输出：
     * 文件名  类名
     * @param key
     * @param values
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
        String preClass = null;
        Double maxValue = -Double.MAX_VALUE;
        for (Text value : values) {
            String[] args = value.toString().split("#");
            if (Double.compare(Double.parseDouble(args[1]), maxValue) > 0) {
                maxValue = Double.parseDouble(args[1]);
                preClass = args[0];
            }
        }
        context.write(key, new Text(preClass));
    }
}

package evaluate;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.lib.output.MultipleOutputs;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.NullWritable;

import java.io.IOException;

public class evaluatereducer extends Reducer<Text, IntWritable, NullWritable, Text>{
    int rightnum = 0;
    int wrongnum = 0;

    @Override
    public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException{
        String keystr = key.toString();
        if(keystr.equals("1"))
        {
            for(IntWritable v:values)
                rightnum += v.get();
        }
        else
        {
            for(IntWritable v:values)
                wrongnum += v.get();
        }
    }
    public void cleanup(Context context) throws IOException, InterruptedException{
        double rate = rightnum*1.0/(rightnum+wrongnum);
        context.write(NullWritable.get(),new Text("right:"+rightnum+"\t"+"wrong:"+wrongnum+"\t"+"rate:"+String.valueOf(rate)));
    }
}

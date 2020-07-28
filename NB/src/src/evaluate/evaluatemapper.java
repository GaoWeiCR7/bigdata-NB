package evaluate;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.io.DoubleWritable;
import preprocess.TextTokenizer;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class evaluatemapper extends Mapper<Object, Text, Text, IntWritable>{

    @Override
    protected void map(Object key, Text value, Context context) throws IOException, InterruptedException{
        String linestr = value.toString();
        String filedir = linestr.split("\t")[0].split("#")[0];
        String nclass = linestr.split("\t")[1];
        if(filedir.equals(nclass))
            context.write(new Text("1"),new IntWritable(1));
        else
            context.write(new Text("0"),new IntWritable(1));
    }
}

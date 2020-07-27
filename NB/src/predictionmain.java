import Utils.FileUtils;
import classification.NaiveBayesModel.WordTotalCountForClass;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.IOException;

public class predictionmain {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException{
        Configuration configuration = new Configuration();
        String[] otherArgs = new GenericOptionsParser(configuration, args).getRemainingArgs();
        if (otherArgs.length < 4) {
            System.err.println("args length wrong\n");
            System.exit(-1);
        }
        Path[] paths = FileUtils.folder(args[0], configuration);

        Job job = Job.getInstance(configuration, "NBM-predict");
        job.setJarByClass(predictionmain.class);
        job.setMapperClass(classification.NaiveBayesModel.MapReduce.PredictionMapper.class);
        job.setReducerClass(classification.NaiveBayesModel.MapReduce.PredictionReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        for(Path p:paths)
            FileInputFormat.addInputPath(job,p);
        FileOutputFormat.setOutputPath(job,new Path(args[1]+"/NB"));

        job.setProfileParams(args[2]+"#"+args[3]);
        job.waitForCompletion(true);
    }
}

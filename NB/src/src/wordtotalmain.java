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

public class wordtotalmain {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException {
        Configuration configuration = new Configuration();
        String[] otherArgs = new GenericOptionsParser(configuration, args).getRemainingArgs();
        if (otherArgs.length < 2) {
            System.err.println("args length wrong\n");
            System.exit(-1);
        }
        Path[] paths = FileUtils.folder(args[0], configuration);

        Job job = Job.getInstance(configuration, "NBM-wordTotalCount");
        job.setJarByClass(wordtotalmain.class);
        job.setMapperClass(classification.NaiveBayesModel.WordTotalCountForClass.WordTotalCountMapper.class);
        job.setReducerClass(classification.NaiveBayesModel.WordTotalCountForClass.PriorProbabilityReducer.class);

        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        for (Path p : paths)
            FileInputFormat.addInputPath(job, p);
        FileOutputFormat.setOutputPath(job, new Path(args[1] + "/wordtotal"));
        job.waitForCompletion(true);
    }
}

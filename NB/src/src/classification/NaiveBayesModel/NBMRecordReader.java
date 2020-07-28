package classification.NaiveBayesModel;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.InputSplit;
import org.apache.hadoop.mapreduce.RecordReader;
import org.apache.hadoop.mapreduce.TaskAttemptContext;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.input.LineRecordReader;

import java.io.IOException;

public class NBMRecordReader extends RecordReader<NullWritable, Text> {
    private FileSplit fileSplit;
    private Configuration configuration;
    private Text value = new Text();
    private boolean isProcess = false;
    private LineRecordReader recordReader = new LineRecordReader();

    @Override
    public void initialize(InputSplit inputSplit, TaskAttemptContext taskAttemptContext) throws IOException, InterruptedException {
        this.fileSplit = (FileSplit) inputSplit;
        configuration = taskAttemptContext.getConfiguration();
        recordReader.initialize(fileSplit, taskAttemptContext);
    }

    @Override
    public boolean nextKeyValue() throws IOException, InterruptedException {
        if (!isProcess) {
            String result = "";
            while (recordReader.nextKeyValue()) {
                result += recordReader.getCurrentValue() + "\n";
            }
            value.set(result);
            isProcess = true;
            return true;
        }
        return false;
    }

    @Override
    public NullWritable getCurrentKey() throws IOException, InterruptedException {
        return NullWritable.get();
    }

    @Override
    public Text getCurrentValue() throws IOException, InterruptedException {
        return value;
    }

    @Override
    public float getProgress() throws IOException, InterruptedException {
        return recordReader.getProgress();
    }

    @Override
    public void close() throws IOException {

    }
}

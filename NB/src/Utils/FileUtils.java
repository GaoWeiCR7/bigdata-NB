package Utils;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FileUtil;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.net.URI;

public class FileUtils {
    public static Path[] folder(String infolder, Configuration conf) throws IOException {
        FileSystem temp = FileSystem.get(URI.create(infolder),conf);
        FileStatus[] res = temp.listStatus(new Path(infolder));
        return FileUtil.stat2Paths(res);
    }
}

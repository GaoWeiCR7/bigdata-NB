package preprocess;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 * 分词器
 * @author zihan
 *
 */
public class TextTokenizer {
    private TextTokenizer() {}
    private static TextTokenizer instance = null;
    private Analyzer analyzer = new SimpleAnalyzer();

    public static TextTokenizer getInstance() {
        if (instance == null) {
            synchronized (TextTokenizer.class) {
                if (instance == null) {
                    instance = new TextTokenizer();
                }
            }
        }
        return instance;
    }

    public List<String> tokenize(String fileName) {
        File file = new File(fileName);
        List<String> list = new LinkedList<>();

        try(Reader reader = new FileReader(file)) {
            TokenStream ts = analyzer.tokenStream("", reader);
            CharTermAttribute cta = ts.addAttribute(CharTermAttribute.class);
            ts.reset();
            while (ts.incrementToken()) {
                list.add(cta.toString());
            }
            ts.close();
        }catch (IOException e) {
            e.printStackTrace();
            Logger.getAnonymousLogger().log(Level.SEVERE, "cannot read this file:" + fileName);
        }
        return list;
    }

    public List<String> tokenizeOneLine(String line) {
        TokenStream ts = analyzer.tokenStream("", line);
        CharTermAttribute cta = ts.addAttribute(CharTermAttribute.class);
        List<String> list = new LinkedList<>();
        try {
            ts.reset();
            while (ts.incrementToken()) {
                list.add(cta.toString());
            }
            ts.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}

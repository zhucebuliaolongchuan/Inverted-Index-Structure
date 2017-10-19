import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.io.File;

public class Main {
    /**
     * @param args
     * @throws IOException
     *
     */
    public static String[] getListFiles(String dir) throws IOException {
        File directory = new File(dir.toString());
        ArrayList<File> files = new ArrayList<>();
        if (directory.isFile()) {
            files.add(directory);
            return null;
        } else if (directory.isDirectory()) {
            File[] fileArr = directory.listFiles();
            String[] fns = new String[fileArr.length];
            for (int i = 0; i < fileArr.length; i++) {
                File fileOne = fileArr[i];
                fns[i] = fileOne.toString();
                System.out.println(fileOne);
            }
            return fns;
        } else {
            return null;
        }
    }

    public static void main(String[] args) throws IOException {
        // Initialize the inverted_index
//        String fn = "/Users/chuanlong/Downloads/inverted-index-structure/data/";
//        String[] fns = getListFiles(fn);
////        String fn = "/Users/chuanlong/Downloads/inverted-index-structure/data/CC-MAIN-20170919112242-20170919132242-00000.warc.wet.gz";
//        int test_size = 3000000;
//        int split_size = 500;
//        InvertedIndexGenerator generator = new InvertedIndexGenerator();
//        generator.generatePosting(fns, test_size, split_size);

        InvertedIndexMerger merger = new InvertedIndexMerger();
        merger.mergeInvertedIndex("inverted_index/sorted.txt", "results/final_inverted_index.txt");

//        InvertedIndexDecoder decoder = new InvertedIndexDecoder();
//        decoder.decodeInvertedIndex("results/final_inverted_index.txt");
    }
}
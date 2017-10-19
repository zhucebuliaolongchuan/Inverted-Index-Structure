import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by chuanlong on 10/14/17.
 */
public class InvertedIndexMerger {

    static int cur_pos = 0;

    //Write Lexicon, write the lexicon to the
    private static void writeLexicon(String term, int pos, int length) {
        try {
            FileWriter fw = new FileWriter("results/lexicon.txt", true);
            String s = term + "\t" + String.valueOf(pos) + "\t" + String.valueOf(length) + "\n";
            fw.write(s);
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //VBytes algorithm, compress the index
    public static byte[] vBytes(int index, int freq) {
        ArrayList<Byte> bytes = new ArrayList<>();
        while (true) {
            bytes.add(0, (byte) (index % 128));
            if (index < 128)
                break;
            index /= 128;
        }
        for (int i = 0; i < bytes.size() - 1; ++i) {
            bytes.set(i, (byte)((int)bytes.get(0) + 128));
        }
        //Write the freq to the byte buffer also
        byte bts[] = new byte[bytes.size() + 1];
        bts[0] = (byte) freq;
        for (int i = 1; i < bytes.size(); i++) {
            bts[i] = bytes.get(i);
        }
        return bts;
    }

    public static void compressandwriteInvertedIndex(String s, DataOutputStream dos) {
        // Get the posting from the string
        String[] indexes = s.split("\t");
        // Initialize the data structure of compressed inverted index
        if (indexes.length != 0) {
            String term = indexes[0];
            HashMap<Integer, Integer> posting = new HashMap<>();
            for (int i = 1; i < indexes.length; ++i) {
                String[] sub_index = indexes[i].split(",");
                if (sub_index.length == 2 && sub_index[0].matches("^[0-9]*") && sub_index[1].matches("^[0-9]*")) {
//                    System.out.println(sub_index);
                    posting.put(Integer.parseInt(sub_index[0]), Integer.parseInt(sub_index[1]));
                } else {
//                    System.out.println("Invalid Posting");
                }
            }
            // Sort the docID and compress the index
            SortedSet<Integer> sorted_indexes = new TreeSet<>(posting.keySet());
            int last = 0;
            int lens = 0;
            for (int key : sorted_indexes) {
                int freq = posting.get(key);
                int offset = key - last;
                last = key;
                byte[] bts = vBytes(offset, freq);
                lens += bts.length;
                try {
                    dos.write(bts);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // Write the lexiconInverted
            writeLexicon(term, cur_pos, lens);
            cur_pos += lens;
        }
    }

    public static void mergeInvertedIndex(String fn, String final_fn) {
        // Initialize the inverted_index
        String curr_s = "";
        String curr_key = "";
        String new_key;
        try {
            // Initialize the input stream object
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fn), "UTF-8"));
            // Initialize the output stream object
            FileOutputStream fos = new FileOutputStream(final_fn);
            DataOutputStream dos = new DataOutputStream(fos);
            String new_line;
            while ((new_line = br.readLine()) != null) {
                String[] posting = new_line.trim().split("\t");
                if (posting.length == 0)
                    continue;
                new_key = posting[0];
                if (new_key.matches("^[a-zA-Z]*") != true)
                    continue;
                if(new_key.equals(curr_key)) {
                    for (int i = 1; i < posting.length; ++i) {
                        curr_s += "\t" + posting[i];
                    }
                } else {
                    // Write the curr_s to the final index file
                    if (curr_s.length() != 0)
                        compressandwriteInvertedIndex(curr_s, dos);
//                    System.out.println("Successfully compress and write the current inverted index");
                    // And also reset the file
                    curr_key = new_key;
                    curr_s = curr_key;
                    for (int i = 1; i < posting.length; ++i) {
                        curr_s += "\t" + posting[i];
                    }
                }
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
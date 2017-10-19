import javafx.geometry.Pos;
import org.apache.commons.io.IOUtils;
import org.apache.commons.math3.geometry.euclidean.oned.Interval;
import org.archive.io.ArchiveReader;
import org.archive.io.ArchiveRecord;
import org.archive.io.warc.WARCReaderFactory;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by chuanlong on 10/16/17.
 */
public class InvertedIndexGenerator {
    private static class Posting {
        int start_pos = 0;
        int freq = 0;
    }

    private static class UrlTable {
        String url = null;
        long length = 0;
    }

    public static void writeInvertedIndex(HashMap<String, Map<Integer, Posting>> inverted_index, int split_size, int docID) throws IOException{
        System.out.println("Writing\t" + String.valueOf(docID / split_size) + "\tinverted index file");
        String file_name = "inverted_index/" + String.valueOf(docID / split_size) + "_inverted_index.txt";
        FileOutputStream fos = new FileOutputStream(file_name);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        for (String key : inverted_index.keySet()) {
            if (key.length() == 0)
                continue;
            String s = key + "\t";
            for (int doc_id : inverted_index.get(key).keySet()) {
                Posting temp = inverted_index.get(key).get(doc_id);
                s += String.valueOf(doc_id) + "," + String.valueOf(temp.freq) + "\t";
            }
            s += '\n';
            oos.writeBytes(s);
        }
        oos.close();
    }

    public static void writePageTable(HashMap<Integer, UrlTable> page_table) {

    }

    public static void generatePosting(String[] fns, int test_size, int split_size) throws IOException {
        HashMap<String, Map<Integer, Posting>> inverted_index = new HashMap<>();
        // Initialize the Page Table
        HashMap<Integer, UrlTable> page_table = new HashMap<>();

        FileOutputStream fos = new FileOutputStream("results/page_table.txt");
        ObjectOutputStream page_table_oos = new ObjectOutputStream(fos);
        // Set up a local compressed WET file for reading
//        String fn = "/Users/chuanlong/Downloads/inverted-index-structure/data/CC-MAIN-20170919112242-20170919132242-00000.warc.wet.gz";
        int docID = 0;
        FileInputStream is;
        ArchiveReader ar;
        for (int i = 0; i < fns.length; ++i) {
            try {
                is = new FileInputStream(fns[i]);

                // The file name identifies the ArchiveReader and indicates if it should be decompressed
                ar = WARCReaderFactory.get(fns[i], is, true);
                // Once we have an ArchiveReader, we can work through each of the records it contains
                // We assign the docID according to the parsing order

                for(ArchiveRecord r : ar) {
                    // The header file contains information such as the type of record, size, creation time, and URL
//            System.out.println(r.getHeader());
//            System.out.println(r.getHeader().getUrl());
                    String current_url = r.getHeader().getUrl();

                    if (current_url == null)
                        continue;

                    // If we want to read the contents of the record, we can use the ArchiveRecord as an InputStream
                    // Create a byte array that is as long as the record's stated length
                    byte[] rawData = IOUtils.toByteArray(r, r.available());

                    // Why don't we convert it to a string and print the start of it? Let's hope it's text!
                    String content = new String(rawData, "UTF-8");

                    // Create the url table <docID : url, length of url>
                    UrlTable value = new UrlTable();
                    value.length = content.length();
                    value.url = current_url;
                    page_table.put(docID, value);
                    String s = String.valueOf(docID) + "\t" + String.valueOf(content.length()) + "\t" + current_url + "\n";
                    page_table_oos.writeBytes(s);

                    // Stemming Parse
                    String[] words = content.split(" |\\\\|\\\"|\\'|\\.|\\(|\\)|\\*|\\||\\&|\\^|\\%|$|#|@|!|>|<|,|。|【|】|》|《|:|~|\\+|`|-|=|/|\n|:|”|“|：|！|、|\\?");

                    // Generate the intermediate Posting structure
                    for (int pos = 0; pos < words.length; ++pos) {
                        String w = words[pos];
                        w = w.trim();
                        w = w.replaceAll("[^0-9a-zA-Z]", "");
                        if (w.matches("^[a-zA-Z0-9]*") != true || w.length() == 0)
                            continue;

                        int doc_id = docID;

                        // Generate the posting
                        if (inverted_index.containsKey(w)) {
                            if (inverted_index.get(w).containsKey(doc_id)) {
                                Posting temp = inverted_index.get(w).get(doc_id);
                                temp.freq += 1;
                                inverted_index.get(w).put(doc_id, temp);
                            } else {
                                Posting temp = new Posting();
                                temp.freq = 1;
                                temp.start_pos = pos;
                                inverted_index.get(w).put(doc_id, temp);
                            }
                        } else {
                            Map<Integer, Posting> item = new HashMap<>();
                            Posting temp = new Posting();
                            temp.freq = 1;
                            // No need to update the start position anymore
                            temp.start_pos = pos;
                            item.put(doc_id, temp);
                            inverted_index.put(w, item);
                        }
                    }
//            System.out.println("=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=");
                    docID += 1;
                    if (docID > test_size)
                        break;
                    if (docID % split_size == 0) {
                        writeInvertedIndex(inverted_index, split_size, docID);
                        inverted_index.clear();
                    }
                }
                is.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        page_table_oos.close();
    }
}
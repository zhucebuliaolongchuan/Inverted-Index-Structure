### Inverted-Index-Structure
[![Travis](https://img.shields.io/travis/rust-lang/rust.svg?style=plastic)]()
[![apm](https://img.shields.io/apm/l/vim-mode.svg?style=plastic)]()


_This program means to produce an **inverted-index structure**, a **lexicon structure** and a **url-page table**, which is foundation data structure of the web pages query._

---


#### 1. **What my program can do:**  
  
* Parse compressed **WET files** provided by [CommonCrawl.com](http://commoncrawl.org/) and fetch all the words in English and digits using the methods from [CommonCrawl.com](http://commoncrawl.org/) also. Ideally loading compressed files other than uncompressed before the running of the program.  
  
* Create the inverted index structure for the extracted words from millions of web pages. **(10GB WET files in compressed format, about three million web pages at all)**  
  
* Adopt I/O efficient algorithms, and the principle of them is similar as Hadoop, emit the inverted index structure of the current key to the byte buffer when it encounters a new term. No worries about when main memory is much smaller than the data set.  
  
* Produced **one file for the final inverted index structure which is in binary format**. And also **the lexicon structure** providing the position information of the block that we want to read. And **one page table** that produced at the very beginning of parsing each web pages. 
   
* The program is **well structured** and is **easily to extend**  


#### 2. **How to run the program:**  
* Runtime Environment:
	* Programming Language: **Java**
	* IDE: **Intellij IDEA**
	* JRE Version: **>= 1.8**
	* Dependencies: **webarchive-commons-jar-with-dependencies.jar**
* Setup:
	* Ideally import the project into an Intellij IDEA ide
	* configure the runtime environment with JRE(>= 1.8 Verision).
	* Import the needed jar package from the CommonCrwal.com for parsing the compressed WET files, such as:     
  ```webarchive-commons-jar-with-dependencies.jar.```  
    **Please put the data directory in an appropriate name and location. Or it will not throw the IOException(FileNotFound, etc.).** 
     
* The program is separated into two parts – InvertedIndexGenerator and
InvertedIndexMerger. Please simply commented out the other class initializer if you want to run the class you want in the Main.class. 
   
* To generate the intermediate inverted index, please **comment out the class for Merger, and then run the program**. It will produce the intermediate inverted index structure waited to be sorted and then merged. And it will also generate the final page table when it done.  
  
* Then, use the **Unix sort and merge** method to sort the entire intermediate inverted index structure. Merge them into one single file that could be processed for the next step. The terminal command is like:  
```LC_ALL=”C” sort *_inverted_index.txt > sorted.txt```  
  
* Finally, **comment out the class for generator**. And then run the program. During this process, it will produce the lexicon structure line by line and output the final inverted index structure in the format of byte stream.


#### 3. **Limitations and Improvements**  

* Better design thinking could be applied to make the program more regular and standardized  
  
* More I/O tricky but efficient method could be applied (other than vBytes)
  
* Keep index compressed even though during the intermediate process(part of Unix
sort)

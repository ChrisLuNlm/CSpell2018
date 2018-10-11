package gov.nih.nlm.nls.cSpell.Ranker;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import gov.nih.nlm.nls.cSpell.Util.*;
/*****************************************************************************
* This is a java object class for Word2Vec object.
*
* <p><b>History:</b>
* <ul>
* <li>2018 baseline
* </ul>
*
* @author chlu
*
* @version    V-2018
*****************************************************************************/
public class Word2Vec 
{
    // public constructor
    public Word2Vec(String inFile)
    {
        boolean verboseFlag = false;
        Init(inFile, verboseFlag);
    }
    public Word2Vec(String inFile, boolean verboseFlag)
    {
        Init(inFile, verboseFlag);
    }
    
    // WordVectors: key: word, value: DoubleVec
    public Map<String, DoubleVec> GetWordVecMap() 
    {
        return wordVecMap_;
    }
    
    // the dimension of DoubleVec
    public int GetDimension() 
    {
        return dimension_;
    }
    
    // total word no (vocabulary count) with DoubleVec
    public int GetWordNo() 
    {
        return wordNo_;
    }
    // check if a word has a wordVec
    public boolean HasWordVec(String word)
    {
        String inWord = GetKeyWord(word);
        boolean hasWordVec = wordVecMap_.containsKey(inWord);
        return hasWordVec;
    }
    // get the wordVec
    // lowercase the word
    public DoubleVec GetWordVec(String word)
    {
        String inWord = GetKeyWord(word);
        DoubleVec out = wordVecMap_.get(inWord);
        return out;
    }
    // the key in word2Vec are all lowercased except for URL, NUM, EMAIL
    private static String GetKeyWord(String inWord)
    {
        if((inWord.equals(Word2VecContext.PAT_URL) == true)
        || (inWord.equals(Word2VecContext.PAT_EMAIL) == true)
        || (inWord.equals(Word2VecContext.PAT_NUM) == true))
        {
            return inWord;
        }
        return inWord.toLowerCase();
    }
    
    // private methods
    // TBD: convert the format [CONTACT] to [EMAIL]
    // TBD: sync the format in PreProcess: [CONTACT], [NUM], ...
    // TBD: make sure the coreTerm does not take out above pattern
    private static String GetSyncWord(String inWord)
    {
        String syncWord = inWord;
        if(inWord.equalsIgnoreCase("CONTACT") == true)
        {
            syncWord = "EMAIL";
        }
        return syncWord;
    }
    /** 
    * Instantiates a Word2Vec object from a file that was generated 
    * by the original word2Vec program.
    * 
    * @param inFile  the file containing the Word2Vec model.
    */
    private void Init(String inFile, boolean verboseFlag)
    {
        if(wordVecMap_ == null) 
        {
            ReadWordVectors(inFile, verboseFlag);
        }
    }
    
    // read word vectors from a input file
    private void ReadWordVectors(String inFile)
    {
        boolean verboseFlag = false;
        ReadWordVectors(inFile, verboseFlag);
    }
    private void ReadWordVectors(String inFile, boolean verboseFlag)
    {
        // init 
        wordVecMap_ = new HashMap<String, DoubleVec>();
        int lineNo = 0;
        String line = null;
        try
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                new FileInputStream(inFile), "UTF-8"));
            // read in line by line from a file
            while((line = in.readLine()) != null)
            {
                StringTokenizer buf = new StringTokenizer(line, " ");
                // first line is the stats wordNo|dimension
                if(lineNo == 0)
                {
                    wordNo_ = Integer.parseInt(buf.nextToken());
                    dimension_ = Integer.parseInt(buf.nextToken());
                    //dimension_ = line0.split(delimiter).length -1;
                }
                else    // word|vector
                {
                    String word = buf.nextToken();
                    double[] array = new double[dimension_];
                    for(int i = 0; i < dimension_; i++)
                    {
                        array[i] = Double.parseDouble(buf.nextToken());
                    }
                    // update wordVec
                    wordVecMap_.put(word, new DoubleVec(array));
                }
                lineNo++;
            }
            // close
            in.close();
        }
        catch(IOException e)
        {
            System.err.println("** ERR@Word2Vec.ReadWordVectors( ), problem of reading file (" + inFile);
            System.err.println("Line: " + lineNo + " - " + line);
            System.err.println("Exception: " + e.toString());
        }
        // print out
        if(verboseFlag == true)
        {
            System.out.println("====== Word2Vec.ReadWordVectors( ) ======");
            System.out.println("- inFile: " + inFile);
            System.out.println("- Word no: " + wordNo_);
            System.out.println("- dimension: " + dimension_);
        }
    }
    // unit test driver
    public static void main(String[] args) 
    {
        //String inFile = "../data/Context/word2Vec.data";
        String inFile = "../data/Context/syn1n.data";
        if(args.length == 1)
        {
            inFile = args[0];
        }
        else if(args.length > 0)
        {
            System.err.println("Usage: java Word2Vec <inFile>");
            System.exit(1);
        }
        // test
        try 
        {
            Word2Vec word2Vec = new Word2Vec(inFile);
            System.out.println("Dimension: " + word2Vec.GetDimension()); 
            System.out.println("Word No: " + word2Vec.GetWordNo()); 
            System.out.println("Word size in WrodVec: " 
                + word2Vec.GetWordVecMap().keySet().size()); 
            System.out.println("HasWordVec(man): " + word2Vec.HasWordVec("man")); 
            System.out.println("HasWordVec(king): " + word2Vec.HasWordVec("king")); 
            System.out.println("HasWordVec(ago): " + word2Vec.HasWordVec("ago")); 
            System.out.println("HasWordVec(a): " + word2Vec.HasWordVec("a")); 
            System.out.println("HasWordVec(ia): " + word2Vec.HasWordVec("ia")); 
            System.out.println("HasWordVec(m): " + word2Vec.HasWordVec("m")); 
            System.out.println("HasWordVec(xyxy): " + word2Vec.HasWordVec("xyxy")); 
        } 
        catch (Exception e) 
        {
            e.printStackTrace();
        }
    }
    
    // data members
    // key: word, value: matrix
    private Map<String, DoubleVec> wordVecMap_ = null;
    private int dimension_;    // 200
    private int wordNo_;    // 20021. same as the key Word no.
}

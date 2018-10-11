package gov.nih.nlm.nls.cSpell.Dictionary;
import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.nio.charset.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.Util.*;
/*****************************************************************************
* This class is the java object for a full dictionary.
* Full dictionary uses 8 field to load words from Lexicon dictionary.
* Full dictionary is not used in the 2018 release.
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
public class FullDictionary implements RootDictionary
{
    // public constructor
    // TBD .. a dictinary with case for abb/acr and proper noun
    /**
    * Public constructor to initiate the dictionary.
    *
    * @param caseFlag flag for case sensitive
    */
    public FullDictionary(boolean caseFlag)
    {
        caseFlag_ = caseFlag;
    }
    /**
    * Public constructor to initiate the dictionary.
    *
    * @param inFile source flat file for the dictionary
    */
    public FullDictionary(String inFile)
    {
        LoadWords(inFile);
    }
    // public methods
    public void AddWord(String word)
    {
        // add to dictionary
        FullDicVarObj fullDicVarObj = new FullDicVarObj(word);
        if(dictionary_.containsKey(word) == true)
        {
            dictionary_.get(word).add(fullDicVarObj);
        }
        else
        {
            HashSet<FullDicVarObj> valueSet = new HashSet<FullDicVarObj>();
            valueSet.add(fullDicVarObj);
            dictionary_.put(word, valueSet);
        }
        // add to file: TBD
    }
    // default the caseFlag is false, not case sensitive
    public boolean IsDicWord(String word)
    {
        boolean caseFlag = false;
        return IsDicWord(word, caseFlag);
    }
    public boolean IsDicWord(String word, boolean caseFlag)
    {
        String inWord = (caseFlag?word:word.toLowerCase());
        boolean inDicFlag = dictionary_.containsKey(inWord);
        return inDicFlag;
    }
    public boolean IsValidWord(String word)
    {
        // TBD ...
        return IsDicWord(word);
    }
    public int GetSize()
    {
        return dictionary_.size();
    }
    public HashSet<String> GetDictionarySet()
    {
        HashSet<String> dicSet = new HashSet<String>(dictionary_.keySet());
        return dicSet;
    }
    public HashMap<String, HashSet<FullDicVarObj>> GetDictionary()
    {
        return dictionary_;
    }
    // the whole line is a word to be added to dictionary
    public void AddDictionaries(String inFiles, String rootPath)
    {
        boolean debugFlag = false;
        AddDictionaries(inFiles, rootPath, debugFlag);
    }
    public void AddDictionaries(String inFiles, String rootPath, 
        boolean debugFlag)
    {
        DebugPrint.Println("- Dictionary Files: [" + inFiles + "].", debugFlag);
        String[] inFileStrArray = inFiles.split(":");
        for(String inFileStr:inFileStrArray)
        {
            String inDicFile = rootPath + inFileStr;
            DebugPrint.Println("--- Add Dictionary: [" + inDicFile + "].",
                debugFlag);
            AddDictionary(inDicFile);
        }
    }
    public void AddDictionaries(String inFilePaths, boolean debugFlag)
    {
        DebugPrint.Println("- Dictionary Files: [" + inFilePaths + "].", debugFlag);
        String[] inFileStrArray = inFilePaths.split(":");
        for(String inFileStr:inFileStrArray)
        {
            DebugPrint.Println("--- Add Dictionary: [" + inFileStr + "].",
                debugFlag);
            AddDictionary(inFileStr);
        }
    }
    public void AddDictionary(String inFile)
    {
        LoadWords(inFile);
    }
    public void AddDictionary(String inFile, boolean lowerCaseFlag)
    {
        LoadWords(inFile);
    }
    // private methods
    // TBD ... too many key ...
    private void LoadWords(String inFile)
    {
        String line = null;
        int lineNo = 0;
        try
        {
            BufferedReader reader = Files.newBufferedReader(
                Paths.get(inFile), Charset.forName("UTF-8"));
            // go through all lines
            while((line = reader.readLine()) != null)
            {
                if(line.startsWith("#") == false)
                {
                    StringTokenizer buf
                        = new StringTokenizer(line, "|");
                    String word = buf.nextToken();
                    long pos = Long.parseLong(buf.nextToken());
                    long infl = Long.parseLong(buf.nextToken());
                    String src = buf.nextToken();
                    boolean acrAbb = Boolean.parseBoolean(buf.nextToken());
                    boolean properNoun = Boolean.parseBoolean(buf.nextToken());
                    String key = word.toLowerCase();
                    FullDicVarObj fullDicVarObj = new FullDicVarObj(word,
                        pos, infl, src, acrAbb, properNoun);
                    if(dictionary_.containsKey(key) == true)
                    {
                        dictionary_.get(key).add(fullDicVarObj);
                    }
                    else
                    {
                        HashSet<FullDicVarObj> valueSet 
                            = new HashSet<FullDicVarObj>();
                        valueSet.add(fullDicVarObj);
                        dictionary_.put(key, valueSet);
                    }
                    lineNo++;
                }
            }
            // close
            reader.close();
            System.out.println("- total LineNo: " + lineNo);
        }
        catch (Exception x1)
        {
            System.err.println("** Err@FullDictionary.LoadWords( ): "
                + x1.toString() + ", [" + line + "]");
        }
    }
    private static void Test()
    {
        System.out.println("===== Unit Test of BasicDictionary =====");
        String lexDicStr = "../data/Dictionary/lexiconDic.data";
        RootDictionary dic = new FullDictionary(lexDicStr);
        System.out.println("----------------------");
        System.out.println("- Dic File: " + lexDicStr);
        System.out.println("- Dic size: " + dic.GetSize());
        System.out.println("----------------------");
        // test words
        ArrayList<String> wordList = new ArrayList<String>();
        wordList.add("test");
        wordList.add("Test");
        wordList.add("TEST");
        wordList.add("liter");
        wordList.add("litre");
        wordList.add("odor");
        wordList.add("odour");
        wordList.add("iodise");
        wordList.add("iodize");
        wordList.add("beveled");
        wordList.add("bevelled");
        wordList.add("hemolyse");
        wordList.add("hemolyze");
        wordList.add("test321");
        for(String word:wordList)
        {
            System.out.println("- IsDicWord(" + word + "): " 
                + dic.IsDicWord(word));
        }
        String word = "test321";
        System.out.println("------ Add [" + word + "] to dictionary ------");
        dic.AddWord(word);
        System.out.println("- Dic size: " + dic.GetSize());
        System.out.println("- IsDicWord(" + word + "): " + dic.IsDicWord(word));
        System.out.println("===== End of Unit Test =====");
    }
    // test driver
    public static void main(String[] args)
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java FullDictionary");
            System.exit(0);
        }
        
        // test case and print out 
        Test();
    }
    // data member
    private final static int DIC_SIZE = 1000000;    // initial size for dic: 1M
    private HashMap<String, HashSet<FullDicVarObj>> dictionary_ 
        = new HashMap<String, HashSet<FullDicVarObj>>(DIC_SIZE);
    private boolean caseFlag_ = false;    // cas esnesitive flag    
}

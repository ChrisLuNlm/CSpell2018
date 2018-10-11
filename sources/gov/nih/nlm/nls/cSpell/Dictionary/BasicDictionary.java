package gov.nih.nlm.nls.cSpell.Dictionary;
import java.util.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.Util.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
import gov.nih.nlm.nls.cSpell.Api.*;
/*****************************************************************************
* This class is the java object for a basic dictionary.
* Basic dictionary uses 1 field to load words from file(s).
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
public class BasicDictionary implements RootDictionary
{
    // public constructor
    /**
    * Public constructor to initiate the dictionary.
    */
    public BasicDictionary()
    {
        // default: case insensitive
        caseFlag_ = false;
    }
    public BasicDictionary(boolean caseFlag)
    {
        caseFlag_ = caseFlag;
    }
    public BasicDictionary(String inFile)
    {
        LoadWords(inFile);
    }
    public BasicDictionary(String inFile, int fieldNo)
    {
        LoadWords(inFile, fieldNo);
    }
    // public methods
    public void AddWord(String inWord)
    {
        // add to dictionary
        String word = (caseFlag_?inWord:inWord.toLowerCase());
        dictionary_.add(word);
        // add to file: TBD
    }
    // check if the input word is a valid word expression, 
    // including possessive, slash or, parenthetic plural form 
    public boolean IsValidWord(String inWord)
    {
        String word = (caseFlag_?inWord:inWord.toLowerCase());
        boolean wordFlag = IsDicWord(word);
        // check possessive
        if(wordFlag == false)
        {
            String orgWord = PossessiveTokenUtil.GetOrgWord(word);
            wordFlag = IsDicWord(orgWord);
        }
        // check or slash: case/test
        if(wordFlag == false)
        {
            if(word.indexOf("/") > -1)
            {
                String[] orWords = word.split("/");
                boolean orFlag = true; 
                for(String orWord:orWords)
                {
                    if(IsValidWord(orWord) == false)
                    {
                        orFlag = false;
                        break;
                    }
                }
                wordFlag = orFlag;
            }
        }
        
        // check parenthic plural forms (s), (es),(ies)
        if(wordFlag == false)
        {
            String orgWord = ParentheticPluralTokenUtil.GetOrgWord(word);
            wordFlag = IsDicWord(orgWord);
        }
    
        // check "-", not sure it is a good idea, so did not implement
        return wordFlag;
    }
    // caseFlag: case sensitive flag, if flase, all uses lowerCase
    // If caseFlag is true, words in dictionary must be have different case.
    // Also, if caseFlag is false, words in dictionary must be lowercased. 
    public boolean IsDicWord(String inWord)
    {
        String word = (caseFlag_?inWord:inWord.toLowerCase());
        boolean inDicFlag = dictionary_.contains(word);
        return inDicFlag;
    }
    public boolean GetCaseFlag()
    {
        return caseFlag_;
    }
    public int GetSize()
    {
        return dictionary_.size();
    }
    // get dictionary information
    public String ToString()
    {
        String outStr = "-- size: " + GetSize() + GlobalVars.LS_STR;
        outStr += "-- caseFlag: " + GetCaseFlag();
        return outStr;
    }
    public HashSet<String> GetDictionarySet()
    {
        return dictionary_;
    }
    // the whole line is a word to be added to dictionary
    public void AddDictionary(String inFile)
    {
        LoadWords(inFile);
    }
    // add multiple dictinoaries from multiple files
    public void AddDictionaries(String inFiles, String rootPath)
    {
        boolean debugFlag = false;
        AddDictionaries(inFiles, rootPath, debugFlag);
    }
    public void AddDictionaries(String inFiles, String rootPath, 
        boolean debugFlag)
    {
        DebugPrint.Println("- Dictionary Files: [" + inFiles + "].", debugFlag);
        // split the dictinoary by :
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
        // split the dictinoary by :
        String[] inFileStrArray = inFilePaths.split(":");
        for(String inFileStr:inFileStrArray)
        {
            DebugPrint.Println("--- Add Dictionary: [" + inFileStr + "].", 
                debugFlag);
            AddDictionary(inFileStr);
        }
    }
    // the specified field is a word to be added to dictionary
    public void AddDictionary(String inFile, int fieldNo)
    {
        LoadWords(inFile, fieldNo);
    }
    // private methods
    private void LoadWords(String inFile)
    {
        // basic dictionary, if caseFlag is false => case-insensitive
        // => lowerCase for the input
        boolean lowerCaseFlag = !caseFlag_;
        dictionary_.addAll(FileInToSet.GetSetByLine(inFile, lowerCaseFlag));
    }
    private void LoadWords(String inFile, int fieldNo)
    {
        // basic dictionary, if caseFlag is false => case-insensitive
        // => lowerCase for the input
        boolean lowerCaseFlag = !caseFlag_;
        dictionary_.addAll(FileInToSet.GetSetByField(inFile, fieldNo,
            lowerCaseFlag));
    }
    private static void Test()
    {
        System.out.println("===== Unit Test of BasicDictionary =====");
        boolean caseFlag = false;
        RootDictionary dic0 
            = DictionaryFactory.GetDictionary(DictionaryFactory.DIC_BASIC, 
            caseFlag);
        // dic0 baselin dictionary
        System.out.println("------- Words from Baseline 11 dicitoaries -------");
        String dicStrs = "../data/Dictionary/eng_medical.dic:../data/Dictionary/center.dic:../data/Dictionary/centre.dic:../data/Dictionary/color.dic:../data/Dictionary/colour.dic:../data/Dictionary/ise.dic:../data/Dictionary/ize.dic:../data/Dictionary/labeled.dic:../data/Dictionary/labelled.dic:../data/Dictionary/yse.dic:../data/Dictionary/yze.dic";
        String[] dicStrArray = dicStrs.split(":");
        ArrayList<String> dicStrList = new ArrayList<String>(Arrays.asList(dicStrArray));
        for(String dicStr:dicStrList)
        {
            dic0.AddDictionary(dicStr);
            System.out.println("- Dic0 File: " + dicStr);
            System.out.println("- Dic0 size: " + dic0.GetSize());
        }
        System.out.println("------- Lexicon element words -------");
        String lexDicEwStr = "../data/Dictionary/lexiconDic.data.ewLc";
        RootDictionary dic1 = new BasicDictionary(lexDicEwStr);
        System.out.println("- Dic1 File: " + lexDicEwStr);
        System.out.println("- Dic1 size: " + dic1.GetSize());
        System.out.println("------- Lexicon words --------");
        String lexDicStr = "../data/Dictionary/lexiconDic.data";
        int fieldNo = 1;
        RootDictionary dic2 = new BasicDictionary(lexDicStr, fieldNo);
        System.out.println("- Dic2 File: " + lexDicStr);
        System.out.println("- Dic2 size: " + dic2.GetSize());
        String numDicStr = "../data/Dictionary/NRVAR.1.uSort.data";
        dic2.AddDictionary(numDicStr);
        System.out.println("- Dic2 File: " + numDicStr);
        System.out.println("- Dic2 size: " + dic2.GetSize());
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
        wordList.add("ella");
        wordList.add("centillionths");
        wordList.add("Down's");
        wordList.add("Downs'");
        wordList.add("spot(s)");
        wordList.add("fetus(es)");
        wordList.add("box(es)");
        wordList.add("waltz(es)");
        wordList.add("mtach(es)");
        wordList.add("splash(es)");
        wordList.add("fly(ies)");
        wordList.add("extremity(ies)");
        wordList.add("CASE/TEST");
        wordList.add("John's/Chris's");
        wordList.add("50mg/100mg");
        wordList.add("case/test");
        wordList.add("neck-lesion");
        wordList.add("day-night");
        wordList.add("pneumonoultramicroscopicsilicovolcanoconiosis");
        wordList.add("Walmart");
        wordList.add("test321");
        System.out.println("input|baseline|L-element|Lexicon|L-RealWord");
        for(String word:wordList)
        {
            System.out.println("- IsDicWord(" + word + "): " 
                + dic0.IsDicWord(word) + ", " + dic1.IsDicWord(word) + ", " 
                + dic2.IsDicWord(word) + ", " + dic2.IsValidWord(word));
        }
        String word = "test321";
        System.out.println("------ Add [" + word + "] to dictionary ------");
        dic0.AddWord(word);
        dic1.AddWord(word);
        dic2.AddWord(word);
        System.out.println("- Dic0 size: " + dic0.GetSize());
        System.out.println("- Dic1 size: " + dic1.GetSize());
        System.out.println("- Dic2 size: " + dic2.GetSize());
        System.out.println("- IsInDic(" + word + "): " + dic0.IsDicWord(word)
            + ", " + dic1.IsDicWord(word) + ", " + dic2.IsDicWord(word));
        System.out.println("===== End of Unit Test =====");
    }
    private static void TestSplitDic(CSpellApi cSpellApi)
    {
        // test split dictionary
        RootDictionary splitWordDic = cSpellApi.GetSplitWordDic();
        
        // test words
        ArrayList<String> wordList = new ArrayList<String>();
        wordList.add("do");
        wordList.add("i");
        wordList.add("ng");
        wordList.add("ilove");
        for(String word:wordList)
        {
            System.out.println("-- SplitDic(" + word + "): " 
                + splitWordDic.IsDicWord(word));
        }
    }
    private static void TestPnDic(CSpellApi cSpellApi)
    {
        // test split dictionary
        RootDictionary pnDic = cSpellApi.GetPnDic();
        
        // test words
        ArrayList<String> wordList = new ArrayList<String>();
        wordList.add("hu");
        wordList.add("Hu");
        for(String word:wordList)
        {
            System.out.println("-- pnDic(" + word + "): " 
                + pnDic.IsDicWord(word));
        }
    }
    // test driver
    public static void main(String[] args)
    {
        String configFile = "../data/Config/cSpell.properties";
        if(args.length > 0)
        {
            System.out.println("Usage: java BasicDictionary");
            System.exit(0);
        }
        
        // init
        CSpellApi cSpellApi = new CSpellApi(configFile);
        // test case and print out 
        //Test();
        TestSplitDic(cSpellApi);
        TestPnDic(cSpellApi);
    }
    // data member
    private final static int DIC_SIZE = 1000000;    // initial size for dic: 1M
    private HashSet<String> dictionary_ = new HashSet<String>(DIC_SIZE);
    private boolean caseFlag_ = false;    // case sensitive flag
}

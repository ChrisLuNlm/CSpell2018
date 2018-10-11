package gov.nih.nlm.nls.cSpell.Ranker;
import java.util.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
/*****************************************************************************
* This class provides a java object of frequency score.
*
* <p><b>History:</b>
* <ul>
* <li>2018 baseline
* </ul>
*
* @author chlu
*
* @version    V-2018
****************************************************************************/
public class FrequencyScore
{
    // public constructor
    public FrequencyScore(String word, WordWcMap wordWcMap)
    {
        word_ = word;
        // calculate score
        score_ = WordCountScore.GetScore(word, wordWcMap);
    }
    // public method
    public String GetWord()
    {
        return word_;
    }
    public double GetScore()
    {
        return score_;
    }
    public String ToString()
    {
        String outStr = ToString(GlobalVars.FS_STR);
        return outStr;    
    }
    public String ToString(String fieldSepStr)
    {
        String outStr = word_ + fieldSepStr 
            + String.format("%1.8f", score_);    //match up to the PRECISION
        return outStr;
    }
    
    // private method
    private static void Test(String word, WordWcMap wordWcMap)
    {
        FrequencyScore fs = new FrequencyScore(word, wordWcMap);
        System.out.println(fs.ToString());
    }
    private static void Tests(WordWcMap wordWcMap)
    {
        ArrayList<String> testStrList = new ArrayList<String>();
        testStrList.add("the");        // first one in the corpus
        testStrList.add("if");        // first one in the corpus
        testStrList.add("you");        // first one in the corpus
        testStrList.add("doctor");
        testStrList.add("Doctor"); // Test Case
        testStrList.add("doctor[123]");
        testStrList.add("'s");
        testStrList.add("container");
        testStrList.add("diagnose");
        testStrList.add("deionized");
        testStrList.add("&eacute;vy");    // last one in the corpus
        testStrList.add("xxxx");    // last one in the corpus
        testStrList.add("doctor's");    // posssive
        testStrList.add("heart's");
        testStrList.add("if you");    // multiwords
        testStrList.add("the doctor");    // multiwords
        testStrList.add("Not exist");
        testStrList.add("brokenribscantsleepatnight");
        testStrList.add("broken");
        testStrList.add("rib");
        testStrList.add("ribs");
        testStrList.add("cant");
        testStrList.add("cants");
        testStrList.add("scant");
        testStrList.add("scants");
        testStrList.add("sleep");
        testStrList.add("leep");
        testStrList.add("lee");
        testStrList.add("pat");
        testStrList.add("at");
        testStrList.add("night");
        testStrList.add("broken ribs cants leep at night");
        testStrList.add("broken ribs cant sleep at night");
        testStrList.add("broken rib scants leep at night");
        testStrList.add("broken rib scants lee pat night");
        testStrList.add("broken rib scant sleep at night");
        System.out.println("=================================================");
        System.out.println("Word|Score");
        System.out.println("=================================================");
        for(String testStr:testStrList)
        {
            Test(testStr, wordWcMap);
        }
    }
    // test Driver
    public static void main(String[] args)
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java FrequencyScore");
            System.exit(0);
        }
        // test
        String inFile = "../data/Frequency/wcWord.data";
        boolean verboseFlag = true;
        WordWcMap wordWcMap = new WordWcMap(inFile, verboseFlag);
        Tests(wordWcMap);
    }
    // data member
    // This is language model, each string has a WC (frequency score)
    private String word_ = new String();
    private double score_ = 0.0;
}

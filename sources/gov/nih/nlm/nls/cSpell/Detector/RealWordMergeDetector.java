package gov.nih.nlm.nls.cSpell.Detector;
import java.util.*;
import gov.nih.nlm.nls.cSpell.Util.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.Dictionary.*;
import gov.nih.nlm.nls.cSpell.Api.*;
/*****************************************************************************
* This class is to detect a real-word for real-word merge correction. 
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
public class RealWordMergeDetector
{
    // private constructor
    private RealWordMergeDetector()
    {
    }
    
    // public methods
    public static boolean IsDetect(String inWord, CSpellApi cSpellApi, 
        boolean debugFlag)
    {
        // check both inWord and remove end punc string
        boolean realWordMergeFlag 
            = IsRealWordMerge(inWord, cSpellApi, debugFlag);
        return realWordMergeFlag;
    }
    public static boolean IsDetect(String inWord, String rmEndPuncStr,
        CSpellApi cSpellApi, boolean debugFlag)
    {
        // check both inWord and remove end punc string, either is a real-word
        boolean realWordMergeFlag 
            = IsRealWordMerge(inWord, cSpellApi, debugFlag)
            || IsRealWordMerge(rmEndPuncStr, cSpellApi, debugFlag);
        return realWordMergeFlag;
    }
    // check dic and exception
    private static boolean IsRealWordMerge(String inWord, 
        CSpellApi cSpellApi, boolean debugFlag)
    {
        // init
        RootDictionary checkDic = cSpellApi.GetSplitWordDic();    // merge Dic
        RootDictionary unitDic = cSpellApi.GetUnitDic();
        // real word merge must:
        // 1. known in the dictionary
        // 2. not exception, such as url, email, digit, ...
        // => if excpetion, even is a non-word, still not a misspelt
        boolean realWordMergeFlag = (checkDic.IsValidWord(inWord)) 
            && (!IsRealWordExceptions(inWord, unitDic));
        if(debugFlag == true)
        {
            boolean wordInDicFlag = checkDic.IsValidWord(inWord);
            boolean wordExceptionFlag = IsRealWordExceptions(inWord, unitDic);
            DebugPrint.PrintRwMergeDetect(inWord, realWordMergeFlag,
                wordInDicFlag, wordExceptionFlag, debugFlag);  
        }
        return realWordMergeFlag;
    }
    // Valid Exceptions: valid English words, but not in the dictionary.
    // Such as digit, punc, digitPunc (no letter), Url, eMail
    // measurement, unit, abbreviation, acronym, proper noun, sp vars
    private static boolean IsRealWordExceptions(String inWord, 
        RootDictionary unitDic)
    {
        boolean validExceptionFlag 
            = (DigitPuncTokenUtil.IsDigit(inWord) == true)    // digit
            || (DigitPuncTokenUtil.IsPunc(inWord) == true) // punc
            || (DigitPuncTokenUtil.IsDigitPunc(inWord) == true)//digitPunc
            || (InternetTokenUtil.IsUrl(inWord) == true)   // url
            || (InternetTokenUtil.IsEmail(inWord) == true)    // eMail
            || (IsEmptyString(inWord) == true)    // can't be empty String
            || (IsSingleCharString(inWord) == true)    // don't merge single char
            || (IsUpperCaseString(inWord) == true)    // upperCase is AA, no merge
            || (MeasurementTokenUtil.IsMeasurements(inWord, unitDic) == true)
            ;
        return validExceptionFlag;    
    }
    // should be moved to Util
    private static boolean IsUpperCaseString(String inWord)
    {
        String inWordUc = inWord.toUpperCase();
        return (inWord.equals(inWordUc));
    }
    private static boolean IsEmptyString(String inWord)
    {
        return (inWord.trim().length() == 0);
    }
    private static boolean IsSingleCharString(String inWord)
    {
        return (inWord.trim().length() == 1);
    }
    // private
    private static void Tests(CSpellApi cSpellApi)
    {
        // test Words
        Test("ment.", cSpellApi);    //
        Test("123", cSpellApi);    //
    }
    private static void Test(String inWord, CSpellApi cSpellApi) 
    {
        boolean debugFlag = true;
        System.out.println("[" + inWord + "]: " 
            + IsDetect(inWord, inWord, cSpellApi, debugFlag));
    }
    
    // public test driver
    public static void main(String[] args)
    {
        String configFile = "../data/Config/cSpell.properties";
        if(args.length == 1)
        {
            configFile = args[0];
        }
        if(args.length > 0)
        {
            System.err.println("Usage: java RealWordMergeDetector <config>");
            System.exit(1);
        }
        // init, read in from config
        CSpellApi cSpellApi = new CSpellApi(configFile);
        // Test
        Tests(cSpellApi);
    }
    // data member
}

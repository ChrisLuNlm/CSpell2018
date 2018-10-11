package gov.nih.nlm.nls.cSpell.Detector;
import java.util.*;
import gov.nih.nlm.nls.cSpell.Util.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.Dictionary.*;
import gov.nih.nlm.nls.cSpell.Api.*;
/*****************************************************************************
* This class is to detect a non-word for non-word merge correction.
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
public class NonWordMergeDetector
{
    // private constructor
    private NonWordMergeDetector()
    {
    }
    
    // public methods
    // rmEndPuncStr is include me gre.
    public static boolean IsDetect(String inWord, String rmEndPuncStr,
        CSpellApi cSpellApi, boolean debugFlag)
    {
        // both inWord and rmEndPunc are non-word merge
        boolean nonWordMergeFlag = IsNonWordMerge(inWord, cSpellApi, debugFlag)
            && IsNonWordMerge(rmEndPuncStr, cSpellApi, debugFlag);
        return nonWordMergeFlag;    
    }
    public static boolean IsNonWordMerge(String inWord, CSpellApi cSpellApi, 
        boolean debugFlag)
    {
        // init
        RootDictionary checkDic = cSpellApi.GetSplitWordDic();    // merge Dic
        RootDictionary unitDic = cSpellApi.GetUnitDic();
        // non-word merge must be:
        // 1. not known in the dictionary
        // 2. not exception, such as url, email, digit, ...
        // => if excpetion, even is a non-word, still not a misspelt
        boolean nonWordMergeFlag = (!checkDic.IsValidWord(inWord)) 
            && (!IsNonWordMergeExceptions(inWord, unitDic));
        // print out debug
        if(debugFlag == true)
        {
            boolean wordDicFlag = checkDic.IsValidWord(inWord);
            boolean wordExceptionFlag 
                = IsNonWordMergeExceptions(inWord, unitDic);
            DebugPrint.PrintNwMergeDetect(inWord, nonWordMergeFlag,
                wordDicFlag, wordExceptionFlag, debugFlag);  
        }
        return nonWordMergeFlag;
    }
    // TBD: remove svDic, pnDic, aaDic
    // Valid Exceptions: valid English words, but not in the dictionary.
    // Such as digit, punc, digitPunc (no letter), Url, eMail
    // measurement, unit, abbreviation, acronym, proper noun, sp vars
    private static boolean IsNonWordMergeExceptions(String inWord, 
        RootDictionary unitDic)
    {
        boolean exceptionFlag 
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
        return exceptionFlag;    
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
            + IsNonWordMerge(inWord, cSpellApi, debugFlag));
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
            System.err.println("Usage: java NonWordMergeDetector <config>");
            System.exit(1);
        }
        // init, read in from config
        CSpellApi cSpellApi = new CSpellApi(configFile);
        // Test
        Tests(cSpellApi);
    }
    // data member
}

package gov.nih.nlm.nls.cSpell.Detector;
import java.util.*;
import gov.nih.nlm.nls.cSpell.Util.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.Dictionary.*;
import gov.nih.nlm.nls.cSpell.Api.*;
import gov.nih.nlm.nls.cSpell.Ranker.*;
/*****************************************************************************
* This class is to detect a real-word for real-word split correction. 
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
public class RealWordSplitDetector
{
    // private constructor
    private RealWordSplitDetector()
    {
    }
    
    // public methods
    public static boolean IsDetect(String inWord, CSpellApi cSpellApi)
    {
        boolean debugFlag = false;
        return IsRealWord(inWord, cSpellApi, debugFlag);
    }
    public static boolean IsDetect(String inWord, CSpellApi cSpellApi,
        boolean debugFlag)
    {
        return IsRealWord(inWord, cSpellApi, debugFlag);
    }
    // check both the inWord and coreStr of inWord
    // the coreStr can be derived from CoreTermObj.GetCoreTerm();
    public static boolean IsDetect(String inWord, String coreStr,
        CSpellApi cSpellApi)
    {
        boolean debugFlag = false;
        return IsDetect(inWord, coreStr, cSpellApi, debugFlag);    
    }
    // check both inWord and coreTerm, either them is a real-word
    public static boolean IsDetect(String inWord, String coreStr,
        CSpellApi cSpellApi, boolean debugFlag)
    {
        boolean validFlag = IsRealWord(inWord, cSpellApi, debugFlag)
            || IsRealWord(coreStr, cSpellApi, debugFlag);
        return validFlag;    
    }
    public static boolean IsRealWord(String inWord, CSpellApi cSpellApi,
        boolean debugFlag)
    {
        // init
        RootDictionary checkDic = cSpellApi.GetCheckDic();
        RootDictionary unitDic = cSpellApi.GetUnitDic();
        WordWcMap wordWcMap = cSpellApi.GetWordWcMap();
        Word2Vec word2VecOm = cSpellApi.GetWord2VecOm();
        int inWordLen = inWord.length();
        // TBD, change method name
        int rwSplitWordMinLength = cSpellApi.GetDetectorRwSplitWordMinLength();
        int rwSplitWordMinWc = cSpellApi.GetDetectorRwSplitWordMinWc();
        // realword must be:
        // 1. known in the dictionary
        // 2. not exception, such as url, email, digit, ...
        // => if excpetion, even is a non-word, no correction
        // 3. must have word2Vector value (inWord is auto converted to LC)
        // 4. frequency must be above a threshhold (inWord is auto to LC)
        // TBD, need to be configureable 200 
        boolean realWordFlag = (checkDic.IsValidWord(inWord)) 
            && (!IsRealWordExceptions(inWord, unitDic)
            && (inWordLen >= rwSplitWordMinLength)
            && (word2VecOm.HasWordVec(inWord) == true)
            && (WordCountScore.GetWc(inWord, wordWcMap) >= rwSplitWordMinWc));
        if(debugFlag == true)
        {
            boolean wordInDicFlag = checkDic.IsValidWord(inWord);
            boolean wordExceptionFlag 
                = IsRealWordExceptions(inWord, unitDic);
            boolean lengthFlag = (inWordLen >= rwSplitWordMinLength);
            boolean word2VecFlag = word2VecOm.HasWordVec(inWord);
            boolean wcFlag = 
                (WordCountScore.GetWc(inWord, wordWcMap) >= rwSplitWordMinWc); 
            DebugPrint.PrintRwSplitDetect(inWord, realWordFlag, wordInDicFlag, 
                wordExceptionFlag, lengthFlag, word2VecFlag, wcFlag, debugFlag);
        }
        return realWordFlag;
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
            || (IsEmptyString(inWord) == true)            // can't be empty String
            || (MeasurementTokenUtil.IsMeasurements(inWord, unitDic) == true)
            // the following 3 could be included in the check dictionary
            // use these 3 if the checkDic does not include pn, aa, sv
            // no need if Lexicon.data.ew is used, bz it include them
            //|| (IsProperNoun(inWord, pnDic) == true)        // proper noun
            //|| (IsAbbAcr(inWord, aaDic) == true)        // abb/acr
            ;
        return validExceptionFlag;    
    }
    private static boolean IsAbbAcr(String inWord, RootDictionary aaDic)
    {
        // Check abbreviation and acronym from Lexicon, case sensitive
        boolean aaFlag = aaDic.IsDicWord(inWord);
        return aaFlag;
    }
    private static boolean IsProperNoun(String inWord, RootDictionary pnDic)
    {
        // Check proper noun from Lexicon, case sensitive
        boolean pnFlag = pnDic.IsDicWord(inWord);
        return pnFlag;
    }
    private static boolean IsEmptyString(String inWord)
    {
        return (inWord.trim().length() == 0);
    }
    // private
    private static void Tests(CSpellApi cSpellApi)
    {
        // test Words
        Test("test123", cSpellApi);    //
        Test("123", cSpellApi);        // digit
        Test("@#$", cSpellApi);
        Test("123@#$", cSpellApi);
        Test("123.34", cSpellApi);
        Test("@#%123", cSpellApi);
        Test("12.3%", cSpellApi);    // digit
        Test("-0.5mg", cSpellApi);
        Test("70kg", cSpellApi);        //measurement
        Test("cm3", cSpellApi);        //measurement: include pure unit
        Test("Gbit", cSpellApi);        //measurement: include pure unit
        Test("12cm", cSpellApi);
        Test("30mg/50kg", cSpellApi);
        Test("45Chris", cSpellApi);
        Test("http://www.amia.org", cSpellApi);    // url
        Test("jeff@amia.org", cSpellApi);        // email
        Test("dur", cSpellApi);        // test for merge
        Test("dur", cSpellApi);    // test for merge
        Test("ing", cSpellApi);        // test for merge
        Test("ing", cSpellApi);    // test for merge
        Test("eighteen", cSpellApi);    // test for merge
        Test("eighteen", cSpellApi);    // test for merge
        Test("Ilost", cSpellApi);    // test for split
        Test("-http://www.ncbi.nlm.nih.gov/sites/ga?disorder=Androgen%20Insensitivity%20Syndrome", cSpellApi);    // test for split
        Test("http://www.ncbi.nlm.nih.gov/sites/ga?disorder=Androgen%20Insensitivity%20Syndrome", cSpellApi);    // test for split
    }
    private static void Test(String inWord, CSpellApi cSpellApi) 
    {
        boolean debugFlag = true;
        System.out.println("[" + inWord + "]: " 
            + IsDetect(inWord, cSpellApi, debugFlag));
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
            System.err.println("Usage: java RealWordSplitDetector <config>");
            System.exit(1);
        }
        // init, read in from config
        CSpellApi cSpellApi = new CSpellApi(configFile);
        // Test
        Tests(cSpellApi);
    }
    // data member
}

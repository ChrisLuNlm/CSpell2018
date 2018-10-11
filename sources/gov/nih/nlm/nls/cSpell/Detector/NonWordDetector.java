package gov.nih.nlm.nls.cSpell.Detector;
import java.util.*;
import gov.nih.nlm.nls.cSpell.Util.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.Dictionary.*;
import gov.nih.nlm.nls.cSpell.Api.*;
/*****************************************************************************
* This class is to detect a non-word for non-word split or 1To1 correction.
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
public class NonWordDetector
{
    // private constructor
    private NonWordDetector()
    {
    }
    
    // public methods
    // Is spelling error, error word candidates, need to be corrected
    // - not in the checking dictionary
    // - not one of expcetion, such as URl, measurement, ...
    public static boolean IsDetect(String inWord, CSpellApi cSpellApi)
    {
        boolean debugFlag = false;
        return IsDetect(inWord, cSpellApi, debugFlag);
    }
    public static boolean IsDetect(String inWord, CSpellApi cSpellApi,
        boolean debugFlag)
    {
        return IsNonWord(inWord, cSpellApi, debugFlag);
    }
    // check both the inWord and coreStr of inWord
    // the coreStr can be derived from CoreTermObj.GetCoreTerm();
    public static boolean IsDetect(String inWord, String coreStr,
        CSpellApi cSpellApi)
    {
        boolean debugFlag = false;
        return IsDetect(inWord, coreStr, cSpellApi, debugFlag);    
    }
    // Check both inWord and coreTerm
    public static boolean IsDetect(String inWord, String coreStr,
        CSpellApi cSpellApi, boolean debugFlag)
    {
        boolean nonWordFlag = IsNonWord(inWord, cSpellApi, debugFlag)
            && IsNonWord(coreStr, cSpellApi, debugFlag);
        return nonWordFlag;    
    }
    public static boolean IsNonWord(String inWord, CSpellApi cSpellApi,
        boolean debugFlag)
    {
        // init
        RootDictionary checkDic = cSpellApi.GetCheckDic();
        RootDictionary unitDic = cSpellApi.GetUnitDic();
        // non-word must be:
        // 1. not known in the dictionary
        // 2. not exception, such as url, email, digit, ...
        // => if excpetion, even is a nor-word, still not a misspelt
        boolean nonWordFlag = (!checkDic.IsValidWord(inWord)) 
            && (!IsNonWordExceptions(inWord, unitDic));
        if(debugFlag == true)
        {
            boolean wordDicFlag = checkDic.IsValidWord(inWord);
            boolean wordExceptionFlag 
                = IsNonWordExceptions(inWord, unitDic);
            DebugPrint.PrintNwDetect(inWord, nonWordFlag, wordDicFlag,
                wordExceptionFlag, debugFlag);
        }
        return nonWordFlag;
    }
    // TBD: remove pnDic, aaDic
    // Valid Exceptions: valid English words, but not in the dictionary.
    // Such as digit, punc, digitPunc (no letter), Url, eMail
    // measurement, unit, 
    // abbreviation, acronym, proper nouns: do not change the F1 after test
    private static boolean IsNonWordExceptions(String inWord, 
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
            // the following could be included in the check dictionary
            // use these if the checkDic does not include pn, aa, sv?
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
    }
    private static void Test(String inWord, CSpellApi cSpellApi) 
    {
        boolean debugFlag = true;
        System.out.println("[" + inWord + "]: " + IsDetect(inWord, cSpellApi, 
            debugFlag));
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
            System.err.println("Usage: java NonWordDetector <config>");
            System.exit(1);
        }
        // init, read in from config
        CSpellApi cSpellApi = new CSpellApi(configFile);
        // Test
        Tests(cSpellApi);
    }
    // data member
}

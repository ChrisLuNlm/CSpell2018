package gov.nih.nlm.nls.cSpell.Detector;
import java.util.*;
import gov.nih.nlm.nls.cSpell.Util.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.Dictionary.*;
import gov.nih.nlm.nls.cSpell.Api.*;
import gov.nih.nlm.nls.cSpell.Ranker.*;
/*****************************************************************************
* This class is to detect a real-word for real-word 1To1 correction. 
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
public class RealWord1To1Detector
{
    // private constructor
    private RealWord1To1Detector()
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
    // core process for detect rewal-word 1-to-1 
    public static boolean IsRealWord(String inWord, CSpellApi cSpellApi,
        boolean debugFlag)
    {
        // init
        RootDictionary checkDic = cSpellApi.GetCheckDic();
        RootDictionary pnDic = cSpellApi.GetPnDic();
        RootDictionary aaDic = cSpellApi.GetAaDic();
        RootDictionary unitDic = cSpellApi.GetUnitDic();
        int inWordLen = inWord.length();
        String inWordLc = inWord.toLowerCase();    // no need, TBD
        WordWcMap wordWcMap = cSpellApi.GetWordWcMap();
        Word2Vec word2VecOm = cSpellApi.GetWord2VecOm();
        int rw1To1WordMinLength = cSpellApi.GetDetectorRw1To1WordMinLength();
        int rw1To1WordMinWc = cSpellApi.GetDetectorRw1To1WordMinWc();
        // realword 1-to-1 must be:
        // 1. known in the dictionary
        // 2. not exception, such as url, email, digit, ...
        // => if excpetion, even is a non-word, no correction
        // 3. must have word2Vector value (inWord is auto converted to LC)
        // 4. frequency must be above a threshhold (inWord is auto to LC)
        // TBD, need to be configureable, 3 and 65
        boolean realWordFlag = (checkDic.IsValidWord(inWord)) 
            && (!IsRealWordExceptions(inWord, pnDic, aaDic, unitDic))
            && (inWordLen >= rw1To1WordMinLength)
            && (word2VecOm.HasWordVec(inWord) == true)
            && (WordCountScore.GetWc(inWord, wordWcMap) >= rw1To1WordMinWc);
        if(debugFlag == true)
        {
            boolean wordInDicFlag = checkDic.IsValidWord(inWord);
            boolean wordExceptionFlag 
                = IsRealWordExceptions(inWord, pnDic, aaDic, unitDic);
            boolean legnthFlag = (inWordLen >= rw1To1WordMinLength);
            boolean word2VecFlag = word2VecOm.HasWordVec(inWord);
            boolean wcFlag 
                = (WordCountScore.GetWc(inWord, wordWcMap) >= rw1To1WordMinWc);
            DebugPrint.PrintRw1To1Detect(inWord, realWordFlag, wordInDicFlag,
                wordExceptionFlag, legnthFlag, word2VecFlag, wcFlag, debugFlag);
        }
        return realWordFlag;
    }
    // Valid Exceptions: valid English words, but not in the dictionary.
    // Such as digit, punc, digitPunc (no letter), Url, eMail
    // measurement, unit, abbreviation, acronym, proper noun, sp vars
    private static boolean IsRealWordExceptions(String inWord, 
        RootDictionary pnDic, RootDictionary aaDic, RootDictionary unitDic)
    {
        boolean validExceptionFlag 
            = (DigitPuncTokenUtil.IsDigit(inWord) == true)    // digit
            || (DigitPuncTokenUtil.IsPunc(inWord) == true) // punc
            || (DigitPuncTokenUtil.IsDigitPunc(inWord) == true)//digitPunc
            || (InternetTokenUtil.IsUrl(inWord) == true)   // url
            || (InternetTokenUtil.IsEmail(inWord) == true)    // eMail
            || (IsEmptyString(inWord) == true)        // can't be empty String
            || (MeasurementTokenUtil.IsMeasurements(inWord, unitDic) == true)
            //|| (IsSpVar(inWord, svDic) == true)        // spVar? TBD
            || (IsProperNoun(inWord, pnDic) == true)        // proper noun
            || (IsAbbAcr(inWord, aaDic) == true)        // abb/acr
            ;
        return validExceptionFlag;    
    }
    private static boolean IsSpVar(String inWord, RootDictionary svDic)
    {
        // Check spVar from Lexicon, case sensitive
        boolean svFlag = svDic.IsDicWord(inWord);
        return svFlag;
    }
    private static boolean IsAbbAcr(String inWord, RootDictionary aaDic)
    {
        // Check abbreviation and acronym from Lexicon, case sensitive
        // should be case sensitive, but here, we implmented aggresive match
        boolean aaFlag = aaDic.IsDicWord(inWord);
        return aaFlag;
    }
    // check if it is a prperNoun
    private static boolean IsProperNoun(String inWord, RootDictionary pnDic)
    {
        // Check proper noun from Lexicon, case sensitive
        // should be case sensitive, but here, we implmented aggresive match
        // and ignroe the case
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
        ArrayList<String> testList = new ArrayList<String>();
        testList.add("it");
        testList.add("ORIGEN");
        testList.add("haberman");
        testList.add("robins");
        testList.add("guys");
        testList.add("its");
        testList.add("too");
        testList.add("Cyra");
        testList.add("its");
        testList.add("then");
        testList.add("anderson");
        testList.add("stereotypy");
        testList.add("thing");
        testList.add("multi");
        testList.add("Noonan's");
        testList.add("gardeners");
        testList.add("mg");
        testList.add("if");
        testList.add("sisters");
        testList.add("husbands");
        testList.add("if");
        testList.add("fathers");
        testList.add("kg");
        testList.add("know");
        testList.add("tried");
        testList.add("to");
        testList.add("to");
        testList.add("canyon");
        testList.add("specially");
        testList.add("law");
        testList.add("domestic");
        testList.add("repot");
        testList.add("Weather");
        testList.add("there");
        testList.add("then");
        testList.add("fine");
        testList.add("m");
        testList.add("Siemens");
        testList.add("month's");
        testList.add("medication's");
        testList.add("quantity's");
        testList.add("undisguised");
        testList.add("bowl");
        testList.add("its");
        testList.add("its");
        testList.add("ti");
        testList.add("i");
        testList.add("off");
        testList.add("Dies");
        testList.add("therefor");
        testList.add("hank");
        testList.add("adema");
        testList.add("lesson");
        testList.add("adema");
        testList.add("ajd");
        testList.add("are");
        testList.add("are");
        testList.add("vs,");
        testList.add("wont");
        testList.add("its");
        testList.add("descended");
        testList.add("nt");
        testList.add("dose");
        testList.add("Its");
        testList.add("do");
        testList.add("our");
        testList.add("in");
        testList.add("effect");
        testList.add("pregnancy");
        testList.add("cn");
        testList.add("d");
        testList.add("leave");
        testList.add("BB");
        testList.add("gastrologist");
        testList.add("lounger");
        testList.add("its");
        testList.add("wat");
        testList.add("d");
        testList.add("av");
        testList.add("AV");
        testList.add("relaxers");
        testList.add("day's");
        testList.add("nd");
        testList.add("wat");
        testList.add("the");
        testList.add("Aisa");
        testList.add("wat");
        testList.add("dnt");
        testList.add("affects");
        testList.add("their");
        testList.add("you");
        testList.add("Ito");
        testList.add("prego");
        testList.add("medical");
        testList.add("hoe");
        testList.add("hoe");
        testList.add("medical");
        testList.add("hoe");
        testList.add("an");
        testList.add("swimmers");
        testList.add("swollen");
        testList.add("swollen");
        testList.add("William's");
        testList.add("Williams'");
        testList.add("tent");
        testList.add("its");
        testList.add("well");
        testList.add("lanais");
        testList.add("FRIENDS");
        testList.add("access");
        testList.add("rply");
        testList.add("bed");
        testList.add("tiered");
        testList.add("its");
        testList.add("loosing");
        testList.add("loosing");
        testList.add("can");
        testList.add("physician's");
        testList.add("to");
        testList.add("where");
        testList.add("twine");
        testList.add("aim");
        testList.add("lease");
        testList.add("4");
        testList.add("spot");
        testList.add("weather");
        testList.add("its");
        testList.add("is");
        testList.add("devises");
        testList.add("were");
        testList.add("versa");
        testList.add("Gud");
        testList.add("doner");
        testList.add("MT");
        testList.add("ITS");
        testList.add("small");
        testList.add("bond");
        testList.add("hav");
        testList.add("abd");
        testList.add("do");
        testList.add("sever");
        testList.add("then");
        testList.add("leave");
        testList.add("meningitidis");
        testList.add("gey");
        testList.add("its");
        testList.add("hav");
        testList.add("Mam");
        testList.add("vertebras");
        testList.add("amd");
        // test 
        int detectNo = 0;
        int notDetectNo = 0;
        for(String testStr:testList)
        {
            if(Test(testStr, cSpellApi) == true)
            {
                System.out.println(testStr);
                detectNo++;
            }
            else
            {
                notDetectNo++;
            }
        }
        System.out.println(testList.size() + "|" + detectNo + "|" 
            + notDetectNo);
    }
    private static boolean Test(String inWord, CSpellApi cSpellApi) 
    {
        boolean debugFlag = false;
        String inWordLc = inWord.toLowerCase();
        boolean isDetect = IsDetect(inWordLc, cSpellApi, debugFlag);
        return isDetect;
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
            System.err.println("Usage: java RealWord1To1Detector <config>");
            System.exit(1);
        }
        // init, read in from config
        CSpellApi cSpellApi = new CSpellApi(configFile);
        // Test
        Tests(cSpellApi);
    }
    // data member
}

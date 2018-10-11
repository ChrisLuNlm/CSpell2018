package gov.nih.nlm.nls.cSpell.Corrector;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import gov.nih.nlm.nls.cSpell.Util.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
import gov.nih.nlm.nls.cSpell.Dictionary.*;
import gov.nih.nlm.nls.cSpell.Detector.*;
import gov.nih.nlm.nls.cSpell.Candidates.*;
import gov.nih.nlm.nls.cSpell.Ranker.*;
import gov.nih.nlm.nls.cSpell.Api.*;
/*****************************************************************************
* This class is to correct non-word one-to-one and split errors.
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
public class NonWordCorrector
{
    // private constructor
    /**
    * Private constructor so no one can instantiate
    */
    private NonWordCorrector()
    {
    }
    // public method
    /**
    * The core method to correct a word by following steps:
    * <ul>
    * <li>Convert inToken to coreTerm
    * <li>detect if misspell (OOV) - non-word
    * <li>get candidates
    *     <ul>
    *     <li>get candidates from 1To1.
    *     <li>get candidates from split.
    *     </ul>
    * <li>Rank candidates
    * <li>Update information
    * </ul>
    * 
    * This method does not use context scores.
    *
    * @param     inTokenObj    the input tokenObj (single word)
    * @param    cSpellApi CSpell Api object
    * 
    * @return    the corrected word in tokenObj if the coreTerm is OOV 
    *             and suggested word found. Otherwise, the original input token 
    *             is returned.
    */
    public static TokenObj GetCorrectTerm(TokenObj inTokenObj, 
        CSpellApi cSpellApi)
    {
        boolean debugFlag = false;
        return GetCorrectTerm(inTokenObj, cSpellApi, debugFlag);
    }
    /**
    * This method does not use context scores to find the correct term.
    *
    * @param     inTokenObj    the input tokenObj (single word)
    * @param    cSpellApi CSpell Api object
    * @param    debugFlag flag for debug print
    * 
    * @return    the corrected word in tokenObj if the coreTerm is OOV 
    *             and suggested word found. Otherwise, the original input token 
    *             is returned.
    */
    public static TokenObj GetCorrectTerm(TokenObj inTokenObj, 
        CSpellApi cSpellApi, boolean debugFlag)
    {
        int tarPos = 0;    // set to 0 if not use context
        ArrayList<TokenObj> nonSpaceTokenList = null;
        return GetCorrectTerm(inTokenObj, cSpellApi, debugFlag, tarPos, 
            nonSpaceTokenList);
    }
    /**
    * This method uses context scores to find the correct term.
    *
    * @param     inTokenObj    the input tokenObj (single word)
    * @param    cSpellApi CSpell Api object
    * @param    debugFlag flag for debug print
    * @param    tarPos position for target token
    * @param    nonSpaceTokenList token list without space token(s)
    * 
    * @return    the corrected word in tokenObj if the coreTerm is OOV 
    *             and suggested word found. Otherwise, the original input token 
    *             is returned.
    */
    public static TokenObj GetCorrectTerm(TokenObj inTokenObj, 
        CSpellApi cSpellApi, boolean debugFlag, int tarPos, 
        ArrayList<TokenObj> nonSpaceTokenList)
    {
        // init
        int funcMode = cSpellApi.GetFuncMode();
        
        // get inWord from inTokenObj and init outTokenObj
        String inWord = inTokenObj.GetTokenStr();
        TokenObj outTokenObj = new TokenObj(inTokenObj);
        // 1. convert a word to coreTerm (no leading/ending space, punc, digit)
        int ctType = CoreTermUtil.CT_TYPE_SPACE_PUNC_DIGIT;
        CoreTermObj coreTermObj = new CoreTermObj(inWord, ctType);
        String coreStr = coreTermObj.GetCoreTerm();
        // 2. non-word detection and correction 
        // check if the coreTerm is spelling errors - non-word
        //!NonWordDetector.IsValidWord(inWord, coreStr, cSpellApi, debugFlag);
        // TBD .. need to separate 1-to-1 and split
        if(NonWordDetector.IsDetect(inWord, coreStr, cSpellApi, debugFlag) == true)
        {
            cSpellApi.UpdateDetectNo();
            // TBD, should take care of possessive xxx's here
            // 3.1 get 1-to-1 candidates set from correction, no split
            HashSet<String> candSet = NonWord1To1Candidates.GetCandidates(
                coreStr, cSpellApi);
            // add split
            // TBD ... 
            if(funcMode != CSpellApi.FUNC_MODE_NW_1)
            {
                // 3.2 get candidates from split
                int maxSplitNo = cSpellApi.GetCanNwMaxSplitNo();
                HashSet<String> splitSet = NonWordSplitCandidates.GetCandidates(
                    coreStr, cSpellApi, maxSplitNo);
                // 3.4 set split candidates to candidate
                if(funcMode == CSpellApi.FUNC_MODE_NW_S)
                {
                    candSet = new HashSet<String>(splitSet);
                }
                else // 3.4 add split candidates
                {
                    candSet.addAll(splitSet);
                }
            }
            // 4. Ranking: get top ranked candidates as corrected terms
            // 4.1 from orthoGraphic
            /*
            // not used context
            String topRankStr = RankByMode.GetTopRankStr(coreStr, candSet, 
                cSpellApi, debugFlag);
            */
            // in case of using context
            String topRankStr = RankNonWordByMode.GetTopRankStr(coreStr, 
                candSet, cSpellApi, debugFlag, tarPos, nonSpaceTokenList);
            // 5 update coreTerm and convert back to tokenObj
            coreTermObj.SetCoreTerm(topRankStr);
            String outWord = coreTermObj.ToString();
            // 6. update info if there is a process
            if(inWord.equals(outWord) == false)
            {
                outTokenObj.SetTokenStr(outWord);
                if(TermUtil.IsMultiword(outWord) == true)
                {
                    cSpellApi.UpdateCorrectNo();
                    outTokenObj.AddProcToHist(TokenObj.HIST_NW_S);    //split
                    DebugPrint.PrintCorrect("NW", "NonWordCorrector-Split",
                        inWord, outWord, debugFlag);
                }
                else // 1To1 correct
                {
                    cSpellApi.UpdateCorrectNo();
                    outTokenObj.AddProcToHist(TokenObj.HIST_NW_1);    
                    DebugPrint.PrintCorrect("NW", "NonWordCorrector-1To1",
                        inWord, outWord, debugFlag);
                }
            }
        }
        return outTokenObj;
    }
    private static String GetCorrectTermStr(String inWord, CSpellApi cSpellApi)
    {
        TokenObj inTokenObj = new TokenObj(inWord);
        TokenObj outTokenObj = GetCorrectTerm(inTokenObj, cSpellApi);
        String outWord = outTokenObj.GetTokenStr();    
        return outWord;
    }
    private static void TestGetCorrectTermStr(CSpellApi cSpellApi)
    {
        String inText = "playsure";    // wordwise: pleasure
        String outText = GetCorrectTermStr(inText, cSpellApi);
        // print out
        System.out.println("--------- GetCorrectTermStr( ) -----------");
        System.out.println("In: [" + inText + "]");
        System.out.println("Out: [" + outText + "]");
    }
    private static void TestGetCorrectTerm(CSpellApi cSpellApi)
    {
        // init
        // all lowerCase
        String inText = "hotflashes";
        // test process: 
        TokenObj inToken = new TokenObj(inText);
        TokenObj outToken = NonWordCorrector.GetCorrectTerm(
            inToken, cSpellApi);
        // result    
        String outText = outToken.GetTokenStr();
        // print out
        System.out.println("--------- GetCorrectTerm( ) -----------");
        System.out.println("In: [" + inText + "]");
        System.out.println("Out: [" + outText + "]");
    }
    // test driver
    public static void main(String[] args)
    {
        String configFile = "../data/Config/cSpell.properties";
        if(args.length > 0)
        {
            System.out.println("Usage: java NonWordCorrector <configFile>");
            System.exit(0);
        }
        
        // init
        CSpellApi cSpellApi = new CSpellApi(configFile);
        
        // test
        TestGetCorrectTermStr(cSpellApi);
        TestGetCorrectTerm(cSpellApi);
    }
    // data member
}

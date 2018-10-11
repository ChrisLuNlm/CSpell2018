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
* This class is to correct real-word split error.
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
public class RealWordSplitCorrector
{
    // private constructor
    /**
    * Private constructor so no one can instantiate
    */
    private RealWordSplitCorrector()
    {
    }
    // public method
    /**
    * The core method to correct a word by following steps:
    * <ul>
    * <li>Convert inToken to coreTerm
    * <li>detect if real-word
    * <li>get split candidates
    * <li>Rank candidates
    *     <ul>
    *     <li>context
    *     </ul>
    * <li>Update information
    *
    * </ul>
    *
    * @param     inTokenObj    the input tokenObj (single word)
    * @param    cSpellApi cSpell API object
    * @param    debugFlag flag for debug print
    * @param    tarPos position of the target token to be split
    * @param    nonSpaceTokenList the token list without space tokens
    * 
    * @return    the split words in tokenObj. 
    */
    // return the original term if no good correctin are found
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
        // check if the coreTerm is real-word
        if((inTokenObj.GetProcHist().size() == 0)  // not processed previously
        && (RealWordSplitDetector.IsDetect(inWord, coreStr, cSpellApi, debugFlag) == true))
        {
            cSpellApi.UpdateDetectNo();
            // TBD, should take care of possessive xxx's here
            // 3. get split candidates set from correction
            int maxSplitNo = cSpellApi.GetCanRwMaxSplitNo();
            HashSet<String> splitSet = RealWordSplitCandidates.GetCandidates(
                coreStr, cSpellApi, maxSplitNo);
            // get candidates from split
            // 4. Ranking: get top ranked candidates as corrected terms
            // in case of using context
            String topRankStr = RankRealWordSplitByMode.GetTopRankStr(coreStr, 
                splitSet, cSpellApi, debugFlag, tarPos, nonSpaceTokenList);
            // 5 update coreTerm and convert back to tokenObj
            coreTermObj.SetCoreTerm(topRankStr);
            String outWord = coreTermObj.ToString();
            // 6. update info if there is a real-word correction
            if(inWord.equals(outWord) == false)
            {
                cSpellApi.UpdateCorrectNo();
                outTokenObj.SetTokenStr(outWord);
                outTokenObj.AddProcToHist(TokenObj.HIST_RW_S);    //split
                DebugPrint.PrintCorrect("RW", "RealWordSplitCorrector",
                    inWord, outWord, debugFlag);
            }
        }
        return outTokenObj;
    }
    private static void TestSplit(CSpellApi cSpellApi)
    {
        // setup test case
        // 10349.txt
        //String inText = "sounding in my ear every time for along time.";
        // 13864.txt
        String inText = "I donate my self to be apart of this study.";
        TextObj textObj = new TextObj(inText);
        ArrayList<TokenObj> inTextList = textObj.GetTokenList();
        ArrayList<TokenObj> nonSpaceTokenList
            = TextObj.GetNonSpaceTokenObjList(inTextList);
        //int tarPos = 7;
        int tarPos = 6;
        TokenObj inTokenObj = nonSpaceTokenList.get(tarPos);
        boolean debugFlag = true;
        System.out.println("====== Real-Word One-To-One Correction Test =====");
        System.out.println("-- inTextList: [" + inText + "]");
        System.out.println("-- tarPos: [" + tarPos + "]");
        System.out.println("-- inTokenObj: [" + inTokenObj.ToString() + "]");
        // get the correct term
        TokenObj outTokenObj = GetCorrectTerm(inTokenObj, 
            cSpellApi, debugFlag, tarPos, nonSpaceTokenList);
        // print out
        System.out.println("--------- GetCorrectTermStr( ) -----------");
        System.out.println("-- outTokenObj: [" + outTokenObj.ToString() + "]");
    }
    // test driver
    public static void main(String[] args)
    {
        String configFile = "../data/Config/cSpell.properties";
        if(args.length == 1)
        {
            configFile = args[0];
        }
        else if(args.length > 0)
        {
            System.out.println("Usage: java RealWordCorrector <configFile>");
            System.exit(0);
        }
        
        // init
        CSpellApi cSpellApi = new CSpellApi(configFile);
        
        // test
        TestSplit(cSpellApi);
    }
    // data member
}

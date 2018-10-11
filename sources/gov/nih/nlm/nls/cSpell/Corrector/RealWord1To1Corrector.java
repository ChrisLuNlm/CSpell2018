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
* This class is to correct real-word 1To1 error.
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
public class RealWord1To1Corrector
{
    // private constructor
    /**
    * Private constructor so no one can instantiate
    */
    private RealWord1To1Corrector()
    {
    }
    // public method
    /**
    * The core method to correct a word by following steps:
    * <ul>
    * <li>Convert inToken to coreTerm
    * <li>detect if real-word
    * <li>get candidates
    *     <ul>
    *     <li>get candidates from one-to-one.
    *     </ul>
    * <li>Rank candidates
    *     <ul>
    *     <li>context
    *     </ul>
    * <li>Update information
    *
    * </ul>
    *
    * @param     inTokenObj    the input tokenObj (single word)
    * @param    cSpellApi CSpell Api object
    * @param    debugFlag flag for debug print
    * @param    tarPos the position for target token
    * @param    nonSpaceTokenList token list without space token(s)
    * 
    * @return    the corrected word in tokenObj if suggested word found. 
    *             Otherwise, the original input token is returned.
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
        // 2. real-word detection and correction 
        // check if the coreTerm is real-word
        if((inTokenObj.GetProcHist().size() == 0)  // not processed previously
        && (RealWord1To1Detector.IsDetect(inWord, coreStr, cSpellApi, debugFlag) == true))
        {
            cSpellApi.UpdateDetectNo();
            // TBD, should take care of possessive xxx's here
            // 3 get 1-to-1 candidates set from correction
            // TBD. realWordFlag to use metaphone ...
            // this process is very slow, 7 min., need to improved
            HashSet<String> candSet = RealWord1To1Candidates.GetCandidates(
                coreStr, cSpellApi);
/**** development analysis print out to see total RW
            totalRwNo_++;
            int candSize = candSet.size();
            if(candSize != 0)
            {
                totalCandNo_ += candSize;
                maxCandSize_ 
                    = ((candSize > maxCandSize_)?candSize:maxCandSize_);
                System.out.println("---- totalRwNo|totalCandNo(" + coreStr
                    + "): " + totalRwNo_ + "|" + candSize + "|" 
                    + totalCandNo_ + "|" + maxCandSize_);
                System.out.println(candSet);    
            }
****/
            // 4. Ranking: get top ranked candidates as corrected terms
            // in case of using context
            String topRankStr = RankRealWord1To1ByCSpell.GetTopRankStr(
                coreStr, candSet, cSpellApi, tarPos, nonSpaceTokenList,
                debugFlag);
            // 5 update coreTerm and convert back to tokenObj
            coreTermObj.SetCoreTerm(topRankStr);
            String outWord = coreTermObj.ToString();
            // 6. update info if there is a real-word correction
            if(inWord.equalsIgnoreCase(outWord) == false)
            {
                cSpellApi.UpdateCorrectNo();
                outTokenObj.SetTokenStr(outWord);
                outTokenObj.AddProcToHist(TokenObj.HIST_RW_1);    // 1-to-1
                DebugPrint.PrintCorrect("RW", "RealWord1To1Corrector",
                    inWord, outWord, debugFlag);
            }
        }
        return outTokenObj;
    }
    private static void Test1To1(CSpellApi cSpellApi)
    {
        // setup test case
        // 51.txt
        //String inText = "You'd thing that this is good.";
        //String inText = "The doctor thing that this is good.";
        String inText = "you would thing that is good.";
        TextObj textObj = new TextObj(inText);
        ArrayList<TokenObj> inTextList = textObj.GetTokenList();
        ArrayList<TokenObj> nonSpaceTokenList
            = TextObj.GetNonSpaceTokenObjList(inTextList);
        int tarPos = 2;
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
            System.out.println("Usage: java RealWord1To1Corrector <configFile>");
            System.exit(0);
        }
        
        // init
        CSpellApi cSpellApi = new CSpellApi(configFile);
        
        // test
        Test1To1(cSpellApi);
    }
    // data member
}

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
* This class is to correct non-word merge error.
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
public class NonWordMergeCorrector
{
    // private constructor
    /**
    * Private constructor so no one can instantiate
    */
    private NonWordMergeCorrector()
    {
    }
    // public method
    /**
    * The core method to correct a word by following steps:
    * <ul>
    * <li>Convert inToken to removeEndPuncStr
    * <li>detect if misspell (OOV) - non-word, exclude Aa
    * <li>get candidates
    *     <ul>
    *     <li>get candidates from merge.
    *     </ul>
    * <li>Rank candidates
    *     <ul>
    *     <li>orthographic
    *     <li>frequency
    *     <li>context
    *     </ul>
    * <li>Update information
    *
    * </ul>
    *
    * @param     tarPos    postion of target token
    * @param    nonSpaceTokenList token list without space token(s)
    * @param    cSpellApi CSpell Api object
    * @param    debugFlag flag for debug print
    * 
    * @return    the corrected merged word in MergeObj if the token is OOV 
    *             and suggested merged word found. 
    *             Otherwise, a null of MergeObj is returned.
    */
    // return the original term if no good correctin are found
    public static MergeObj GetCorrectTerm(int tarPos, 
        ArrayList<TokenObj> nonSpaceTokenList, CSpellApi cSpellApi, 
        boolean debugFlag) 
    {
        // get tarWord from tarTokenObj and init outTokenObj
        TokenObj tarTokenObj = nonSpaceTokenList.get(tarPos);
        String tarWord = tarTokenObj.GetTokenStr();
        MergeObj outMergeObj = null;    // no merge if it is null
        // 1. only remove ending punctuation for coreTerm
        String coreStr = TermUtil.StripEndPuncSpace(tarWord).toLowerCase();
        // 2. non-word correction 
        // check if tarWord and removeEndPuncStr is OOV
        if(NonWordMergeDetector.IsDetect(tarWord, coreStr, cSpellApi, debugFlag) == true)
        {
            cSpellApi.UpdateDetectNo();
            // 3. get candidates from merge
            HashSet<MergeObj> mergeSet = NonWordMergeCandidates.GetCandidates(
                tarPos, nonSpaceTokenList, cSpellApi); 
            // 4. Ranking: get top ranked candidates as corrected terms
            // 4.1 just use frenquency or context, no orthoGraphic
            // in case of using context
            outMergeObj = RankNonWordMergeByMode.GetTopRankMergeObj(mergeSet, 
                cSpellApi, tarPos, nonSpaceTokenList, debugFlag);
        }
        return outMergeObj;
    }
    private static void TestGetCorrectTerm(CSpellApi cSpellApi)
    {
        // init
        // all lowerCase
        String inText = "Dur ing my absent.";
        boolean debugFlag = true;
        ArrayList<TokenObj> inTokenList = TextObj.TextToTokenList(inText);
        // 1. convert to the non-empty token list
        ArrayList<TokenObj> nonSpaceTokenList
            = TextObj.GetNonSpaceTokenObjList(inTokenList);
        // result    
        int tarPos = 0;
        MergeObj mergeObj = NonWordMergeCorrector.GetCorrectTerm(tarPos, 
            nonSpaceTokenList, cSpellApi, debugFlag);
        // print out
        System.out.println("--------- GetCorrectTerm( ) -----------");
        System.out.println("In: [" + inText + "]");
        System.out.println("In nonSpaceTokenList: [" + nonSpaceTokenList.size()
            + "]");
        System.out.println("Out MergeObj: [" + mergeObj.ToString() + "]");
    }
    // test driver
    public static void main(String[] args)
    {
        String configFile = "../data/Config/cSpell.properties";
        if(args.length > 0)
        {
            System.out.println("Usage: java NonWordMergeCorrector <configFile>");
            System.exit(0);
        }
        
        // init
        CSpellApi cSpellApi = new CSpellApi(configFile);
        
        // test
        TestGetCorrectTerm(cSpellApi);
    }
    // data member
}

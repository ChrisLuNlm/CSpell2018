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
* This class is to correct real-word merge error.
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
public class RealWordMergeCorrector
{
    // private constructor
    /**
    * Private constructor so no one can instantiate
    */
    private RealWordMergeCorrector()
    {
    }
    // public method
    /**
    * The core method to correct a word by following steps:
    * <ul>
    * <li>detect if real-word for merge
    * <li>get candidates
    *     <ul>
    *     <li>get candidates from merge.
    *     </ul>
    * <li>Rank candidates
    *     <ul>
    *     <li>context
    *     <li>frequency (TBD)
    *     </ul>
    * <li>Update information
    *
    * </ul>
    *
    * @param     tarPos    the position of target tokenObj
    * @param    nonSpaceTokenList token list without space tokens
    * @param    cSpellApi for all dictioanry and Word2Vec data
    * @param    debugFlag boolean flag for debug print
    * 
    * @return    the corrected merged word in MergeObj if the target token 
    *             matches real-word merged rules. 
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
        // 1. only remove ending punctuation for coreTerm
        // No coreStr is used for real-word merge for less aggressive
        //String coreStr = TermUtil.StripEndPuncSpace(tarWord).toLowerCase();
        // 2. real-word merge correction 
        // check if tarWord and removeEndPuncStr is OOV
        MergeObj outMergeObj = null;    // no merge if it is null
        if((tarTokenObj.GetProcHist().size() == 0)    // not processed previously
        && (RealWordMergeDetector.IsDetect(tarWord, cSpellApi, debugFlag) == true))
        {
            cSpellApi.UpdateDetectNo();
            // TBD, should take care of possessive xxx's here
            // 3. get candidates from merge
            // set mergeWithHypehn to false for real-word merge
            HashSet<MergeObj> mergeSet = RealWordMergeCandidates.GetCandidates(
                tarPos, nonSpaceTokenList, cSpellApi);
            // 4. Ranking: get top ranked candidates as corrected terms
            // 4.1 just use frenquency or context, no orthoGraphic
            // in case of using context
            // need the context & frequency score for the orgMergeTerm
            outMergeObj = RankRealWordMergeByMode.GetTopRankMergeObj(mergeSet, 
                cSpellApi, debugFlag, tarPos, nonSpaceTokenList);
        }
        return outMergeObj;
    }
    private static void TestGetCorrectTerm(CSpellApi cSpellApi)
    {
        // init
        // all lowerCase
        //String inText = "Dur ing my absent.";
        String inText = "He was diagnosed early on set dementia 3 year ago.";
        boolean debugFlag = true;
        ArrayList<TokenObj> inTokenList = TextObj.TextToTokenList(inText);
        // 1. convert to the non-empty token list
        ArrayList<TokenObj> nonSpaceTokenList
            = TextObj.GetNonSpaceTokenObjList(inTokenList);
        // result: tar = on    
        int tarPos = 4;
        MergeObj mergeObj = GetCorrectTerm(tarPos, nonSpaceTokenList, 
            cSpellApi, debugFlag);
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
            System.out.println("Usage: java RealWordMergeCorrector <configFile>");
            System.exit(0);
        }
        
        // init
        CSpellApi cSpellApi = new CSpellApi(configFile);
        
        // test
        TestGetCorrectTerm(cSpellApi);
    }
    // data member
}

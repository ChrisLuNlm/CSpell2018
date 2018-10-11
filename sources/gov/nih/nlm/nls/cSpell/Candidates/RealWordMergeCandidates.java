package gov.nih.nlm.nls.cSpell.Candidates;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.Util.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
import gov.nih.nlm.nls.cSpell.Dictionary.*;
import gov.nih.nlm.nls.cSpell.Api.*;
import gov.nih.nlm.nls.cSpell.Ranker.*;
/*****************************************************************************
* This class generates real-word merge candidates.
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
public class RealWordMergeCandidates 
{
    /**
    * Private constructor 
    */
    private RealWordMergeCandidates()
    {
    }
    // mergeNo = 1, only merge to the right or to the left
    // mergerNo = 2, merge to the right x2, to the left x2, 
    // or to the rightx1 and to the left x1
    // inTextList: No empty space token
    public static HashSet<String> GetCandidateStrs(int tarPos, 
        ArrayList<TokenObj> inTextList, CSpellApi cSpellApi) 
    {
        HashSet<MergeObj> mergeSet = GetCandidates(tarPos, inTextList, 
            cSpellApi);
        // convert from MergeObj to Str    
        HashSet<String> mergeStrSet = new HashSet<String>(mergeSet.stream()
            .map(mObj -> mObj.GetMergeWord())
            .collect(Collectors.toList()));
        
        return mergeStrSet;
    }
    public static HashSet<MergeObj> GetCandidates(int tarPos, 
        ArrayList<TokenObj> nonSpaceTextList, CSpellApi cSpellApi)
    {
        // 0. get vars from cSpellApi
        RootDictionary suggestDic = cSpellApi.GetSuggestDic();
        RootDictionary aADic = cSpellApi.GetAaDic();
        RootDictionary mwDic = cSpellApi.GetMwDic();
        // get all merge candidates, recursively
        HashSet<MergeObj> mergeSet = new HashSet<MergeObj>();
        int maxMergeNo = cSpellApi.GetCanRwMaxMergeNo();
        // default: no merge with hyphen for real-word
        boolean mergeWithHyphen = cSpellApi.GetCanRwMergeWithHyphen();
        // go through all merge no
        // set no shrot word merge: exclude me at => meat
        boolean shortWordMerge = false;
        for(int mergeNo = 1; mergeNo <= maxMergeNo; mergeNo++)
        {
            HashSet<MergeObj> curMergeSet 
                = CandidatesUtilMerge.GetMergeSetByMergeNo(tarPos,
                nonSpaceTextList, mergeNo, mergeWithHyphen, shortWordMerge,
                suggestDic, aADic, mwDic);
            // add valid merge candidate
            for(MergeObj mergeObj:curMergeSet)
            {
                if(IsValidMergeCand(mergeObj, cSpellApi) == true)
                {
                    mergeSet.add(mergeObj);
                }
            }
        }
        return mergeSet;
    }
    // private methods
    private static boolean IsValidMergeCand(MergeObj mergeObj, 
        CSpellApi cSpellApi)
    {
        // WC is not used here
        WordWcMap wordWcMap = cSpellApi.GetWordWcMap();
        Word2Vec word2VecOm = cSpellApi.GetWord2VecOm();
        String coreMergeStr = mergeObj.GetCoreMergeWord();
        int rwMergeCandMinWc = cSpellApi.GetCanRwMergeCandMinWc();
        boolean flag = ((word2VecOm.HasWordVec(coreMergeStr))
        && (WordCountScore.GetWc(coreMergeStr, wordWcMap) >= rwMergeCandMinWc));
        return flag;
    }
    private static void Test()
    {
        // init cSpellApi
        String configFile = "../data/Config/cSpell.properties";
        CSpellApi cSpellApi = new CSpellApi(configFile);
        System.out.println("===== Unit Test of MergeCandidates =====");
        //String inText = "He was dia gnosed  early onset deminita 3 year ago.";
        // example from 73.txt
        //String inText = "I have seven live births with no problems dur ing my pregnancies. That is a dis appoint ment";
        String inText = "He was diagnosed on set early.";
        ArrayList<TokenObj> inTextList = TextObj.TextToTokenList(inText);
        String inStr = inTextList.stream()
            .map(obj -> obj.GetTokenStr())
            .collect(joining("|"));
        System.out.println(" - inTextList (" + inTextList.size() + "): ["
            + inStr + "]");
        System.out.println("-------------------------");
        for(TokenObj tokenObj:inTextList)
        {
            System.out.println(tokenObj.ToString());
        }
        int tarPos = 3;
        System.out.println("-------------------------");
        System.out.println("- tarPos: " + tarPos);
        System.out.println("- maxMergeNo: " + cSpellApi.GetCanRwMaxMergeNo());
        System.out.println("------ merge set -------");
        // pre-Process: convert to the non-empty token list
        ArrayList<TokenObj> nonSpaceTextList 
            = TextObj.GetNonSpaceTokenObjList(inTextList);
        // get the candidate for a specified target position
        HashSet<MergeObj> mergeSet = GetCandidates(tarPos, nonSpaceTextList, 
            cSpellApi);
                    
        // print out
        for(MergeObj mergeObj:mergeSet)
        {
            System.out.println(mergeObj.ToString());
        }
    }
    // test driver
    public static void main(String[] args)
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java RealWordMergeCandidates");
            System.exit(0);
        }
        
        // test case and print out 
        Test();
    }
    // data member
}

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
/*****************************************************************************
* This class generates non-word merge candidates. 
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
public class NonWordMergeCandidates 
{
    /**
    * Private constructor 
    */
    private NonWordMergeCandidates()
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
        int maxMergeNo = cSpellApi.GetCanNwMaxMergeNo();
        // default: non-word includes merge with hyphen
        boolean mergeWithHyphen = cSpellApi.GetCanNwMergeWithHyphen();
        // allow short word merge: a m => am
        boolean shortWordMerge = true;
        for(int mergeNo = 1; mergeNo <= maxMergeNo; mergeNo++)
        {
            HashSet<MergeObj> curMergeSet 
                = CandidatesUtilMerge.GetMergeSetByMergeNo(tarPos,
                nonSpaceTextList, mergeNo, mergeWithHyphen, shortWordMerge,
                suggestDic, aADic, mwDic);
            mergeSet.addAll(curMergeSet);
        }
        return mergeSet;
    }
    // private methods
    private static void Test()
    {
        // init cSpellApi
        String configFile = "../data/Config/cSpell.properties";
        CSpellApi cSpellApi = new CSpellApi(configFile);
        System.out.println("===== Unit Test of MergeCandidates =====");
        //String inText = "He was dia gnosed  early onset deminita 3 year ago.";
        // example from 73.txt
        //String inText = "I have seven live births with no problems dur ing my pregnancies. That is a dis appoint ment";
        String inText = "That is a disa ppoint ment.";
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
        int tarPos = 4;
        System.out.println("-------------------------");
        System.out.println("- tarPos: " + tarPos);
        System.out.println("- maxMergeNo: " + cSpellApi.GetCanNwMaxMergeNo());
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
            System.out.println("Usage: java NonWordMergeCandidates");
            System.exit(0);
        }
        
        // test case and print out 
        Test();
    }
    // data member
}

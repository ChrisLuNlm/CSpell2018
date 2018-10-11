package gov.nih.nlm.nls.cSpell.Candidates;
import java.util.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.Ranker.*;
import gov.nih.nlm.nls.cSpell.Dictionary.*;
import gov.nih.nlm.nls.cSpell.Api.*;
/*****************************************************************************
* This class generates non-word 1To1 candidates.
*
* <p><b>History:</b>
* <ul>
* <li>2018 baseline
* </ul>
*
* @author chlu
*
* @version    V-2018
****************************************************************************/
public class NonWord1To1Candidates
{
    // private constructor
    private NonWord1To1Candidates()
    {
    }
    // public method
    // Get candidates from dictionary by Edit-distance:
    // 1. get all possible combinations from insert, remove, replace, switch
    //    chars. However, it does not include space (so no split).
    // 2. check if the combination is in dictionary
    public static HashSet<String> GetCandidates(String inWord, 
        CSpellApi cSpellApi)
    {
        int maxLength = cSpellApi.GetCanNw1To1WordMaxLength();
        // find all possibility
        HashSet<String> candidatesByEd 
            = CandidatesUtil1To1.GetCandidatesByEd(inWord, maxLength);
        // filter out those are not valid words
        HashSet<String> candidates = new HashSet<String>();
        for(String candByEd:candidatesByEd)
        {
            // check if valid one-to-one candidate word
            if(IsValid1To1Cand(inWord, candByEd, cSpellApi) == true)
            {
                candidates.add(candByEd);
            }
        }
        return candidates;
    }
    private static boolean IsValid1To1Cand(String inWord, String cand,
        CSpellApi cSpellApi)
    {
        RootDictionary suggestDic = cSpellApi.GetSuggestDic();
        // real-word, check phonetic and suggDic
        // non-word, check if it is in the suggestion Dic
        boolean    flag = suggestDic.IsDicWord(cand);
        return flag;
    }
    private static void Test(String inStr)
    {
        // get candidates with dictionary
        String configFile = "../data/Config/cSpell.properties";
        CSpellApi cSpellApi = new CSpellApi(configFile);
        HashSet<String> candSet = GetCandidates(inStr, cSpellApi);
        System.out.println("-- canSet.size(): " + candSet.size()); 
        System.out.println(candSet);
    }
    // test driver
    public static void main(String[] args) 
    {
        String inStr = "abc";
        if(args.length == 1)
        {
            inStr = args[0];
        }
        else if(args.length > 0)
        {
            System.err.println("*** Usage: java Candidates <inStr>");
            System.exit(1);
        }
        Test(inStr);
    }
}

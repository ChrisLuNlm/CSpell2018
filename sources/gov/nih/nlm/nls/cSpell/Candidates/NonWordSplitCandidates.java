package gov.nih.nlm.nls.cSpell.Candidates;
import java.util.*;
import java.lang.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.Ranker.*;
import gov.nih.nlm.nls.cSpell.Dictionary.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
import gov.nih.nlm.nls.cSpell.Api.*;
/*****************************************************************************
* This class generates non-word split candidates. 
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
public class NonWordSplitCandidates
{
    // private constructor
    private NonWordSplitCandidates()
    {
    }
    // public method
    // filter out with dictionary
    // Use no Abb/Acr dictionary to exclude terms are abb/acr
    // The inWord must be a coreTerm.
    public static HashSet<String> GetCandidates(String inWord, 
        CSpellApi cSpellApi, int maxSplitNo)
    {
        // init from cSpellApi
        RootDictionary mwDic = cSpellApi.GetMwDic();
        // 1. find all possibie split combination by spaces
        // must be <= maxSplitNo
        HashSet<String> splitSet = CandidatesUtilSplit.GetSplitSet(
            inWord, maxSplitNo);
        // filter out those are OOV
        HashSet<String> candidates = new HashSet<String>();
        // 2. multiwords: check the whole list of split terms
        // only inlcude dictionary that have multiword - lexicon
        // TBD: this will find "perse" to "per se", however, "perse" is
        // a valid word in eng_medical.dic so cSpell can't correct it.
        // Need to refine the dictionary later!
        for(String split:splitSet)
        {
            if(mwDic.IsDicWord(split) == true)
            {
                candidates.add(split);
            }
        }
        // 3. if no multiwords found from step 2.
        // check each split terms, mark as candidate if they are in Dic,
        // Acr/Abb are excluded to eliminate noise such as 'a', 'ab', etc.
        if(candidates.size() == 0)
        {
            for(String split:splitSet)
            {
                // add to candidate if all split words are valid
                if(IsValidSplitWords(split, cSpellApi) == true)
                {
                    candidates.add(split);
                }
            }
        }
        return candidates;
    }
    // check all split words form a term to verify it is a valid
    // inTerm is the term to be split
    // the inTerm is a coreTerm
    public static boolean IsValidSplitWords(String inTerm, CSpellApi cSpellApi)
    {
        //RootDictionary unitDic = cSpellApi.GetUnitDic();
        ArrayList<String> splitWordList = TermUtil.ToWordList(inTerm);
        boolean validFlag = true;
        // go through all split words, they can be:
        // 1. digit (pure number)
        // 2. unit
        // 3. word in the split word dictionary: English + ProperNoun (not Aa)
        // if any splitWord is not above, the split is false
        for(String splitWord:splitWordList)
        {
            /* remove unit and digit beacuse:
             * 1. they are handled in ND
             * 2. some unit are Aa, such as ng, cause noise [FP]
             * - seing => se i ng, no good
            if((DigitPuncTokenUtil.IsDigit(splitWord) == false) // digit 
            && (unitDic.IsDicWord(splitWord) == false) // unit
            && (IsValidSplitWord(splitWord, cSpellApi) == false))// split word
            */
            if(IsValidSplitWord(splitWord, cSpellApi) == false)
            {
                validFlag = false;
                break;
            }
        }
        return validFlag;
    }
    // for the split, we don't want Aa as a valid word
    // because it will cause too much noise (less precision)
    private static boolean IsValidSplitWord(String inWord, CSpellApi cSpellApi)
    {
        // splitWord uses LexiconNoAa for Dic
        RootDictionary splitWordDic = cSpellApi.GetSplitWordDic();
        // 1. check if in the splitWordDic, No Aa
        boolean flag = splitWordDic.IsDicWord(inWord);
        // 2. is obsolete code because Aa is check in splitWordDic
        // 2. check pure Aa, further remove Aa
        // pureAaDic are words exlcude those overlap with not-Aa
        // such as cel is an overlap, it is aa or not-aa
        if(flag == true)
        {
            // if Aa and length < Mix. Split Aa word length
            // Set minSplitAaWordLength to a large number for excluding all paa
            //
            // This is already done in splitWordDic
            // no need, it reduced recall and precision (ofcourse => incourse)
            /**
            if((inWord.length() < minSplitAaWordLength)
            && (aaDic.IsDicWord(inWord) == true))
            {
                flag = false;
            }
            **/
        }
        
        return flag;
    }
    // test all split combonation
    private static void TestSplitCandidates(String inStr)
    {
        // init dictionary
        String configFile = "../data/Config/cSpell.properties";
        CSpellApi cSpellApi = new CSpellApi(configFile);
        int maxSplitNo = cSpellApi.GetCanNwMaxSplitNo();
        // test 2 for candidate
        System.out.println("====== test candidates (with Dic check) ======"); 
        System.out.println("----- Final Candidate for split -----"); 
        System.out.println("----- inStr: [" + inStr + "]");
        HashSet<String> candSet1 = GetCandidates(inStr, cSpellApi, maxSplitNo);
        System.out.println("-- canSet1.size(): " + candSet1.size()); 
        System.out.println(candSet1); 
        // other tests 3 for not multiword case
        System.out.println("====== test candidates (with Dic check) ======"); 
        inStr = "perse";
        System.out.println("----- inStr: [" + inStr + "]");
        HashSet<String> candSet11 = GetCandidates(inStr, cSpellApi, maxSplitNo);
        System.out.println("-- canSet11.size(): " + candSet11.size()); 
        System.out.println(candSet11);
        System.out.println("-------------------------------------"); 
        // other test 4 for not multiword case
        System.out.println("====== test candidates (with Dic check) ======"); 
        inStr = "iloveyou";
        System.out.println("----- inStr: [" + inStr + "]");
        HashSet<String> candSet2 = GetCandidates(inStr, cSpellApi, maxSplitNo);
        System.out.println("-- canSet2.size(): " + candSet2.size()); 
        System.out.println(candSet2); 
        System.out.println("-------------------------------------"); 
    }
    // test driver
    public static void main(String[] args) 
    {
        // example: knowabout, viseversa, hotflashes, testsplit,
        // Amlodipine5mgs
        String inStr = "Amlodipine5mgs";
        int maxSplitNo = 2;
        if(args.length == 1)
        {
            inStr = args[0];
        }
        else if(args.length > 0)
        {
            System.err.println("*** Usage: java SplitCandidates <inStr>");
            System.exit(1);
        }
        // 1. test
        TestSplitCandidates(inStr);
    }
}

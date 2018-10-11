package gov.nih.nlm.nls.cSpell.Candidates;
import java.util.*;
import java.lang.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.Ranker.*;
import gov.nih.nlm.nls.cSpell.Dictionary.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
import gov.nih.nlm.nls.cSpell.Api.*;
/*****************************************************************************
* This class generates real-word split candidates 
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
public class RealWordSplitCandidates
{
    // private constructor
    private RealWordSplitCandidates()
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
        // filter out those are not valid
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
            // go through each split words
            for(String split:splitSet)
            {
                // add to candidate if all split words are valid
                if(IsValidSplitCand(split, cSpellApi) == true)
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
    private static boolean IsValidSplitCand(String inTerm, CSpellApi cSpellApi)
    {
        // 1. check the sort split words
        // 2. check Split words
        boolean validFlag = 
            ((CheckShortSplitWords(inTerm, cSpellApi) == true)
            && (CheckSplitWords(inTerm, cSpellApi) == true));
        return validFlag;
    }
    // check all split words
    private static boolean CheckSplitWords(String inTerm, CSpellApi cSpellApi)
    {
        // convert to word list
        ArrayList<String> splitWordList = TermUtil.ToWordList(inTerm);
        // go through all split words, they can be:
        // 1. digit (pure number)
        // 2. unit
        // 3. word in the split word dictionary: English + ProperNoun (not Aa)
        // if any splitWord is not above, the split is false
        boolean flag = true;
        for(String splitWord:splitWordList)
        {
            // check each split word
            if(IsValidSplitWord(splitWord, cSpellApi) == false)
            {
                flag = false;
                break;
            }
        }
        return flag;
    }
    // These are hueristic rule for real-wrod split
    // check the total no of short word for split words in inTerm (candidate)
    // short word is configurable, such as 2 or 3
    // the total no of split shot word must less than a number, default is 2
    // This rule is added to filter out: some -> so me, 
    // filter out: another -> a not her (shortSplitWordNo = 3)
    // filter out: anyone -> any one (shortSplitWordNo = 2)
    // 1. keep: away -> a way (shortSplitWordNo = 1)
    // 2. filter: out soon -> so on (shortSplitWordNo = 2)
    // 3. filter: out anyway -> any way (shortSplitWordNo = 2)
    private static boolean CheckShortSplitWords(String inTerm, 
        CSpellApi cSpellApi)
    {
        // init
        int shortSplitWordLength = cSpellApi.GetCanRwShortSplitWordLength();
        int maxShortSplitWordNo = cSpellApi.GetCanRwMaxShortSplitWordNo();
        // convert to word list
        ArrayList<String> wordList = TermUtil.ToWordList(inTerm);
        boolean flag = true;
        int shortSplitWordNo = 0;    // total no of short split word 1
        for(String word:wordList)
        {
            // find shor word
            if(word.length() <= shortSplitWordLength)
            {
                shortSplitWordNo++;
            }
        }
        // check the total no of short split words (length <= 2)
        if(shortSplitWordNo >= maxShortSplitWordNo)
        {
            flag = false;
        }
        return flag;
    }
    // for the split, we don't want Aa as a valid word
    // because it will cause too much noise (less precision)
    // TBD ... re-organize
    private static boolean IsValidSplitWord(String inWord, CSpellApi cSpellApi)
    {
        // splitWord uses LexiconNoAa for Dic
        RootDictionary splitWordDic = cSpellApi.GetSplitWordDic();
        WordWcMap wordWcMap = cSpellApi.GetWordWcMap();
        Word2Vec word2VecOm = cSpellApi.GetWord2VecOm();
        RootDictionary unitDic = cSpellApi.GetUnitDic();
        RootDictionary pnDic = cSpellApi.GetPnDic();
        //RootDictionary aaDic = cSpellApi.GetAaDic();
        int rwSplitCandMinWc = cSpellApi.GetCanRwSplitCandMinWc();
        // real-word cand split word must:
        // 1. check if in the splitWordDic, No Aa with a small length
        // such as cel is an overlap, it is aa or not-aa
        // 2. has word2Vec
        // 3. has WC
        // 4. not unit, mg -> ...
        // 5. not properNoun, human -> Hu man, where Hu is pn
        // children -> child ren, where ren is pn
        boolean flag = (splitWordDic.IsDicWord(inWord))
            && (word2VecOm.HasWordVec(inWord) == true)    // must have w2v
            && (WordCountScore.GetWc(inWord, wordWcMap) >= rwSplitCandMinWc)
            && (!unitDic.IsDicWord(inWord))
            //&& (!aaDic.IsDicWord(inWord))
            && (!pnDic.IsDicWord(inWord));
        
        return flag;
    }
    private static void TestSplitCandidates(String inStr)
    {
        // init dictionary
        String configFile = "../data/Config/cSpell.properties";
        CSpellApi cSpellApi = new CSpellApi(configFile);
        int maxSplitNo = cSpellApi.GetCanRwMaxSplitNo();
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

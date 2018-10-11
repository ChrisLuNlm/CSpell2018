package gov.nih.nlm.nls.cSpell.Ranker;
import java.io.*;
import java.util.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.Util.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
import gov.nih.nlm.nls.cSpell.Dictionary.*;
import gov.nih.nlm.nls.cSpell.Candidates.*;
import gov.nih.nlm.nls.cSpell.Api.*;
/*****************************************************************************
* This class ranks and finds the best ranked candidates for real-word 1To1
* correction by CSpell scoring system.
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
public class RankByCSpellRealWord1To1
{
    // private constructor
    private RankByCSpellRealWord1To1()
    {
    }
    // return candidate str list sorted by wordNo score, higher first
    public static ArrayList<String> GetCandidateStrList(
        String wordStr, HashSet<String> candidates, WordWcMap wordWcMap,
        int tarPos, int tarSize, ArrayList<TokenObj> nonSpaceTokenList,
        Word2Vec word2VecIm, Word2Vec word2VecOm, boolean word2VecSkipWord,
        int contextRadius, double wf1, double wf2, double wf3,
        boolean debugFlag)
    {
        ArrayList<CSpellScore> candScoreList
            = GetCandidateScoreList(wordStr, candidates, wordWcMap,
            tarPos, tarSize, nonSpaceTokenList, word2VecIm, word2VecOm,
            word2VecSkipWord, contextRadius, wf1, wf2, wf3, debugFlag);
        ArrayList<String> candStrList = new ArrayList<String>();    
        for(CSpellScore cs:candScoreList)
        {
            candStrList.add(cs.GetCandStr());
        }
        return candStrList;
    }
    // return candidate scoreObj list sorted by score, higher first
    public static ArrayList<CSpellScore> GetCandidateScoreList(
        String wordStr, HashSet<String> candidates, WordWcMap wordWcMap,
        int tarPos, int tarSize, ArrayList<TokenObj> nonSpaceTokenList,
        Word2Vec word2VecIm, Word2Vec word2VecOm, boolean word2VecSkipWord,
        int contextRadius, double wf1, double wf2, double wf3,
        boolean debugFlag)
    {
        HashSet<CSpellScore> candScoreSet 
            = GetCandidateScoreSet(wordStr, candidates, wordWcMap,
            tarPos, tarSize, nonSpaceTokenList, word2VecIm, word2VecOm,
            word2VecSkipWord, contextRadius, wf1, wf2, wf3, debugFlag);
        ArrayList<CSpellScore> candScoreList 
            = new ArrayList<CSpellScore>(candScoreSet);
        // sort the set to list
        CSpellScoreRw1To1Comparator<CSpellScore> csc 
            = new CSpellScoreRw1To1Comparator<CSpellScore>();
        Collections.sort(candScoreList, csc);    
        return candScoreList;
    }
    // return candidate set with cSpell score
    // wordStr is the srcTxt used to calculate the score between it and cand
    public static HashSet<CSpellScore> GetCandidateScoreSet(
        String wordStr, HashSet<String> candidates, WordWcMap wordWcMap,
        int tarPos, int tarSize, ArrayList<TokenObj> nonSpaceTokenList,
        Word2Vec word2VecIm, Word2Vec word2VecOm, boolean word2VecSkipWord,
        int contextRadius, double wf1, double wf2, double wf3, 
        boolean debugFlag)
    {
        HashSet<CSpellScore> candScoreSet 
            = new HashSet<CSpellScore>();
        for(String cand:candidates)
        {
            // find context for each candidates
            DoubleVec contextVec = Word2VecContext.GetContextVec(tarPos,
                tarSize, nonSpaceTokenList, word2VecIm, contextRadius,
                word2VecSkipWord, debugFlag);
             
            CSpellScore cs = new CSpellScore(wordStr, cand, wordWcMap,
                contextVec, word2VecOm, wf1, wf2, wf3); 
            candScoreSet.add(cs);
        }
        return candScoreSet;
    }
    // return the best ranked str from candidates using cSpell score
    public static String GetTopRankStr(String inStr, 
        HashSet<String> candidates, WordWcMap wordWcMap,
        int tarPos, int tarSize, ArrayList<TokenObj> nonSpaceTokenList,
        Word2Vec word2VecIm, Word2Vec word2VecOm, boolean word2VecSkipWord,
        int contextRadius, double wf1, double wf2, double wf3)
    {
        boolean debugFlag = false;
        return GetTopRankStr(inStr, candidates, wordWcMap, tarPos, tarSize,
            nonSpaceTokenList, word2VecIm, word2VecOm, word2VecSkipWord,
            contextRadius, wf1, wf2, wf3, debugFlag);
    }
    public static String GetTopRankStr(String inStr, 
        HashSet<String> candidates, WordWcMap wordWcMap,
        int tarPos, int tarSize, ArrayList<TokenObj> nonSpaceTokenList,
        Word2Vec word2VecIm, Word2Vec word2VecOm, boolean word2VecSkipWord,
        int contextRadius, double wf1, double wf2, double wf3, 
        boolean debugFlag)
    {
        String topRankStr = inStr;
        // get the sorted list
        ArrayList<CSpellScore> candScoreList
            = GetCandidateScoreList(inStr, candidates, wordWcMap,
            tarPos, tarSize, nonSpaceTokenList, word2VecIm, word2VecOm,
            word2VecSkipWord, contextRadius, wf1, wf2, wf3, debugFlag);
        if(candScoreList.size() > 0)
        {
            topRankStr = candScoreList.get(0).GetCandStr();
        }
        return topRankStr;
    }
    // private methods
    // test Driver
    public static void main(String[] args)
    {
    }
    // data member
}

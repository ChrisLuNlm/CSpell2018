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
* This class ranks and finds the best ranked candidates for non-word correction
* by CSpell score system (playoffs).
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
public class RankByCSpellNonWord
{
    // private constructor
    private RankByCSpellNonWord()
    {
    }
    // return candidate str list sorted by wordNo score, higher first
    public static ArrayList<String> GetCandidateStrList(String wordStr, 
        HashSet<String> candidates, WordWcMap wordWcMap,
        int tarPos, int tarSize, ArrayList<TokenObj> nonSpaceTokenList,
        Word2Vec word2VecIm, Word2Vec word2VecOm, boolean word2VecSkipWord,
        int contextRadius, int compareMode, double wf1, double wf2, 
        double wf3, boolean debugFlag)
    {
        ArrayList<CSpellScore> candScoreList
            = GetCandidateScoreList(wordStr, candidates, wordWcMap,
            tarPos, tarSize, nonSpaceTokenList, word2VecIm, word2VecOm,
            word2VecSkipWord, contextRadius, compareMode, wf1, wf2, wf3,
            debugFlag);
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
        int contextRadius, int compareMode, double wf1, double wf2, double wf3,
        boolean debugFlag)
    {
        HashSet<CSpellScore> candScoreSet 
            = GetCandidateScoreSet(wordStr, candidates, wordWcMap,
            tarPos, tarSize, nonSpaceTokenList, word2VecIm, word2VecOm,
            word2VecSkipWord, contextRadius, wf1, wf2, wf3, debugFlag);
        ArrayList<CSpellScore> candScoreList 
            = new ArrayList<CSpellScore>(candScoreSet);
        // sort the set to list by context
        CSpellScoreNw1To1Comparator<CSpellScore> csc 
            = new CSpellScoreNw1To1Comparator<CSpellScore>();
        csc.SetCompareMode(compareMode);    
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
        int contextRadius, double rangeFactor, double nwS1MinOScore,
        double wf1, double wf2, double wf3)
    {
        boolean debugFlag = false;
        return GetTopRankStr(inStr, candidates, wordWcMap, tarPos, tarSize,
            nonSpaceTokenList, word2VecIm, word2VecOm, word2VecSkipWord,
            contextRadius, rangeFactor, nwS1MinOScore, 
            wf1, wf2, wf3, debugFlag);
    }
    // 2-stage ranking
    public static String GetTopRankStr(String inStr, HashSet<String> candidates,
        WordWcMap wordWcMap, int tarPos, int tarSize, 
        ArrayList<TokenObj> nonSpaceTokenList, Word2Vec word2VecIm, 
        Word2Vec word2VecOm, boolean word2VecSkipWord, int contextRadius, 
        double rangeFactor, double nwS1MinOScore, 
        double wf1, double wf2, double wf3, boolean debugFlag)
    {
        String topRankStr = inStr;
        // 1. find the top orthograpics score from sorted list by orthographics
        // need the sorted list to find the top orthographics score
        int compareMode = CSpellScoreNw1To1Comparator.COMPARE_BY_ORTHOGRAPHICS;
        // previous way
        //int compareMode = CSpellScoreNw1To1Comparator.COMPARE_BY_COMBO;
        ArrayList<CSpellScore> candScoreList
            = GetCandidateScoreList(inStr, candidates, wordWcMap,
            tarPos, tarSize, nonSpaceTokenList, word2VecIm, word2VecOm,
            word2VecSkipWord, contextRadius, compareMode, wf1, wf2, wf3,
            debugFlag);
        // 2. Set a range for the candidates to find all possible top rank
        // use the highest context and frequecny score to final rank.
        double maxFScore = 0.0d;
        double maxCScore = 0.0d;    // not -1.0d
        double maxNScore = 0.0d;
        String topRankStrByC = null;
        String topRankStrByF = null;
        String topRankStrByN = null;
        if(candScoreList.size() >= 1)
        {
            double topOScore = candScoreList.get(0).GetOScore().GetScore();
            double range = topOScore*rangeFactor;
            // 2.1 find all cands within range
            for(CSpellScore candScore:candScoreList)
            {
                // within the range, find the highest frequency
                if((topOScore - candScore.GetOScore().GetScore()) <= range)
                {
                    double candCScore = candScore.GetCScore().GetScore();
                    if((candCScore != 0.0d)
                    && (candCScore > maxCScore))
                    {
                        // find the topRank by context
                        topRankStrByC = candScore.GetCandStr();
                        maxCScore = candCScore;
                    }
                    double candFScore = candScore.GetFScore().GetScore();
                    if(candFScore > maxFScore)
                    {
                        // find the topRank by frequency
                        topRankStrByF = candScore.GetCandStr();
                        maxFScore = candFScore;
                    }
                    double candNScore = candScore.GetNScore().GetScore();
                    if(candNScore > maxNScore)
                    {
                        // find the topRank by Noisy Channel
                        topRankStrByN = candScore.GetCandStr();
                        maxNScore = candNScore;
                    }
                }
            }
            // 3. set topRankStr to context
            if(topRankStrByC != null)
            {
                topRankStr = topRankStrByC;
            }
            // 4. set topRankStr to Noisy Channel
            else if(topRankStrByN != null)
            {
                topRankStr = topRankStrByN;
            }
            // 5. 1 candidate, use orthographic
            else if(candScoreList.size() == 1)
            {
                // "Lactoccocus lactis" to "Lactococcus lactis"
                if(topOScore >= nwS1MinOScore)    // empirical value
                {
                    topRankStr = candScoreList.get(0).GetOScore().GetTarStr();
                }
            }
            /*** no need, used for dev and testing
            // 6. set topRankStr to frequency
            else if(topRankStrByF != null)
            {
                topRankStr = topRankStrByF;
            }
            // 7. set topRankStr to Orthographics
            else
            {
                topRankStr = candScoreList.get(0).GetCandStr();
            }
            */
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

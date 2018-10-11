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
* This class ranks and finds the best ranked candidates by ensemble method.
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
public class RankByEnsemble
{
    // private constructor
    private RankByEnsemble()
    {
    }
    // return the best ranked str from candidates using cSpell score
    public static String GetTopRankStr(String inStr, 
        HashSet<String> candidates, WordWcMap wordWcMap,
        int tarPos, int tarSize, ArrayList<TokenObj> nonSpaceTokenList,
        Word2Vec word2VecIm, Word2Vec word2VecOm, boolean word2VecSkipWord,
        int contextRadius, double rangeFactor, double wf1, double wf2, 
        double wf3)
    {
        boolean debugFlag = false;
        return GetTopRankStr(inStr, candidates, wordWcMap, tarPos, tarSize,
            nonSpaceTokenList, word2VecIm, word2VecOm, word2VecSkipWord,
            contextRadius, rangeFactor, wf1, wf2, wf3, debugFlag);
    }
    public static String GetTopRankStr(String inStr, HashSet<String> candidates,
        WordWcMap wordWcMap, int tarPos, int tarSize, 
        ArrayList<TokenObj> nonSpaceTokenList, Word2Vec word2VecIm, 
        Word2Vec word2VecOm, boolean word2VecSkipWord, int contextRadius, 
        double rangeFactor, double wf1, double wf2, double wf3,
        boolean debugFlag)
    {
        String topRankStr = inStr;
        // get the sorted list
        int compareMode = CSpellScoreNw1To1Comparator.COMPARE_BY_ENSEMBLE;
        ArrayList<CSpellScore> candScoreList
            = RankByCSpellNonWord.GetCandidateScoreList(
            inStr, candidates, wordWcMap, tarPos, tarSize, nonSpaceTokenList, 
            word2VecIm, word2VecOm, word2VecSkipWord, contextRadius, 
            compareMode, wf1, wf2, wf3, debugFlag);
        // Set a range for the candidates to find all possible top rank
        // use the highest context and frequecny score to final rank.
        if(candScoreList.size() >= 0)
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

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
* This class ranks and finds the best ranked candidates for real-word split
* by ContextSocre.
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
public class RankRealWordSplitByContext
{
    // private constructor
    private RankRealWordSplitByContext()
    {
    }
    // return the best ranked str from candidates using word2Vec score
    // inTokenList, includes space token, is not coreTerm.Lc
    // return the orignal inStr if no candidate has score > 0.0d
    public static String GetTopRankStr(String inStr, HashSet<String> candidates,
        int tarPos, int tarSize, ArrayList<TokenObj> nonSpaceTokenList,
        Word2Vec word2VecIm, Word2Vec word2VecOm, boolean word2VecSkipWord,
        int contextRadius, int shortSplitWordLength, int maxShortSplitWordNo,
        double rwSplitFactor, int maxCandNo, boolean debugFlag)
    {
        // init
        String topRankStr = inStr;
        // Find the correction str 
        if(candidates.size() > 0)
        {
            // 1. sorted score list for each candidates ...
            // This ranking can be improved if n-gram model (frequecny) is used
            ArrayList<ContextScore> candScoreList
                = RankByContext.GetCandidateScoreList(candidates, tarPos, 
                tarSize, nonSpaceTokenList, word2VecIm, word2VecOm, 
                word2VecSkipWord, contextRadius, debugFlag);
            // 1.1 get the top tank candidate
            ContextScore topContextScore = candScoreList.get(0);
            // 2. validate the top rank
            // 2.1 wordVec for context
            DoubleVec contextVec = Word2VecContext.GetContextVec(tarPos,
                tarSize, nonSpaceTokenList, word2VecIm, contextRadius,
                word2VecSkipWord, debugFlag);
            // 2.2 wordVec for the original words before split    
            ContextScore orgContextScore
                = new ContextScore(inStr, contextVec, word2VecOm);
            // 2.3 compare the top rank split to the original string b4 split
            if(IsTopCandValid(inStr, orgContextScore, topContextScore, 
                rwSplitFactor, debugFlag) == true)
            {
                // no correction: if score is not good enough for corection
                topRankStr = topContextScore.GetTerm();
            }
            // debug print
            if(debugFlag == true)
            {
                // print focus token (original)
                DebugPrint.PrintCScore(orgContextScore.ToString(), debugFlag);
                // print candidates
                ContextScoreComparator<ContextScore> csc
                    = new ContextScoreComparator<ContextScore>();
                candScoreList.stream()
                    .sorted(csc) // sort it
                    .limit(maxCandNo) // limit the number for print out
                    .map(obj -> obj.ToString())
                    .forEach(str -> DebugPrint.PrintCScore(str, debugFlag));
            }
        }
        return topRankStr;
    }
    // private methods
    private static boolean IsTopCandValid(String inStr,
        ContextScore orgContextScore, ContextScore topContextScore,
        double rwSplitFactor, boolean debugFlag)
    {
        // return false if no topCand found
        if((topContextScore == null)
        || (topContextScore.GetTerm().equals(inStr)))
        {
            return false;
        }
        // Score rules for split
        double orgScore = orgContextScore.GetScore();
        double topScore = topContextScore.GetScore();
        boolean flag = false;
        // 2.1 no split correction if orgScore is 0.0d, no word2Vec information
        if(orgScore < 0.0d)
        {
            // 2.2a split if the org score is negative and top score is positive
            if(topScore > 0.0d)
            {
                flag = true;
            }
            // 2.2b split if the org score is negative and top score is better
            // not used for now, saved for future usage
            else if((topScore < 0.0d)
            && (topScore > orgScore*rwSplitFactor))
            {
                flag = true;
            }
        }
        // not used for now, saved for future usage
        else if(orgScore > 0.0d)
        {
            // 2.3a merge if the org score is positive and better 0.01*topScore
            if(topScore*rwSplitFactor > orgScore)
            {
                flag = true;
            }
        }
        return flag;
    }
    // test Driver
    public static void main(String[] args)
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java RankRealWordSplitByContext");
            System.exit(0);
        }
        // test
    }
    // data member
}

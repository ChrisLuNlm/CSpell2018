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
* This class ranks and finds the best ranked candidates for real-word merge
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
public class RankRealWordMergeByContext
{
    // private constructor
    private RankRealWordMergeByContext()
    {
    }
    // return the best ranked str from candidates using word2Vec score
    // inTokenList, includes space token, is not coreTerm.Lc
    // return null if no candidate is found to correct
    public static MergeObj GetTopRankMergeObj(HashSet<MergeObj> candidates,
        ArrayList<TokenObj> nonSpaceTokenList,
        Word2Vec word2VecIm, Word2Vec word2VecOm, boolean word2VecSkipWord,
        int contextRadius, double rwMergeFactor, boolean debugFlag)
    {
        // init the topRankMergeObj
        MergeObj topRankMergeObj = null;
        if(candidates.size() > 0)
        {
            // 1. find sorted score list for each candidates ...
            ArrayList<ContextScore> candScoreList = GetCandidateScoreList(
                candidates, nonSpaceTokenList, word2VecIm, word2VecOm, 
                word2VecSkipWord, contextRadius, debugFlag);
            // 2. find the top ranked str
            // the 0 element has the highest score because it is sorted        
            // only 1 candidate, use it for nonWord
            ContextScore topContextScore = null;
            if(candScoreList.size() > 0)
            {
                topContextScore = candScoreList.get(0);
            }
            // 3. find the mergeObj from the topRankStr (if exist)
            if(topContextScore != null)
            {
                // 3.1. convert mergeObj set to string set
                // key: coreMergeWord, MergeObj
                HashMap<String, MergeObj> candStrMergeObjMap
                    = new HashMap<String, MergeObj>();
                for(MergeObj mergeObj:candidates)
                {
                    String mergeWord = mergeObj.GetCoreMergeWord();
                    candStrMergeObjMap.put(mergeWord, mergeObj);
                }
                HashSet<String> candStrSet
                    = new HashSet<String>(candStrMergeObjMap.keySet());
                // 3.2 convert back from top rank str to MergeObj
                // topRankStr should never be null because candidates is > 0
                String topRankStr = topContextScore.GetTerm();
                topRankMergeObj = candStrMergeObjMap.get(topRankStr);
                // 4. compare the top rank merge to the original string b4 merge
                // 1. get the word2Vec score for the orgMergeTerm b4 merge
                // 1.1 wordVec for context
                int tarPos = topRankMergeObj.GetStartPos();
                // tarSize is the total token No of the orgMergeWords
                int tarSize = topRankMergeObj.GetEndPos() 
                    - topRankMergeObj.GetStartPos() + 1;
                DoubleVec contextVec = Word2VecContext.GetContextVec(tarPos,
                    tarSize, nonSpaceTokenList, word2VecIm, contextRadius,
                    word2VecSkipWord, debugFlag);
                // 1.2 wordVec for the original words before merge     
                String orgMergeWord = topRankMergeObj.GetOrgMergeWord(); 
                ContextScore orgContextScore 
                    = new ContextScore(orgMergeWord, contextVec, word2VecOm);
                // validate top merge candidate, set to null if false    
                if(IsTopCandValid(orgContextScore, topContextScore,
                    rwMergeFactor, debugFlag) == false)
                {
                    // set to null if score is not good enough for corection
                    topRankMergeObj = null;
                }
            }
        }
        return topRankMergeObj;
    }
    // check score rule for real-word merge correctionrrayList<TokenObj>
    // nonSpaceTokenList,
    private static boolean IsTopCandValid(ContextScore orgContextScore,
        ContextScore topContextScore, double rwMergeFactor, boolean debugFlag)
    {
        // Score rules for merge
        double orgScore = orgContextScore.GetScore();
        double topScore = topContextScore.GetScore();
        boolean flag = false;
        // 2.1 no merge correction if orgScore is 0.0d, no word2Vec information
        if(orgScore < 0.0d)
        {
            // 2.2a merge if the org score is negative and top score is positive
            if(topScore > 0.0d)
            {
                flag = true;
            }
            // 2.2b merge if the org score is negative and top score is better
            // this is needed for higher recall and F1
            else if((topScore < 0.0d)
            && (topScore > orgScore*rwMergeFactor))
            {
                flag = true;
            }
        }
        else if(orgScore > 0.0d)
        {
            // 2.3a merge if the org score is positive and better 0.01*topScore
            if(topScore*rwMergeFactor > orgScore)
            {
                flag = true;
            }
        }
        return flag;
    }
    // return candidate scoreObj list sorted by score, higher first
    public static ArrayList<ContextScore> GetCandidateScoreList(
        HashSet<MergeObj> candidates, ArrayList<TokenObj> nonSpaceTokenList, 
        Word2Vec word2VecIm, Word2Vec word2VecOm, boolean word2VecSkipWord, 
        int contextRadius, boolean debugFlag)
    {
        // find score object set for each candidates ...
        HashSet<ContextScore> candScoreSet 
            = GetCandidateScoreSet(candidates, nonSpaceTokenList, word2VecIm, 
            word2VecOm, word2VecSkipWord, contextRadius, debugFlag);
        // sorted by the score, higher go first
        ArrayList<ContextScore> candScoreList 
            = new ArrayList<ContextScore>(candScoreSet);
        ContextScoreComparator<ContextScore> csc 
            = new ContextScoreComparator<ContextScore>();
        Collections.sort(candScoreList, csc);    
        // print detail
        for(ContextScore contextScore:candScoreList)
        {
            DebugPrint.PrintCScore(contextScore.ToString(), debugFlag);
        }
        return candScoreList;
    }
    // return candidate set with context score
    // word2Vec is the word|wordVec map to get the wordVec 
    // Not sorted, because it is a set
    // tarPos: starting position of target token
    // tarSize: token size of target token (single word = 1)
    public static HashSet<ContextScore> GetCandidateScoreSet(
        HashSet<MergeObj> candidates, ArrayList<TokenObj> nonSpaceTokenList, 
        Word2Vec word2VecIm, Word2Vec word2VecOm, boolean word2VecSkipWord, 
        int contextRadius, boolean debugFlag)
    {
        HashSet<ContextScore> candScoreSet = new HashSet<ContextScore>();
        // get context score for all candidates
        // go through all merge candidates, all have differetn context  
        for(MergeObj mergeObj:candidates)
        {
            // 1. get the context and contextVec, using input matrix
            int tarPos = mergeObj.GetStartPos();
            int tarSize = mergeObj.GetEndPos() - mergeObj.GetStartPos() + 1;
            DoubleVec contextVec = Word2VecContext.GetContextVec(tarPos, 
                tarSize, nonSpaceTokenList, word2VecIm, contextRadius, 
                word2VecSkipWord, debugFlag);
            // 2. get ContextSocre for each merge, use output matrix
            String mergeWord = mergeObj.GetCoreMergeWord(); 
            ContextScore cs = new ContextScore(mergeWord, contextVec, 
                word2VecOm);
            candScoreSet.add(cs);
        }
        return candScoreSet;
    }
    // return the best ranked str from candidates using context score
    // this method is replaced by GetTopRankStr, which sorted by comparator
    public static MergeObj GetTopRankMergeObjByScore(
        HashSet<MergeObj> candidates, ArrayList<TokenObj> nonSpaceTokenList, 
        Word2Vec word2VecIm, Word2Vec word2VecOm, boolean word2VecSkipWord, 
        int contextRadius, boolean debugFlag)
    {
        MergeObj topRankMergeObj = null;
        double maxScore = 0.0d;
        for(MergeObj mergeObj:candidates)
        {
            // 1. get the context and contextVec
            int tarPos = mergeObj.GetStartPos();
            int tarSize = mergeObj.GetEndPos() - mergeObj.GetStartPos() + 1;
            DoubleVec contextVec = Word2VecContext.GetContextVec(
                tarPos, tarSize, nonSpaceTokenList, word2VecIm, contextRadius,
                word2VecSkipWord, debugFlag);
            // 2. get ContextSocre for each merge, use output matrix
            String mergeWord = mergeObj.GetCoreMergeWord();
            ContextScore cs = new ContextScore(mergeWord, contextVec, 
                word2VecOm);
            double score = cs.GetScore();
            // update only if the score is > 0.0d
            if(score > maxScore)
            {
                topRankMergeObj = mergeObj;
                maxScore = score;
            }
        }
        return topRankMergeObj;
    }
    // private methods
    // test Driver
    public static void main(String[] args)
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java RankRealWordMergeByContext");
            System.exit(0);
        }
        // test
    }
    // data member
}

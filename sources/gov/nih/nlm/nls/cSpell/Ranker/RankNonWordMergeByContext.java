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
* This class ranks and finds the best ranked candidates for non-word merge
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
public class RankNonWordMergeByContext
{
    // private constructor
    private RankNonWordMergeByContext()
    {
    }
    // return the best ranked str from candidates using word2Vec score
    // inTokenList, includes space token, is not coreTerm.Lc
    // return the orignal inStr if no candidate has score > 0.0d
    public static MergeObj GetTopRankMergeObj(HashSet<MergeObj> candidates,
        ArrayList<TokenObj> nonSpaceTokenList, Word2Vec word2VecIm, 
        Word2Vec word2VecOm, boolean word2VecSkipWord, int contextRadius, 
        boolean debugFlag)
    {
        MergeObj topRankMergeObj = null;
        if(candidates.size() > 0)
        {
            // 1. find sorted score list for each candidates ...
            ArrayList<ContextScore> candScoreList = GetCandidateScoreList(
                candidates, nonSpaceTokenList, word2VecIm, word2VecOm, 
                word2VecSkipWord, contextRadius, debugFlag);
            // 2. find the hgihest str
            // the 0 element has the highest score because it is sorted        
            String topRankStr = null;
            // only 1 candidate, use it for nonWord
            if(candScoreList.size() == 1)
            {
                topRankStr = candScoreList.get(0).GetTerm();
            }
            else if(candScoreList.size() > 0)    // multiple candidates
            {
                // 1. Check the score, the top rank must be > 0.0
                // This shold use the corrdinated comparator, which +, 0, -
                //if(candScoreList.get(0).GetScore() > 0.0d)
                // 2. Use score system 2
                // Check the score, no updated if the top score is 0.0
                // It works for top score is + or -
                // if the top is 0.0, no updated because top can is not in w2v
                // if top score is 0, we don't know is it better than -
                // top rank rules: score can't be 0.0d
                if(candScoreList.get(0).GetScore() != 0.0d)
                {
                    topRankStr = candScoreList.get(0).GetTerm();
                }
            }
            // 3. find the mergeObj from the topRankStr
            if(topRankStr != null)
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
                topRankMergeObj = candStrMergeObjMap.get(topRankStr);
            }
        }
        return topRankMergeObj;
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
    // tarSize: token size of target token (single word = 1, merge > 1)
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
    // this test is not verified
    private static int RunTest(boolean detailFlag, int tarPos, int tarSize,
        int contextRadius, long limitNo)
    {
        // init dic
        String configFile = "../data/Config/cSpell.properties";
        CSpellApi cSpellApi = new CSpellApi(configFile);
        cSpellApi.SetRankMode(CSpellApi.RANK_MODE_CONTEXT);
        Word2Vec word2VecIm = cSpellApi.GetWord2VecIm();
        Word2Vec word2VecOm = cSpellApi.GetWord2VecOm();
        boolean word2VecSkipWord = cSpellApi.GetWord2VecSkipWord();
        ContextScoreComparator<ContextScore> csc 
            = new ContextScoreComparator<ContextScore>();
        // provide cmdLine interface
        int returnValue = 0;
        try
        {
            BufferedReader stdInput 
                = new BufferedReader(new InputStreamReader(System.in));
            try
            {
                String inText = null;
                System.out.println("- Please input a text, only a spell error allowed (type \"Ctl-d\" to quit) > ");
                while((inText = stdInput.readLine()) != null)
                {
                    // ---------------------------------
                    // Get spell correction on the input
                    // ---------------------------------
                    // convert input text to TokenObj
                    TextObj textObj = new TextObj(inText);
                    ArrayList<TokenObj> inTextList = textObj.GetTokenList(); 
                    // remove space token from the list
                    ArrayList<TokenObj> nonSpaceTokenList
                        = TextObj.GetNonSpaceTokenObjList(inTextList);
                    // *2 because tokenList include space
                    String tarWord = inTextList.get(tarPos*2).GetTokenStr();
                    for(int i = 1; i < tarSize; i++)
                    {
                        int ii = (tarPos + 1)*2;
                        tarWord += " " + inTextList.get(ii).GetTokenStr();
                    }
                    System.out.println("- input text: [" + inText + "]"); 
                    System.out.println("- target: [" + tarPos + "|"
                        + tarSize + "|" + tarWord + "]"); 
                    System.out.println("- context radius: " + contextRadius); 
                    // get all possible candidates
                    HashSet<MergeObj> candSet
                        = NonWordMergeCandidates.GetCandidates(tarPos,
                        nonSpaceTokenList, cSpellApi);
                    System.out.println("-- canSet.size(): " + candSet.size());
                    // get final suggestion
                    MergeObj topRankMergeObj = GetTopRankMergeObj(candSet, 
                        nonSpaceTokenList, word2VecIm, word2VecOm, 
                        word2VecSkipWord, contextRadius, detailFlag);
                    System.out.println("- top rank merge Obj: " 
                        + topRankMergeObj.ToString()); 
                    // print details
                    if(detailFlag == true)
                    {
                        HashSet<ContextScore> candScoreSet 
                            = GetCandidateScoreSet(candSet, nonSpaceTokenList, 
                            word2VecIm, word2VecOm, word2VecSkipWord, 
                            contextRadius, detailFlag);
                        System.out.println("------ Suggestion List ------");    
                        candScoreSet.stream()
                            .sorted(csc)    // sort it 
                            .limit(limitNo)    // limit the number for print out
                            .map(obj -> obj.ToString())
                            .forEach(str -> System.out.println(str));
                    }
                    // print the prompt
                    System.out.println("- Please input a text, only a spell error allowed (type \"Ctl-d\" to quit) > ");
                }
            }
            catch (Exception e2)
            {
                System.err.println(e2.getMessage());
                returnValue = -1;
            }
        }
        catch (Exception e)
        { 
            System.err.println(e.getMessage());
            returnValue = -1;
        }
        return returnValue;
    }
    
    // test Driver
    public static void main(String[] args)
    {
        boolean detailFlag = false;
        int tarPos = 2;    // the pos of error, not include space
        int tarSize = 1;    // the size of total token of merge words
        int contextRadius = 2; // the context size of either sides (window/2)
        long limitNo = 10;    // limit no  of condidates
        if(args.length == 5)
        {
            String option = args[0];
            if(option.equals("-d") == true)
            {
                detailFlag = true;
            }
            tarPos = Integer.parseInt(args[1]);
            tarSize = Integer.parseInt(args[2]);
            contextRadius = Integer.parseInt(args[3]);
            limitNo = Long.parseLong(args[4]);
        }
        else if(args.length == 4)
        {
            String option = args[0];
            if(option.equals("-d") == true)
            {
                detailFlag = true;
            }
            tarPos = Integer.parseInt(args[1]);
            tarSize = Integer.parseInt(args[2]);
            contextRadius = Integer.parseInt(args[3]);
        }
        else if(args.length == 2)
        {
            String option = args[0];
            if(option.equals("-d") == true)
            {
                detailFlag = true;
            }
            tarSize = Integer.parseInt(args[1]);
        }
        else if(args.length == 1)
        {
            String option = args[0];
            if(option.equals("-d") == true)
            {
                detailFlag = true;
            }
        }
        else if(args.length > 0)
        {
            System.out.println("Usage: java RankByContext <-d> <tarPos> <tarSize> <contextRadius> <limitNo>");
            System.exit(0);
        }
        // test
        int returnValue = RunTest(detailFlag, tarPos, tarSize, contextRadius,
            limitNo);
        System.exit(returnValue);
    }
    // data member
}

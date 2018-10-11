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
* correction by CSpell system.
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
public class RankRealWord1To1ByCSpell
{
    // private constructor
    private RankRealWord1To1ByCSpell()
    {
    }
    // return the best ranked str from candidates using word2Vec score
    // inTokenList, includes space token, is not coreTerm.Lc
    // return the orignal inStr if no candidate has score > 0.0d
    public static String GetTopRankStr(String inStr, HashSet<String> candidates,
        CSpellApi cSpellApi, int tarPos, ArrayList<TokenObj> nonSpaceTokenList,
        boolean debugFlag)
    {
        // init
        WordWcMap wordWcMap = cSpellApi.GetWordWcMap();
        Word2Vec word2VecIm = cSpellApi.GetWord2VecIm();
        Word2Vec word2VecOm = cSpellApi.GetWord2VecOm();
        int contextRadius = cSpellApi.GetRw1To1ContextRadius();
        boolean word2VecSkipWord = cSpellApi.GetWord2VecSkipWord();
        int maxCandNo = cSpellApi.GetCanMaxCandNo();
        double wf1 = cSpellApi.GetOrthoScoreEdDistFac();
        double wf2 = cSpellApi.GetOrthoScorePhoneticFac();
        double wf3 = cSpellApi.GetOrthoScoreOverlapFac();
        int tarSize = 1;    // only for one-to-one, no merge here
        String topRankStr = inStr;
        // use cSpell top candidates
        int topNo = 1;    // top sort
        String inStrLc = inStr.toLowerCase();
        ArrayList<CSpellScore> cSpellScoreList 
            = RankByCSpellRealWord1To1.GetCandidateScoreList(
            inStrLc, candidates, wordWcMap, tarPos, tarSize,
            nonSpaceTokenList, word2VecIm, word2VecOm, word2VecSkipWord,
            contextRadius, wf1, wf2, wf3, debugFlag);
        // Find the correction str and correct
        if(cSpellScoreList.size() > 0)
        {
            // the rw top rank must be in both NC and orthographic
            CSpellScore topScore = cSpellScoreList.get(0);
            double topFScore = topScore.GetFScore().GetScore();    //frequency
            double topEScore = topScore.GetOScore().GetEdScore(); // Edit Dist
            double topPScore = topScore.GetOScore().GetPhoneticScore();    //Phone
            double topOScore = topScore.GetOScore().GetOverlapScore(); //overlap
            ContextScore orgContextScore = null; 
            // check the frequency
            // get the max score of frequency, eidt, phonetic, and overlap
            // the top rank must have all top score for above
            if((topFScore == CSpellScore.GetMaxFScore(cSpellScoreList))
            && (topEScore == CSpellScore.GetMaxEScore(cSpellScoreList))
            && (topPScore == CSpellScore.GetMaxPScore(cSpellScoreList))
            && (topOScore == CSpellScore.GetMaxOScore(cSpellScoreList)))
            {
                ContextScore topContextScore = topScore.GetCScore();
                // 1.1 wordVec for context
                DoubleVec contextVec = Word2VecContext.GetContextVec(tarPos, 
                    tarSize, nonSpaceTokenList, word2VecIm, contextRadius, 
                    word2VecSkipWord, debugFlag);
                // 1.2 wordVec for the original words before one-to-one
                orgContextScore 
                    = new ContextScore(inStr, contextVec, word2VecOm);
                FrequencyScore orgFScore = new FrequencyScore(inStr, wordWcMap);
                // pass the orgContextScore
                if(IsTopCandValid(inStr, orgContextScore, topScore, orgFScore,
                    cSpellApi, debugFlag) == true)
                {
                    // no correction: if score is not good enough for corection
                    topRankStr = topScore.GetCandStr();
                    // debug print for ananlysis
                    /*** 
                    System.out.println("======= cSpellScoreList.size(): "
                        + cSpellScoreList.size() + " ========");
                    System.out.println(inStr 
                        + "," + String.format("%1.8f", orgFScore.GetScore()) 
                        + "," + String.format("%1.8f", orgContextScore.GetScore()));
                    System.out.println(CSpellScore.GetScoreHeader());
                    for(CSpellScore cSpellScore: cSpellScoreList)
                    {
                        System.out.println(cSpellScore.ToString(","));
                    }
                    ***/
                }
            }
            // debug print
            if(debugFlag == true)
            {
                // print focus token (original)
                if(orgContextScore != null)
                {
                    DebugPrint.PrintScore(orgContextScore.ToString(), 
                        debugFlag);
                }
                else
                {
                    DebugPrint.PrintScore("No score for focus (" + inStr + ")",
                        debugFlag);
                }
                // print candidate
                cSpellScoreList.stream()
                    .limit(maxCandNo) // limit the number for print out
                    .map(obj -> obj.ToString())
                    .forEach(str -> DebugPrint.PrintScore(str, debugFlag));
            }
        }
        return topRankStr;
    }
    // Use context and frequency scor eto validate the top ranked candidate
    private static boolean IsTopCandValid(String inStr,
        ContextScore orgContextScore, CSpellScore topCSpellScore, 
        FrequencyScore orgFreqScore, CSpellApi cSpellApi, boolean debugFlag)
    {
        ContextScore topContextScore = topCSpellScore.GetCScore();
        // return false if no topCand found
        if((topContextScore == null)
        || (topContextScore.GetTerm().equals(inStr)))
        {
            return false;
        }
        // Score rules for one-to-one
        double orgScore = orgContextScore.GetScore();
        double topScore = topContextScore.GetScore();
        boolean flag = false;
        double rw1To1CFactor = cSpellApi.GetRankRw1To1CFac();
        // 2.1 no 1-to-1 correction if orgScore is 0.0d, no word2Vec information
        if(orgScore < 0.0d)
        {
            // 2.2a one-to-one if the org score is negative and top score is positive
            if(topScore > 0.0d)
            {
                // further check by ratio, dist, and min. by CScore and FScore
                if(IsTopCandValidByScores(orgContextScore, orgFreqScore, 
                    topContextScore, topCSpellScore, cSpellApi) == true)
                {
                    flag = true;
                }
            }
            // 2.2b 1-to-1 if the org score is negative, top score is better
            else if((topScore < 0.0d)
            && (topScore > orgScore*rw1To1CFactor))
            {
                flag = true;
            }
        }
        else if(orgScore > 0.0d)
        {
            // 2.3a merge if the org score is positive, better 0.01*topScore
            if(topScore*rw1To1CFactor > orgScore)
            {
                flag = true;
            }
        }
        return flag;
    }
    // private methods
    private static boolean IsTopCandValidByScores(
        ContextScore orgContextScore, FrequencyScore orgFreqScore, 
        ContextScore topContextScore, CSpellScore topCSpellScore, 
        CSpellApi cSpellApi)
    {
        // init
        boolean flag = false;
        double rw1To1CandCsFactor = cSpellApi.GetRankRw1To1CandCsFac();
        double rw1To1WordMinCs = cSpellApi.GetRankRw1To1WordMinCs();
        double rw1To1CandMinCs = cSpellApi.GetRankRw1To1CandMinCs();
        double rw1To1CandCsDist = cSpellApi.GetRankRw1To1CandCsDist();
        double rw1To1CandFsFactor = cSpellApi.GetRankRw1To1CandFsFac();
        double rw1To1CandMinFs = cSpellApi.GetRankRw1To1CandMinFs();
        double rw1To1CandFsDist = cSpellApi.GetRankRw1To1CandFsDist();
        double orgScore = orgContextScore.GetScore();
        double topScore = topContextScore.GetScore();
        // another rule for word2Vec on real-word
        // check contect score:
        // 1. the topScore is bigger enough to cover the orgScore
        // 2. the distance is > a value for confidence
        if(((topScore/-orgScore) > rw1To1CandCsFactor)// 609|826|0.6804
        && (orgScore > rw1To1WordMinCs)    // 0.6956 to 0.6952
        && (topScore > rw1To1CandMinCs)    // 
        && ((topScore - orgScore) > rw1To1CandCsDist))//609|796|0.6920
        {
            // check frequency, all positive:
            // 1. cand has better frequency
            // 2. the difference is withint a range
            double orgFScore = orgFreqScore.GetScore();
            double topFScore = topCSpellScore.GetFScore().GetScore();
            if(((topFScore/orgFScore) > rw1To1CandFsFactor)
            && (topFScore > rw1To1CandMinFs)// 609|787|0.6956
            && ((topFScore > orgFScore)     // top has better freq
             || ((orgFScore - topFScore) < rw1To1CandFsDist)))    // within freq range
            {
                flag = true;
            }
        }
        return flag;
    }
    // These are hueristic rule for real-word one-to-one correction
    // check if all one-to-one words in inTerm (candidate)
    // 1. must have wordVec.
    private static boolean Check1To1Words(String inTerm, 
        Word2Vec word2VecOm)
    {
        ArrayList<String> wordList = TermUtil.ToWordList(inTerm);
        boolean flag = true;
        for(String word:wordList)
        {
            if(word2VecOm.HasWordVec(word) == false)
            {
                flag = false;
                break;
            }
        }
        return flag;
    }
    private static boolean CheckRealWord1To1Rules(
        ContextScore topContextScore, String inStr, int tarPos, int tarSize,
        ArrayList<TokenObj> nonSpaceTokenList, Word2Vec word2VecIm, 
        Word2Vec word2VecOm, boolean word2VecSkipWord,
        int contextRadius, double rw1To1Factor, boolean debugFlag)
    {
        // return false if no topCand found
        if((topContextScore == null)
        || (topContextScore.GetTerm().equals(inStr)))
        {
            return false;
        }
        // 1. get the word2Vec score for the org inStr b4 one-to-one
        // 1.1 wordVec for context
        DoubleVec contextVec = Word2VecContext.GetContextVec(tarPos, tarSize,
            nonSpaceTokenList, word2VecIm, contextRadius, 
            word2VecSkipWord, debugFlag);
        // 1.2 wordVec for the original words before one-to-one
        ContextScore orgCs = new ContextScore(inStr, contextVec, word2VecOm);
        DebugPrint.Println("--- Real-Word One-To-One Context Score Detail: ---",
            debugFlag);
        DebugPrint.Println("- Score - orgTerm: " + orgCs.ToString(), debugFlag);
        DebugPrint.Println("- Score - top 1-to-1: " 
            + topContextScore.ToString(), debugFlag);
        DebugPrint.Println("- rw1To1Factor: " + rw1To1Factor, debugFlag);
        // Score rules for one-to-one
        double orgScore = orgCs.GetScore();
        double topScore = topContextScore.GetScore();
        boolean flag = false;
        // 2.1 no one-to-one correction if orgScore is 0.0d, no word2Vec information
        if(orgScore < 0.0d)
        {
            // 2.2a one-to-one if the org score is negative and top score is positive
            if(topScore > 0.0d)
            {
                // another rule for word2Vec on real-word
                if(((topScore - orgScore) > 0.085)
                && (orgScore > -0.085))    // help from 0.6812 to 0.6877
                {
                    flag = true;
                }
            }
            // 2.2b one-to-one if the org score is negative and top score is better
            else if((topScore < 0.0d)
            && (topScore > orgScore*rw1To1Factor))
            {
                flag = true;
            }
        }
        else if(orgScore > 0.0d)
        {
            // 2.3a merge if the org score is positive and better 0.01*topScore
            if(topScore*rw1To1Factor > orgScore)
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
            System.out.println("Usage: java RankRealWord1To1ByCSpell");
            System.exit(0);
        }
        // test
    }
    // data member
}

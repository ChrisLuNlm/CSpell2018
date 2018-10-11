package gov.nih.nlm.nls.cSpell.Ranker;
import java.util.*;
import gov.nih.nlm.nls.cSpell.Util.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
import gov.nih.nlm.nls.cSpell.Api.*;
/**************************************************************************** 
* This class provides detail of scores for different ranking methods.
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
public class ScoreDetailByMode
{
    // private constructor
    private ScoreDetailByMode()
    {
    }
    // TBD, this file should be deleted by moving each method to 
    // the assocaited ranking class
    // public method
    public static void PrintContextScore(HashSet<String> candSet,
        int tarPos, int tarSize, ArrayList<TokenObj> inTextList,
        Word2Vec word2VecIm, Word2Vec word2VecOm, boolean word2VecSkipWord,
        int contextRadius, int maxCandNo, boolean debugFlag)
    {
        if(debugFlag == true)
        {
            ContextScoreComparator<ContextScore> csc
                = new ContextScoreComparator<ContextScore>();
            HashSet<ContextScore> cScoreSet
                = RankByContext.GetCandidateScoreSet(candSet, 
                    tarPos, tarSize, inTextList, word2VecIm, word2VecOm, 
                    word2VecSkipWord, contextRadius, debugFlag);
            cScoreSet.stream()
                .sorted(csc) // sort it
                .limit(maxCandNo) // limit the number for print out
                .map(obj -> obj.ToString())
                .forEach(str -> DebugPrint.PrintCScore(str, debugFlag));
        }
    }
    public static void PrintFrequencyScore(HashSet<String> candSet,
        WordWcMap wordWcMap, int maxCandNo, boolean debugFlag)
    {
        if(debugFlag == true)
        {
            FrequencyScoreComparator<FrequencyScore> fsc
                = new FrequencyScoreComparator<FrequencyScore>();
            HashSet<FrequencyScore> fScoreSet
                = RankByFrequency.GetCandidateScoreSet(candSet, wordWcMap);
            fScoreSet.stream()
                .sorted(fsc) // sort it
                .limit(maxCandNo) // limit the number for print out
                .map(obj -> obj.ToString())
                .forEach(str -> DebugPrint.PrintFScore(str, debugFlag));
        }
    }
    public static void PrintOrthographicScore(String inStr, 
        HashSet<String> candSet, int maxCandNo, double wf1, double wf2,
        double wf3, boolean debugFlag)
    {
        if(debugFlag == true)
        {
            OrthographicScoreComparator<OrthographicScore> osc
                = new OrthographicScoreComparator<OrthographicScore>();
            HashSet<OrthographicScore> oScoreSet
                = RankByOrthographic.GetCandidateScoreSet(inStr, candSet,
                    wf1, wf2, wf3);
            oScoreSet.stream()
                .sorted(osc) // sort it
                .limit(maxCandNo) // limit the number for print out
                .map(obj -> obj.ToString())
                .forEach(str -> DebugPrint.PrintOScore(str, debugFlag));
        }
    }
    public static void PrintNoisyChannelScore(String inStr, 
        HashSet<String> candSet, WordWcMap wordWcMap, int maxCandNo, 
        double wf1, double wf2, double wf3, boolean debugFlag)
    {
        if(debugFlag == true)
        {
            NoisyChannelScoreComparator<NoisyChannelScore> ncsc
                = new NoisyChannelScoreComparator<NoisyChannelScore>();
            HashSet<NoisyChannelScore> ncScoreSet
                = RankByNoisyChannel.GetCandidateScoreSet(inStr, candSet, 
                wordWcMap, wf1, wf2, wf3);
            ncScoreSet.stream()
                .sorted(ncsc) // sort it
                .limit(maxCandNo) // limit the number for print out
                .map(obj -> obj.ToString())
                .forEach(str -> DebugPrint.PrintNScore(str, debugFlag));
        }
    }
    // this detail does not print how cSpell really fidn the top rank
    // it is sorted by CSpell score
    // CSpell use the cSpell score + context and frequency to find the top
    public static void PrintCSpellScore(String inStr, HashSet<String> candSet, 
        WordWcMap wordWcMap, int maxCandNo, int tarPos, int tarSize, 
        ArrayList<TokenObj> nonSpaceTokenList, Word2Vec word2VecIm, 
        Word2Vec word2VecOm, boolean word2VecSkipWord, int contextRadius, 
        double wf1, double wf2, double wf3, boolean debugFlag)
    {
        if(debugFlag == true)
        {
            // NW 1To1
            CSpellScoreNw1To1Comparator<CSpellScore> csc
                = new CSpellScoreNw1To1Comparator<CSpellScore>();
            HashSet<CSpellScore> cScoreSet
                = RankByCSpellNonWord.GetCandidateScoreSet(inStr, candSet, 
                wordWcMap, tarPos, tarSize, nonSpaceTokenList, word2VecIm, 
                word2VecOm, word2VecSkipWord, contextRadius, 
                wf1, wf2, wf3, debugFlag);
            cScoreSet.stream()
                .sorted(csc) // sort it
                .limit(maxCandNo) // limit the number for print out
                .map(obj -> obj.ToString())
                .forEach(str -> DebugPrint.PrintScore(str, debugFlag));
        }
    }
    // private method
    // data member
}

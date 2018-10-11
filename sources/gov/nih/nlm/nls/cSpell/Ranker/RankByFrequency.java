package gov.nih.nlm.nls.cSpell.Ranker;
import java.io.*;
import java.util.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.Dictionary.*;
import gov.nih.nlm.nls.cSpell.Candidates.*;
import gov.nih.nlm.nls.cSpell.Api.*;
/*****************************************************************************
* This class ranks and finds the best ranked candidates by FrequencyScore.
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
public class RankByFrequency
{
    // private constructor
    private RankByFrequency()
    {
    }
    // return candidate str list sorted by score, higher first
    public static ArrayList<String> GetCandidateStrList(
        HashSet<String> candidates, WordWcMap wordWcMap)
    {
        ArrayList<FrequencyScore> candScoreList 
            = GetCandidateScoreList(candidates, wordWcMap);
        ArrayList<String> candStrList = new ArrayList<String>();    
        for(FrequencyScore fs:candScoreList)
        {
            candStrList.add(fs.GetWord());
        }
        return candStrList;
    }
    // return candidate scoreObj list sorted by score, higher first
    public static ArrayList<FrequencyScore> GetCandidateScoreList(
        HashSet<String> candidates, WordWcMap wordWcMap)
    {
        HashSet<FrequencyScore> candScoreSet 
            = GetCandidateScoreSet(candidates, wordWcMap);
        ArrayList<FrequencyScore> candScoreList 
            = new ArrayList<FrequencyScore>(candScoreSet);
        // sort the list, higher fo first
        FrequencyScoreComparator<FrequencyScore> fsc 
            = new FrequencyScoreComparator<FrequencyScore>();
        Collections.sort(candScoreList, fsc);    
        return candScoreList;
    }
    // return candidate set with frequency score
    // wordWcMap is the word|WC map to calculate the score
    // Not sorted, because it is a set
    public static HashSet<FrequencyScore> GetCandidateScoreSet(
        HashSet<String> candidates, WordWcMap wordWcMap)
    {
        HashSet<FrequencyScore> candScoreSet 
            = new HashSet<FrequencyScore>();
        // find scores for all candidates
        for(String cand:candidates)
        {
            FrequencyScore fs = new FrequencyScore(cand, wordWcMap); 
            candScoreSet.add(fs);
        }
        return candScoreSet;
    }
    // return the best ranked str from candidates using frequency score
    public static String GetTopRankStr(HashSet<String> candidates,
        WordWcMap wordWcMap)
    {
        String topRankStr = new String();
        ArrayList<FrequencyScore> candScoreList
            = GetCandidateScoreList(candidates, wordWcMap);
        if(candScoreList.size() > 0)
        {
            topRankStr = candScoreList.get(0).GetWord();
        }
        return topRankStr;
    }
    // return the best ranked str from candidates using frequency score
    public static String GetTopRankStrByScore(
        HashSet<String> candidates, WordWcMap wordWcMap)
    {
        String topRankStr = new String();
        double maxScore = 0.0;
        for(String cand:candidates)
        {
            FrequencyScore fs = new FrequencyScore(cand, wordWcMap);
            double score = fs.GetScore();
            if(score > maxScore)
            {
                topRankStr = cand;
                maxScore = score;
            }
        }
        return topRankStr;
    }
    // private methods
    private static int RunTest(boolean detailFlag, long limitNo)
    {
        // init dic
        String configFile = "../data/Config/cSpell.properties";
        CSpellApi cSpellApi = new CSpellApi(configFile);
        cSpellApi.SetRankMode(CSpellApi.RANK_MODE_FREQUENCY);
        WordWcMap wordWcMap = cSpellApi.GetWordWcMap();
        // provide cmdLine interface
        int returnValue = 0;
        FrequencyScoreComparator<FrequencyScore> fsc 
            = new FrequencyScoreComparator<FrequencyScore>();
        try
        {
            BufferedReader stdInput 
                = new BufferedReader(new InputStreamReader(System.in));
            try
            {
                String inText = null;
                System.out.println("- Please input a text (type \"Ctl-d\" to quit) > ");
                while((inText = stdInput.readLine()) != null)
                {
                    // ---------------------------------
                    // Get spell correction on the input
                    // ---------------------------------
                    // get all possible candidates
                    HashSet<String> candSet 
                        = NonWord1To1Candidates.GetCandidates(
                        inText, cSpellApi);
                    System.out.println("-- canSet.size(): " + candSet.size());
                    // get final suggestion
                    String topRankStr = GetTopRankStr(candSet, wordWcMap);
                    System.out.println("- top rank str: " + topRankStr); 
                    // print details
                    if(detailFlag == true)
                    {
                        HashSet<FrequencyScore> candScoreSet 
                            = GetCandidateScoreSet(candSet, wordWcMap);
                        System.out.println("------ Suggestion List ------");    
                        candScoreSet.stream()
                            .sorted(fsc)    // sort it 
                            .limit(limitNo)    // limit the number for print out
                            .map(obj -> obj.ToString())
                            .forEach(str -> System.out.println(str));
                    }
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
        long limitNo = 10;
        if(args.length == 2)
        {
            String option = args[0];
            if(option.equals("-d") == true)
            {
                detailFlag = true;
            }
            limitNo = Long.parseLong(args[1]);
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
            System.out.println("Usage: java RankByFrequency <-d> <limitNo>");
            System.exit(0);
        }
        // test
        int returnValue = RunTest(detailFlag, limitNo);
        System.exit(returnValue);
    }
    // data member
}

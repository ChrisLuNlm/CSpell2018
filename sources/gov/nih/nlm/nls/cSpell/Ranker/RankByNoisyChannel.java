package gov.nih.nlm.nls.cSpell.Ranker;
import java.io.*;
import java.util.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.Dictionary.*;
import gov.nih.nlm.nls.cSpell.Candidates.*;
import gov.nih.nlm.nls.cSpell.Api.*;
/*****************************************************************************
* This class ranks and finds the best ranked candidates by Noisy Channel score.
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
public class RankByNoisyChannel
{
    // private constructor
    private RankByNoisyChannel()
    {
    }
    // return candidate str list sorted by wordNo score, higher first
    public static ArrayList<String> GetCandidateStrList(String wordStr, 
        HashSet<String> candidates, WordWcMap wordWcMap, double wf1,
        double wf2, double wf3)
    {
        ArrayList<NoisyChannelScore> candScoreList = GetCandidateScoreList(
            wordStr, candidates, wordWcMap, wf1, wf2, wf3);
        ArrayList<String> candStrList = new ArrayList<String>();    
        for(NoisyChannelScore ncs:candScoreList)
        {
            candStrList.add(ncs.GetCandStr());
        }
        return candStrList;
    }
    // return candidate scoreObj list sorted by score, higher first
    public static ArrayList<NoisyChannelScore> GetCandidateScoreList(
        String wordStr, HashSet<String> candidates, WordWcMap wordWcMap,
        double wf1, double wf2, double wf3)
    {
        HashSet<NoisyChannelScore> candScoreSet = GetCandidateScoreSet(
            wordStr, candidates, wordWcMap, wf1, wf2, wf3);
        ArrayList<NoisyChannelScore> candScoreList 
            = new ArrayList<NoisyChannelScore>(candScoreSet);
        // sort the set to list
        NoisyChannelScoreComparator<NoisyChannelScore> ncsc 
            = new NoisyChannelScoreComparator<NoisyChannelScore>();
        Collections.sort(candScoreList, ncsc);    
        return candScoreList;
    }
    // return candidate set with noisy channel score
    // wordStr is the srcTxt used to calculate the score between it and cand
    public static HashSet<NoisyChannelScore> GetCandidateScoreSet(
        String wordStr, HashSet<String> candidates, WordWcMap wordWcMap,
        double wf1, double wf2, double wf3)
    {
        HashSet<NoisyChannelScore> candScoreSet 
            = new HashSet<NoisyChannelScore>();
        for(String cand:candidates)
        {
            NoisyChannelScore ncs = new NoisyChannelScore(wordStr, cand,
                wordWcMap, wf1, wf2, wf3); 
            candScoreSet.add(ncs);
        }
        return candScoreSet;
    }
    // return the best ranked str from candidates using noisy channel score
    public static String GetTopRankStr(String inStr, HashSet<String> candidates,
        WordWcMap wordWcMap, double wf1, double wf2, double wf3)
    {
        String topRankStr = inStr;
        // get the sorted list
        ArrayList<NoisyChannelScore> candScoreList = GetCandidateScoreList(
            inStr, candidates, wordWcMap, wf1, wf2, wf3);
        if(candScoreList.size() > 0)
        {
            topRankStr = candScoreList.get(0).GetCandStr();
        }
        return topRankStr;
    }
    // private methods
    private static int RunTest(boolean detailFlag, long limitNo)
    {
        // init dic
        String configFile = "../data/Config/cSpell.properties";
        CSpellApi cSpellApi = new CSpellApi(configFile);
        WordWcMap wordWcMap = cSpellApi.GetWordWcMap();
        double wf1 = cSpellApi.GetOrthoScoreEdDistFac();
        double wf2 = cSpellApi.GetOrthoScorePhoneticFac();
        double wf3 = cSpellApi.GetOrthoScoreOverlapFac();
        cSpellApi.SetRankMode(CSpellApi.RANK_MODE_NOISY_CHANNEL);
        // provide cmdLine interface
        int returnValue = 0;
        NoisyChannelScoreComparator<NoisyChannelScore> ncsc 
            = new NoisyChannelScoreComparator<NoisyChannelScore>();
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
                    String topRankStr = GetTopRankStr(inText, candSet, 
                        wordWcMap, wf1, wf2, wf3);
                    System.out.println("- top tank str: " + topRankStr); 
                    // print details
                    if(detailFlag == true)
                    {
                        HashSet<NoisyChannelScore> candScoreSet 
                            = GetCandidateScoreSet(inText, candSet, wordWcMap,
                                wf1, wf2, wf3);
                        System.out.println("------ Suggestion List ------");    
                        candScoreSet.stream()
                            .sorted(ncsc)    // sort it 
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
            System.out.println("Usage: java RankByNoisyChannel <-d> <limitNo>");
            System.exit(0);
        }
        // test
        int returnValue = RunTest(detailFlag, limitNo);
        System.exit(returnValue);
    }
    // data member
}

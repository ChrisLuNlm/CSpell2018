package gov.nih.nlm.nls.cSpell.Ranker;
import java.util.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
/*****************************************************************************
* This class is the java object of frequency score by word count.
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
public class WordCountScore
{
    // private constructor
    public WordCountScore()
    {
        // calculate score
    }
    // public method
    public static double GetScore(String inWord, WordWcMap wordWcMap)
    {
        //double score = GetScoreByChurch(inWord, wordWcMap);
        //double score = GetScoreByCrowell(inWord, wordWcMap);
        //double score = GetScoreByPeter(inWord, wordWcMap);
        //double score = GetWc(inWord, wordWcMap);
        // Halil 
        //double score = GetUnigramFreqScore(inWord, wordWcMap);
        //double score = GetScoreDev1(inWord, wordWcMap);
        
        // default - multiword
        double score = GetScoreDev2(inWord, wordWcMap); 
        // Baseline Orginal code
        //double score = GetCorpusFreqScore(inWord, wordWcMap);

        // Not used: double score = GetAdjustScoreMin(inWord, wordWcMap);
        // Not used: double score = GetAdjustScoreAvg(inWord, wordWcMap);
        return score;
    }
    // single word score, multiwords is 0.0
    private static double GetScoreDev1(String inWord, WordWcMap wordWcMap)
    {
        long maxWc = wordWcMap.GetMaxWc();
        double wc = GetWc(inWord, wordWcMap);
        double score = wc / (1.0 * maxWc);
        return score;
    }
    // include multiwords, multiwords = avg. score
    private static double GetScoreDev2(String inWord, WordWcMap wordWcMap)
    {
        // check multiword case for split
        boolean normFlag = false;    // don't use punctuation for determiner
        ArrayList<String> wordList = TermUtil.ToWordList(inWord, normFlag);
        double score = 0.0;
        double totalScore = 0.0;
        int totalWords = wordList.size();
        //double maxWc = GetAdjustedWc(wordWcMap.GetMaxWc());
        // use the average score for the multiwords
        for(String word:wordList)
        {
            //double curScore = GetScoreByChurch(word, wordWcMap);
            //double curScore = GetScoreByCrowell(word, wordWcMap);
            //double curScore = GetScoreByPeter(word, wordWcMap);
            //double curScore = GetUnigramFreqScore(word, wordWcMap);
            //double curScore = GetWc(word, wordWcMap);
            double curScore = GetScoreDev1(word, wordWcMap);
            totalScore += curScore;
        }
        if(totalScore > 0.0)
        {
            score = totalScore / totalWords;
        }
        return score;
    }
    // 1990, 1191 paper from Church
    private static double GetScoreByCrowell(String inWord, WordWcMap wordWcMap)
    {
        double wc = GetWc(inWord, wordWcMap);
        double score = 0.5;    // assign to 0.5 if word is not in the corpus
        if(wc != 0.0)
        {
            score = (1.0 + Math.log(wc));
        }
        return score;
    }
    private static double GetScoreByChurch(String inWord, WordWcMap wordWcMap)
    {
        long totalWc = wordWcMap.GetTotalWc();
        double wc = GetWc(inWord, wordWcMap);
        double score = (1.0 + wc) / (1.0 * totalWc);
        return score;
    }
    private static double GetScoreByPeter(String inWord, WordWcMap wordWcMap)
    {
        long totalWc = wordWcMap.GetTotalWc();
        double wc = GetWc(inWord, wordWcMap);
        double score = wc / (1.0 * totalWc);
        return score;
    }
    public static double GetAdjustScoreMin(String inWord, WordWcMap wordWcMap)
    {
        // check multiword case for split
        boolean normFlag = false;    // don't use punctuation for determiner
        ArrayList<String> wordList = TermUtil.ToWordList(inWord, normFlag);
        double score = 0.0;
        double totalScore = 0.0;
        int totalWords = wordList.size();
        double maxWc = GetAdjustedWc(wordWcMap.GetMaxWc());
        // use the average score for the multiwords
        double minScore = Integer.MAX_VALUE;
        for(String word:wordList)
        {
            double curScore = GetWordScore(word, maxWc, wordWcMap);
            minScore = (curScore < minScore ? curScore:minScore);
        }
        if(minScore < Integer.MAX_VALUE)
        {
            score = minScore;
        }
        return score;
    }
    // get socre for single word and multiwords (for split cases)
    // 1). multiword: score = avg. score of allwords
    // 2). single word: score =  log(adjust WC) / log (adjust Max. WC).
    public static double GetAdjustScoreAvg(String inWord, WordWcMap wordWcMap)
    {
        // check multiword case for split
        boolean normFlag = false;    // don't use punctuation for determiner
        ArrayList<String> wordList = TermUtil.ToWordList(inWord, normFlag);
        double score = 0.0;
        double totalScore = 0.0;
        long totalWords = wordList.size();
        double maxWc = GetAdjustedWc(wordWcMap.GetMaxWc());
        // use the average score for the multiwords
        for(String word:wordList)
        {
            totalScore += GetWordScore(word, maxWc, wordWcMap);
        }
        if(totalWords > 0)
        {
            score = totalScore / totalWords;
        }
        return score;
    }
    // score = WC(word)/total_word_cout
    // score range is between 0.0 ~ 1.0
    // should be the same as GetCorpusFreqScore
    // Get score for a singel word
    private static double GetWordScore(String inWord, double maxWc,
        WordWcMap wordWcMap)
    {
        double wc = GetAdjustedWc(inWord, wordWcMap);
        double score = (Math.log(wc) / Math.log(maxWc));
        return score;
    }
    // should be the same as GetUnigramFreqScore
    // score range is between 0.0 ~ 1.0
    // not used because it is no good
    private static double GetWordScore2(String inWord, double maxWc,
        WordWcMap wordWcMap)
    {
        double wc = 1.0d * GetWc(inWord, wordWcMap);
        double totalWc = 1.0d * wordWcMap.GetTotalWc();
        double score = (Math.log(wc/totalWc) / Math.log(maxWc/totalWc));
        return score;
    }
    // add Adjust WC = CONS + COEF*WC 
    // This is used to calcualted the score
    // Set 1.0 so log will not return Nan for words not exist 
    // Set 10.0 * WC so that word in the corpus has higher rank 
    public static double GetAdjustedWcOld(long wc)
    {
        double ajustWc = CONS + COEF*Math.log(wc);
        return ajustWc;
    }
    public static double GetAdjustedWc(long wc)
    {
        double ajustWc = CONS/2.0;
        if(wc != 0.0)
        {
            ajustWc = CONS + COEF*(Math.log(wc)/Math.log(10));
        }
        return ajustWc;
    }
    public static double GetAdjustedWc(String inWord, WordWcMap wordWcMap)
    {
        int wc = GetWc(inWord, wordWcMap);
        return GetAdjustedWc(wc);
    }
    public static double GetWordPossOverTotalWc(String inWord, 
        WordWcMap wordWcMap)
    {
        boolean caseFlag = false;
        return GetWordPossOverTotalWc(inWord, wordWcMap, caseFlag);
    }
    public static double GetWordPossOverMaxWc(String inWord, 
        WordWcMap wordWcMap)
    {
        boolean caseFlag = false;
        return GetWordPossOverMaxWc(inWord, wordWcMap, caseFlag);
    }
    // org code from baseline, TBM
    public static double GetCorpusFreqScore(String inWord, WordWcMap wordWcMap)
    {
        // get the wc
        HashMap<String, Integer> wWcMap = wordWcMap.GetWordWcMap();
        int freq = (wWcMap.containsKey(inWord)?wWcMap.get(inWord):0);
        // check if inWord is a multiword
        List<String> spls = Arrays.asList(inWord.split("[ ]"));
        boolean isSplit = spls.size() >=2;
        if(isSplit == false)
        {
            // check possessive, this is not right:
            // all XXX's will result in same scsore is XXX is bigger than 's
            if(inWord.endsWith("'s"))
            {
                spls = new ArrayList<String>();
                spls.add(inWord.substring(0, inWord.length()-2));
                spls.add("'s");
                isSplit = true;
            }
        }
        else
        {
            //System.out.println("---- split: [" + inWord + "]"); 
        }
        // use the min. wc of split word in the multiword's case
        if (freq == 0 && isSplit)
        {
            int min = Integer.MAX_VALUE;
            for(String spl: spls)
            {
                // check if it is an empty string
                String rpStr = spl.replaceAll("[A-Za-z]", "");
                //System.out.println("- split: rpStr: [" + spl + "|" + rpStr + "]");
                if (spl.replaceAll("[A-Za-z]", "").equals(spl)) continue;
                int splFreq = (wWcMap.containsKey(spl) ? wWcMap.get(spl) : 0);
                //System.out.println("Corpus count:" + spl + "|" + wWcMap.get(spl) + "|" + splFreq);
                // use the min. freq of each word as the freq of the multiwords
                if (splFreq >= 0 && splFreq < min)
                {
                    min = splFreq;
                }
            }
            // use the min. freq of the split words as whole word?
            freq = min;
        }
        if (freq == 0) return 0.0;
        long maxWc = wordWcMap.GetMaxWc();
        double score = (Math.log(freq) / Math.log(maxWc));
        return score;
    }
    // org code from baseline, TBM, From Ensemble
    public static double GetUnigramFreqScore(String inWord, WordWcMap wordWcMap)
    {
        HashMap<String, Integer> wWcMap = wordWcMap.GetWordWcMap();
        int freq = (wWcMap.containsKey(inWord)?wWcMap.get(inWord):0);
        List<String> spls = Arrays.asList(inWord.split("[ ]"));
        boolean isSplit = spls.size() >=2;
        if (isSplit == false)
        {
            if(inWord.endsWith("'s"))
            {
                spls = new ArrayList<String>();
                spls.add(inWord.substring(0, inWord.length()-2));
                spls.add("'s");
                isSplit = true;
            }
        }
        // use the min. wc of split word in the multiword's case
        if (freq == 0 && isSplit)
        {
            int min = Integer.MAX_VALUE;
            for(String spl: spls)
            {
                if (spl.replaceAll("[A-Za-z]", "").equals(spl)) continue;
                int splFreq = (wWcMap.containsKey(spl) ? wWcMap.get(spl) : 0);
                //System.out.println("Corpus count:" + spl + "|" + wWcMap.get(spl) + "|" + splFreq);
                if (splFreq >= 0 && splFreq < min) 
                {
                    min = splFreq;
                }
            }
            freq = min;
        }
        if (freq == 0) return 0.0;    // to avoid infinity
        long maxWc = wordWcMap.GetMaxWc();
        long totalWc = wordWcMap.GetTotalWc();
        double score = (Math.log(1.0*freq/totalWc) / Math.log(1.0*maxWc/totalWc));
        return score;
    }
    // private method
    // possibility = WC(w0rd)/max_word_cout
    private static double GetWordPossOverMaxWc(String inWord, 
        WordWcMap wordWcMap, boolean caseFlag)
    {
        double wc = 1.0d * GetWc(inWord, wordWcMap, caseFlag);
        double maxWc = 1.0d * wordWcMap.GetMaxWc();
        double score = wc/maxWc;
        return score;
    }
    // possibility = WC(w0rd)/total_word_cout
    private static double GetWordPossOverTotalWc(String inWord, 
        WordWcMap wordWcMap, boolean caseFlag)
    {
        double wc = 1.0d * GetWc(inWord, wordWcMap, caseFlag);
        double totalWc = 1.0d * wordWcMap.GetTotalWc();
        double score = wc/totalWc;
        return score;
    }
    // convert inWord to lowercase, key in word Frequency are all lowercase
    public static int GetWc(String inWord, WordWcMap wordWcMap)
    {
        boolean caseFlag = false;    // lowercase, case insensitive
        return GetWc(inWord, wordWcMap, caseFlag);
    }
    private static int GetWc(String inWord, WordWcMap wordWcMap,
        boolean caseFlag)
    {
        String inWordLc = inWord;
        // ignore case
        if(caseFlag == false)
        {
            inWordLc = inWord.toLowerCase();
        }
        // the key of wWcMap are lowercased in the Beta version
        HashMap<String, Integer> wWcMap = wordWcMap.GetWordWcMap();
        int wc = 0;
        if(wWcMap.get(inWordLc) != null)
        {
            wc = wWcMap.get(inWordLc).intValue();
        }
        return wc;
    }
    private static void Test(String inWord, WordWcMap wordWcMap)
    {
        System.out.println(inWord + "|" 
            + String.format("%1.4f", GetScore(inWord, wordWcMap)) + "|"
            + String.format("%1.4f", GetAdjustedWc(inWord, wordWcMap)) + "|"
            + GetWc(inWord, wordWcMap) + "|"
            + Math.log(GetWc(inWord, wordWcMap)) + "|"
            + (Math.log(GetWc(inWord, wordWcMap)/Math.log(10))) + "|"
            //+ String.format("%1.4f", GetAdjustScoreMin(inWord, wordWcMap)) + "|"
            //+ String.format("%1.4f", GetAdjustScoreAvg(inWord, wordWcMap)) + "|"
            + String.format("%1.4f", GetWordPossOverMaxWc(inWord, wordWcMap)));
    }
    private static void Tests(WordWcMap wordWcMap)
    {
        ArrayList<String> testStrList = new ArrayList<String>();
        testStrList.add("the");        // first one in the corpus
        testStrList.add("&eacute;vy");    // last one in the corpus
        testStrList.add("xxxx");    // not in the corpus
        testStrList.add("spondylitis");        // first one in the corpus
        testStrList.add("spondyl");        // first one in the corpus
        testStrList.add("its");        // first one in the corpus
        testStrList.add("if");        // first one in the corpus
        testStrList.add("you");        // first one in the corpus
        testStrList.add("doctor");
        testStrList.add("Doctor"); // Test Case
        testStrList.add("doctor[123]");
        testStrList.add("'s");
        testStrList.add("container");
        testStrList.add("diagnose");
        testStrList.add("deionized");
        testStrList.add("diabetic");
        testStrList.add("diabetics");
        testStrList.add("doctor's");    // posssive
        testStrList.add("heart's");
        testStrList.add("if you");    // multiwords
        testStrList.add("the doctor");    // multiwords
        testStrList.add("Not exist");
        testStrList.add("brokenribscantsleepatnight");
        testStrList.add("broken");
        testStrList.add("rib");
        testStrList.add("ribs");
        testStrList.add("cant");
        testStrList.add("cants");
        testStrList.add("scant");
        testStrList.add("scants");
        testStrList.add("sleep");
        testStrList.add("leep");
        testStrList.add("lee");
        testStrList.add("pat");
        testStrList.add("at");
        testStrList.add("night");
        testStrList.add("broken ribs cants leep at night");
        testStrList.add("broken ribs cant sleep at night");
        testStrList.add("broken rib scants leep at night");
        testStrList.add("broken rib scants lee pat night");
        testStrList.add("broken rib scant sleep at night");
        testStrList.add("friend share");
        testStrList.add("assistance");
        testStrList.add("baraclude and");
        testStrList.add("xifaxan as");
        testStrList.add("pamphlets");
        testStrList.add("damage");
        testStrList.add("withdrawal");
        testStrList.add("tachycardia");
        testStrList.add("always");
        testStrList.add("itching");
        testStrList.add("philtrum");
        testStrList.add("achalasia");
        testStrList.add("swollen");
        testStrList.add("of course");
        testStrList.add("antenatal");
        testStrList.add("microsomia");
        testStrList.add("migraine");
        testStrList.add("hemorrhage");
        System.out.println("=================================================");
        System.out.println("Word|Score|Adjust|Wc|Wc/max");
        System.out.println("=================================================");
        for(String testStr:testStrList)
        {
            Test(testStr, wordWcMap);
        }
    }
    // test Driver
    public static void main(String[] args)
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java WordCountScore");
            System.exit(0);
        }
        // test
        String inFile = "../data/Frequency/wcWord.data";
        boolean verboseFlag = true;
        WordWcMap wordWcMap = new WordWcMap(inFile, verboseFlag);
        Tests(wordWcMap);
    }
    // data member
    private static final double CONS = 0.0d; 
    private static final double COEF = 1.0d; 
}

package gov.nih.nlm.nls.cSpell.Candidates;
import java.util.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
import gov.nih.nlm.nls.cSpell.Ranker.*;
import gov.nih.nlm.nls.cSpell.Dictionary.*;
import gov.nih.nlm.nls.cSpell.Api.*;
/*****************************************************************************
* This class generates real-word 1To1 candidates. 
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
public class RealWord1To1Candidates
{
    // private constructor
    private RealWord1To1Candidates()
    {
    }
    // TBD... this is the bottle neck because so many real-words call this
    // needs to speed up
    //
    // public method
    // Get candidates from dictionary by Edit-distance:
    // 1. get all possible combinations from insert, remove, replace, switch
    //    chars. However, it does not include space (so no split).
    // 2. check if the combination is in dictionary
    public static HashSet<String> GetCandidates(String inWord, 
        CSpellApi cSpellApi)
    {
        int maxLength = cSpellApi.GetCanRw1To1WordMaxLength();
        String inWordLc = inWord.toLowerCase();
        // 1. get it from the memoery to speed up running time
        HashSet<String> candidates = candMap_.get(inWordLc);
        // 2. generate candidates on the fly, find all possibile candidates
        if(candidates == null)
        {
            // 2.1. get all possible candidates 
            // bottle neck for real-word: 7 min.
            HashSet<String> candidatesByEd 
                = CandidatesUtil1To1.GetCandidatesByEd(inWord, maxLength);
            // filter out those are not valid words
            candidates = new HashSet<String>();
            // 2.2. bottle neck for real-word: 2 min.
            for(String candByEd:candidatesByEd)
            {
                // check if valid one-to-one candidate word
                if(IsValid1To1Cand(inWordLc, candByEd, cSpellApi) == true)
                {
                    candidates.add(candByEd);
                }
            }
            // update candMap_ and save to memory to speed up runing time 
            // TBD, need to set the maxKeyNo for candMap_ to prevent 
            // max. key size need to be <= 2**31-1 = 2,147,483,647
            // slow performance and crash could happen if too many keys 
            if(candMap_.containsKey(inWordLc) == false)
            {
                candMap_.put(inWordLc, candidates);
                // warning msg< suggest value: < 1,500,000,000 for performance
                int maxHashKeySize = cSpellApi.GetCanRw1To1CandMaxKeySize();
                int hashKeySize = candMap_.keySet().size();
                if(hashKeySize > maxHashKeySize)
                {
                    if((hashKeySize%100) == 0)
                    {
                        System.err.println("** WARNING@RealWord1To1Candidates.GetCandidates: the size of key in RW-1To1-Cand-HashMap is too big (" 
                        + hashKeySize + " > " + maxHashKeySize
                        + "). Please rerun the cSpell and increase the max. hash key size in the cSpell config (must < 2,147,483,647).");
                    }
                }
            }
        }
        return candidates;
    }
    // real-word candidate has more restriction than non-word
    // TBD, need to organize the code ...
    // the check should be done in the ranking
    // Core process for real-word candidates
    private static boolean IsValid1To1Cand(String inWord, String cand,
        CSpellApi cSpellApi)
    {
        RootDictionary suggestDic = cSpellApi.GetSuggestDic();
        Word2Vec word2VecOm = cSpellApi.GetWord2VecOm();
        WordWcMap wordWcMap = cSpellApi.GetWordWcMap();
        // real-word, check phonetic and suggDic
        // 1. check suggDic
        // 1.1 edDist <= 1
        // 1.2 edDist <= 2 && phonetic dist <= 1
        // 2. check if inflections, not a candidate real-word, not correct
        boolean flag = false;
        int rw1To1CandMinWc = cSpellApi.GetCanRw1To1CandMinWc();
        int rw1To1CandMinLength = cSpellApi.GetCanRw1To1CandMinLength();
        String inWordLc = inWord.toLowerCase();
        int inWordLen = inWordLc.length();
        int candLen = cand.length();
        int lenDiff = inWordLen - candLen;
        // 1. check suggDic and inflVars
        if((suggestDic.IsDicWord(cand) == true)    //  in suggDic
        && (word2VecOm.HasWordVec(cand) == true)    // must have w2v
        && (candLen >= rw1To1CandMinLength)    // candidate length must >= 2
        && (WordCountScore.GetWc(cand, wordWcMap) >= rw1To1CandMinWc)
        && (InflVarsUtil.IsInflectionVar(inWordLc, cand) == false))    // not inflVars
        //&& ((lenDiff <= 1) && (lenDiff >= -1))) // length diff <= 1
        {
            // more restriction for real-word candidates
            int pmDist = Metaphone2.GetDistance(inWordLc, cand);
            int prDist = RefinedSoundex.GetDistance(inWordLc, cand);
            int leadDist = GetLeadCharDist(inWordLc, cand);
            int endDist = GetEndCharDist(inWordLc, cand);
            int lengthDist = GetLengthDist(inWordLc, cand);
            int totalDist1 = leadDist + endDist + lengthDist + pmDist + prDist;
            int editDist = EditDistance.GetDistanceForRealWord(inWordLc, cand);
            int totalDist2 = editDist + pmDist + prDist;
            // if they sound the same
            if((pmDist == 0) && (prDist == 0))
            {
                flag = true;
            }
            // if they sound similar and orthographic is also similar
            // fixed from empierical test, not configuable
            else if((totalDist1 < 3) 
            && (totalDist2 < 4) 
            //&& (pmDist == 0))
            && (pmDist*prDist == 0))
            {
                flag = true;
            }
        }
        return flag;
    }
    
    private static int GetLengthDist(String str1, String str2)
    {
        int len1 = str1.length();
        int len2 = str2.length();
        int lengthDist = ((len1 >= len2)?(len1-len2):(len2-len1));
        return lengthDist;
    }
    private static int GetEndCharDist(String str1, String str2)
    {
        int index1 = str1.length()-1;
        int index2 = str2.length()-1;
        int dist = ((str1.charAt(index1) == str2.charAt(index2))?0:1);
        return dist;
    }
    private static int GetLeadCharDist(String str1, String str2)
    {
        int dist = ((str1.charAt(0) == str2.charAt(0))?0:1);
        return dist;
    }
    // 90% of real-word error have same begin characters
    private static boolean HasSameBeginChar(String str1, String str2)
    {
        boolean flag = (str1.charAt(0) == str2.charAt(0));
        return flag;
    }
    // simplified way to check if two string are inlfectional variants
    // this method is simplied, assuming one of the input is base
    private static boolean IsInflectionVarTbd(String str1, String str2)
    {
        boolean flag = false;
        // 1. to assign base string by comparing length
        int len1 = str1.length();
        int len2 = str2.length();
        String baseStr = str1;
        String inflStr = str2;
        // same length, not inflectional vars, exclude irreg, such as see|saw
        if(len1 == len2)
        {
            return false;
        }
        else if(len1 > len2)    // assume the short string is the base
        {
            baseStr = str2;
            inflStr = str1;
        }
        // check the inflections
        HashSet<String> inflSet = InflVarsUtil.GetInflVars(baseStr);
        flag = inflSet.contains(inflStr);
        return flag;
    }
    private static void TestTpStr(String str1, String str2, CSpellApi cSpellApi)
    {
        HashSet<String> candSet = GetCandidates(str1.toLowerCase(), cSpellApi);
        boolean flag = candSet.contains(str2); 
        if(flag == true)
        {
            totalTpNo_++;
        }
        totalTpStrNo_++;
        System.out.println(flag + "|" + totalTpNo_ + "|" + totalTpStrNo_ 
            + "|" + str1 + "|" + str2 
            + "|" + EditDistance.GetDistanceForRealWord(str1, str2) 
            + "|" + RefinedSoundex.GetDistanceDetailStr(str1, str2)
            + "|" + Metaphone2.GetDistanceDetailStr(str1, str2, 10));
    }
    private static void TestFpStr(String str1, String str2, CSpellApi cSpellApi)
    {
        HashSet<String> candSet = GetCandidates(str1.toLowerCase(), cSpellApi);
        boolean flag = candSet.contains(str2); 
        if(flag == true)
        {
            totalFpNo_++;
        }
        totalFpStrNo_++;
        System.out.println(flag + "|" + totalFpNo_ + "|" + totalFpStrNo_ 
            + "|" + str1 + "|" + str2 
            + "|" + EditDistance.GetDistanceForRealWord(str1, str2) 
            + "|" + RefinedSoundex.GetDistanceDetailStr(str1, str2)
            + "|" + Metaphone2.GetDistanceDetailStr(str1, str2, 10));
    }
    private static void TestTestSet(CSpellApi cSpellApi) 
    {
        // get candidates with dictionary
        // TP
        System.out.println("====== TP examples ======");
        TestTpStr("then", "than", cSpellApi);
        TestTpStr("bowl", "bowel", cSpellApi);
        TestTpStr("effect", "affect", cSpellApi);
        TestTpStr("their", "there", cSpellApi);
        TestTpStr("weather", "whether", cSpellApi);
        TestTpStr("small", "smell", cSpellApi);
        TestTpStr("undisguised", "undiagnosed", cSpellApi);
        TestTpStr("stereotypy", "stereotypic", cSpellApi);
        TestTpStr("specially", "especially", cSpellApi);
        TestTpStr("haberman", "habermann", cSpellApi);
        TestTpStr("therefor", "therefore", cSpellApi);
        TestTpStr("pregnancy", "pregnant", cSpellApi);
        TestTpStr("anderson", "andersen", cSpellApi);
        TestTpStr("domestic", "damaged", cSpellApi);
        TestTpStr("medical", "medicine", cSpellApi);
        TestTpStr("devises", "devices", cSpellApi);
        TestTpStr("loosing", "losing", cSpellApi);
        TestTpStr("access", "excess", cSpellApi);
        TestTpStr("tiered", "tired", cSpellApi);
        TestTpStr("sever", "severe", cSpellApi);
        TestTpStr("repot", "report", cSpellApi);
        TestTpStr("thing", "think", cSpellApi);
        TestTpStr("tried", "tired", cSpellApi);
        TestTpStr("adema", "edema", cSpellApi);
        TestTpStr("lease", "least", cSpellApi);
        TestTpStr("doner", "donor", cSpellApi);
        TestTpStr("leave", "live", cSpellApi);
        TestTpStr("hank", "thank", cSpellApi);
        TestTpStr("well", "swell", cSpellApi);
        TestTpStr("dose", "does", cSpellApi);
        TestTpStr("tent", "tend", cSpellApi);
        TestTpStr("fine", "find", cSpellApi);
        TestTpStr("spot", "stop", cSpellApi);
        TestTpStr("bond", "bone", cSpellApi);
        TestTpStr("know", "now", cSpellApi);
        TestTpStr("bed", "bad", cSpellApi);
        TestTpStr("our", "are", cSpellApi);
        TestTpStr("are", "arm", cSpellApi);
        TestTpStr("gey", "get", cSpellApi);
        TestTpStr("law", "lat", cSpellApi);
        TestTpStr("off", "of", cSpellApi);
        TestTpStr("too", "to", cSpellApi);
        // possessive does not have neough data in the word2Vec, not handled 
        //TestTpStr("month's", "months", cSpellApi);
        //TestTpStr("quantity's", "quantities", cSpellApi);
        //TestTpStr("guys", "guy's", cSpellApi);
        //TestTpStr("Noonan's", "Noonan", cSpellApi);
        //TestTpStr("sisters", "sisters'", cSpellApi);
        //TestTpStr("Williams'", "Williams", cSpellApi);
        // FP
        System.out.println("====== FP examples ======");
        TestFpStr("swelling", "stealing", cSpellApi);
        TestFpStr("mother", "bother", cSpellApi);
        TestFpStr("affected", "unaffected", cSpellApi);
        TestFpStr("accidental", "accident", cSpellApi);
        TestFpStr("developed", "develops", cSpellApi);
        TestFpStr("currently", "correctly", cSpellApi);
        TestFpStr("medication", "education", cSpellApi);
        TestFpStr("irritating", "irritation", cSpellApi);
        TestFpStr("generally", "general", cSpellApi);
        TestFpStr("exercises", "exercised", cSpellApi);
        TestFpStr("professionals", "professions", cSpellApi);
        TestFpStr("prediction", "reduction", cSpellApi);
        TestFpStr("publications", "duplications", cSpellApi);
        TestFpStr("show", "how", cSpellApi);
        TestFpStr("many", "any", cSpellApi);
        TestFpStr("heat", "eat", cSpellApi);
        System.out.println("====== FP examples: diff.6531.txt ======");
        TestFpStr("live", "love", cSpellApi);
        TestFpStr("your", "our", cSpellApi);
        TestFpStr("from", "form", cSpellApi);
        TestFpStr("please", "place", cSpellApi);
        TestFpStr("life", "live", cSpellApi);
        TestFpStr("going", "growing", cSpellApi);
        TestFpStr("which", "watch", cSpellApi);
        TestFpStr("every", "ever", cSpellApi);
        TestFpStr("thrush", "through", cSpellApi);
        TestFpStr("please", "place", cSpellApi);
        TestFpStr("which", "watch", cSpellApi);
        TestFpStr("while", "awhile", cSpellApi);
        TestFpStr("main", "man", cSpellApi);
        TestFpStr("fear", "far", cSpellApi);
        TestFpStr("what", "wheat", cSpellApi);
        TestFpStr("order", "older", cSpellApi);
        TestFpStr("advance", "advice", cSpellApi);
        TestFpStr("thuss", "thus", cSpellApi);
        TestFpStr("fold", "food", cSpellApi);
        TestFpStr("legs", "less", cSpellApi);
        TestFpStr("contact", "contract", cSpellApi);
        TestFpStr("last", "least", cSpellApi);
        TestFpStr("donor", "done", cSpellApi);
        TestFpStr("hypertension", "hypotension", cSpellApi);
        TestFpStr("hour", "her", cSpellApi);
        TestFpStr("anyone", "alone", cSpellApi);
        TestFpStr("contact", "contract", cSpellApi);
        TestFpStr("itch", "itchy", cSpellApi);
        TestFpStr("pressure", "pleasure", cSpellApi);
        TestFpStr("consult", "consist", cSpellApi);
        TestFpStr("genetic", "generic", cSpellApi);
        TestFpStr("with", "width", cSpellApi);
        TestFpStr("make", "maze", cSpellApi);
        TestFpStr("regions", "reasons", cSpellApi);
        TestFpStr("where", "were", cSpellApi);
        TestFpStr("currently", "correctly", cSpellApi);
        TestFpStr("case", "cause", cSpellApi);
        TestFpStr("going", "growing", cSpellApi);
        TestFpStr("working", "worrying", cSpellApi);
        TestFpStr("happen", "happy", cSpellApi);
        TestFpStr("caused", "cause", cSpellApi);
        TestFpStr("support", "sport", cSpellApi);
        TestFpStr("left", "lift", cSpellApi);
        TestFpStr("joint", "join", cSpellApi);
        TestFpStr("taking", "talking", cSpellApi);
        TestFpStr("would", "world", cSpellApi);
        TestFpStr("know", "now", cSpellApi);
        TestFpStr("also", "als", cSpellApi);
        TestFpStr("advised", "advises", cSpellApi);
        TestFpStr("sides", "sites", cSpellApi);
        TestFpStr("spot", "spout", cSpellApi);
        TestFpStr("into", "onto", cSpellApi);
        TestFpStr("advance", "advice", cSpellApi);
        TestFpStr("bent", "bend", cSpellApi);
        TestFpStr("head", "had", cSpellApi);
        TestFpStr("finding", "funding", cSpellApi);
        TestFpStr("would", "world", cSpellApi);
        TestFpStr("first", "fist", cSpellApi);
        TestFpStr("service", "survive", cSpellApi);
        TestFpStr("help", "held", cSpellApi);
        TestFpStr("below", "blow", cSpellApi);
        TestFpStr("hear", "her", cSpellApi);
        TestFpStr("currently", "correctly", cSpellApi);
        TestFpStr("power", "peer", cSpellApi);
        TestFpStr("mother", "matter", cSpellApi);
        TestFpStr("since", "sense", cSpellApi);
        TestFpStr("lungs", "links", cSpellApi);
        TestFpStr("over", "offer", cSpellApi);
        TestFpStr("soon", "sun", cSpellApi);
        TestFpStr("better", "bother", cSpellApi);
        TestFpStr("know", "knee", cSpellApi);
        TestFpStr("mouth", "math", cSpellApi);
        // inflVar
        TestFpStr("passed", "passes", cSpellApi);
        TestFpStr("experienced", "experiences", cSpellApi);
        TestFpStr("exercises", "exercised", cSpellApi);
        TestFpStr("advised", "advises", cSpellApi);
        TestFpStr("taken", "takes", cSpellApi);
        TestFpStr("giving", "given", cSpellApi);
        // irreg
        TestFpStr("give", "gave", cSpellApi);
        TestFpStr("understood", "understand", cSpellApi);
        TestFpStr("worse", "worst", cSpellApi);
        TestFpStr("woman", "women", cSpellApi);
        TestFpStr("sent", "send", cSpellApi);
        // derivation
        TestFpStr("vaginal", "vagina", cSpellApi);
        TestFpStr("generally", "general", cSpellApi);
        TestFpStr("intestinal", "intestine", cSpellApi);
    }
    private static void Tests(CSpellApi cSpellApi) 
    {
        ArrayList<String> testList = new ArrayList<String>();
        TestCand("too", "to", cSpellApi);
        TestCand("then", "than", cSpellApi);
        TestCand("thing", "think", cSpellApi);
        TestCand("sisters", "sisters'", cSpellApi);
        TestCand("know", "now", cSpellApi);
        TestCand("tried", "tired", cSpellApi);
        TestCand("specially", "especially", cSpellApi);
        TestCand("law", "lat", cSpellApi);
        TestCand("domestic", "damaged", cSpellApi);
        TestCand("Weather", "whether", cSpellApi);
        TestCand("there", "their", cSpellApi);
        TestCand("then", "than", cSpellApi);
        TestCand("fine", "find", cSpellApi);
        TestCand("bowl", "bowel", cSpellApi);
        TestCand("off", "of", cSpellApi);
        TestCand("Dies", "Does", cSpellApi);
        TestCand("descended", "undescended", cSpellApi);
        TestCand("effect", "affect", cSpellApi);
        TestCand("pregnancy", "pregnant", cSpellApi);
        TestCand("leave", "live", cSpellApi);
        TestCand("affects", "effects", cSpellApi);
        TestCand("their", "there", cSpellApi);
        TestCand("you", "your", cSpellApi);
        TestCand("medical", "medicine", cSpellApi);
        TestCand("medical", "medicine", cSpellApi);
        TestCand("swollen", "swelling", cSpellApi);
        TestCand("swollen", "swelling", cSpellApi);
        TestCand("well", "swell", cSpellApi);
        TestCand("FRIENDS", "friend's", cSpellApi);
        TestCand("access", "excess", cSpellApi);
        TestCand("where", "were", cSpellApi);
        TestCand("spot", "stop", cSpellApi);
        TestCand("weather", "whether", cSpellApi);
        TestCand("were", "we're", cSpellApi);
        TestCand("small", "smell", cSpellApi);
        TestCand("bond", "bone", cSpellApi);
        TestCand("then", "than", cSpellApi);
        TestCand("leave", "live", cSpellApi);
        TestCand("meningitidis", "meningitis", cSpellApi);
        System.out.println(totalNo_ + "|" + totalCandNo_);
    }
    private static boolean TestCand(String inWord, String cand, 
        CSpellApi cSpellApi)
    {
        HashSet<String> candSet = GetCandidates(inWord, cSpellApi);
        boolean hasCand = candSet.contains(cand);
        totalNo_++;
        if(hasCand == true)
        {
            totalCandNo_++;
            System.out.println(inWord + ", " + cand);
        }
        return hasCand;
    }
    private static void TestDists()
    {
        TestDist("small", "smell");
        TestDist("to", "too");
        TestDist("affect", "effect");
        TestDist("given", "give");
        TestDist("worst", "worse");
        TestDist("caused", "cause");
        TestDist("kriz", "chris");
    }
    private static void TestDist(String str1, String str2)
    {
        System.out.println(str1 + "|" + str2 + "|" + GetLengthDist(str1, str2)
            + "|" + GetLeadCharDist(str1, str2) + "|" 
            + GetEndCharDist(str1, str2));
    }
    // test driver
    public static void main(String[] args) 
    {
        
        if(args.length > 0)
        {
            System.err.println("*** Usage: java RealWord1To1Candidates");
            System.exit(1);
        }
        // init
        String configFile = "../data/Config/cSpell.properties";
        CSpellApi cSpellApi = new CSpellApi(configFile);
        //Tests(cSpellApi);
        //TestDists();
        TestTestSet(cSpellApi);    // test candidate rule for TP and FP
    }
    // data member
    // key: inWordLc, value: candSet
    // this canMap is used to speed up the candidate generating process.
    private static HashMap<String, HashSet<String>> candMap_ 
        = new HashMap<String, HashSet<String>>();
    
    // for testing purpose, should be delete
    private static int totalNo_ = 0;
    private static int totalCandNo_ = 0;
    private static int totalTpNo_ = 0;
    private static int totalTpStrNo_ = 0;
    private static int totalFpNo_ = 0;
    private static int totalFpStrNo_ = 0;
}

package gov.nih.nlm.nls.cSpell.Ranker;
import java.util.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.Api.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
/**************************************************************************** 
* This class ranks and finds the best candidate for real-word split by
* specifying different ranking method.
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
public class RankRealWordSplitByMode
{
    // private constructor
    private RankRealWordSplitByMode()
    {
    }
    // public method
    // real-word ranking use context
    // tarPos: start from 0, not include empty space token
    public static String GetTopRankStr(String inStr, HashSet<String> candidates,
        CSpellApi cSpellApi, boolean debugFlag, int tarPos, 
        ArrayList<TokenObj> nonSpaceTokenList)
    {
        String topRankStr = GetTopRankStrByContext(inStr, candidates,
            cSpellApi, debugFlag, tarPos, nonSpaceTokenList);
        return topRankStr;
    }
    // private
    // TBD ...
    private static String GetTopRankStrByCSpell(String inStr,
        HashSet<String> candidates, CSpellApi cSpellApi, boolean debugFlag, 
        int tarPos, ArrayList<TokenObj> nonSpaceTokenList)
    {
        String topRankStr = inStr;
        return topRankStr;
    }
    private static String GetTopRankStrByContext(String inStr,
        HashSet<String> candidates, CSpellApi cSpellApi, boolean debugFlag, 
        int tarPos, ArrayList<TokenObj> nonSpaceTokenList)
    {
        // init
        Word2Vec word2VecIm = cSpellApi.GetWord2VecIm();
        Word2Vec word2VecOm = cSpellApi.GetWord2VecOm();
        //WordWcMap wordWcMap = cSpellApi.GetWordWcMap();
        int contextRadius = cSpellApi.GetRwSplitContextRadius();
        boolean word2VecSkipWord = cSpellApi.GetWord2VecSkipWord();
        int maxCandNo = cSpellApi.GetCanMaxCandNo();
        int tarSize = 1;    // only for split, the target size = 1
        double rwSplitCFactor = cSpellApi.GetRankRwSplitCFac();
        int shortSplitWordLength = cSpellApi.GetCanRwShortSplitWordLength();
        int maxShortSplitWordNo = cSpellApi.GetCanRwMaxShortSplitWordNo();
        // include detail print
        String topRankStr = RankRealWordSplitByContext.GetTopRankStr(inStr, 
            candidates, tarPos, tarSize, nonSpaceTokenList, word2VecIm, 
            word2VecOm, word2VecSkipWord, contextRadius, shortSplitWordLength,
            maxShortSplitWordNo, rwSplitCFactor, maxCandNo, debugFlag);
        return topRankStr;    
    }
    
    // test Driver is implmeneted in real-word corrector
    public static void main(String[] args)
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java RankRealWordByMode");
            System.exit(1);
        }
    }
    // data member
}

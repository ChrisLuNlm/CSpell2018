package gov.nih.nlm.nls.cSpell.Ranker;
import java.util.*;
import static java.util.stream.Collectors.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.Util.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
/*****************************************************************************
* This class is the context utility for word2Vec.
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
public class Word2VecContext
{
    // private constructor
    public Word2VecContext()
    {
        // calculate score
    }
    // public method
    // specify the window radius
    // tarPos: the index of target word in the non-space tokenlist
    // tarSize:
    public static DoubleVec GetContextVec(int tarPos, int tarSize, 
        ArrayList<TokenObj> nonSpaceTokenList, Word2Vec w2vIm, int radius, 
        boolean word2VecSkipWord, boolean debugFlag)
    {
        // 1. get the context
        ArrayList<String> contextList = GetContext(tarPos, tarSize,
            nonSpaceTokenList, w2vIm, radius, word2VecSkipWord, debugFlag);
        // 2. get the wordVec for the context    
        DoubleVec contextVec = Word2VecScore.GetAvgWordVecForList(
            contextList, w2vIm);
        return contextVec;    
    }
    // context from all inTextList, no specify on window radius
    public static DoubleVec GetContextVec(int tarPos, int tarSize, 
        ArrayList<TokenObj> nonSpaceTokenList, Word2Vec w2vIm, 
        boolean word2VecSkipWord, boolean debugFlag)
    {
        // 1. get the context
        ArrayList<String> contextList = GetContext(tarPos, tarSize,
            nonSpaceTokenList, w2vIm, word2VecSkipWord, debugFlag);
        // 2. get the wordVec for the context    
        DoubleVec contextVec = Word2VecScore.GetAvgWordVecForList(
            contextList, w2vIm);
        return contextVec;    
    }
    // specify the radius
    public static ArrayList<String> GetContext(int tarPos, int tarSize, 
        ArrayList<TokenObj> nonSpaceTokenList, Word2Vec w2vIm, 
        boolean word2VecSkipWord, boolean debugFlag)
    {
        int radius = 0;    // raidus is not needed when Context = true
        boolean allContext = true;
        return GetContext(tarPos, tarSize, nonSpaceTokenList, w2vIm, radius,
            word2VecSkipWord, debugFlag, allContext);
    }
    public static ArrayList<String> GetContext(int tarPos, int tarSize, 
        ArrayList<TokenObj> nonSpaceTokenList, Word2Vec w2vIm, int radius, 
        boolean word2VecSkipWord, boolean debugFlag)
    {
        boolean allContext = false;
        return GetContext(tarPos, tarSize, nonSpaceTokenList, w2vIm, radius,
            word2VecSkipWord, debugFlag, allContext);
    }
    private static ArrayList<String> GetContext(int tarPos, int tarSize, 
        ArrayList<TokenObj> nonSpaceTokenList, Word2Vec w2vIm, int radius, 
        boolean word2VecSkipWord, boolean debugFlag, boolean allContext)
    {
        // normal TokenObj to string, use coreTerm.lc
        ArrayList<String> normTextList = new ArrayList<String>();
        for(TokenObj tokenObj:nonSpaceTokenList)
        {
            // norm the token, such as [NUM], [URL], [EMAIL]
            // TBD, should be done in pre-correction, preProcess
            String normWord = NormWordForWord2Vec(tokenObj.GetTokenStr());
            normTextList.add(normWord);
        }
        // get the context list by normStr (becasue normStr is key in w2v)
        ArrayList<String> contextList = GetContextForTar(tarPos, tarSize, 
            normTextList, w2vIm, radius, word2VecSkipWord, allContext);
        DebugPrint.PrintContext(contextList, debugFlag);
        return contextList;    
    }
    public static String NormWordForWord2Vec(String inWord)
    {
        // 1. CoreTerm
        int ctType = CoreTermUtil.CT_TYPE_SPACE_PUNC;
        boolean lcFlag = true;
        String inWordCtLc = CoreTermUtil.GetCoreTerm(inWord, ctType, lcFlag);
        // 2. find patterns of [NUM], [URL], [EMAIL]
        String inWordPat = inWordCtLc;
        if(InternetTokenUtil.IsUrl(inWordCtLc) == true)
        {
            inWordPat = PAT_URL;
        }
        else if(InternetTokenUtil.IsEmail(inWordCtLc) == true)
        {
            inWordPat = PAT_EMAIL;
        }
        else if(DigitPuncTokenUtil.IsPunc(inWordCtLc) == true)
        {
            inWordPat = new String();   // remove puctuation
        }
        else if(DigitPuncTokenUtil.IsDigitPunc(inWordCtLc) == true)
        {
            inWordPat = PAT_NUM;    // add puctuation test to remove
        }
        // Add test set special case
        // TBD: convert the format [CONTACT] to [EMAIL]
        // TBD: not to implemented, because it is better to
        // sync the format in PreProcess: [CONTACT], [NUM], ...
        // TBD: make sure the coreTerm does not take out above pattern
        /*
        else if(inWord.equals("[CONTACT]") == true)
        {
            inWordPat = PAT_EMAIL;    // could be Telephone number [PAT_NUM]
        }
        */
        // 3. TBD: take care of xxx's
        return inWordPat;
    }
     
    // Get context:
    // tarPos: target word position
    // tarSize: no. of tokens for target word (merge should be > 1)
    // inTextList: No empty space token
    // w2vIm: context must use word2Vec input matrix
    // radius: number of tokens before / after the tarPos
    // boolean word2VecSkipWord: skip word if the word does not have wordVec
    private static ArrayList<String> GetContextForTar(int tarPos, 
        int tarSize, ArrayList<String> nonSpaceTokenList, Word2Vec w2vIm, 
        int radius, boolean word2VecSkipWord, boolean allContext)
    {
        // output context
        ArrayList<String> outContextList = new ArrayList<String>();
        // 2. find context before the tar token
        int tokenNo = 0;
        for(int i = tarPos-1; i >= 0; i--)
        {
            String inWord = nonSpaceTokenList.get(i);
            // check if has wordVec if word2VecSkipWord = true
            if((word2VecSkipWord == false)
            || (w2vIm.HasWordVec(inWord) == true))
            {
                tokenNo++;
                if((tokenNo <= radius)
                || (allContext == true))
                {
                    outContextList.add(0, inWord);
                }
                else
                {
                    break;
                }
            }
        }
        // 3. find context after the tar token
        int endPos = tarPos + tarSize;     // target could be multiwords
        tokenNo = 0;
        for(int i = endPos; i < nonSpaceTokenList.size(); i++)
        {
            String inWord = nonSpaceTokenList.get(i);
            if((word2VecSkipWord == false)
            || (w2vIm.HasWordVec(inWord) == true))
            {
                tokenNo++;
                if((tokenNo <= radius)
                || (allContext == true))
                {
                    outContextList.add(inWord);
                }
                else
                {
                    break;
                }
            }
        }
        
        return outContextList;
    }
    // private method
    private static void Test(Word2Vec w2vIm, Word2Vec w2vOm)
    {
    }
    private static void Tests(Word2Vec w2vIm, Word2Vec w2vOm)
    {
        String inText = "... last 10 years #$% was dianosed test123 yahoo.com early on set deminita 3 year ago.";
        ArrayList<TokenObj> inTextList = TextObj.TextToTokenList(inText);
        System.out.println("======= Word2VecContext ======================");
        System.out.println(" - inText: [" + inText + "]");
        String inStr = inTextList.stream()
                .map(obj -> obj.GetTokenStr())
                .collect(joining("|"));
        System.out.println(" - inTextList (" + inTextList.size() + "): [" 
            + inStr + "]");
        
        int tarPos = 0;
        int tarSize = 1;
        int index = 0;
        int radius = 3;
        boolean debugFlag = false;
        System.out.println("------ Test GetContext (no skip), radius=3 ...");
        // remove space token from the list
        ArrayList<TokenObj> nonSpaceTokenList
            = TextObj.GetNonSpaceTokenObjList(inTextList);
        for(TokenObj tokenObj:inTextList)
        {
            // not the space token
            if(tokenObj.IsSpaceToken() == false)
            {
                String tokenStr = tokenObj.GetTokenStr();
                // word2VecSkipWord = false (no skip)
                ArrayList<String> contextList = GetContext(tarPos, tarSize, 
                    nonSpaceTokenList, w2vIm, radius, false, debugFlag);
                String contextStr = contextList.stream()
                    .collect(joining("|"));
                System.out.println(tarPos + "|" + index + "|" + tokenStr 
                    + ": [" + contextStr + "]");
                tarPos++;
            }
            index++;
        }
        System.out.println("------ Test GetContext (skip) , radius=3 ...");
        System.out.println(" - inText: [" + inText + "]");
        tarPos = 0;
        for(TokenObj tokenObj:inTextList)
        {
            // not the space token
            if(tokenObj.IsSpaceToken() == false)
            {
                String tokenStr = tokenObj.GetTokenStr();
                // word2VecSkipWord = true (skip)
                ArrayList<String> contextList2 = GetContext(tarPos, tarSize, 
                    nonSpaceTokenList, w2vIm, radius, true, debugFlag);
                String contextStr2 = contextList2.stream()
                    .collect(joining("|"));
                System.out.println(tarPos + "|" + index + "|" + tokenStr 
                    + ": [" + contextStr2 + "]");
                tarPos++;
            }
            index++;
        }
        System.out.println("------ Test GetContext (skip) , all ...");
        System.out.println(" - inText: [" + inText + "]");
        tarPos = 0;
        // not the space token
        for(TokenObj tokenObj:nonSpaceTokenList)
        {
            String tokenStr = tokenObj.GetTokenStr();
            // word2VecSkipWord = true (skip)
            ArrayList<String> contextList3 = GetContext(tarPos, tarSize, 
                nonSpaceTokenList, w2vIm, true, debugFlag);
            String contextStr3 = contextList3.stream()
                .collect(joining("|"));
            System.out.println(tarPos + "|" + tokenStr 
                + ": [" + contextStr3 + "]");
            tarPos++;
        }
    }
    // test Driver
    public static void main(String[] args)
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java Word2VecContext");
            System.exit(0);
        }
        // test
        String inImFile = "../data/Context/syn0.data";
        String inOmFile = "../data/Context/syn1n.data";
        boolean verboseFlag = true;
        Word2Vec w2vIm = new Word2Vec(inImFile, verboseFlag);
        Word2Vec w2vOm = new Word2Vec(inOmFile, verboseFlag);
        Tests(w2vIm, w2vOm);
    }
    // data member
    public static final String PAT_URL = "[URL]";
    public static final String PAT_EMAIL = "[EMAIL]";
    public static final String PAT_NUM = "[NUM]";
}

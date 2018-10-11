package gov.nih.nlm.nls.cSpell.Ranker;
import java.util.*;
import static java.util.stream.Collectors.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.Util.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
/*****************************************************************************
* This class is the java object of context score (by word2Vec).
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
public class Word2VecScore
{
    // private constructor
    public Word2VecScore()
    {
        // calculate score
    }
    // public method
    // inTerm: candidate (can be multiword)
    // contextVec: wordVec of context
    // word2VecOm: word2Vec output matrix - syn1neg
    // Use CWOB model to predict the target word = H X OM
    public static double GetScore(String inTerm, DoubleVec contextVec,
        Word2Vec w2vOm)
    {
        // 1. Get Avg. Vec for term (candidate from prediction)
        DoubleVec termVec = GetWordVecForTerm(inTerm, w2vOm);
        // 2. got the inner dot between hidden layer (context) and OM
        // to predict the output matrix in CBOW
        double score = GetCwobScore(termVec, contextVec);
        return score;
    }
    // try two different method, inner dot or cos
    private static double GetCwobScore(DoubleVec vec1, DoubleVec vec2)
    {
        //double score = DoubleVecUtil.GetInnerDot(vec1, vec2);
        // normailzed score between -1.0 to 1.0
        double score = DoubleVecUtil.GetCosineSimilarity(vec1, vec2);
        
        return score;
    }
    // this method is to be deleted because it has same result as GetScore()
    public static double GetScore2(String inTerm, DoubleVec contextVec,
        Word2Vec w2vOm)
    {
        ArrayList<String> inWordList = TermUtil.ToWordList(inTerm);
        double score = 0.0d;
        int count = 0;
        for(String word:inWordList)
        {
            DoubleVec wordVec = w2vOm.GetWordVec(word);
            if(wordVec != null)
            {
                score += GetCwobScore(wordVec, contextVec);
            }
            count++;
        }
        // add score first, then calculate the avg.
        score = score/count;
        return score;
    }
    // from ensemble paper, use the word vector (Input Matrix) for w2v
    // word2VecIm: word2Vec input matrix - syn0
    // Similarity score use word2Vec Im
    public static double GetSimilarityScore(String inTerm, DoubleVec contextVec,
        Word2Vec word2VecIm)
    {
        // 1. Get Avg. score for inTerm
        DoubleVec termVec = GetWordVecForTerm(inTerm, word2VecIm);
        // 2. Get Cosine similarity between contextVec and tarVec
        double score = GetSimilarityScore(termVec, contextVec);
        return score;
    }
    // private method
    private static double GetSimilarityScore(DoubleVec vec1, DoubleVec vec2)
    {
        double score = DoubleVecUtil.GetCosineSimilarity(vec1, vec2);
        // set the min. to 0.0
        score = (score <= 0.0?0.0d:score);
        return score;
    }
    // use cosineSimilarity instead of inner dot
    private static double GetInnerDotScore(DoubleVec vec1, DoubleVec vec2)
    {
        double score = DoubleVecUtil.GetInnerDot(vec1, vec2);
        // set the min. to 0.0
        // it seems -score is useful for split ....
        //score = (score <= 0.0?0.0d:score);
        return score;
    }
    // Use Avg. word2Vec Om for each word in the inTerm 
    private static DoubleVec GetWordVecForTerm(String inTerm, Word2Vec w2vOm)
    {
        ArrayList<String> inWordList = TermUtil.ToWordList(inTerm);
        // avg. the wordVec if inTerm is a multiword
        DoubleVec outWordVec = GetAvgWordVecForList(inWordList, w2vOm);
        // TBD: take care of possesive
        return outWordVec;
    }
    // Average wordVec for a list of words
    public static DoubleVec GetAvgWordVecForList(List<String> wordList,
        Word2Vec word2Vec)
    {
        // init the matrix to all zero
        int dimension = word2Vec.GetDimension();
        DoubleVec aveWordVec = new DoubleVec(dimension);
        int count = 0;
        for(String word:wordList)
        {
            DoubleVec wordVec = word2Vec.GetWordVec(word);
            if(wordVec != null)
            {
                aveWordVec.Add(wordVec);
            }
            count++;
        }
        // calculate the avg.
        if(count != 0)
        {
            aveWordVec.Divide(count);
        }
        return aveWordVec;
    }
    
    private static void TestWin(String inTerm, DoubleVec contextVec,
        Word2Vec w2vIm, Word2Vec w2vOm)
    {
        double cwobScore = GetScore(inTerm, contextVec, w2vOm);
        double cwobScore2 = GetScore2(inTerm, contextVec, w2vOm);
        double cosScore = GetSimilarityScore(inTerm, contextVec, w2vIm);
        System.out.print(String.format("%1.4f", cwobScore) + "-"
            + String.format("%1.4f", cwobScore2) + "|");
        /*
        System.out.println(inTerm + "|" + String.format("%1.8f", cwobScore)
            + "|" + String.format("%1.8f", cosScore));
        */
    }
    private static void Test(String inTerm, DoubleVec contextVec,
        Word2Vec w2vIm, Word2Vec w2vOm)
    {
        double cwobScore = GetScore(inTerm, contextVec, w2vOm);
        double cwobScore2 = GetScore2(inTerm, contextVec, w2vOm);
        double cosScore = GetSimilarityScore(inTerm, contextVec, w2vIm);
        System.out.println(inTerm + "|" + String.format("%1.8f", cwobScore)
            + "|" + String.format("%1.8f", cwobScore2)
            + "|" + String.format("%1.8f", cosScore));
    }
    private static void Tests(Word2Vec w2vIm, Word2Vec w2vOm)
    {
        String inText = "for the last 10 years    was dianosed\n early on set deminita 3 years ago";
        ArrayList<TokenObj> inTextList = TextObj.TextToTokenList(inText);
        // remove space token from the list
        ArrayList<TokenObj> nonSpaceTokenList
            = TextObj.GetNonSpaceTokenObjList(inTextList);
        ArrayList<String> testStrList = new ArrayList<String>();
        testStrList.add("diagnosed");
        testStrList.add("diagnose");
        testStrList.add("dianosed");
        // init context
        int tarPos = 6;
        int tarSize = 1;
        int radius = 2;
        boolean word2VecSkipWord = true;
        boolean debugFlag = false;
        DoubleVec contextVec = Word2VecContext.GetContextVec(tarPos, tarSize, 
            nonSpaceTokenList, w2vIm, radius, word2VecSkipWord, debugFlag);
        System.out.println("===== Test diagnosed|diagnose|dianosed (window-2) =====");
        System.out.println("inText: [" + inText + "]");
        System.out.println("============================================");
        System.out.println("Candidates|CBOW score|CBOW score 2|Similarity score"); 
        System.out.println("============================================");
        for(String testStr:testStrList)
        {
            Test(testStr, contextVec, w2vIm, w2vOm);
        }
        System.out.println("===== Test diagnosed|diagnose|dianosed (whole text) =====");
        contextVec = Word2VecContext.GetContextVec(tarPos, tarSize, 
            nonSpaceTokenList, w2vIm, word2VecSkipWord, debugFlag);
        for(String testStr:testStrList)
        {
            Test(testStr, contextVec, w2vIm, w2vOm);
        }    
        String inText1 = "Not all doctors know about this syndrome.";
        ArrayList<TokenObj> inTextList1 = TextObj.TextToTokenList(inText1);
        // remove space token from the list
        ArrayList<TokenObj> nonSpaceTokenList1
            = TextObj.GetNonSpaceTokenObjList(inTextList1);
        System.out.println("===== Test know about|know|about (window) =====");
        ArrayList<String> testStrList1 = new ArrayList<String>();
        testStrList1.add("know about");
        testStrList1.add("know");
        testStrList1.add("about");
        tarPos = 3;
        tarSize = 2;
        radius = 2;
        contextVec = Word2VecContext.GetContextVec(tarPos, tarSize, 
            nonSpaceTokenList1, w2vIm, radius, word2VecSkipWord, debugFlag);
        Test(testStrList1.get(0), contextVec, w2vIm, w2vOm);
        contextVec = Word2VecContext.GetContextVec(tarPos, tarSize, 
            nonSpaceTokenList1, w2vIm, word2VecSkipWord, debugFlag);
        Test(testStrList1.get(0), contextVec, w2vIm, w2vOm);
        tarPos = 3;
        tarSize = 1;
        contextVec = Word2VecContext.GetContextVec(tarPos, tarSize, 
            nonSpaceTokenList1, w2vIm, radius, word2VecSkipWord, debugFlag);
        Test(testStrList1.get(1), contextVec, w2vIm, w2vOm);
        contextVec = Word2VecContext.GetContextVec(tarPos, tarSize, 
            nonSpaceTokenList1, w2vIm, word2VecSkipWord, debugFlag);
        Test(testStrList1.get(1), contextVec, w2vIm, w2vOm);
        tarPos = 4;
        tarSize = 1;
        contextVec = Word2VecContext.GetContextVec(tarPos, tarSize, 
            nonSpaceTokenList1, w2vIm, radius, word2VecSkipWord, debugFlag);
        Test(testStrList1.get(2), contextVec, w2vIm, w2vOm);
        contextVec = Word2VecContext.GetContextVec(tarPos, tarSize, 
            nonSpaceTokenList1, w2vIm, word2VecSkipWord, debugFlag);
        Test(testStrList1.get(2), contextVec, w2vIm, w2vOm);
        
        String inText2 = "for the last   10 years was diagnosed early on set dementia 3 years ago.";
        ArrayList<TokenObj> inTextList2 = TextObj.TextToTokenList(inText2);
        // remove space token from the list
        ArrayList<TokenObj> nonSpaceTokenList2
            = TextObj.GetNonSpaceTokenObjList(inTextList2);
        ArrayList<String> testStrList2 = new ArrayList<String>();
        testStrList2.add("onset");
        testStrList2.add("on set");
        System.out.println("===== Test onset|on set (window-3) =====");
        System.out.println("inText: [" + inText + "]");
        tarPos = 8;
        tarSize = 2;
        radius = 3;
        contextVec = Word2VecContext.GetContextVec(tarPos, tarSize, 
            nonSpaceTokenList2, w2vIm, radius, word2VecSkipWord, debugFlag);
        for(String testStr:testStrList2)
        {
            Test(testStr, contextVec, w2vIm, w2vOm);
        }
        tarPos = 8;
        tarSize = 1;
        radius = 3;
        contextVec = Word2VecContext.GetContextVec(tarPos, tarSize, 
            nonSpaceTokenList2, w2vIm, radius, word2VecSkipWord, debugFlag);
        Test("on", contextVec, w2vIm, w2vOm);
        tarPos = 9;
        contextVec = Word2VecContext.GetContextVec(tarPos, tarSize, 
            nonSpaceTokenList2, w2vIm, radius, word2VecSkipWord, debugFlag);
        Test("set", contextVec, w2vIm, w2vOm);
        System.out.println("===== Test onset|on set (whole text) =====");
        radius = nonSpaceTokenList2.size();
        contextVec = Word2VecContext.GetContextVec(tarPos, tarSize, 
            nonSpaceTokenList2, w2vIm, word2VecSkipWord, debugFlag);
        for(String testStr:testStrList2)
        {
            Test(testStr, contextVec, w2vIm, w2vOm);
        }
        System.out.println("===== Go through each tokens with diff radius 1-9) =====");
        System.out.println("tarPos|tarWord|r=1|r=2|r=3|r=4|r=5|r=6|r=7|r=8|r=9");
        //String inText3 = "Broken bones can not sleep at night!";
        String inText3 = "not xyxy all doctors know about this syndrome.";
        ArrayList<TokenObj> inTextList3 = TextObj.TextToTokenList(inText3);
        // remove space token from the list
        ArrayList<TokenObj> nonSpaceTokenList3
            = TextObj.GetNonSpaceTokenObjList(inTextList3);
        tarPos = 0;
        tarSize = 1;
        radius = 0;
        for(TokenObj tokenObj:nonSpaceTokenList3)
        {
            // skip the space token
            String tokenStr = tokenObj.GetTokenStr();
            String inStr = Word2VecContext.NormWordForWord2Vec(tokenStr);
            System.out.print(tarPos + "|" + tokenStr + "|");
            // print out all radius
            for(int r = 1; r < 10; r++)
            {
                contextVec = Word2VecContext.GetContextVec(tarPos, tarSize, 
                    inTextList2, w2vIm, r, word2VecSkipWord, debugFlag);
                TestWin(inStr, contextVec, w2vIm, w2vOm);
            }
            System.out.println("");
            tarPos++;    
        }
    }
    // test Driver
    public static void main(String[] args)
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java Word2VecScore");
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
}

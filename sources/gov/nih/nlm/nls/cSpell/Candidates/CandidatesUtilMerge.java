package gov.nih.nlm.nls.cSpell.Candidates;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.Util.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
import gov.nih.nlm.nls.cSpell.Dictionary.*;
import gov.nih.nlm.nls.cSpell.Api.*;
/*****************************************************************************
* This is the java utility class to generate merge candidates.
* A merge requries for a specified target (position) form a inText. 
* It needs suggestDic to make sure the (coreTerm of) mergedword is 
* in the dictionary. The merge candidate is represented as a mergeObj, 
* which include data for reconstruct the inText after merge.
*
* <p><b>History:</b>
* <ul>
* <li>2018 baseline
* </ul>
*
* @author chlu
*
* @version    V-2018
*****************************************************************************/
public class CandidatesUtilMerge
{
    /**
    * Private constructor 
    */
    private CandidatesUtilMerge()
    {
    }
    // protected method
    // get merge word by merge no, including shift window, fixed window size
    protected static HashSet<MergeObj> GetMergeSetByMergeNo(int tarPos, 
        ArrayList<TokenObj> nonSpaceTextList, int mergeNo, 
        boolean mergeWithHyphen, boolean shortWordMerge,
        RootDictionary suggestDic, RootDictionary aADic, RootDictionary mwDic)
    {
        // output merge object list
        HashSet<MergeObj> mergeSet = new HashSet<MergeObj>();
        // find the merge object
        int startPos = tarPos - mergeNo;    // start pos index
        startPos = ((startPos > 0)? startPos : 0);
        int size = nonSpaceTextList.size();
        // find the merge word, merged by remove spcae or repalce with "-"
        // shift window by i
        int startIndex = 0;
        int tarIndex = nonSpaceTextList.get(tarPos).GetIndex();
        String tarWord = nonSpaceTextList.get(tarPos).GetTokenStr();
        int endIndex = 0;
        // these are vars to be used to MergeObj
        int objStartPos = 0;
        int objTarPos = tarPos;
        int objEndPos = 0;
        // all possible merges
        for(int i = startPos; i <= tarPos; i++)
        {
            // get the merged word with fixed window size (mergeNo)
            String mergeWordBySpace = new String();
            String mergeWordByHyphen = new String();
            String orgMergeWord = new String();    // the original word b4 merge
            boolean completeFlag = true;
            startIndex = nonSpaceTextList.get(i).GetIndex();
            boolean firstToken = true;
            objStartPos = i;
            objEndPos = i + mergeNo;
            int shortWordNo = 0;
            // merge operations
            for(int j = 0; j <= mergeNo; j++)
            {
                int curPos = i+j;
                if(curPos < size)    // check window size
                {
                    TokenObj curTokenObj = nonSpaceTextList.get(curPos); 
                    String tokenStr = curTokenObj.GetTokenStr();
                    // should move to a Util function file
                    // don't combine if exception of puntuaction 
                    if((DigitPuncTokenUtil.IsDigit(tokenStr) == true) // digit
                    || (DigitPuncTokenUtil.IsPunc(tokenStr) == true) // punc
                    || (DigitPuncTokenUtil.IsDigitPunc(tokenStr) == true)//digitPunc
                    || (InternetTokenUtil.IsUrl(tokenStr) == true)   // url
                    || (InternetTokenUtil.IsEmail(tokenStr) == true))  // eMail
                    //|| (MeasurementTokenUtil.IsMeasurements(tokenStr, unitDic) == true))
                    {
                        completeFlag = false;
                        break;
                    }
                    else    // where merege operation happen
                    {
                        // don't put the "-" or " " for the first token
                        if(firstToken == true)
                        {
                            mergeWordBySpace = tokenStr;
                            mergeWordByHyphen = tokenStr;
                            orgMergeWord = tokenStr;
                            firstToken = false;
                            shortWordNo = UpdateShortWordNo(tokenStr,
                                SHORT_WORD_LENGTH, shortWordNo);
                        }
                        else
                        {
                            mergeWordBySpace += tokenStr;
                            mergeWordByHyphen += GlobalVars.HYPHEN_STR 
                                + tokenStr;
                            orgMergeWord += GlobalVars.SPACE_STR + tokenStr;
                            shortWordNo = UpdateShortWordNo(tokenStr,
                                SHORT_WORD_LENGTH, shortWordNo);
                        }
                        endIndex = curTokenObj.GetIndex();
                    }
                }
                else    // end of the text list, break out of the loop
                {
                    completeFlag = false;
                    break;
                }
            }
            // must complete the fixed window for merging
            if(completeFlag == true)
            {
                // the orginal word (before merge) can't be a multiword
                // such as "non clinical"
                if(mwDic.IsDicWord(orgMergeWord) == false)
                {
                    // check short word merge
                    if((shortWordMerge == true)    // non-word allows short w merge
                    || (shortWordNo <= MAX_SHORT_WORD_NO))    // real-word
                    {
                        AddMergeObj(tarWord, orgMergeWord, mergeWordBySpace, 
                            mergeNo, startIndex, tarIndex, endIndex, 
                            objStartPos, objTarPos, objEndPos, mergeSet, 
                            suggestDic, aADic);
                        // Add merge with hyphen to candidate set
                        if(mergeWithHyphen == true)
                        {
                            AddMergeObj(tarWord, orgMergeWord, 
                                mergeWordByHyphen, mergeNo, startIndex, 
                                tarIndex, endIndex, objStartPos, objTarPos, 
                                objEndPos, mergeSet, suggestDic, aADic);
                        }
                    }
                }
            }
        }
        return mergeSet;
    }
    // private method
    private static int UpdateShortWordNo(String word, int shortWordLength,
        int inShortWordNo)
    {
        int outShortWordNo = inShortWordNo;
        if(IsShortWordForMerge(word, shortWordLength) == true)
        {
            outShortWordNo++;
        }
        return outShortWordNo;
    }
    private static boolean IsShortWordForMerge(String word, int shortWordLength)
    {
        boolean shortWord = ((word.length() <= shortWordLength)?true:false);
        return shortWord;
    }
    private static void AddMergeObj(String tarWord, String orgMergeWord,
        String mergeWord, int mergeNo, int startIndex, int tarIndex, 
        int endIndex, int startPos, int tarPos, int endPos,
        HashSet<MergeObj> mergeSet, RootDictionary suggestDic, 
        RootDictionary aADic)
    {
        // 1. convert merged word to coreTerm
        int ctType = CoreTermUtil.CT_TYPE_SPACE_PUNC;
        boolean lcFlag = true;
        // only take care of the end punctuation for the coreTerm
        String coreStr = TermUtil.StripEndPuncSpace(mergeWord);
        // 2. check if the coreStr of mergeWord is in suggest Dic
        // the merge word is not a Aa, assuming no merge for Aa
        // becase Aa is short enough
        if((suggestDic.IsDicWord(coreStr) == true)
        && (aADic.IsDicWord(coreStr) == false))
        {
            MergeObj mergeObj = new MergeObj(tarWord, orgMergeWord, mergeWord, 
                coreStr, mergeNo, startIndex, tarIndex, endIndex,
                startPos, tarPos, endPos);
            mergeSet.add(mergeObj);
        }
    }
    // private methods
    private static void Test()
    {
    }
    // test driver
    public static void main(String[] args)
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java MergeCandidatesUtil");
            System.exit(0);
        }
        
        // test case and print out 
        Test();
    }
    // data member
    private static int SHORT_WORD_LENGTH = 2;    // TBD: configuable
    private static int MAX_SHORT_WORD_NO = 1;    // TBD: configuable
}

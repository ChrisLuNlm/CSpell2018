package gov.nih.nlm.nls.cSpell.Corrector;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import gov.nih.nlm.nls.cSpell.Util.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
import gov.nih.nlm.nls.cSpell.Dictionary.*;
import gov.nih.nlm.nls.cSpell.Candidates.*;
import gov.nih.nlm.nls.cSpell.Ranker.*;
import gov.nih.nlm.nls.cSpell.Api.*;
/*****************************************************************************
* This class is to correct merge and update the in token list by the specified
* mergeObjList and inTokenList.
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
public class MergeCorrector
{
    // private constructor
    /**
    * Private constructor so no one can instantiate
    */
    private MergeCorrector()
    {
    }
    // clean up mergeObjList:
    // 1. contain, remove the previous one
    // 2. overlap, remove the latter one
    // This is a quick fix for window = 2. the permanemnt fix should be a 
    // real-time update on each merge
    private static ArrayList<MergeObj> CleanUpMergeObjList(
        ArrayList<MergeObj> mergeObjList)
    {
        ArrayList<MergeObj> outMergeObjList = new ArrayList<MergeObj>();
        boolean skipNext = false;
        for(int i = 0; i < mergeObjList.size(); i++)
        {
            MergeObj mergeObj1 = mergeObjList.get(i);
            if(i < mergeObjList.size()-1)
            {
                MergeObj mergeObj2 = mergeObjList.get(i+1);    // next mergeObj
                int startPos1 = mergeObj1.GetStartPos();
                int startPos2 = mergeObj2.GetStartPos();
                int endPos1 = mergeObj1.GetEndPos();
                int endPos2 = mergeObj2.GetEndPos();
                // mergeObj2 contains mergeObj1
                if((startPos1 == startPos2)
                && (endPos1 < endPos2))
                {
                    continue;
                }
                // merObj2 has overlap with mergeObj1
                else if((startPos2 > startPos1)
                && (startPos2 < endPos1))
                {
                    outMergeObjList.add(mergeObj1);
                    skipNext = true;
                }
                else
                {
                    if(skipNext == true)
                    {
                        skipNext = false;
                    }
                    else
                    {
                        outMergeObjList.add(mergeObj1);
                    }
                }
            }
            else
            {
                // add the last mergeObj
                if(skipNext == false)
                {
                    outMergeObjList.add(mergeObj1);
                }
            }
        }
        return outMergeObjList;
    }
    // public method
    // the input mergeObjList is in the same order of index as inTokenList
    // TBD: has bug: "imple ment ation" => implementimplementation
    public static ArrayList<TokenObj> CorrectTokenListByMerge(
        ArrayList<TokenObj> inTokenList, ArrayList<MergeObj> mergeObjList,
        String procHistStr, boolean debugFlag, CSpellApi cSpellApi)
    {
        // 0. unify the mergeObjList to remove contain and overlap
        ArrayList<MergeObj> mergeObjListC = CleanUpMergeObjList(mergeObjList);
        
        ArrayList<TokenObj> outTokenList = new ArrayList<TokenObj>();
        // 1. go through all mergeObj
        int curIndex = 0;
        for(MergeObj mergeObj:mergeObjListC)
        {
            //System.out.println(mergeObj.ToString());
            int startIndex = mergeObj.GetStartIndex();
            int endIndex = mergeObj.GetEndIndex();
            // 1. update tokens before merge start
            for(int i = curIndex; i < startIndex; i++)
            {
                outTokenList.add(inTokenList.get(i));
            }
            // 2. update merge at target
            String mergeWord = mergeObj.GetMergeWord();
            String orgMergeWord = mergeObj.GetOrgMergeWord();
            String tarWord = mergeObj.GetTarWord();
            TokenObj mergeTokenObj = new TokenObj(orgMergeWord, mergeWord);
            // update process history
            for(int i = startIndex; i <= endIndex; i++)
            {
                // merge focus token
                if(i == mergeObj.GetTarIndex())
                {
                    cSpellApi.UpdateCorrectNo();
                    mergeTokenObj.AddProcToHist(procHistStr
                        + TokenObj.MERGE_START_STR + tarWord 
                        + TokenObj.MERGE_END_STR);
                    //DebugPrint.PrintCorrect("NW", 
                    DebugPrint.PrintCorrect(procHistStr, 
                        "MergeCorrector (" + tarWord + ")", 
                        orgMergeWord, mergeWord, debugFlag);     
                }
                else    // not merge focus token, context
                {
                    TokenObj contextToken = inTokenList.get(i);
                    ArrayList<String> contextProcHist 
                        = contextToken.GetProcHist();
                    for(String procHist:contextProcHist)
                    {
                        mergeTokenObj.AddProcToHist(procHist 
                            + TokenObj.MERGE_START_STR 
                            + contextToken.GetTokenStr()
                            + TokenObj.MERGE_END_STR);
                    }
                }
            }
            outTokenList.add(mergeTokenObj);
            curIndex = endIndex + 1;
        }
        // 2. add tokens after the last merge Obj
        for(int i = curIndex; i < inTokenList.size(); i++)
        {
            outTokenList.add(inTokenList.get(i));
        }
        return outTokenList;
    }
    // test driver
    public static void main(String[] args)
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java MergeCorrector <configFile>");
            System.exit(0);
        }
        
        // init
    }
    // data member
}

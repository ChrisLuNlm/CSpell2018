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
* This class is to process non-word merge correction.
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
public class ProcNonWordMerge
{
    // private constructor
    /**
    * Private constructor so no one can instantiate
    */
    private ProcNonWordMerge()
    {
    }
    // public method
    // TBD, dummy, use MergeCandidates.java.new as reference
    public static ArrayList<TokenObj> Process(ArrayList<TokenObj> inTokenList, 
        CSpellApi cSpellApi, boolean debugFlag)
    {
        DebugPrint.PrintProcess("2. NonWord-Merge", debugFlag);
        DebugPrint.PrintInText(TextObj.TokenListToText(inTokenList), debugFlag);
        // pre-porcess
        // update Pos for the inTokenList
        TextObj.UpdateIndexPos(inTokenList);
        // 1. remove non space-token and convert to non-space-token list
        ArrayList<TokenObj> nonSpaceTokenList
            = TextObj.GetNonSpaceTokenObjList(inTokenList);
        // 2. process: go through each token for detection and correction
        // to find merge corrections (mergeObjList)
        int index = 0;
        ArrayList<MergeObj> mergeObjList = new ArrayList<MergeObj>();
        while(index < inTokenList.size())
        {
            TokenObj curTokenObj = inTokenList.get(index);
            
            // update the tarPos
            // not space-token
            if(curTokenObj.IsSpaceToken() == false)
            {
                int tarPos = inTokenList.get(index).GetPos();
                // correct term is the highest ranked candidates
                MergeObj mergeObj = NonWordMergeCorrector.GetCorrectTerm(
                    tarPos, nonSpaceTokenList, cSpellApi, debugFlag);
                if(mergeObj == null)    // no merge correction
                {
                    index++;
                }
                else    // has merge correction
                {
                    mergeObjList.add(mergeObj);
                    // next token after end token, this ensure no overlap merge
                    index = mergeObj.GetEndIndex() + 1;    
                }
            }
            else    // space token
            {
                // update index 
                index++;
            }
        }
        // update the output for merge for the whole inTokenList, 
        // has to update after the loop bz merge might 
        // happen to the previous token
        // update the tokenObj up to the merge, then go to the next token
        // update operation info also
        ArrayList<TokenObj> outTokenList 
            = MergeCorrector.CorrectTokenListByMerge(
            inTokenList, mergeObjList, TokenObj.HIST_NW_M, debugFlag,
            cSpellApi);
                
        return outTokenList;
    }
    private static void TestProcess(CSpellApi cSpellApi)
    {
        // init
        // all lowerCase
        String inText = "She had problems dur ing her pregnancies. That is a dis appoint ment. Good!";
        // test process:  must use ArrayList<TextObj>
        ArrayList<TokenObj> inTokenList = TextObj.TextToTokenList(inText);
        boolean debugFlag = true;
        // process
        ArrayList<TokenObj> outTokenList = Process(inTokenList, cSpellApi, 
            debugFlag);
        // result
        String outText = TextObj.TokenListToText(outTokenList);
        // print out
        System.out.println("------ GetCorrection by Process( ) ------");
        System.out.println("In: [" + inText + "]");
        System.out.println("Out: [" + outText + "]");
        System.out.println("----- Details -----------");
        // print out operation details
        System.out.println(TextObj.TokenListToOperationDetailStr(outTokenList));
    }
    // test driver
    public static void main(String[] args)
    {
        String configFile = "../data/Config/cSpell.properties";
        if(args.length > 0)
        {
            System.out.println("Usage: java SpellCorrection <configFile>");
            System.exit(0);
        }
        
        // init
        CSpellApi cSpellApi = new CSpellApi(configFile);
        // test
        TestProcess(cSpellApi);
    }
    // data member
}

package gov.nih.nlm.nls.cSpell.Corrector;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import gov.nih.nlm.nls.cSpell.Util.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
import gov.nih.nlm.nls.cSpell.Dictionary.*;
import gov.nih.nlm.nls.cSpell.Candidates.*;
import gov.nih.nlm.nls.cSpell.Ranker.*;
import gov.nih.nlm.nls.cSpell.NdCorrector.*;
import gov.nih.nlm.nls.cSpell.Api.*;
/*****************************************************************************
* This class is to core program to process correction.
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
public class ProcCorrector 
{
    // private constructor
    /**
    * Private constructor so no one can instantiate
    */
    private ProcCorrector()
    {
    }
    // public method
    public static ArrayList<TokenObj> ProcessByTokenObj(
        ArrayList<TokenObj> inTokenList, CSpellApi cSpellApi, boolean debugFlag)
    {
        // fucnMode
        int funcMode = cSpellApi.GetFuncMode();
        // init non-dictionary correction, this process is always on
        int ndMaxSplitNo = cSpellApi.GetCanNdMaxSplitNo();    // ND split
        HashMap<String, String> infExpMap 
            = cSpellApi.GetInformalExpressionMap();
        ArrayList<TokenObj> ndTokenList = ProcNdCorrector.Process(
            inTokenList, ndMaxSplitNo, infExpMap, debugFlag);
        ArrayList<TokenObj> outTokenList = null;
        switch(funcMode)
        {
            case CSpellApi.FUNC_MODE_ND:
                outTokenList = ndTokenList;
                break;
            // TBD, NW 1-to-1 and split need to be seperated    
            case CSpellApi.FUNC_MODE_NW_1:
            case CSpellApi.FUNC_MODE_NW_S:
            case CSpellApi.FUNC_MODE_NW_S_1:
                // non-word one-to-one and split
                outTokenList = ProcNonWord.Process(
                    ndTokenList, cSpellApi, debugFlag);
                break;
            case CSpellApi.FUNC_MODE_NW_M:
                // non-word merge
                outTokenList = ProcNonWordMerge.Process(
                    ndTokenList, cSpellApi, debugFlag);
                break;
            case CSpellApi.FUNC_MODE_NW_A:
                // 1. non-word merge
                ArrayList<TokenObj> nwMergeList = ProcNonWordMerge.Process(
                    ndTokenList, cSpellApi, debugFlag);
                // 2. non-word one-to-one and split
                outTokenList = ProcNonWord.Process(nwMergeList, cSpellApi, 
                    debugFlag);
                break;
            // real-word one-to-one
            case CSpellApi.FUNC_MODE_RW_1: 
                // 1. non-word merge
                nwMergeList = ProcNonWordMerge.Process(ndTokenList, cSpellApi, 
                    debugFlag);
                // 2. non-word one-to-one and split
                ArrayList<TokenObj> nw1To1SplitList = ProcNonWord.Process(
                    nwMergeList, cSpellApi, debugFlag);
                // 3. real-word, 1-to-1 ...    
                outTokenList = ProcRealWord1To1.Process(nw1To1SplitList,
                    cSpellApi, debugFlag);
                break;
            // real-word split
            case CSpellApi.FUNC_MODE_RW_S:
                // 1. non-word merge
                nwMergeList = ProcNonWordMerge.Process(ndTokenList, cSpellApi, 
                    debugFlag);
                // 2. non-word one-to-one and split
                nw1To1SplitList = ProcNonWord.Process(
                    nwMergeList, cSpellApi, debugFlag);
                // 3. real-word one-to-one and split
                outTokenList = ProcRealWordSplit.Process(nw1To1SplitList,
                    cSpellApi, debugFlag);
                break;
            // real-word merge
            case CSpellApi.FUNC_MODE_RW_M:
                // 1. non-word merge
                nwMergeList = ProcNonWordMerge.Process(ndTokenList, cSpellApi, 
                    debugFlag);
                // 2. non-word one-to-one and split
                nw1To1SplitList = ProcNonWord.Process(
                    nwMergeList, cSpellApi, debugFlag);
                // 3. real-word merge
                outTokenList = ProcRealWordMerge.Process(nw1To1SplitList, 
                    cSpellApi, debugFlag);
                break;
            // real-word merge and split
            case CSpellApi.FUNC_MODE_RW_M_S:
                // 1. non-word merge
                nwMergeList = ProcNonWordMerge.Process(ndTokenList, cSpellApi, 
                    debugFlag);
                // 2. non-word one-to-one and split
                nw1To1SplitList = ProcNonWord.Process(
                    nwMergeList, cSpellApi, debugFlag);
                // 3. real-word merge
                ArrayList<TokenObj> rwMergeList = ProcRealWordMerge.Process(
                    nw1To1SplitList, cSpellApi, debugFlag);
                // 4. real-word split
                outTokenList = ProcRealWordSplit.Process(rwMergeList,
                    cSpellApi, debugFlag);
                break;
            // real-word all: merge, split, 1-to-1
            case CSpellApi.FUNC_MODE_RW_A:
                // 1. non-word merge
                nwMergeList = ProcNonWordMerge.Process(ndTokenList, cSpellApi, 
                    debugFlag);
                // 2. non-word one-to-one and split
                nw1To1SplitList = ProcNonWord.Process(
                    nwMergeList, cSpellApi, debugFlag);
                // 3. real-word merge
                rwMergeList = ProcRealWordMerge.Process(
                    nw1To1SplitList, cSpellApi, debugFlag);
                // 4. real-word split
                ArrayList<TokenObj> rwSplitList = ProcRealWordSplit.Process(
                    rwMergeList, cSpellApi, debugFlag);
                // 5. real-word, 1-to-1
                outTokenList = ProcRealWord1To1.Process(rwSplitList,
                    cSpellApi, debugFlag);
                break;
        }
        return outTokenList;    
    }
    // private method
    private static void TestProcess(CSpellApi cSpellApi)
    {
        // init
        // test non-word, one-to-one, split, and merge correction, all lowerCase
        String inText = "hotflashes and knowaboutare not forr playsure dur ing my disa ppoint ment.";
        // test process:  must use ArrayList<TextObj>
        ArrayList<TokenObj> inTokenList = TextObj.TextToTokenList(inText);
        boolean debugFlag = true;
        // process
        ArrayList<TokenObj> outTokenList 
            = ProcessByTokenObj(inTokenList, cSpellApi, debugFlag);
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

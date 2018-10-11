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
* This class is to process real-word split correction.
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
public class ProcRealWordSplit
{
    // private constructor
    /**
    * Private constructor so no one can instantiate
    */
    private ProcRealWordSplit()
    {
    }
    // public method
    // Use: for loop, the latest and greatest implementation
    // original implementation with for loop, To be deleted
    // the core of spell-correction, include split
    // inTokenList is the whole text
    public static ArrayList<TokenObj> Process(ArrayList<TokenObj> inTokenList, 
        CSpellApi cSpellApi, boolean debugFlag)
    {
        DebugPrint.PrintProcess("6. RealWord-Split", debugFlag);
        DebugPrint.PrintInText(TextObj.TokenListToText(inTokenList), debugFlag);
        // init the output TokenList
        ArrayList<TokenObj> outTokenList = new ArrayList<TokenObj>();
        // process: go through each token for detection and correction
        // for real-word split correction
        int tarPos = 0;    // the position of the tokenObj in the inTokenList
        // remove space token from the list
        ArrayList<TokenObj> nonSpaceTokenList
            = TextObj.GetNonSpaceTokenObjList(inTokenList);
        // use the inTokenList to keep the same space token
        TokenObj outTokenObj = null;
        for(TokenObj tokenObj:inTokenList)
        {
            // skip space token
            if(tokenObj.IsSpaceToken() == false)
            {
                // correct term is the highest ranked candidate
                outTokenObj = RealWordSplitCorrector.GetCorrectTerm(
                    tokenObj, cSpellApi, debugFlag, tarPos, nonSpaceTokenList);
                // used tarPos for context module
                tarPos++;
            }
            else
            {
                outTokenObj = tokenObj;
            }
            // add the corrected tokenObj to the output token list
            // use FlatMap because there might be a split
            // TBD
            Split1To1Corrector.AddSplit1To1Correction(
                outTokenList, outTokenObj);
        }
        return outTokenList;
    }
    private static void TestProcess(CSpellApi cSpellApi)
    {
        // init
        // all lowerCase
        String inText = "hotflashes and knowaboutare not forr playsure.";
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
        if(args.length == 1)
        {
            configFile = args[0];
        }
        else if(args.length > 0)
        {
            System.out.println("Usage: java ProcessNonWord <configFile>");
            System.exit(0);
        }
        
        // init
        CSpellApi cSpellApi = new CSpellApi(configFile);
        // test
        TestProcess(cSpellApi);
    }
    // data member
}

package gov.nih.nlm.nls.cSpell.Api;
import java.util.*;
import java.util.stream.*;
import java.util.function.*;
import gov.nih.nlm.nls.cSpell.Util.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
import gov.nih.nlm.nls.cSpell.NdCorrector.*;
import gov.nih.nlm.nls.cSpell.Dictionary.*;
import gov.nih.nlm.nls.cSpell.Candidates.*;
import gov.nih.nlm.nls.cSpell.Ranker.*;
import gov.nih.nlm.nls.cSpell.Corrector.*;
/*****************************************************************************
* This class is API for all spelling error correction in CSpell.
* It is called by CSpellApi.
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
public class CorrectionApi
{
    // private constructor
    /**
    * private constructor for CorrectionApi, no one can instantiate.
    */
    private CorrectionApi()
    {
    }
    // Correction
    public static String ProcessToStr(String inText, CSpellApi cSpellApi)
    {
        boolean debugFlag = false;
        return ProcessToStr(inText, cSpellApi, debugFlag);
    }
    // use String
    public static String ProcessToStr(String inText, CSpellApi cSpellApi,
        boolean debugFlag)
    {
        // 1. input
        ArrayList<TokenObj> inTokenList = TextObj.TextToTokenList(inText);
        ArrayList<TokenObj> outTokenList 
            = ProcessByTokenObj(inTokenList, cSpellApi, debugFlag);
        // 2. convert results to text
        String outText = TextObj.TokenListToText(outTokenList);
        return outText;
    }
    public static ArrayList<TokenObj> ProcessByTokenObj(
        ArrayList<TokenObj> inTokenList, CSpellApi cSpellApi)
    {
        boolean debugFlag = false;
        return ProcessByTokenObj(inTokenList, cSpellApi, debugFlag);
    }
    // Core method: of spell-correction, include split
    public static ArrayList<TokenObj> ProcessByTokenObj(
        ArrayList<TokenObj> inTokenList, CSpellApi cSpellApi, boolean debugFlag)
    {
        return ProcCorrector.ProcessByTokenObj(inTokenList, cSpellApi, 
            debugFlag);
    }
    // privat methods
    private static void Test(CSpellApi cSpellApi)
    {
        System.out.println("----- Test Pre-Correction Text: -----");
        String inText = "We cant spel ACHindex 987Pfimbria dianosed.";
        //CSpellApi cSpellApi = new CSpellApi(configFile);
        ArrayList<TokenObj> inTokenList = TextObj.TextToTokenList(inText);
        ArrayList<TokenObj> outTokenList = ProcessByTokenObj(inTokenList, 
            cSpellApi); 
        String outText = TextObj.TokenListToText(outTokenList); 
        // print out
        System.out.println("--------------------");
        System.out.println("In: [" + inText + "]");
        System.out.println("Out: [" + outText + "]");
        System.out.println("----- Details -----------");
        int index = 0;
        for(TokenObj tokenObj:outTokenList)
        {
            System.out.println(index + "|" + tokenObj.ToString());
            index++;
        }
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
            System.out.println("Usage: java CorrectionApi <configFile>");
            System.exit(0);
        }
        // init, read in from config
        CSpellApi cSpellApi = new CSpellApi(configFile);
        Test(cSpellApi);
    }
    // data member
    // processes related data
}

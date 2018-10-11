package gov.nih.nlm.nls.cSpell.Api;
import java.util.*;
import java.util.stream.*;
import java.util.function.*;
import gov.nih.nlm.nls.cSpell.Util.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
import gov.nih.nlm.nls.cSpell.NdCorrector.*;
/*****************************************************************************
* This class is API of non-dictionary-based correction in CSpell. It is called
* by CSpellApi.
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
public class NdCorrectionApi
{
    // public constructor
    /**
    * Private constructor for NdCorrectionApi so no one can instantiate.
    */
    private NdCorrectionApi()
    {
    }
    // pre-Correction
    public static String ProcessByStr(String inText, int maxSpRecursiveNo, 
        HashMap<String, String> infExpMap) 
    {
        boolean debugFlag = false;
        return ProcessByStr(inText, maxSpRecursiveNo, infExpMap, debugFlag);
    }
    // use TextObj (instead of TextIoObj)
    public static String ProcessByStr(String inText, int maxSpRecursiveNo,
        HashMap<String, String> infExpMap, boolean debugFlag)
    {
        ArrayList<TokenObj> outTokenList 
            = Process(inText, maxSpRecursiveNo, infExpMap, debugFlag);
        // result text: convert from TokenObj to str
        String outText = TextObj.TokenListToText(outTokenList);
        return outText;
    }
    public static ArrayList<TokenObj> Process(String inText, 
        int maxSpRecursiveNo, HashMap<String, String> infExpMap)
    {
        ArrayList<TokenObj> inTokenList = TextObj.TextToTokenList(inText);
        boolean debugFlag = false;
        return Process(inText, maxSpRecursiveNo, infExpMap, debugFlag);
    }
    //the core of pre-correction api
    public static ArrayList<TokenObj> Process(String inText, 
        int maxSpRecursiveNo, HashMap<String, String> infExpMap, 
        boolean debugFlag)
    {
        // 1. input
        ArrayList<TokenObj> inTokenList = TextObj.TextToTokenList(inText);
        // 2. process on each tokenObj
        return ProcNdCorrector.Process(inTokenList, maxSpRecursiveNo, 
            infExpMap, debugFlag);
    }
    // privat methods
    private static void Test(String configFile)
    {
        // init
        System.out.println("----- Test Pre-Correction Text: -----");
        String inText = "We  cant theredve hell.Plz u r good123. ";
        CSpellApi cSpellApi = new CSpellApi(configFile);
        int ndMaxSplitNo = cSpellApi.GetCanNdMaxSplitNo();
        HashMap<String, String> infExpMap
            = cSpellApi.GetInformalExpressionMap();
        String outText = ProcessByStr(inText, ndMaxSplitNo, infExpMap);
        // print out
        System.out.println("--------------------");
        System.out.println("In: [" + inText + "]");
        System.out.println("Out: [" + outText + "]");
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
            System.out.println("Usage: java NdCorrectionApi <configFile>");
            System.exit(0);
        }
        Test(configFile);
    }
    // data member
    // processes related data
}

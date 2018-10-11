package gov.nih.nlm.nls.cSpell.NdCorrector;
import java.util.*;
import java.util.stream.*;
import java.util.function.*;
import gov.nih.nlm.nls.cSpell.Util.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
import gov.nih.nlm.nls.cSpell.Api.*;
/*****************************************************************************
* This class is core for processing non-dictionary-based correction in CSpell.
* Including:
* <ul>
* <li>XmlHtmlHandler
* <li>LeadingDigitSplitter
* <li>LeadingPuncSplitter
* <li>EndingDigitSplitter
* <li>EndingPuncSplitter
* <li>InformalExpHandler
* </ul>
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
public class ProcNdCorrector
{
    // private constructor
    /**
    * Private constructor includes only static methods and no one can 
    * instaniate.
    */
    private ProcNdCorrector()
    {
    }
    // pre-Correction
    // the core process of pre-correction
    // use Java 8 stream
    public static ArrayList<TokenObj> Process(ArrayList<TokenObj> inTokenList, 
        int ndMaxSplitNo, HashMap<String, String> infExpMap)
    {
        boolean debugFlag = false;
        return Process(inTokenList, ndMaxSplitNo, infExpMap, debugFlag);
    }
    public static ArrayList<TokenObj> Process(ArrayList<TokenObj> inTokenList, 
        int ndMaxSplitNo, HashMap<String, String> infExpMap, 
        boolean debugFlag)
    {
        DebugPrint.PrintProcess("1. NonDictionary", debugFlag);
        DebugPrint.PrintInText(TextObj.TokenListToText(inTokenList), debugFlag);
        // process on each tokenObj
        ArrayList<TokenObj> outTokenList = new ArrayList<TokenObj>(
            inTokenList.stream()
            //inTokenList.parallelStream()    // parallel processing 
            .map(token -> XmlHtmlHandler.Process(token, debugFlag))    
            .map(token -> EndingPuncSplitter.Process(token, ndMaxSplitNo, debugFlag))    
            // must flatmap after split
            .flatMap(token -> TextObj.FlatTokenToArrayList(token).stream())
            .map(token -> LeadingPuncSplitter.Process(token, ndMaxSplitNo, debugFlag))
            // must flatmap after split
            .flatMap(token -> TextObj.FlatTokenToArrayList(token).stream())
            .map(token -> LeadingDigitSplitter.Process(token, debugFlag)) 
            // must flatmap after split
            .flatMap(token -> TextObj.FlatTokenToArrayList(token).stream())
            .map(token -> EndingDigitSplitter.Process(token, debugFlag))
            // must flatmap after split
            .flatMap(token -> TextObj.FlatTokenToArrayList(token).stream())
            // informal expression
            .map(token -> InformalExpHandler.Process(token, infExpMap, debugFlag))    
            .collect(Collectors.toList()));
        return outTokenList;
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
        boolean debugFlag = true;    
            
        // 1. convert input to TokenObjs
        ArrayList<TokenObj> inTokenList = TextObj.TextToTokenList(inText);
        ArrayList<TokenObj> outTokenList = ProcNdCorrector.Process(
            inTokenList, ndMaxSplitNo, infExpMap, debugFlag);
        String outText = TextObj.TokenListToText(outTokenList); 
        // print out
        System.out.println("--------------------");
        System.out.println("In: [" + inText + "]");
        System.out.println("Out: [" + outText + "]");
        System.out.println("----- Details -----------");
        int index = 0;
        for(TokenObj tokenObj:outTokenList)
        {
            System.out.println(index + "|" + tokenObj.ToHistString());
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
            System.out.println("Usage: java NdCorrector <configFile>");
            System.exit(0);
        }
        Test(configFile);
    }
    // data member
    // processes related data
}

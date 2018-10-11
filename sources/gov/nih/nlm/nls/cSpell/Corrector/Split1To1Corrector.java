package gov.nih.nlm.nls.cSpell.Corrector;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import gov.nih.nlm.nls.cSpell.NlpUtil.*;
/*****************************************************************************
* This class is the java class to correct split and 1To1.
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
public class Split1To1Corrector 
{
    // public constructor
    /**
    * Private constructor
    */
    private Split1To1Corrector()
    {
    }
    // 3 operations:
    // convert a tokenObj to a arrayList of tokenObjs:
    // 1. merge (delete) a tokenObj if the str is empty (length = 0)
    // 2. keep the same tokenObj if str is a single word
    // 3. split a tokenObj if the str contains space
    public static void AddSplit1To1Correction(ArrayList<TokenObj> inList, 
        TokenObj inToken)
    {    
        String tokenStr = inToken.GetTokenStr();
        // 1. do not add to the list if the token is empty
        if((tokenStr == null) || (tokenStr.length() == 0))
        {
            // do nothing
        }
        // 2. keep the same tokenObj if there is no change
        // 1-to-1 correction
        else if(TermUtil.IsMultiword(tokenStr) == false)
        {
            Add1To1Correction(inList, inToken);
            // TB Deleted
            //inList.add(inToken);
        }
        // 3. split a tokenObj to an arrayList if the str has space
        else
        {
            AddSplitCorrection(inList, inToken);
            /* TB deleted
            ArrayList<TokenObj> tempTokenList = new ArrayList<TokenObj>(); 
            // keep token and delimiters
            String[] tokenArray = tokenStr.split(TextObj.patternStrSpace_);
            tempTokenList = new ArrayList<TokenObj>(Arrays.stream(tokenArray)
                .map(token -> new TokenObj(inToken, token))
                .collect(Collectors.toList()));
            inList.addAll(tempTokenList);
            */
        }
    }
    // private method
    private static void Add1To1Correction(ArrayList<TokenObj> inList,
        TokenObj inToken)
    {
        inList.add(inToken);
    }
    // use flat map to add split words to the list
    private static void AddSplitCorrection(ArrayList<TokenObj> inList,
        TokenObj inToken)
    {
        ArrayList<TokenObj> tempTokenList = new ArrayList<TokenObj>(); 
        // keep token and delimiters
        String tokenStr = inToken.GetTokenStr();
        String[] tokenArray = tokenStr.split(TextObj.patternStrSpace_);
        // flat Map
        tempTokenList = new ArrayList<TokenObj>(Arrays.stream(tokenArray)
            .map(token -> new TokenObj(inToken, token))
            .collect(Collectors.toList()));
        inList.addAll(tempTokenList);
    }
    // test driver
    public static void main(String[] args)
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java Split1To1Corrector <inFile>");
            System.exit(0);
        }
        
        // init
    }
    // data member
}

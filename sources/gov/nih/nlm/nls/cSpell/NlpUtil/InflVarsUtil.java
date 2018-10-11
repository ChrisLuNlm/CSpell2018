package gov.nih.nlm.nls.cSpell.NlpUtil;
import java.util.*;
import java.util.regex.*;
import gov.nih.nlm.nls.cSpell.Util.*;
/*****************************************************************************
* This NLP utility class generates simplified regular inflection variants.
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
public class InflVarsUtil 
{
    /**
    * Private constructor 
    */
    private InflVarsUtil()
    {
    }
    // public methods
    // A simplfied way to compare if two strings are inflectional varaint to 
    // each other
    public static boolean IsInflectionVar(String str1, String str2)
    {
        boolean flag = false;
        // 1. to assign base string by comparing length
        int len1 = str1.length();
        int len2 = str2.length();
        String baseStr = str1;
        String inflStr = str2;
        // same length, not inflectional vars, exclude irreg, such as see|saw
        if(len1 == len2)
        {
            return false;
        }
        else if(len1 > len2)    // assume the short string is the base
        {
            baseStr = str2;
            inflStr = str1;
        }
        // check the inflections
        HashSet<String> inflSet = InflVarsUtil.GetInflVars(baseStr);
        flag = inflSet.contains(inflStr);
        return flag;
    }
    public static HashSet<String> GetInflVarsVerb(String baseStr)
    {
        HashSet<String> inflVarSet = new HashSet<String>();
        String inflVar = null;
        char lastChar = GetLastChar(baseStr);
        char last2Char = GetLast2Char(baseStr);
        String lastCharStr = new Character(lastChar).toString();
        String last2CharStr = new Character(last2Char).toString();
        if((baseStr.endsWith("s"))
        || (baseStr.endsWith("z"))
        || (baseStr.endsWith("x"))
        || (baseStr.endsWith("ch"))
        || (baseStr.endsWith("sh")))
        {
            inflVar = baseStr + "es";
            inflVarSet.add(inflVar);
            inflVar = baseStr + "ed";
            inflVarSet.add(inflVar);
            inflVar = baseStr + "ing";
            inflVarSet.add(inflVar);
        }
        else if(baseStr.endsWith("ie"))
        {
            inflVar = baseStr + "s";
            inflVarSet.add(inflVar);
            inflVar = baseStr + "d";
            inflVarSet.add(inflVar);
            inflVar = baseStr.substring(0, baseStr.length()-2) + "ying";
            inflVarSet.add(inflVar);
        }
        else if((baseStr.endsWith("ee"))
        || (baseStr.endsWith("oe"))
        || (baseStr.endsWith("ye")))
        {
            inflVar = baseStr + "s";
            inflVarSet.add(inflVar);
            inflVar = baseStr + "d";
            inflVarSet.add(inflVar);
            inflVar = baseStr + "ing";
            inflVarSet.add(inflVar);
        }
        else if((baseStr.endsWith("y"))
        && (consonants_.contains(last2CharStr)))
        {
            inflVar = baseStr.substring(0, baseStr.length()-1) + "ies";
            inflVarSet.add(inflVar);
            inflVar = baseStr.substring(0, baseStr.length()-1) + "ied";
            inflVarSet.add(inflVar);
            inflVar = baseStr.substring(0, baseStr.length()-1) + "ing";
            inflVarSet.add(inflVar);
        }
        else if((baseStr.endsWith("e"))
        && (eioySets_.contains(last2CharStr) == false))
        {
            inflVar = baseStr + "s";
            inflVarSet.add(inflVar);
            inflVar = baseStr + "d";
            inflVarSet.add(inflVar);
            inflVar = baseStr + "n";    // give to given, irreg
            inflVarSet.add(inflVar);
            inflVar = baseStr.substring(0, baseStr.length()-1) + "ing";
            inflVarSet.add(inflVar);
        }
        else
        {
            inflVar = baseStr + "s";
            inflVarSet.add(inflVar);
            inflVar = baseStr + "ed";
            inflVarSet.add(inflVar);
            inflVar = baseStr + "ing";
            inflVarSet.add(inflVar);
        }
        return inflVarSet;
    }
    public static HashSet<String> GetInflVarsNoun(String baseStr)
    {
        HashSet<String> inflVarSet = new HashSet<String>();
        String inflVar = null;
        char lastChar = GetLastChar(baseStr);
        char last2Char = GetLast2Char(baseStr);
        String lastCharStr = new Character(lastChar).toString();
        String last2CharStr = new Character(last2Char).toString();
        if((baseStr.endsWith("s"))
        || (baseStr.endsWith("z"))
        || (baseStr.endsWith("x"))
        || (baseStr.endsWith("ch"))
        || (baseStr.endsWith("sh")))
        {
            inflVar = baseStr + "es";
            inflVarSet.add(inflVar);
        }
        else if((baseStr.endsWith("y"))
        && (consonants_.contains(last2CharStr)))
        {
            inflVar = baseStr.substring(0, baseStr.length()-1) + "ies";
            inflVarSet.add(inflVar);
        }
        else if(baseStr.endsWith("ie"))
        {
            inflVar = baseStr + "s";
            inflVarSet.add(inflVar);
        }
        else
        {
            inflVar = baseStr + "s";
            inflVarSet.add(inflVar);
        }
        return inflVarSet;
    }
    public static HashSet<String> GetInflVarsAdj(String baseStr)
    {
        HashSet<String> inflVarSet = new HashSet<String>();
        String inflVar = null;
        char lastChar = GetLastChar(baseStr);
        char last2Char = GetLast2Char(baseStr);
        String last2CharStr = new Character(last2Char).toString();
        if((lastChar == 'y')
        && (consonants_.contains(last2CharStr)))
        {
            inflVar = baseStr.substring(0, baseStr.length()-1) + "ier";
            inflVarSet.add(inflVar);
            inflVar = baseStr.substring(0, baseStr.length()-1) + "iest";
            inflVarSet.add(inflVar);
        }
        else if(lastChar == 'e')
        {
            inflVar = baseStr + "r";
            inflVarSet.add(inflVar);
            inflVar = baseStr + "st";
            inflVarSet.add(inflVar);
        }
        else
        {
            inflVar = baseStr + "er";
            inflVarSet.add(inflVar);
            inflVar = baseStr + "est";
            inflVarSet.add(inflVar);
        }
        return inflVarSet;
    }
    // get all possible regular inflVars for a base 
    public static HashSet<String> GetInflVars(String baseStr)
    {
        HashSet<String> inflVarSet = GetInflVarsAdj(baseStr);
        inflVarSet.addAll(GetInflVarsNoun(baseStr));
        inflVarSet.addAll(GetInflVarsVerb(baseStr));
        return inflVarSet;
    }
    // get all possible base
    private static char GetLastChar(String inStr)
    {
        int length = inStr.length();
        char out = inStr.toLowerCase().charAt(length-1);
        return out;
    }
    private static char GetLast2Char(String inStr)
    {
        char out = ' ';    // an empty character
        int length = inStr.length();
        if(length >= 2)
        {
            out = inStr.toLowerCase().charAt(length-2);
        }
        return out;
    }
    // privage methods
    private static void Test(String inStr)
    {
        System.out.println("-------------------------------------"); 
        HashSet<String> inflVarSetN = GetInflVarsNoun(inStr);
        System.out.println("- noun(" + inStr + "): " + inflVarSetN);
        HashSet<String> inflVarSetV = GetInflVarsVerb(inStr);
        System.out.println("- verb(" + inStr + "): " + inflVarSetV);
        HashSet<String> inflVarSetA = GetInflVarsAdj(inStr);
        System.out.println("- adj(" + inStr + "): " + inflVarSetA);
        HashSet<String> inflVarSet = GetInflVars(inStr);
        System.out.println("- All(" + inStr + "): " + inflVarSet);
    }
    private static void Tests()
    {
        System.out.println("===== Unit Test of InflVarsUtil =====");
        Test("study");
        Test("test");
        Test("tie");
        Test("church");
        System.out.println("===== End of Unit Test =====");
    }
    private static void TestIsInflVars()
    {
        System.out.println("age|aged|" + IsInflectionVar("age", "aged"));
        System.out.println("aged|age|" + IsInflectionVar("aged", "age"));
        System.out.println("give|given|" + IsInflectionVar("give", "given"));
    }
    // test driver
    public static void main(String[] args)
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java InflVarsUtil");
            System.exit(0);
        }
        
        // test case and print out 
        Tests();
        TestIsInflVars();
    }
    // data member
    private static HashSet<String> vowels_ = new HashSet<String>();
    private static HashSet<String> eioySets_ = new HashSet<String>();
    private static HashSet<String> consonants_ = new HashSet<String>();
    static
    {
        vowels_.add("a");
        vowels_.add("e");
        vowels_.add("i");
        vowels_.add("o");
        vowels_.add("u");
        eioySets_.add("e");
        eioySets_.add("i");
        eioySets_.add("o");
        eioySets_.add("y");
        consonants_.add("b");
        consonants_.add("c");
        consonants_.add("d");
        consonants_.add("f");
        consonants_.add("g");
        consonants_.add("h");
        consonants_.add("j");
        consonants_.add("k");
        consonants_.add("l");
        consonants_.add("m");
        consonants_.add("n");
        consonants_.add("p");
        consonants_.add("q");
        consonants_.add("r");
        consonants_.add("s");
        consonants_.add("t");
        consonants_.add("v");
        consonants_.add("w");
        consonants_.add("x");
        consonants_.add("y");
        consonants_.add("z");
    }
}

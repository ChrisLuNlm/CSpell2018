package gov.nih.nlm.nls.cSpell.Dictionary;
import java.util.*;
/*****************************************************************************
* This class converts categories and inflections from number and name.
*
* <p><b>History:</b>
*
* @author chlu
*
* @version    V-2018
****************************************************************************/
public class CatInflConvert
{
    static public String GetCatName(String catNum)
    {
        return catName_.get(catNum);
    }
    static public String GetInflName(String inflNum)
    {
        return inflName_.get(inflNum);
    }
    static public String GetCatNum(String catName)
    {
        return catNum_.get(catName);
    }
    static public String GetInflNum(String inflName)
    {
        return inflNum_.get(inflName);
    }
    // data member
    private static HashMap<String, String> catNum_ 
        = new HashMap<String, String>(12);
    private static HashMap<String, String> catName_ 
        = new HashMap<String, String>(12);
    private static HashMap<String, String> inflNum_ 
        = new HashMap<String, String>(25);
    private static HashMap<String, String> inflName_ 
        = new HashMap<String, String>(25);
    static
    {
        catNum_.put("adj", "1"); 
        catNum_.put("adv", "2");
        catNum_.put("aux", "4");
        catNum_.put("compl", "8");
        catNum_.put("conj", "16");
        catNum_.put("det", "32");
        catNum_.put("modal", "64");
        catNum_.put("noun", "128");
        catNum_.put("prep", "256");
        catNum_.put("pron", "512");
        catNum_.put("verb", "1024");
        catName_.put("1", "adj"); 
        catName_.put("2", "adv");
        catName_.put("4", "aux");
        catName_.put("8", "compl");
        catName_.put("16", "conj");
        catName_.put("32", "det");
        catName_.put("64", "modal");
        catName_.put("128", "noun");
        catName_.put("256", "prep");
        catName_.put("512", "pron");
        catName_.put("1024", "verb");
        inflNum_.put("base", "1");
        inflNum_.put("comparative", "2");
        inflNum_.put("superlative", "4");
        inflNum_.put("plural", "8");
        inflNum_.put("presPart", "16");
        inflNum_.put("past", "32");
        inflNum_.put("pastPart", "64");
        inflNum_.put("pres3s", "128");
        inflNum_.put("positive", "256");
        inflNum_.put("singular", "512");
        inflNum_.put("infinitive", "1024");
        inflNum_.put("pres123p", "2048");
        inflNum_.put("pastNeg", "4096");
        inflNum_.put("pres123pNeg", "8192");
        inflNum_.put("pres1s", "16384");
        inflNum_.put("past1p23pNeg", "32768");
        inflNum_.put("past1p23p", "65536");
        inflNum_.put("past1s3sNeg", "131072");
        inflNum_.put("pres1p23p", "262144");
        inflNum_.put("pres1p23pNeg", "524288");
        inflNum_.put("past1s3s", "1048576");
        inflNum_.put("pres", "2097152");
        inflNum_.put("pres3sNeg", "4194304");
        inflNum_.put("presNeg", "8388608");
        inflName_.put("1", "base");
        inflName_.put("2", "comparative");
        inflName_.put("4", "superlative");
        inflName_.put("8", "plural");
        inflName_.put("16", "presPart");
        inflName_.put("32", "past");
        inflName_.put("64", "pastPart");
        inflName_.put("128", "pres3s");
        inflName_.put("256", "positive");
        inflName_.put("512", "singular");
        inflName_.put("1024", "infinitive");
        inflName_.put("2048", "pres123p");
        inflName_.put("4096", "pastNeg");
        inflName_.put("8192", "pres123pNeg");
        inflName_.put("16384", "pres1s");
        inflName_.put("32768", "past1p23pNeg");
        inflName_.put("65536", "past1p23p");
        inflName_.put("131072", "past1s3sNeg");
        inflName_.put("262144", "pres1p23p");
        inflName_.put("524288", "pres1p23pNeg");
        inflName_.put("1048576", "past1s3s");
        inflName_.put("2097152", "pres");
        inflName_.put("4194304", "pres3sNeg");
        inflName_.put("8388608", "presNeg");
    }
}

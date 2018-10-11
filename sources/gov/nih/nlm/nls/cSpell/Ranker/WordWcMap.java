package gov.nih.nlm.nls.cSpell.Ranker;
import java.io.*;
import java.util.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
/*****************************************************************************
* This class converts and stores word (key) and frequency (value) in Map.
*
* <p><b>History:</b>
* <ul>
* <li>2018 baseline
* </ul>
*
* @author chlu
*
* @version    V-2018
****************************************************************************/
public class WordWcMap
{
    // public constructor
    public WordWcMap(String inFile)
    {
        InitMap(inFile, false);
    }
    public WordWcMap(String inFile, boolean verboseFlag)
    {
        InitMap(inFile, verboseFlag);
    }
    // public method
    public long GetMaxWc()
    {
        return maxWc_;
    }
    public long GetTotalWc()
    {
        return totalWc_;
    }
    public long GetTotalWordNo()
    {
        return totalWordNo_;
    }
    public HashMap<String, Integer> GetWordWcMap()
    {
        return wordWcMap_;
    }
    // private method
    private void InitMap(String inFile, boolean verboseFlag)
    {
        if(verboseFlag == true)
        {
            System.out.println("- Get WordWcMap from: " + inFile);
        }
        int lineNo = 0;
        String line = null;
        try
        {
            BufferedReader in = new BufferedReader(new InputStreamReader(
                new FileInputStream(inFile), "UTF-8"));
            // read in line by line from a file
            while((line = in.readLine()) != null)
            {
                lineNo++;
                StringTokenizer buf
                    = new StringTokenizer(line, GlobalVars.FS_STR);
                Integer wc = new Integer(buf.nextToken());
                String word = buf.nextToken();
                if((word.startsWith("[") && word.endsWith("]")) == false)
                {
                    wordWcMap_.put(word, wc);
                    totalWc_ += wc;
                    totalWordNo_++;
                    maxWc_ = (wc > maxWc_?wc:maxWc_);
                }
            }
            // close
            in.close();
            // print out all word set
            if(verboseFlag == true)
            {
                System.out.println("-- Total line no: " + lineNo);
                System.out.println("-- Total word no: " + totalWordNo_ + " ("
                    + wordWcMap_.keySet().size() + ")");
                System.out.println("-- Total word count: " + totalWc_);
                System.out.println("-- Max. word count: " + maxWc_);
            }
        }
        catch(Exception e)
        {
            System.err.println("Line: " + lineNo + " - " + line);
            System.err.println("** ERR@WordWcMap( ), problem of reading file (" + inFile + ") @ line [" + lineNo + "]: " + line);
            System.err.println("Exception: " + e.toString());
        }
    }
    // test Driver
    public static void main(String[] args)
    {
        if(args.length > 0)
        {
            System.out.println("Usage: java WordWcMap");
            System.exit(0);
        }
        // test
        String inFile = "../data/Freq/baselineWordFreq.data";
        boolean verboseFlag = true;
        WordWcMap wordWcMap = new WordWcMap(inFile, verboseFlag);
    }
    // data member
    private long maxWc_ = 0;        // max word count in the map
    private long totalWc_ = 0;    // total word count in the Map
    private long totalWordNo_ = 0;    // total word no
    private HashMap<String, Integer> wordWcMap_ 
        = new HashMap<String, Integer>();    // key: word, value:WC
}

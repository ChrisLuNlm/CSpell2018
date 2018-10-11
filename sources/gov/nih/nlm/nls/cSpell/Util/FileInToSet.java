package gov.nih.nlm.nls.cSpell.Util;
import java.nio.file.*;
import java.nio.charset.*;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.*;
import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.nio.charset.*;
import java.util.function.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
/*****************************************************************************
* This is a utility class for file input to a Set. It read input from a file 
* and convert to a set.
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
public class FileInToSet
{
    public static Set<String> GetSetByLine(String inFile)
    {
        boolean verboseFlag = false;
        boolean lowerCaseFlag = true;
        return GetSetByLine(inFile, lowerCaseFlag, verboseFlag);
    }
    // use the whole line
    public static Set<String> GetSetByLine(String inFile, boolean lowerCaseFlag)
    {
        boolean verboseFlag = false;
        return GetSetByLine(inFile, lowerCaseFlag, verboseFlag);
    }
    public static Set<String> GetSetByLine(String inFile, boolean lowerCaseFlag,
        boolean verboseFlag)
    {
        Set<String> outSet = null;
        if(verboseFlag == true)
        {
            System.out.println("- Get Set by line from: " + inFile);
        }
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                new FileInputStream(inFile), "UTF-8"));
            /* nio does not support certain char, used the old way
            BufferedReader reader = Files.newBufferedReader(
                Paths.get(inFile), Charset.forName("UTF-8"));
            */
            if(lowerCaseFlag == true)    // not case sensitive
            {
                outSet = reader.lines()
                    .map(str -> str.toLowerCase())
                    .collect(toSet());
            }
            else
            {
                outSet = reader.lines().collect(toSet());
            }
            // close
            reader.close();
        }
        catch (Exception x1)
        {
            System.err.println("** Err@FileInToSet.GetSetByLine( ): " 
                + x1.toString());
        }
        return outSet;
    }
    public static HashSet<String> GetHashSetByLine(String inFile)
    {
        Set<String> outSet = GetSetByLine(inFile);
        return new HashSet<String>(outSet);
    }
    public static HashSet<String> GetHashSetByLine(String inFile,
        boolean lowerCaseFlag)
    {
        Set<String> outSet = GetSetByLine(inFile, lowerCaseFlag);
        return new HashSet<String>(outSet);
    }
    // use the specified field
    public static Set<String> GetSetByField(String inFile, int fieldNo,
        boolean lowercaseFlag)
    {
        boolean verboseFlag = false;
        return GetSetByField(inFile, fieldNo, lowercaseFlag, verboseFlag);
    }
    public static Set<String> GetSetByField(String inFile, int fieldNo, 
        boolean lowercaseFlag, boolean verboseFlag)
    {
        if(verboseFlag == true)
        {
            System.out.println("- Get Set by field from: " + inFile);
        }
        int fNo = fieldNo - 1;    // change from 1 to 0
        Set<String> outSet = null;
        try
        {
            BufferedReader reader = Files.newBufferedReader(
                Paths.get(inFile), Charset.forName("UTF-8"));
            // lowercase
            if(lowercaseFlag == true)
            {
                outSet = reader.lines()    
                    //.parallel()    // seems slower
                    .filter(line -> line.startsWith("#") == false)
                    .map(line -> line.split("\\|")[fNo].toLowerCase())
                    //.distinct() // no need, bz it is collected to set
                    .collect(toSet());
            }
            else
            {
                outSet = reader.lines()    
                    //.parallel()    // seems slower
                    .filter(line -> line.startsWith("#") == false)
                    .map(line -> line.split("\\|")[fNo])
                    //.distinct()
                    .collect(toSet());
            }
            
            reader.close();
        }
        catch (Exception x1)
        {
            System.err.println("** Err@FileInToSet.GetSetByField( ): "
                + x1.toString());
        }
        return outSet;
    }
    public static HashSet<String> GetHashSetByField(String inFile,
        int fieldNo, boolean lowercaseFlag)
    {
        Set<String> outSet = GetSetByField(inFile, fieldNo, lowercaseFlag);
        return new HashSet<String>(outSet);
    }
}

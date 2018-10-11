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
* This is a utility class for file input to Map. It reads input from a file 
* and convert to a map&lt;String, String&gt; or map&lt;String, 
* Set&lt;Strings&gt;&gt;.
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
public class FileInToMap
{
    // 1st field is key, stored in String
    // 2nd field is value, stored in String
    public static HashMap<String, String> GetHashMapByFields(String inFile)
    {
        boolean verboseFlag = false;
        return GetHashMapByFields(inFile, verboseFlag);
    }
    public static HashMap<String, String> GetHashMapByFields(String inFile,
        boolean verboseFlag)
    {
        // print out if verbose
        if(verboseFlag == true)
        {
            System.out.println("- Get HashMap by Fields from: " + inFile);
        }
        HashMap<String, String> outMap = new HashMap<String, String>();
        String line = null;
        try
        {
            BufferedReader reader = Files.newBufferedReader(
                Paths.get(inFile), Charset.forName("UTF-8"));
            
            // go through all lines
            while((line = reader.readLine()) != null)
            {
                if(line.startsWith(GlobalVars.CT_STR) == false)
                {
                    StringTokenizer buf 
                        = new StringTokenizer(line, GlobalVars.FS_STR);
                    String keyStr = buf.nextToken();
                    String valueStr = buf.nextToken();
                    outMap.put(keyStr, valueStr);
                }
            }
            // close
            reader.close();
        }
        catch (Exception x1)
        {
            System.err.println("** Err@FileInToMap.GetHashMapByFields( ): " 
                + x1.toString() + ", [" + line + "]");
        }
        return outMap;
    }
    // 1st field is key, stored in String
    // the rest fields are values, stored in hashSet<String>
    public static HashMap<String, HashSet<String>> GetHashMapSetByFields(
        String inFile)
    {
        boolean verboseFlag = false;
        return GetHashMapSetByFields(inFile, verboseFlag);
    }
    public static HashMap<String, HashSet<String>> GetHashMapSetByFields(
        String inFile, boolean verboseFlag)
    {
        if(verboseFlag == true)
        {
            System.out.println("- Get HashMapSet by Field from: " + inFile);
        }
        HashMap<String, HashSet<String>> outMap 
            = new HashMap<String, HashSet<String>>();
        String line = null;
        try
        {
            BufferedReader reader = Files.newBufferedReader(
                Paths.get(inFile), Charset.forName("UTF-8"));
            
            // go through all lines
            while((line = reader.readLine()) != null)
            {
                if(line.startsWith(GlobalVars.CT_STR) == false)
                {
                    StringTokenizer buf 
                        = new StringTokenizer(line, GlobalVars.FS_STR);
                    String keyStr = buf.nextToken();
                    HashSet<String> values = new HashSet<String>();
                    while(buf.hasMoreTokens() == true)
                    {
                        values.add(buf.nextToken());
                    }
                    outMap.put(keyStr, values);
                }
            }
            // close
            reader.close();
        }
        catch (Exception x1)
        {
            System.err.println("** Err@FileInToMap.GetHashMapSetByFields( ): " 
                + x1.toString() + ", [" + line + "]");
        }
        return outMap;
    }
}

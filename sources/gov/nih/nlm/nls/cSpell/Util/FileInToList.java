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
* This is a utility class for file input to List. It reads input from a file 
* and convert them to a list.
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
public class FileInToList
{
    // private constructor so no can instantiate
    private FileInToList()
    {
    }
    public static List<String> GetListByLine(String inFile)
    {
        boolean verboseFlag = false;
        return GetListByLine(inFile, verboseFlag);
    }
    public static List<String> GetListByLine(String inFile, boolean verboseFlag)
    {
        List<String> outList = null;
        if(verboseFlag == true)
        {
            System.out.println("- Get List by line from: " + inFile);
        }
        try
        {
            BufferedReader reader = Files.newBufferedReader(
                Paths.get(inFile), Charset.forName("UTF-8"));
            outList = reader.lines().collect(toList());
            // close
            reader.close();
        }
        catch (Exception x1)
        {
            System.err.println("** Err@FileInToList.GetListByLine( ): " 
                + x1.toString());
        }
        return outList;
    }
}

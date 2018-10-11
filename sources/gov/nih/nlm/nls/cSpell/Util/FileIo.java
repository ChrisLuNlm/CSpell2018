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
* This is a utility class for file I/O.
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
public class FileIo
{
    // add line separator at the end
    public static Consumer<String> PrintlnStrToFile(BufferedWriter writer)
    {
        return str -> {
            try
            {
                writer.write(str);
                writer.newLine();
            }
            catch(Exception x){}
        };
    }
    public static Consumer<String> PrintStrToFile(BufferedWriter writer)
    {
        return str -> {
            try
            {
                writer.write(str);
            }
            catch(Exception x){}
        };
    }
    public static String GetStrFromFile(String inFile)
    {
        boolean verboseFlag = false;
        return GetStrFromFile(inFile, verboseFlag);
    }
    // this is use to retrieve brat data bz there is a newline at the end
    public static String GetStrFromFileAddNewLineAtTheEnd(String inFile)
    {
        boolean verboseFlag = false;
        return GetStrFromFileAddNewLineAtTheEnd(inFile, verboseFlag);
    }
    public static String GetStrFromFileAddNewLineAtTheEnd(String inFile, 
        boolean verboseFlag)
    {
        String outStr = new String();
        if(verboseFlag == true)
        {
            System.out.println("===== Get String from: " + inFile);
        }
        try
        {
            BufferedReader reader = Files.newBufferedReader(
                Paths.get(inFile), Charset.forName("UTF-8"));
            outStr = reader.lines()
                .collect(Collectors.joining(GlobalVars.LS_STR));
            // add newLine at the end due to the brat data
            outStr += GlobalVars.LS_STR;    
            // close
            reader.close();
        }
        catch (Exception x1)
        {
            System.err.println("** Err@FileIo.GetStrFromFileAddNewLineAtTheEnd( ): " 
                + x1.toString());
        }
        return outStr;
    }
    public static String GetStrFromFile(String inFile, boolean verboseFlag)
    {
        String outStr = null;
        if(verboseFlag == true)
        {
            System.out.println("===== Get String from: " + inFile);
        }
        try
        {
            BufferedReader reader = Files.newBufferedReader(
                Paths.get(inFile), Charset.forName("UTF-8"));
            
            outStr = reader.lines()
                .collect(Collectors.joining(GlobalVars.LS_STR));
            // close
            reader.close();
        }
        catch (Exception x1)
        {
            System.err.println("** Err@FileIo.GetStrFromFile( ): " 
                + x1.toString());
        }
        return outStr;
    }
}

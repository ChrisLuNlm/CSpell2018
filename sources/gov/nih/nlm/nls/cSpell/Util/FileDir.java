package gov.nih.nlm.nls.cSpell.Util;
import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.nio.charset.*;
/*****************************************************************************
* This is a utility class for files and directory.
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
public class FileDir
{
    public static void main(String[] args)
    {
        String inDir 
            = "../../SpellCorrection/CHQA_SpellCorrection_Dataset/brat";
        if(args.length == 1)
        {
            inDir = args[0];
        }
        else if(args.length > 0)
        {
            System.err.println("Usage: java FileDir <inDir>");
            System.exit(0);
        }
        // test
        String matchPattern = "[0-9]*\\.txt";
        boolean verboseFlag = true;
        ArrayList<String> fileList = GetFilesInADirectoryToList(inDir,
            matchPattern, verboseFlag);
        // print result
        System.out.println("----- FileDir( ) -----");
        System.out.println("- fileList.size(): " + fileList.size());
        System.out.println(fileList);
    }
    // public methods
    public static ArrayList<String> GetFilesInADirectoryToList(String inDir,
        String matchPattern)
    {
        boolean verboseFlag = false;
        return GetFilesInADirectoryToList(inDir, matchPattern, verboseFlag);
    }
    public static ArrayList<String> GetFilesInADirectoryToList(String inDir,
        String matchPattern, boolean verboseFlag)
    {
        int fileNo = 0;
        ArrayList<String> fileList = new ArrayList<String>();
        try
        {
            DirectoryStream<Path> ds = Files.newDirectoryStream(
                FileSystems.getDefault().getPath(inDir), matchPattern);
            for(Path p:ds)
            {
                fileNo++;
                fileList.add(p.getFileName().toString());
            }
            // close
            ds.close();
        }
        // Logic for case when file doesn't exist
        catch (NoSuchFileException x)
        {
            System.err.format("** Err@FileDir.GetFilesInADirectoryToList( ): %s: no such directory%n", inDir);
        }
        // Logic for other sort of file error
        catch (IOException x)
        {
            // File permission problems are caught here.
            System.err.format("** Err@FileDir.GetFilesInADirectoryToList( ): exception %s%n", x);
        }
        if(verboseFlag == true)
        {
            System.out.println("--- FileDir.GetFilesInADirectoryToList( ) ---");
            System.out.println("- In Dir: " + inDir);
            System.out.println("- Total file no: " + fileNo);
        }
        return fileList;
    }
}

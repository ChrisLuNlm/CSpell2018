package gov.nih.nlm.nls.cSpell.Util;
import java.io.*;
import java.util.*;
import java.nio.file.*;
import java.nio.charset.*;
import gov.nih.nlm.nls.cSpell.Lib.*;
/*****************************************************************************
* This is a utility class for debug print.
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
public class DebugPrint
{
    // private constructor
    private DebugPrint()
    {
    }
    
    // public methods
    public static void Print(String inStr, boolean debugFlag)
    {
        if(debugFlag == true)
        {
            System.out.print(inStr);
        }
    }
    public static void Println(String inStr, boolean debugFlag)
    {
        if(debugFlag == true)
        {
            System.out.println(inStr);
        }
    }
    // print process
    public static void PrintProcess(String procStr, boolean debugFlag)
    {
        String debugStr = "====== " + procStr + " Process ======";
        Println(debugStr, debugFlag);
    }
    public static void PrintInText(String inStr, boolean debugFlag)
    {
        String debugStr = "--- inText: [" + inStr + "]"; 
        Println(debugStr, debugFlag);
    }
    // print out the str correction
    public static void PrintCorrect(String funcStr, String methodStr, 
        String inStr, String outStr, boolean debugFlag)
    {
        String debugStr = "- Correct: [" + inStr + GlobalVars.FS_STR + outStr 
            + GlobalVars.FS_STR + funcStr + GlobalVars.FS_STR + methodStr + "]";
        Println(debugStr, debugFlag);
    }
    // format: inWord|nonWordMerge (!wordInDic && !wordException)
    public static void PrintNwDetect(String inWord, boolean nwFlag, 
        boolean dicFlag, boolean exceptionFlag, boolean debugFlag)
    {
        String debugStr = "- Detect: [" + inWord + GlobalVars.FS_STR
            + nwFlag + " (" + !dicFlag + " & " + !exceptionFlag + ")]";
        Println(debugStr, debugFlag);
    }
    public static void PrintNwMergeDetect(String inWord, boolean nwMergeFlag, 
        boolean dicFlag, boolean exceptionFlag, boolean debugFlag)
    {
        String debugStr = "- Detect: [" + inWord + GlobalVars.FS_STR
            + nwMergeFlag + " (" + !dicFlag + " & " + !exceptionFlag + ")]";
        Println(debugStr, debugFlag);
    }
    public static void PrintRwMergeDetect(String inWord, boolean rwMergeFlag, 
        boolean dicFlag, boolean exceptionFlag, boolean debugFlag)
    {
        String debugStr = "- Detect: [" + inWord + GlobalVars.FS_STR
            + rwMergeFlag + " (" + dicFlag + " & " + !exceptionFlag + ")]";
        Println(debugStr, debugFlag);
    }
    public static void PrintRwSplitDetect(String inWord, boolean rwSplitFlag, 
        boolean dicFlag, boolean exceptionFlag, boolean lengthFlag, 
        boolean word2VecFlag, boolean wcFlag, boolean debugFlag)
    {
        String debugStr = "- Detect: [" + inWord + GlobalVars.FS_STR
            + rwSplitFlag + " (" + dicFlag + " & " + !exceptionFlag + " & "
            + lengthFlag + " & " + word2VecFlag + " & " + wcFlag + ")]";
        Println(debugStr, debugFlag);
    }
    public static void PrintRw1To1Detect(String inWord, boolean rwSplitFlag, 
        boolean dicFlag, boolean exceptionFlag, boolean lengthFlag, 
        boolean word2VecFlag, boolean wcFlag, boolean debugFlag)
    {
        String debugStr = "- Detect: [" + inWord + GlobalVars.FS_STR
            + rwSplitFlag + " (" + dicFlag + " & " + !exceptionFlag + " & "
            + lengthFlag + " & " + word2VecFlag + " & " + wcFlag + ")]";
        Println(debugStr, debugFlag);
    }
    public static void PrintContext(ArrayList<String> contextList,
        boolean debugFlag)
    {
        String debugStr = "- Context: " + contextList;
        Println(debugStr, debugFlag);
    }
    // Noisy Channel score
    public static void PrintNScore(String nScore, boolean debugFlag)
    {
        String debugStr = "- NScore: " + nScore;
        Println(debugStr, debugFlag);
    }
    // orthographic score
    public static void PrintOScore(String oScore, boolean debugFlag)
    {
        String debugStr = "- OScore: " + oScore;
        Println(debugStr, debugFlag);
    }
    // context score
    public static void PrintCScore(String cScore, boolean debugFlag)
    {
        String debugStr = "- CScore: " + cScore;
        Println(debugStr, debugFlag);
    }
    // frequency score
    public static void PrintFScore(String fScore, boolean debugFlag)
    {
        String debugStr = "- FScore: " + fScore;
        Println(debugStr, debugFlag);
    }
    // cSpell score: all
    public static void PrintScore(String score, boolean debugFlag)
    {
        String debugStr = "- Score: " + score;
        Println(debugStr, debugFlag);
    }
}

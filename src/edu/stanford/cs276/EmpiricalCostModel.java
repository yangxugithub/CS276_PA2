package edu.stanford.cs276;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class EmpiricalCostModel implements EditCostModel
{
    //bigram counter
    Map<String, Integer> BiGramCounter = new HashMap<String, Integer>();
    Map<String, Integer> UniGramCounter = new HashMap<String, Integer>();
    Map<String, Integer> DelCounter = new HashMap<String, Integer>();
    Map<String, Integer> InsCounter = new HashMap<String, Integer>();
    Map<String, Integer> SubCounter = new HashMap<String, Integer>();
    Map<String, Integer> TransCounter = new HashMap<String, Integer>();

    //unigrap counter
    //del counter
    //ins counter
    //sub counter
    //trans counter
    public void UpdateCounterOfSubOrTrans(String noisy, String clean)
    {
        int i = 0;
        for (i = 0; i < noisy.length(); i++)
        {
            if (noisy.charAt(i) != clean.charAt(i))
            {
                break;
            }
        }
        String tmpKey = "";
        //sub
        if (
                ((i+1) == noisy.length()) ||
                ((i+1) < noisy.length() && noisy.charAt(i+1) == clean.charAt(i+1))
           )
        {
            tmpKey += noisy.charAt(i);
            tmpKey += clean.charAt(i);
            if (SubCounter.containsKey(tmpKey))
            {
                Integer tmpValue = SubCounter.get(tmpKey);
                SubCounter.put(tmpKey, tmpValue+1);
            }
            else
            {
                SubCounter.put(tmpKey, 1);
            } 
        }
        //transposition
        else
        {
            tmpKey += clean.charAt(i);
            tmpKey += clean.charAt(i+1);
            if (TransCounter.containsKey(tmpKey))
            {
                Integer tmpValue = TransCounter.get(tmpKey);
                TransCounter.put(tmpKey, tmpValue);
            }
            else
            {
                TransCounter.put(tmpKey, 1);
            }
        }
    }
    // noisy is one letter longer
    public void UpdateCounterIns(String noisy, String clean)
    {
        int i = 0;
        String tmpKey = "", tmpKey2 = "";
        for (i = 0; i < noisy.length(); i++)
        {
            if (noisy.charAt(i) != clean.charAt(i))
            {
                break;
            }
        }
        if (i-1 >= 0)
        {
            tmpKey += noisy.charAt(i-1);
        }
        else
        {
            tmpKey += '^';
        }
        tmpKey += noisy.charAt(i);
        
        if (i-1 >= 0 && noisy.charAt(i) == noisy.charAt(i-1))
        {
            if (i-2 >= 0)
            {
                tmpKey2 += noisy.charAt(i-2);
            }
            else
            {
                tmpKey2 += '^';
            }
            tmpKey2 += noisy.charAt(i);
        }
        
        if (InsCounter.containsKey(tmpKey))
        {
            Integer tmpValue = InsCounter.get(tmpKey);
            InsCounter.put(tmpKey, tmpValue+1);
        }
        else
        {
            InsCounter.put(tmpKey, 1);
        }
        
        if (tmpKey2.length() == 0)
        {
            return;
        }
        if (InsCounter.containsKey(tmpKey2))
        {
            Integer tmpValue = InsCounter.get(tmpKey2);
            InsCounter.put(tmpKey2, tmpValue+1);
        }
        else
        {
            InsCounter.put(tmpKey2, 1);
        }
    }
    
    // noisy is one letter shorter
    public void UpdateCounterDel(String noisy, String clean)
    {
        int i = 0;
        String tmpKey = "", tmpKey2 = "";
        for (i = 0; i < noisy.length(); i++)
        {
            if (noisy.charAt(i) != clean.charAt(i))
            {
                break;
            }
        }
        if (i-1 >= 0)
        {
            tmpKey += clean.charAt(i-1);
        }
        else
        {
            tmpKey += '^';
        }
        tmpKey += clean.charAt(i);
        
        if (DelCounter.containsKey(tmpKey))
        {
            Integer tmpValue = DelCounter.get(tmpKey);
            DelCounter.put(tmpKey, tmpValue+1);
        }
        else
        {
            DelCounter.put(tmpKey, 1);
        }
    }

    public static int getMinValue(int... nums)
    {
        int min = nums[0];
        for (int num : nums)
        {
            min = Math.min(min, num);
        }
        return min;
    }
    
    public static String getEditType(int minValue, int... nums)
    {
        if (minValue == nums[0])
        {
            return "sub";
        }
        if (minValue == nums[1])
        {
            return "ins";
        }
        if (minValue == nums[2])
        {
            return "del";
        }
        if (minValue == nums[3])
        {
            return "trs";
        }
        return "error";
    }
 // src is the candidate, the correct one
 // dst is the query, the misspelled one
     public static int getEditTypes(String src, String dst)
     {
         int alphabetLength = 150;
         int srcLength = src.length();
         int dstLength = dst.length();
         final int INFINITY = srcLength + dstLength;
         int[][] H = new int[srcLength + 2][dstLength + 2];
         String [][] Path = new String[srcLength + 2][dstLength + 2];
         Path[0][0] = "start";
         H[0][0] = INFINITY;
         for (int i = 0; i <= srcLength; i++)
         {
             H[i + 1][1] = i;
             H[i + 1][0] = INFINITY;

             // Path[i+1][1] = 
         }
         for (int j = 0; j <= dstLength; j++)
         {
             H[1][j + 1] = j;
             H[0][j + 1] = INFINITY;
         }
         int[] DA = new int[alphabetLength];
         Arrays.fill(DA, 0);
         for (int i = 1; i <= srcLength; i++)
         {
             int DB = 0;
             System.out.println("i = "+i);
             for (int j = 1; j <= dstLength; j++)
             {
                 int i1 = DA[dst.charAt(j - 1)];
                 int j1 = DB;
                 int d = ((src.charAt(i - 1) == dst.charAt(j - 1)) ? 0 : 1);
                 if (d == 0)
                     DB = j;
                 int minValue = getMinValue(
                         H[i][j] + d,  // subs
                         H[i + 1][j] + 1, // insertion
                         H[i][j + 1] + 1,  // deletion
                         H[i1][j1] + (i - i1 - 1) + 1 + (j - j1 - 1)); // transposition
                 H[i + 1][j + 1] = minValue;
                 String editType = getEditType(
                         minValue,
                         H[i][j] + d,  // subs
                         H[i + 1][j] + 1, // insertion
                         H[i][j + 1] + 1,  // deletion
                         H[i1][j1] + (i - i1 - 1) + 1 + (j - j1 - 1)); // transposition);
                 if (d == 0)
                 {
                     Path[i + 1][j+1] = "mat";    
                 }
                 else
                 {
                     Path[i + 1][j+1] = editType;
                 }
                 

             }
             System.out.println("---------------------------------------------");
             DA[src.charAt(i - 1)] = i;
         }

         for (int i = 1; i <= srcLength + 1; i++)
         {
             String row = "";
             for (int j = 1; j <= dstLength + 1; j++)
             {
                 row += Path[i][j];
                 row += ", ";
             }
             System.out.println(row);
         }

         List<String> edits = GetEditsByBackTracking(src, dst, Path);

         if (edits.size() != H[srcLength + 1][dstLength + 1])
         {
             System.out.println("!!!!! edits found not equal to distance!");
         }

         return H[srcLength + 1][dstLength + 1];
     }

     public static List<String> GetEditsByBackTracking(String src, String dst, String[][] Path)
     {
         List<String> edits = new ArrayList<String>();
         int srcLength = src.length();
         int dstLength = dst.length();

         int i = srcLength + 1;
         int j = dstLength + 1;
         int loopCount = 0;
         while ( i > 1 || j > 1 && loopCount <= 1000)
         {
             String editType = "";
             if (Path[i][j] == "trs")
             {
                 editType += src.charAt(i - 3);
                 editType += src.charAt(i - 2);
                 i -= 2;
                 j -= 2;
                 System.out.println( "Trans : " + editType);
                 edits.add("Trans" + editType);
             }
             else if (Path[i][j] == "del")
             {
                 editType += src.charAt(i - 3);
                 editType += src.charAt(i - 2);
                 System.out.println("Del: " + editType);
                 edits.add("Del" + editType);
                 i -= 1;
             }
             else if (Path[i][j] == "ins")
             {
                 editType += src.charAt(i - 2);
                 editType += dst.charAt(j - 2);
                 System.out.println("Ins: " + editType);
                 edits.add("Ins" + editType);
                 j -= 1;
             }
             else if (Path[i][j] == "sub")
             {
                 editType += dst.charAt(j-2);
                 editType += src.charAt(i-2);
                 System.out.println("sub: " + editType);
                 edits.add("Sub" + editType);
                 i -= 1;
                 j -= 1;
             }
             else if (Path[i][j] == "mat")
             {
                 // editType += 
                 i -= 1;
                 j -= 1;
                 System.out.println("match");
             }
             loopCount += 1;
         }

         return edits;
     }


    public EmpiricalCostModel(String editsFile) throws IOException
    {   
        BufferedReader input = new BufferedReader(new FileReader(editsFile));
        System.out.println("Constructing edit distance map...");
        String line = null;
        while ((line = input.readLine()) != null)
        {
            Scanner lineSc = new Scanner(line);
            lineSc.useDelimiter("\t");
            String noisy = lineSc.next();
            String clean = lineSc.next();

            if (noisy.length() == clean.length())
            {
                UpdateCounterOfSubOrTrans(noisy, clean);
            }
            else if (noisy.length() < clean.length())
            {
                UpdateCounterDel(noisy, clean);
            }
            else
            {
                UpdateCounterIns(noisy, clean);
            }            
        }

        input.close();
        System.out.println("Done.");
    }

    // You need to update this to calculate the proper empirical cost
    @Override
    public double editProbability(String original, String R, int distance)
    {
        return 0.5;
        /*
         * Your code here
         */
    }
}

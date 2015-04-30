package edu.stanford.cs276;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
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

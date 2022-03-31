import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class PSMInfo {
	public int scanNumber;
	public int chargeState;
	public ArrayList<Result> resultList;
		
	
	PSMInfo()
	{
		resultList = new ArrayList<Result>();
	}
	
	public void Print(PrintWriter outputFile)
	{
		Collections.sort(resultList);
		for(int i=0; i< ProgressiveSearch.pep_num; i++)
		{
			if(resultList.size() <= i)
				break;
			if( i>0 && resultList.get(i).xcorr == resultList.get(i-1).xcorr)
				resultList.get(i).Print(outputFile, resultList.get(i-1).result[ProgressiveSearch.num_index]);
			else
				resultList.get(i).Print(outputFile, i+1);
		}
	}
	
	public void AddResult(String s_split[])
	{
		String currentPeptide = s_split[ProgressiveSearch.plain_peptide_index].replace('L', 'I') + s_split[ProgressiveSearch.modification_index];
        for (Result result : resultList)
        {
        	if ( result.GetSeq().equals(currentPeptide))
        	{
        		result.AddProtein(s_split[ProgressiveSearch.protein_index], Integer.parseInt(s_split[ProgressiveSearch.proteinCount_index]));
        		return;
        	}
        }
        Result result = new Result(s_split);
        resultList.add(result);
	}
	
	public int compareTo(PSMInfo psm) 
	{
		if( this.scanNumber > psm.scanNumber)
			return -1;
		else if( this.scanNumber < psm.scanNumber)
			return 1;
		else
			return 0;
	}
}

class Result implements Comparable<Result> 
{
	public double xcorr;
	public String peptideSeq;
	public String result[];
	public HashSet<String> proteinList; 
	public int proteinCount;
	
	//ArrayList<Integer> proteinList2;

	
	Result(String s_split[])
	{
		proteinList = new HashSet<String>();
		result = s_split;
		peptideSeq = s_split[ProgressiveSearch.plain_peptide_index];
		xcorr = Double.parseDouble(s_split[ProgressiveSearch.xcorr_index]);
		proteinCount = Integer.parseInt(s_split[ProgressiveSearch.proteinCount_index]);
		String protein[] = s_split[ProgressiveSearch.protein_index].split(",");
		//proteinList2 = new ArrayList<Integer>();
		for( int i=0; i<protein.length; i++ )
		{
			proteinList.add(protein[i]);
			//if (ProgressiveSearch.dbIndexing.containsKey(protein[i]))
			//	proteinList2.add( ProgressiveSearch.dbIndexing.get(protein[i]));
		}
	}
	
	public String GetSeq()
	{
		return peptideSeq.replace('L', 'I') + result[ProgressiveSearch.modification_index];
	}
	
	public void AddProtein(String proteinList, int proteinCount)
	{
		this.proteinCount += proteinCount;
		String protein[] = proteinList.split(",");
		//proteinList2 = new ArrayList<Integer>();
		for( int i=0; i<protein.length; i++ )
		{
			if( this.proteinList.contains(protein[i]))
				this.proteinCount--;
			this.proteinList.add(protein[i]);
		}
	}

	public void Print(PrintWriter outputFile, int rank)
	{
		String proteinList2[] = proteinList.toArray( new String[proteinList.size()]);
		String protein = "";
		
		for( int i=0; i<proteinList2.length; i++)
		{
			if( ProgressiveSearch.dbIndexing.get(proteinList2[i])!=null)
			{
				if( i >0)
					protein += ",";
				protein += proteinList2[i];
			}
			else
				proteinCount--;
		}
		result[ProgressiveSearch.num_index] = String.valueOf(rank);
		//result[ProgressiveSearch.protein_index] = String.join(",", proteinList);
		result[ProgressiveSearch.protein_index] = protein;
		result[ProgressiveSearch.proteinCount_index]  = String.valueOf(proteinCount);
		outputFile.println(String.join("\t", result));
	}
	
	public void Print(PrintWriter outputFile, String rank)
	{
		String proteinList2[] = proteinList.toArray( new String[proteinList.size()]);
		String protein = "";
		
		for( int i=0; i<proteinList2.length; i++)
		{
			if( ProgressiveSearch.dbIndexing.get(proteinList2[i])!=null)
			{
				if( i >0)
					protein += ",";
				protein += proteinList2[i];
			}
			else
				proteinCount--;
		}
		result[ProgressiveSearch.num_index] = rank;
		//result[ProgressiveSearch.protein_index] = String.join(",", proteinList);
		result[ProgressiveSearch.protein_index] = protein;
		result[ProgressiveSearch.proteinCount_index]  = String.valueOf(proteinCount);
		outputFile.println(String.join("\t", result));
	}
	
	public int compareTo(Result result) 
	{
		if( this.xcorr > result.xcorr)
			return -1;
		else if( this.xcorr < result.xcorr)
			return 1;
		else
			return 0;
	}
}

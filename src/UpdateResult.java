import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;

import javax.crypto.spec.PSource;

public class UpdateResult {


	static HashMap<PSM_ID, PSMInfo> psmList = new HashMap<PSM_ID, PSMInfo>();
	//Make IntersectionDB search result
	

	public static void UpdatePSM(String oldDBResult, String deletionResult, String insertionResult) throws IOException
	{


		CheckTime.Start();
		BufferedReader old_result = new BufferedReader(new FileReader(oldDBResult));
		BufferedReader del_result = new BufferedReader(new FileReader(deletionResult));
		BufferedReader insertion_result = new BufferedReader(new FileReader(insertionResult));

		//PrintWriter newDBResult = new PrintWriter(new BufferedWriter(new FileWriter("newDBResult_ProgressiveSearch.txt")));
		PrintWriter newDBResult = new PrintWriter(new BufferedWriter(new FileWriter(ProgressiveSearch.outDir + "Comet-P_result.txt")));
		String s;
		
		while (!(s = old_result.readLine()).split("\t")[0].equals("scan")) {
			newDBResult.println(s);
		}
		newDBResult.println(s);
		while (!(s = del_result.readLine()).split("\t")[0].equals("scan")) {}
		while (!(s = insertion_result.readLine()).split("\t")[0].equals("scan")) {}
		

		// Old DB Result PSM
		System.out.println("Read Old Result");
		while ((s = old_result.readLine())!= null) 
		{
			String s_split[] = s.split("\t");
			int scanNumber = Integer.parseInt(s_split[ProgressiveSearch.scan_index] );
			int charge = Integer.parseInt(s_split[ProgressiveSearch.charge_index] );
			PSM_ID psm = new PSM_ID(scanNumber, charge);
			if(psmList.get(psm)==null)
			{
				PSMInfo psmInfo = new PSMInfo();
				psmInfo.AddResult(s_split);
				psmList.put(psm, psmInfo);
			}
			else
			{
				psmList.get(psm).AddResult(s_split);
			}
		}
		
		
		// Deletion Result PSM
		System.out.println("Read Deletion Result");
		HashMap<PSM_ID, PSMInfo> deletionPSMList = new HashMap<PSM_ID, PSMInfo>(); //to Replace PSM result
		while ((s = del_result.readLine())!= null) 
		{
			String s_split[] = s.split("\t");
			int scanNumber = Integer.parseInt(s_split[ProgressiveSearch.scan_index] );
			int charge = Integer.parseInt(s_split[ProgressiveSearch.charge_index] );
			PSM_ID psm = new PSM_ID(scanNumber, charge);
			if(deletionPSMList.get(psm)==null)
			{
				PSMInfo psmInfo = new PSMInfo();
				psmInfo.AddResult(s_split);
				deletionPSMList.put(psm, psmInfo);
			}
			else
			{
				deletionPSMList.get(psm).AddResult(s_split);
			}
			
		}
		for( PSM_ID psm : deletionPSMList.keySet())
			psmList.put(psm, deletionPSMList.get(psm)); //replace
		

		System.out.println("Read Insertion Result");
		while ((s = insertion_result.readLine())!= null) 
		{
			String s_split[] = s.split("\t");
			int scanNumber = Integer.parseInt(s_split[ProgressiveSearch.scan_index] );
			int charge = Integer.parseInt(s_split[ProgressiveSearch.charge_index] );
			PSM_ID psm = new PSM_ID(scanNumber, charge);
			
			if(psmList.get(psm)==null)
			{
				PSMInfo psmInfo = new PSMInfo();
				psmInfo.AddResult(s_split);
				psmList.put(psm, psmInfo);
			}
			else
			{
				psmList.get(psm).AddResult(s_split);
			}
		}

		if(ProgressiveSearch.psmMode)
		{

			ArrayList<PSM_ID> keySet = new ArrayList( psmList.keySet() );
			Collections.sort(keySet);
			System.out.println("Write Result");
			for( PSM_ID psm : keySet)
			{
				psmList.get(psm).Print(newDBResult);	
			}
		}
		else
		{
			System.out.println("Calculate Histogram");
			ArrayList<PSM_ID> keySet = new ArrayList( psmList.keySet() );
			Collections.sort(keySet);
			HashMap<PSM_ID, HistogramInfo> histogramList = UpdateHistogram();
			System.out.println("Write Result");
			for( PSM_ID psm : keySet)
			{
				psmList.get(psm).Print(newDBResult);	
				histogramList.get(psm).Print(newDBResult);
			}
			histogramList = null;
		}
		
		old_result.close();
		del_result.close();
		insertion_result.close();
		newDBResult.close();
		CheckTime.End();
		psmList =null;
	}
	
	
	public static HashMap<PSM_ID, HistogramInfo> UpdateHistogram() throws IOException 
	{
		BufferedReader oldDB_Histogram = new BufferedReader(new FileReader(ProgressiveSearch.oldDB_result_histogram));
		BufferedReader delDB_Histogram = new BufferedReader(new FileReader(ProgressiveSearch.outDir + "deletedDB_result_histogram.txt"));
		BufferedReader deletion_Histogram = new BufferedReader(new FileReader(ProgressiveSearch.outDir + "deletion_result_histogram.txt"));
		BufferedReader insertion_Histogram = new BufferedReader(new FileReader(ProgressiveSearch.outDir + "insertedDB_result_histogram.txt"));
		PrintWriter newDBHistogram = new PrintWriter(new BufferedWriter(new FileWriter(ProgressiveSearch.outDir + "newDB_result_histogram.txt")));
		
		HashMap<PSM_ID, HistogramInfo> histogramList = new HashMap<PSM_ID, HistogramInfo>();
		
		String s;

		System.out.println("  - Old DB");
		while (( s = oldDB_Histogram.readLine() )!=null )
		{
			String s_split[] = s.split("\t");
			int scanNumber = Integer.parseInt(s_split[0]);
			int charge = Integer.parseInt(s_split[1]);
			int binSize = Integer.parseInt(s_split[2]);
			HistogramInfo histoInfo = new HistogramInfo(scanNumber, charge);
			for( int i=0; i< binSize; i++)
			{
				s = oldDB_Histogram.readLine();
				s_split = s.split("\t");
				histoInfo.histogram.put( Integer.parseInt(s_split[0]), Integer.parseInt(s_split[1]) );
			}

			histogramList.put( new PSM_ID(scanNumber, charge), histoInfo);
		}

		System.out.println("  - del DB");
		while (( s = delDB_Histogram.readLine() )!=null )
		{
			String s_split[] = s.split("\t");
			int scanNumber = Integer.parseInt(s_split[0]);
			int charge = Integer.parseInt(s_split[1]);
			int binSize = Integer.parseInt(s_split[2]);
			HistogramInfo histoInfo = histogramList.get(new PSM_ID(scanNumber, charge));
			for( int i=0; i< binSize; i++)
			{
				s = delDB_Histogram.readLine();
				s_split = s.split("\t");
				int bin = Integer.parseInt(s_split[0]);
				int count = histoInfo.histogram.get(bin) - Integer.parseInt(s_split[1]);
				histoInfo.histogram.put(bin, count );
			}
			histogramList.put(new PSM_ID(scanNumber, charge), histoInfo);
		}

		System.out.println("  - deletion");
		while (( s = deletion_Histogram.readLine() )!=null )
		{
			String s_split[] = s.split("\t");
			int scanNumber = Integer.parseInt(s_split[0]);
			int charge = Integer.parseInt(s_split[1]);
			int binSize = Integer.parseInt(s_split[2]);
			HistogramInfo histoInfo =  new HistogramInfo(scanNumber, charge);
			for( int i=0; i< binSize; i++)
			{
				s = deletion_Histogram.readLine();
				s_split = s.split("\t");
				histoInfo.histogram.put( Integer.parseInt(s_split[0]), Integer.parseInt(s_split[1]) );
			}
			histogramList.put(new PSM_ID(scanNumber, charge), histoInfo);
		}

		System.out.println("  - insertion");
		while (( s = insertion_Histogram.readLine() )!=null )
		{
			String s_split[] = s.split("\t");
			int scanNumber = Integer.parseInt(s_split[0]);
			int charge = Integer.parseInt(s_split[1]);
			int binSize = Integer.parseInt(s_split[2]);
			HistogramInfo histoInfo;
			if( histogramList.get(new PSM_ID(scanNumber, charge))!=null)
			{
				histoInfo = histogramList.get(new PSM_ID(scanNumber, charge));
				for( int i=0; i< binSize; i++)
				{
					s = insertion_Histogram.readLine();
					s_split = s.split("\t");
					int range = Integer.parseInt(s_split[0]);
					int count ;
					if(histoInfo.histogram.get(range)!=null)					
						count = histoInfo.histogram.get(range) + Integer.parseInt(s_split[1]);
					else
						count = Integer.parseInt(s_split[1]);
					histoInfo.histogram.put(range, count );
				}
			}
			else
			{
				histoInfo = new HistogramInfo(scanNumber, charge);
				for( int i=0; i< binSize; i++)
				{
					System.out.println(Integer.parseInt(s_split[0]));
					s = insertion_Histogram.readLine();
					s_split = s.split("\t");
					histoInfo.histogram.put(Integer.parseInt(s_split[0]), Integer.parseInt(s_split[1]) );
				}
			}
			
			histogramList.put(new PSM_ID(scanNumber, charge), histoInfo);
		}

		ArrayList<PSM_ID> keySet = new ArrayList( histogramList.keySet() );
		Collections.sort(keySet);
		for( PSM_ID psm : keySet)
		{
			histogramList.get(psm).PrintWithScanNumber(newDBHistogram);
		}
		
		oldDB_Histogram.close();
		delDB_Histogram.close();
		deletion_Histogram.close();
		insertion_Histogram.close();
		newDBHistogram.close();
		
		return histogramList;
	}
	
	/*
	public static void UpdateDeltaCn(String updatedResult) throws IOException 
	{
		CheckTime.Start();
		BufferedReader inputFile = new BufferedReader(new FileReader(updatedResult));
		PrintWriter newDBResult = new PrintWriter(new BufferedWriter(new FileWriter("finalResult.txt")));

		String s;
		while (!(s = inputFile.readLine()).split("\t")[0].equals("scan")) {
			newDBResult.println(s);
		}
		newDBResult.println(s);
		s = inputFile.readLine();
		String s_split[] = s.split("\t");
		int current_scanNumber = Integer.parseInt(s_split[ProgressiveSearch.scan_index]);
		int current_charge = Integer.parseInt(s_split[ProgressiveSearch.charge_index]);
		int count = 1;

		String s_before;
		String s_split_before[] = s_split ;
	*/
		/*
		while((s = inputFile.readLine())!=null)
		{
			if( count <=6 )
			{
				newDBResult.println(String.join("\t", s_split));
			}

			s_split = s.split("\t");
			int s_scanNumber = Integer.parseInt(s_split[ProgressiveSearch.scan_index]);
			int s_charge = Integer.parseInt(s_split[ProgressiveSearch.charge_index]);
			
			if( current_scanNumber == s_scanNumber && current_charge == s_charge)
			{
				s_split[ProgressiveSearch.num_index] = String.valueOf(++count);
			}
			else
			{
				current_scanNumber = s_scanNumber;
				current_charge = s_charge;
				count=1;
			}
		}
		*/
	/*
		HashMap<PSM_ID, HistogramInfo> histogramList = UpdateHistogram();
		while((s = inputFile.readLine())!=null)
		{
			if (count <= ProgressiveSearch.pep_num+1 && count >=2 )
			{
				newDBResult.println(String.join("\t", s_split_before));
				s_split_before = s_split;
			}

			s_split = s.split("\t");
			int s_scanNumber = Integer.parseInt(s_split[ProgressiveSearch.scan_index]);
			int s_charge = Integer.parseInt(s_split[ProgressiveSearch.charge_index]);

			if( current_scanNumber == s_scanNumber && current_charge == s_charge)
			{
				s_split[ProgressiveSearch.num_index] = String.valueOf(++count);
				double deltaCN_n1 = Double.parseDouble(s_split[ProgressiveSearch.xcorr_index]);
				double deltaCN_n2 = Double.parseDouble(s_split_before[ProgressiveSearch.xcorr_index]);
				double min_deltaCn = 1 - (deltaCN_n1 + 0.00005) / (deltaCN_n2 - 0.00005);
				double max_deltaCn = 1 - (deltaCN_n1 - 0.00005) / (deltaCN_n2 + 0.00005);
				double recalculatedDeltaCN = 1 - deltaCN_n1 / deltaCN_n2;
				if (recalculatedDeltaCN < min_deltaCn || recalculatedDeltaCN > max_deltaCn)
					s_split_before[ProgressiveSearch.deltaCN_index] = String.format("%.4f", recalculatedDeltaCN);
						
			}	
			else //restart with rank 1
			{
				if(count <= ProgressiveSearch.pep_num)
				{
					newDBResult.println(String.join("\t", s_split_before));
					s_split_before = s_split;
				}
				if( histogramList.get(new PSM_ID(s_scanNumber, s_charge))!=null)
					histogramList.get(new PSM_ID(s_scanNumber, s_charge)).Print(newDBResult);
				else
				{
					System.out.println("Err: There is no Histogram Info");
				}
				
				s_split_before = s_split;
				current_scanNumber = s_scanNumber;
				current_charge = s_charge;
				count=1;
			}
		}
		if(count <= ProgressiveSearch.pep_num+1)
		{
			newDBResult.println(String.join("\t", s_split_before));
			if(count <= ProgressiveSearch.pep_num)
			{
				newDBResult.println(String.join("\t", s_split_before));
				s_split_before = s_split;
			}
			if( histogramList.get(current_scanNumber)!=null)
				histogramList.get(current_scanNumber).Print(newDBResult);
		}

		
		newDBResult.close();
		CheckTime.End();
		
	}
	*/
	/*
	
	public static void ReplaceResult(String oldDBResult, String deletionResult, String insertionResult) throws IOException
	{

		CheckTime.Start();
		BufferedReader old_result = new BufferedReader(new FileReader(oldDBResult));
		BufferedReader del_result = new BufferedReader(new FileReader(deletionResult));
		PrintWriter intersectionDBResult = new PrintWriter(new BufferedWriter(new FileWriter("intersectionDBResult_ProgressiveSearch.txt")));
		
		String s;
		
		while (!(s = old_result.readLine()).split("\t")[0].equals("scan")) {
			intersectionDBResult.println(s);
		}
		intersectionDBResult.println(s);
		while (!(s = del_result.readLine()).split("\t")[0].equals("scan")) {}
		

		// Old DB Result PSM
		System.out.println("Read Old Result");
		while ((s = old_result.readLine())!= null) 
		{
			String s_split[] = s.split("\t");
			int scanNumber = Integer.parseInt(s_split[ProgressiveSearch.scan_index] );
			int charge = Integer.parseInt(s_split[ProgressiveSearch.charge_index] );
			PSM_ID psm = new PSM_ID(scanNumber, charge);
			if(psmList.get(psm)==null)
			{
				PSMInfo psmInfo = new PSMInfo();
				psmInfo.AddResult(s_split);
				psmList.put(psm, psmInfo);
			}
			else
			{
				psmList.get(psm).AddResult(s_split);
			}
		}
		
		
		System.out.println("Read Deletion Result");
		// Deletion Result PSM
		HashMap<PSM_ID, PSMInfo> deletionPSMList = new HashMap<PSM_ID, PSMInfo>();
		while ((s = del_result.readLine())!= null) 
		{
			String s_split[] = s.split("\t");
			int scanNumber = Integer.parseInt(s_split[ProgressiveSearch.scan_index] );
			int charge = Integer.parseInt(s_split[ProgressiveSearch.charge_index] );
			PSM_ID psm = new PSM_ID(scanNumber, charge);
			if(deletionPSMList.get(psm)==null)
			{
				PSMInfo psmInfo = new PSMInfo();
				psmInfo.AddResult(s_split);
				deletionPSMList.put(psm, psmInfo);
			}
			else
			{
				deletionPSMList.get(psm).AddResult(s_split);
			}
			
		}
		for( PSM_ID psm : deletionPSMList.keySet())
			psmList.put(psm, deletionPSMList.get(psm));
		
		

		System.out.println("Sort");
		ArrayList<PSM_ID> keySet = new ArrayList( psmList.keySet() );
		Collections.sort(keySet);

		
		
		System.out.println("Write Result");
		for( PSM_ID psm : keySet)
			psmList.get(psm).Print(intersectionDBResult);	
		
		old_result.close();
		del_result.close();
		intersectionDBResult.close();
		CheckTime.End();
	}

	//Make NewDB search result
	public static void UpdateResult(String intersectionDBResult, String insertionResult) throws IOException
	{
		CheckTime.Start();
		
		BufferedReader insertion_result = new BufferedReader(new FileReader(insertionResult));

		PrintWriter newDBResult = new PrintWriter(new BufferedWriter(new FileWriter("newDBResult_ProgressiveSearch.txt")));
		PrintWriter newDBHistogram = new PrintWriter(new BufferedWriter(new FileWriter("newDBResult_Histogram2.txt")));
		
		String s;
		
		while (!(s = insertion_result.readLine()).split("\t")[0].equals("scan")) {
			newDBResult.println(s);}
		newDBResult.println(s);
		
		//HashMap<Integer, PSMInfo> psmList = new HashMap<Integer, PSMInfo>();

		System.out.println("Read Insertion Result");
		while ((s = insertion_result.readLine())!= null) 
		{
			String s_split[] = s.split("\t");
			int scanNumber = Integer.parseInt(s_split[ProgressiveSearch.scan_index] );
			int charge = Integer.parseInt(s_split[ProgressiveSearch.charge_index] );
			PSM_ID psm = new PSM_ID(scanNumber, charge);
			
			//if(!psmList.containsKey(psm))
			if(psmList.get(psm)==null)
			{
				PSMInfo psmInfo = new PSMInfo();
				psmInfo.AddResult(s_split);
				psmList.put(psm, psmInfo);
			}
			else
			{
				psmList.get(psm).AddResult(s_split);
			}
		}

		System.out.println("Calculate Histogram");
		ArrayList<PSM_ID> keySet = new ArrayList( psmList.keySet() );
		Collections.sort(keySet);
		HashMap<PSM_ID, HistogramInfo> histogramList = UpdateHistogram();
		System.out.println("Write Result");
		for( PSM_ID psm : keySet)
		{
			psmList.get(psm).Print(newDBResult);	
			histogramList.get(psm).Print(newDBResult);
			histogramList.get(psm).PrintWithScanNumber(newDBHistogram);
		}
		
		newDBResult.close();
		newDBHistogram.close();
		CheckTime.End();
		psmList =null;
		histogramList = null;
	}
	 */
}

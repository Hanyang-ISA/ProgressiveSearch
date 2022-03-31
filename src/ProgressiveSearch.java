import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class ProgressiveSearch {

	// Input File
	public static PrintWriter Search_info;
	public static String oldDB_result;
	public static String oldDB_result_histogram;
	public static String oldDB;
	public static String newDB;
	public static String spectrum;
	public static String param;
	public static String outDir;
	
	// Column Index
	public static int scan_index = -1;
	public static int num_index = -1;
	public static int charge_index = -1;
	public static int evalue_index = -1;
	public static int deltaCN_index = -1;
	public static int peptide_index = -1;
	public static int plain_peptide_index = -1;
	public static int modification_index = -1;
	public static int xcorr_index = -1;
	public static int protein_index = -1;
	public static int proteinCount_index = -1;
	
	// Param Value
	public static String dec_prefix = "";
	public static int pep_num = -1;
	public static int ntt = -1;
	public static Boolean psmMode;

	public static HashMap<String, Integer> dbIndexing = new HashMap<String, Integer>();
	//public static ArrayList<String> dbHeader = new ArrayList<String>();
	
	public static void main(String[] args) throws Exception 
	{
		
		int index = 0;
		psmMode = false;
		
		if( args[index].equals("Comet-P"))
		{
			index++;
			if( args.length != 7)
			{
				ShowUsage();
				return;
			}
			psmMode = true;
			oldDB_result = args[index++];
		}
		else if( args[index].equals("Comet-E"))
		{
			index++;
			if( args.length != 8)
			{
				ShowUsage();
				return;
			}
			psmMode = false;
			oldDB_result = args[index++];
			oldDB_result_histogram = args[index++];
		}
		else
		{
			ShowUsage();
			return;
		}
		
		oldDB = args[index++];
		newDB = args[index++];
		spectrum = args[index++];
		param = args[index++];
		outDir = args[index++];

		if(outDir.substring(0, 2) != "./" )
		{
			outDir = "./" + outDir;
		}
		if(outDir.substring(outDir.length()-1, outDir.length()) != "/" )
		{
			outDir += "/";
		}
		String command= "mkdir " + outDir;
		//System.out.println(command);
		try {
			Process p = Runtime.getRuntime().exec(command);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
		} catch (Exception e) {
			System.out.println(e);
		}

		Search_info = new PrintWriter(new BufferedWriter(new FileWriter(ProgressiveSearch.outDir + "ProgressiveSearchLog.txt")));

		run();

		Search_info.close();
	}
	
	private static void ShowUsage()
	{
		System.out.println("\n\n Progressive Search version \"2022.03\"");
		System.out.println(" (c) Hanyang University\n");

		System.out.println(" Progressive Search Usage:");
		System.out.println(" Comet-P: java -jar ProgressiveSearch.jar  Comet-P  <old_DB_result>  <DB_old>  <DB_new>  <Spectrum>  <Comet_Param>  <out_dir>");
		System.out.println(" Comet-E: java -jar ProgressiveSearch.jar  Comet-E  <old_DB_result>  <old_DB_result_histogram>  <DB_old>  <DB_new>  <Spectrum>  <Comet_Param>  <out_dir>\n");
		System.out.println("    -parameters:\t<old_DB_result>\tComet search result of old DB result");
		System.out.println("\t\t\t<DB_old>\told database");
		System.out.println("\t\t\t<DB_new>\tnew database");
		System.out.println("\t\t\t<Spectrum>\tspectrum file");
		System.out.println("\t\t\t<Comet_Param>\tComet.params file");	
		System.out.println("\t\t\t<out_dir>\toutput directory\n\n");		
	}
	

	static void run() throws Exception {

		
		// DB compare
		System.out.println("\n\n### DB Compare");
		Search_info.println("\n\n### DB Compare");
		DatabaseCompare.run();
		
		/*
		// Old DB result
		System.out.println("\n\n### Old DB result");
		Search_info.println("\n\n### Old DB result");
		CometSearch.Search(spectrum, oldDB, param, "oldDBResult");

		// New DB result
		System.out.println("\n\n### New DB result(ProgressiveSearchMode)");
		Search_info.println("\n\n### New DB result(ProgressiveSearchMode)");
		CometSearch.Search(spectrum, newDB, param, "newDBResult_Progressive");
		
		// New DB result
		System.out.println("\n\n### New DB result(Original)");
		Search_info.println("\n\n### New DB result(Original)");
		CometSearch.SearchOriginal(spectrum, newDB, param, "newDBResult_OriginalComet");
		
		// New DB result
		System.out.println("\n\n### New DB result(Original_withoutEvalue)");
		Search_info.println("\n\n### New DB result(Original_withoutEvalue)");
		CometSearch.SearchComet_withoutEvalue(spectrum, newDB, param, "newDBResult_OriginalComet_withOutEvalue");
		*/
		
		// FindColumnIndex
		FindColumnIndex();
		FindParamValue();
	
		
		// Extract Del Spectrum
		System.out.println("\n\n### Extract Del Spectrum"); 
		Search_info.println("\n\n### Extract Del Spectrum");
		ExtractSpectrum.extract();
		
		
		// Deletion result (deletion result)
		System.out.println("\n\n### Deletion result");
		Search_info.println("\n\n### Deletion result");
		CometSearch.Search(ProgressiveSearch.outDir + spectrum.substring(0, spectrum.length()-4)+"_extracted.mgf", ProgressiveSearch.outDir + "shared.fasta", param, ProgressiveSearch.outDir + "deletion_result");
		
		
		// Insertion result (insertion result)
		System.out.println("\n\n### Insertion result");
		Search_info.println("\n\n### Insertion result");
		CometSearch.Search(spectrum, ProgressiveSearch.outDir + "inserted.fasta", param, ProgressiveSearch.outDir + "insertedDB_result");
		
		
		// FindColumnIndex
		FindColumnIndex();
		FindParamValue();

		if(!psmMode)
		{
			
			// deleted DB result (del Histogram result)
			System.out.println("\n\n### deleted DB result");
			Search_info.println("\n\n### deleted DB result");
			CometSearch.Search(spectrum, ProgressiveSearch.outDir + "deleted.fasta", param, ProgressiveSearch.outDir + "deletedDB_result");
		}

		DatabaseIndexing(newDB);
		
		//Update PSM
		System.out.println("\n\n### Update PSM");
		Search_info.println("\n\n### Update PSM");
		UpdateResult.UpdatePSM(oldDB_result, ProgressiveSearch.outDir + "deletion_result.txt", ProgressiveSearch.outDir + "insertedDB_result.txt");
		
		if(!psmMode)
		{
			
			/*
			// Union Result
			System.out.println("\n\n### Convert to Intersection Database result");
			Search_info.println("\n\n### Convert to Intersection Database result");
			UpdateResult.ReplaceResult(oldDB_result, "deletionResult.txt", "insertionResult.txt");
			
			
			// Union Result
			System.out.println("\n\n### Convert to New Database result");
			Search_info.println("\n\n### Convert to New Database result");
			UpdateResult.UpdateResult("intersectionDBResult_ProgressiveSearch.txt", "insertionResult.txt");
			
			*/
			// Recalculate Evalue
			System.out.println("\n\n### Recalculate Evalue");
			Search_info.println("\n\n### Recalculate Evalue");
			CometSearch.CalculateEvalue(spectrum, newDB, param, ProgressiveSearch.outDir + "Comet-E_result");
		}		
	}

	
	static void FindColumnIndex() throws IOException
	{
		BufferedReader old_result = new BufferedReader(new FileReader(oldDB_result));
		String s;
		while( !(s=old_result.readLine()).split("\t")[0].equals("scan")) {}
		old_result.close();
		String[] sSplit = s.split("\t");
		for(int j=0; j<sSplit.length; j++)
		{
			if(sSplit[j].equals("scan"))
				scan_index = j;
			if(sSplit[j].equals("num"))
				num_index = j;
			if(sSplit[j].equals("charge"))
				charge_index = j;
			if(sSplit[j].equals("delta_cn"))
				deltaCN_index = j;
			if(sSplit[j].equals("xcorr"))
				xcorr_index = j;
			if(sSplit[j].equals("modified_peptide"))
				peptide_index = j;
			if(sSplit[j].equals("plain_peptide"))
				plain_peptide_index = j;
			if(sSplit[j].equals("modifications"))
				modification_index = j;
			if(sSplit[j].equals("protein"))
				protein_index = j;
			if(sSplit[j].equals("e-value"))
				evalue_index = j;
			if(sSplit[j].equals("protein_count"))
				proteinCount_index = j;
		}
		
	}
	

	static void FindParamValue() throws IOException
	{
		BufferedReader param_read = new BufferedReader(new FileReader(param));
		String s;
		while( (s=param_read.readLine())!=null)
		{
			if( s.startsWith("num_output_lines") )
				
				pep_num = Integer.parseInt( s.split("=|#")[1].trim() );
			else if( s.startsWith("num_enzyme_termini") )
				ntt = Integer.parseInt( s.split("=|#")[1].trim() );
			else if( s.startsWith("decoy_prefix") )
				dec_prefix =  s.split("=|#")[1].trim();
		}
		param_read.close();
	}
	
	public static void DatabaseIndexing(String newDB) throws IOException
	{
		BufferedReader new_DB = new BufferedReader(new FileReader(newDB));
		String s;
		int count = 0;
		while ((s = new_DB.readLine()) != null) {
			if (s.charAt(0) == '>') {
				String header = s.split(" ")[0];
				header = header.substring(1, header.length());
				dbIndexing.put(header, count++);		
			}
		}
	}
}

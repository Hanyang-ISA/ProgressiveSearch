import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class CometSearch {

	public static void Search(String spectrum, String database, String param, String outputName) 
	{
		CheckTime.Start();
		String command;
		
		if(ProgressiveSearch.psmMode)
			command= "./Comet-P.exe -D" + database + " -P" + param + " -N" + outputName + " " + spectrum;
		else
			command = "./Comet-E.exe -D" + database + " -P" + param + " -N" + outputName + " " + spectrum;
			
		
		System.out.println(command);
		ProgressiveSearch.Search_info.println("command:\t" + command);
		try {
			Process p = Runtime.getRuntime().exec(command);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		} catch (Exception e) {
			System.out.println(e);
		}

		CheckTime.End();
	}

	public static void CalculateEvalue(String spectrum, String database, String param, String outputName) 
	{
		CheckTime.Start();
		
		String command = "./Comet-E.exe -D" + database + " -P" + param + " -N" + outputName + " -S"+ ProgressiveSearch.outDir +"Comet-P_result.txt " + spectrum;

		//PrintWriter newDBResult = new PrintWriter(new BufferedWriter(new FileWriter("Comet-P_SearchResult.txt")));
		System.out.println(command);
		ProgressiveSearch.Search_info.println("command:\t" + command);
		try {
			Process p = Runtime.getRuntime().exec(command);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		} catch (Exception e) {
			System.out.println(e);
		}

		CheckTime.End();
	}
	
	public static void SearchOriginal(String spectrum, String database, String param, String outputName) 
	{
		CheckTime.Start();
		String command = "./comet_Original.exe -D" + database + " -P" + param + " -N" + outputName + " " + spectrum;
		
		System.out.println(command);
		ProgressiveSearch.Search_info.println("command:\t" + command);
		try {
			Process p = Runtime.getRuntime().exec(command);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		} catch (Exception e) {
			System.out.println(e);
		}

		CheckTime.End();
	}
	
	public static void SearchComet_withoutEvalue(String spectrum, String database, String param, String outputName) 
	{
		CheckTime.Start();
		String command = "./comet_withoutEvalue.exe -D" + database + " -P" + param + " -N" + outputName + " " + spectrum;
		
		System.out.println(command);
		ProgressiveSearch.Search_info.println("command:\t" + command);
		try {
			Process p = Runtime.getRuntime().exec(command);
			BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = null;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		CheckTime.End();
	}
}

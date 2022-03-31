import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class ResultCompare {

	
	// Column Index
	public static int scan_index = -1;
	public static int num_index = -1;
	public static int charge_index = -1;
	public static int evalue_index = -1;
	public static int deltaCN_index = -1;
	public static int peptide_index = -1;
	public static int plain_peptide_index = -1;
	public static int modification_index = -1;
	public static int protein_index = -1;
	public static int xcorr_index = -1;


	public static void main(String[] args) throws Exception {

		FindColumnIndex();
		
		
		/*
		BufferedReader cometResult = new BufferedReader(new FileReader("cometResult.txt"));
		BufferedReader preogressiveResult = new BufferedReader(new FileReader("progressiveResult.txt"));

		PrintWriter compareResult = new PrintWriter(new BufferedWriter(new FileWriter("compareResult.txt")));
		

		String s;

		while (!(s = cometResult.readLine()).split("\t")[0].equals("scan")) {}
		while (!(s = preogressiveResult.readLine()).split("\t")[0].equals("scan")) {}
		
		while (true) {
			s = cometResult.readLine();
			if(s==null)
				break;
			
			String comtResult_split[] = s.split("\t");
			s = preogressiveResult.readLine();
			
			if(s==null)
				System.out.println("err - cometResult not finished");
			
			String preogressiveResult_split[] = s.split("\t");
			
			
			if( !(comtResult_split[scan_index] + "_" + comtResult_split[charge_index]).equals(preogressiveResult_split[scan_index] + "_" + preogressiveResult_split[charge_index]) )
			{
				System.out.println("err - results dont match : " + comtResult_split[scan_index] + "_" + comtResult_split[charge_index] +" - " +preogressiveResult_split[scan_index] + "_" + preogressiveResult_split[charge_index] );
				break;
			}
			else
			{
				compareResult.println(comtResult_split[scan_index]+"\t"+comtResult_split[num_index]+"\t"+comtResult_split[charge_index]+"\t"+
						comtResult_split[evalue_index]+"\t"+comtResult_split[xcorr_index]+"\t"+comtResult_split[deltaCN_index]+"\t"+
						preogressiveResult_split[evalue_index]+"\t"+preogressiveResult_split[xcorr_index]+"\t"+preogressiveResult_split[deltaCN_index]);
			}
			
		}
		
		cometResult.close();
		preogressiveResult.close();
		compareResult.close();
		*/
		

		BufferedReader cometResult = new BufferedReader(new FileReader("cometResult.txt"));
		BufferedReader preogressiveResult = new BufferedReader(new FileReader("progressiveResult.txt"));

		PrintWriter cometResult_f = new PrintWriter(new BufferedWriter(new FileWriter("cometResult_filtered.txt")));
		PrintWriter preogressiveResult_f = new PrintWriter(new BufferedWriter(new FileWriter("progressiveResult_filtered.txt")));
		

		String s;

		while (!(s = cometResult.readLine()).split("\t")[0].equals("scan")) {}
		while (!(s = preogressiveResult.readLine()).split("\t")[0].equals("scan")) {}

		
		while ((s = cometResult.readLine())!=null)
		{
			String split[] = s.split("\t");
			cometResult_f.println(split[scan_index]+"\t"+split[num_index]+"\t"+split[charge_index]+"\t"+split[xcorr_index]+"\t"+split[deltaCN_index]);
		}
		
		while ((s = preogressiveResult.readLine())!=null)
		{
			String split[] = s.split("\t");
			preogressiveResult_f.println(split[scan_index]+"\t"+split[num_index]+"\t"+split[charge_index]+"\t"+split[xcorr_index]+"\t"+split[deltaCN_index]);
		}
		
		cometResult.close();
		preogressiveResult.close();
		cometResult_f.close();
		preogressiveResult_f.close();

	}
	

	static void FindColumnIndex() throws IOException
	{
		BufferedReader old_result = new BufferedReader(new FileReader("cometResult.txt"));
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
		}
	}
	
}

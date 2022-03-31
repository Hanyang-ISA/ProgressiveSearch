import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class ExtractSpectrum {

	public static void extract() throws Exception {

		CheckTime.Start();
		HashSet<Integer> delSpectrumIndexList = Compare();
		ExtractSpectra(delSpectrumIndexList);
		CheckTime.End();
	}

	static HashSet<Integer> Compare() throws Exception {

		String s;
		HashSet<Integer> del_scan = new HashSet<Integer>();
		BufferedReader deleted_DB = new BufferedReader(new FileReader(ProgressiveSearch.outDir + "deleted.fasta"));
		HashMap<String, Integer> deleted_Protein = new HashMap<String, Integer>();
		while ((s = deleted_DB.readLine()) != null) {
			if (s.startsWith(">")) {
				String header = s.split(" ")[0].substring(1);
				deleted_Protein.put(header, 1);
				deleted_Protein.put(ProgressiveSearch.dec_prefix + header, 1);
			}
		}
		deleted_DB.close();


		BufferedReader bufferedReader = new BufferedReader(new FileReader(ProgressiveSearch.oldDB_result));

		while (!(s = bufferedReader.readLine()).split("\t")[0].equals("scan")) {
		}

		while ((s = bufferedReader.readLine()) != null) {
			String s_split[] = s.split("\t");
			if(s_split.length == 1 )
			{
				int count = Integer.parseInt( s.split("\t")[0]);
				for(int i=0; i<count; i++)
					bufferedReader.readLine();
			}
			else
			{
				String proteinList[] = s_split[ProgressiveSearch.protein_index].split(",");
				Boolean isDeletedSpectrum = true;
				for (int i = 0; i < proteinList.length; i++) {
					if (deleted_Protein.get(proteinList[i]) == null) {
						isDeletedSpectrum = false;
						break;
					}
				}
				if (isDeletedSpectrum) {
					del_scan.add(Integer.parseInt(s_split[0]));
				}
			}
			
		}
		ProgressiveSearch.Search_info.println("Number of del spectrum: " + del_scan.size());
		return del_scan;
	}

	static void ExtractSpectra(HashSet<Integer> scan_list) throws Exception {

		BufferedReader spectrum = new BufferedReader(new FileReader(ProgressiveSearch.spectrum));
		PrintWriter extrated_spectrum = new PrintWriter(new BufferedWriter(new FileWriter(ProgressiveSearch.outDir + ProgressiveSearch.spectrum.substring(0, ProgressiveSearch.spectrum.length()-4)+"_extracted.mgf")));

		String s;
		String header = "";

		while ((s = spectrum.readLine()) != null) {
			if (s.equals("")) {

			} else if (s.startsWith("SCANS=")) {
				int current_scan = Integer.parseInt(s.split("SCANS=")[1]);
				if (scan_list.contains(current_scan)) {
					extrated_spectrum.println(header);
					extrated_spectrum.println(s);
					while (!(s = spectrum.readLine()).equals("END IONS")) {
						extrated_spectrum.println(s);
					}
					extrated_spectrum.println(s);
				} else {
					while (!(s = spectrum.readLine()).equals("END IONS")) {

					}
				}
				header = "";
			} else {
				if (header == "")
					header = s;
				else
					header += "\n" + s;

			}
		}
		spectrum.close();
		extrated_spectrum.close();
	}
}

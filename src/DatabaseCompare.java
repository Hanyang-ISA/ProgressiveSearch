import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;

public class DatabaseCompare {

	static CheckTime checkTime;

	public static void run() throws IOException {
		CheckTime.Start();
		compare();
		CheckTime.End();
	}

	public static void compare() throws IOException {

		BufferedReader old_DB = new BufferedReader(new FileReader(ProgressiveSearch.oldDB));
		BufferedReader new_DB = new BufferedReader(new FileReader(ProgressiveSearch.newDB));

		PrintWriter DB_inter = new PrintWriter(new BufferedWriter(new FileWriter(ProgressiveSearch.outDir + "shared.fasta")));
		PrintWriter DB_ins = new PrintWriter(new BufferedWriter(new FileWriter(ProgressiveSearch.outDir +"inserted.fasta")));
		PrintWriter DB_del = new PrintWriter(new BufferedWriter(new FileWriter(ProgressiveSearch.outDir +"deleted.fasta")));

		HashSet<DatabaseUnit> ArrayList_DB = new HashSet<DatabaseUnit>();

		long amino_count[] = new long[30];

		String s;
		String head = "";
		String seq = "";

		// read old DB
		int old_Protein_cnt = 0;
		int old_Seq_cnt = 0;
		while ((s = old_DB.readLine()) != null) {
			if (s.charAt(0) == '>') {
				old_Protein_cnt++;
				if (seq != "")
					ArrayList_DB.add(new DatabaseUnit(head, seq));
				seq = "";
				head = s.split(" ")[0];
			} else {
				seq += s;
				old_Seq_cnt += s.length();
			}
		}
		ArrayList_DB.add(new DatabaseUnit(head, seq));

		// read new DB
		int new_Protein_cnt = 0;
		int new_Seq_cnt = 0;
		int ins_Protein_cnt = 0;
		int ins_Seq_cnt = 0;
		int del_Protein_cnt = 0;
		int del_Seq_cnt = 0;
		int intersection_Protein_cnt = 0;
		int intersection_Seq_cnt = 0;
		seq = "";
		
		while ((s = new_DB.readLine()) != null) {
			if (s.charAt(0) == '>')
			{
				new_Protein_cnt++;
				if (seq != "") 
				{
					if (ArrayList_DB.contains(new DatabaseUnit(head, seq))) 
					{
						intersection_Protein_cnt++;
						intersection_Seq_cnt += seq.length();
						DB_inter.println(head);
						DB_inter.println(seq);
						ArrayList_DB.remove(new DatabaseUnit(head, seq));
					} 
					else
					{
						ins_Protein_cnt++;
						ins_Seq_cnt += seq.length();
						DB_ins.println(head);
						DB_ins.println(seq);
					}
				}
				seq = "";
				head = s.split(" ")[0];
			} else {
				seq += s;
				new_Seq_cnt += s.length();
			}
		}
		if (ArrayList_DB.contains(new DatabaseUnit(head, seq))) {
			intersection_Protein_cnt++;
			intersection_Seq_cnt += seq.length();
			DB_inter.println(head);
			DB_inter.println(seq);
			ArrayList_DB.remove(new DatabaseUnit(head, seq));
		} else {
			DB_ins.println(head);
			DB_ins.println(seq);
		}

		for (DatabaseUnit unit : ArrayList_DB) {

			DB_del.println(unit.header);
			DB_del.println(unit.seq);
			del_Protein_cnt++;
			del_Seq_cnt += unit.seq.length();
			for (int i = 0; i < unit.seq.length(); i++) {
				amino_count[unit.seq.charAt(i) - 'A']++;
			}
		}

		DB_inter.close();
		DB_ins.close();
		DB_del.close();

		ProgressiveSearch.Search_info.println("DB\t#protein\t#seq");
		ProgressiveSearch.Search_info.println("Old\t" + old_Protein_cnt + "\t" + old_Seq_cnt);
		ProgressiveSearch.Search_info.println("New\t" + new_Protein_cnt + "\t" + new_Seq_cnt);
		ProgressiveSearch.Search_info.println("ins\t" + ins_Protein_cnt + "\t" + ins_Seq_cnt);
		ProgressiveSearch.Search_info.println("del\t" + del_Protein_cnt + "\t" + del_Seq_cnt);
		ProgressiveSearch.Search_info.println("srd\t" + intersection_Protein_cnt + "\t" + intersection_Seq_cnt);

		System.out.println("DB\t#protein\t#seq");
		System.out.println("Old\t" + old_Protein_cnt + "(" + (float)old_Protein_cnt / (float)new_Protein_cnt * 100.0  + "%)" + "\t" + old_Seq_cnt + "(" + (float)old_Seq_cnt / (float)new_Seq_cnt * 100.0  + "%)");
		System.out.println("New\t" + new_Protein_cnt + "(" + (float)new_Protein_cnt / (float)new_Protein_cnt * 100.0  + "%)" + "\t" + new_Seq_cnt + "(" + (float)ins_Seq_cnt / (float)new_Seq_cnt * 100.0  + "%)");
		System.out.println("ins\t" + ins_Protein_cnt + "(" + (float)ins_Protein_cnt / (float)new_Protein_cnt * 100.0  + "%)" + "\t" + ins_Seq_cnt + "(" + (float)ins_Seq_cnt / (float)new_Seq_cnt * 100.0  + "%)");
		System.out.println("del\t" + del_Protein_cnt + "(" + (float)del_Protein_cnt / (float)new_Protein_cnt * 100.0  + "%)" + "\t" + del_Seq_cnt + "(" + (float)del_Seq_cnt / (float)new_Seq_cnt * 100.0  + "%)");
		System.out.println("srd\t" + intersection_Protein_cnt + "(" + (float)intersection_Protein_cnt / (float)new_Protein_cnt * 100.0  + "%)" + "\t" + intersection_Seq_cnt + "(" + (float)intersection_Seq_cnt / (float)new_Seq_cnt * 100.0  + "%)");

	}
}



class DatabaseUnit {
	String header;
	String seq;

	public DatabaseUnit(String header, String seq) {
		this.header = header;
		this.seq = seq;
	}

	@Override
	public int hashCode() {
		return (header + seq).hashCode();
	}

	@Override
	public boolean equals(Object a) {
		DatabaseUnit obj = (DatabaseUnit) a;
		return ( this.header.equals(obj.header) && this.seq.equals(obj.seq) );
	}
}

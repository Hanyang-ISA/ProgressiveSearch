import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;

public class HistogramInfo {

	public int scanNumber;
	public int charge;
	public int histoSize;
	
	public HashMap< Integer, Integer> histogram;
	
	HistogramInfo(int scanNumber, int charge)
	{
		this.scanNumber = scanNumber;
		this.charge = charge;
		this.histoSize = 0;
		histogram = new HashMap<Integer, Integer>();
	}
	
	HistogramInfo(int scanNumber, int charge, int histoSize)
	{
		this.scanNumber = scanNumber;
		this.charge = charge;
		this.histoSize = histoSize;
		histogram = new HashMap<Integer, Integer>();
	}

	public void Print(PrintWriter printWriter)
	{
		
		Object[] mapkey = histogram.keySet().toArray();
		Arrays.sort(mapkey);
		printWriter.println("BinCount" + "\t" + charge + "\t" + histogram.size());
		for (Object nKey : mapkey)
		{
			printWriter.println(nKey + "\t" + histogram.get(nKey));
		}
	}
	
	public void PrintWithScanNumber(PrintWriter printWriter)
	{
		
		Object[] mapkey = histogram.keySet().toArray();
		Arrays.sort(mapkey);
		printWriter.println(scanNumber + "\t" + charge + "\t" + histogram.size());
		for (Object nKey : mapkey)
		{
			printWriter.println(nKey + "\t" + histogram.get(nKey));
		}
	}
}

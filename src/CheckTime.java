
public class CheckTime {

	static double start, end;;

	public static void Start() {
		start = System.currentTimeMillis();
	}

	public static void End() {
		double time = (System.currentTimeMillis() - start) / 1000.0;
		ProgressiveSearch.Search_info.println("time:\t" + time);
		//System.out.println("time:\t" + time);
	}

}

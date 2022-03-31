import java.util.Objects;

public class PSM_ID implements Comparable<PSM_ID> {

	public int scanNumber;
	public int charge;

	public PSM_ID(int scanNumber, int charge) {
		this.scanNumber = scanNumber;
		this.charge = charge;
	}

	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		PSM_ID other = (PSM_ID) o;

		return other.scanNumber == scanNumber && other.charge == charge;
	}
	
	@Override
	public int hashCode() {
        //int result = scanNumber;
        //result = 31 * result + charge;
        //return result;
		return Objects.hash(scanNumber + "-" + charge);
	}

	@Override
	public int compareTo(PSM_ID psm) {
		if (this.scanNumber < psm.scanNumber)
			return -1;
		else if (this.scanNumber > psm.scanNumber)
			return 1;
		else {
			if (this.charge < psm.charge)
				return -1;
			else if (this.charge > psm.charge)
				return 1;
			else
				return 0;
		}
	}

}

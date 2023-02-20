package dnsmatch;

public class DNSRecord {

	public static final int NIM_LENGTH = 10;
	public String NIM;
	public String nama;
	public String ttl;
	public String semester;
	public float ipk;

	public String ocr_image_filename; //scanned/ocred filename 

	public DNSRecord(String nIM2, String nama2, float ipk2, String semester2) {
		// TODO Auto-generated constructor stub
		this.NIM = nIM2;
		this.nama = nama2;
		this.ipk = ipk2;
		this.semester = semester2;
	}

	public String toString() {
		return "NIM: " + NIM + ", NAMA:" + nama + ", IPK=" + ipk + ", TTL:" + ttl + ", Semester:" + semester +", "+ocr_image_filename;
	}

	public final int extract_year() throws Exception {
		// TODO Auto-generated method stub
		if (this.NIM == null)
			throw new Exception("NIM is null: " + this);
		String y2 = this.NIM.substring(2, 4);
//		System.out.println("NIM 2 is : #"+y2 + "#"); System.exit(-1);
		switch (y2) {
		case "17":
			return 2017;
		case "18":
			return 2018;
		case "19":
			return 2019;
		case "20":
			return 2020;
		case "21":
			return 2021;
		case "22":
			return 2022;

		}
		return -1;
	}
}

package panlibin.study.string;

import java.io.IOException;

public class RuntimeConstantsPool {
	public static void main(String[] args) {
		char[] value = { 105, 66, 121, 116, 101, 67, 111, 100, 101 };
		String s1 = new String(value);
		s1.intern();
		try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

package panlibin.study.string;

import java.io.IOException;

public class FinalRefString {
	public static void main(String[] args) {
		final String s1 = "aa";
		String s2 = s1+"bb";
		String s3 = "aabb";
		System.out.println(s3 == s2 );// false
		try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

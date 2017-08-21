package panlibin.study.string;

import java.io.IOException;

public class ConstructorCreateString {
	public static void main(String[] args) {
		/**
		 	How this works?(这些动作都在编译的时候完成)
			String s1 = new String("iByteCode");
			st1.JVM常量池中是否存在iByteCode字符串的引用，发现常量池中不存在iByteCode字符串的引用;
			st2.创建一个iByteCode字符串对象，并将引用保存到常量池中;
			st3.JVM使用iByteCode作为参数构造一个新的String对象
			String s2 = "iByteCode";
			st4.JVM常量池中是否存在iByteCode字符串的引用，发现常量池中存在iByteCode字符串引用；
			st5.将iByteCode的关联给s2;
		 */
		String s1 = new String("iByteCode");
		String s2 = "iByteCode";
		System.out.println(s1 == s2);// false
		System.out.println("iByteCode" == s2);// true
		System.out.println("iByteCode" == s1);//false
		try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

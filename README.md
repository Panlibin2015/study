# study
![index](https://github.com/Panlibin2015/study/raw/master/img/DirectCreateString.png)
![index](https://github.com/Panlibin2015/study/raw/master/img/ConstructorCreateString.png)
![index](https://github.com/Panlibin2015/study/raw/master/img/RuntimeConstant.png)
![index](https://github.com/Panlibin2015/study/raw/master/img/FinalCreateString1.png)
![index](https://github.com/Panlibin2015/study/raw/master/img/FinalCreateString2.png)
	2.1 不可变类
	2.1.1 什么是不可变类
		不可变类：所谓的不可变类是指这个类的实例一旦创建完成后，就不能改变其成员变量值。如JDK内部自带的很多不可变类：Interger、Long和			String等。
    		可变类：相对于不可变类，可变类创建实例后可以改变其成员变量值，开发中创建的大部分类都属于可变类。
	2.1.2 不可变类的设计方法
		1. 类添加final修饰符，保证类不被继承。
		2. 保证所有成员变量必须私有，并且加上final修饰
		3. 通过构造器初始化所有成员，进行深拷贝(deep copy)
		4. 避免成员变量逃逸,包括setter、getter

		下面来验证下上面的定义：
		情况一：
			public class A {
				private int state = 1;
				public int getState(){
					return this.state;
				}
			}
			类A是一个不可变类，我们就不能改变其成员变量的值。

			class B extends A {
				public int getState(){
					return super.state++;
				}
			}
			此时类B继承类A，并且重写了getState()方法，而重写的方法中使state自增：
			A a = new B();
			a.getState();

			所以继承会破坏类的不可变性。

		情况二：
			我们对A类进行改进：
			public final class A {
				private int state = 1;
				public int getState(){
					return this.state;
				}
			}
			当我们用final对A类修饰后，A类就变成了一个不可继承的类，从而避免了情况一。
			但是我们知道Java有一个特性：动态性。
			public class Test {
				public static void main(String[] args){
					A a = new A();
					System.out.println(a.getState());// 1
					Class<?> clazz = a.getClass();
					Field stateField = clazz.getDeclaredField("state");
					stateField.setAccessible(true);
					stateField.set(a,2);
					stateField.setAccessible(false);
					System.out.println(a.getState())// 2
				}
			}
		我们通过Java反射改变了Class A的state成员变量的值。

		情况三：
			我们对A类再进行改进：
			public final class A｛
				private final int state = 1;
				public int getState(){
					return this.state;
				}
			｝
			至此我们的不可变类A完成了。

		情况四：
			如果A类state不是一个基本类型：
			public final class A {
				private final int[] states;
				public A (int[] states) {
					this.states = states;
				}

				public int[] getStates() {
					return this.states;
				}

				public String toString(){
					return Arrays.toString(states)+"";
				}
			}
			显然，这里存在逃逸现象。
			public class Test {
				public static void main(String[] args){
					int[] states = {1,2,3};
					A a = new A(states);
					System.out.println(a.toString());// {1,2,3}
					states[0]=122;
					System.out.println(a.toString());// {122,2,3}
				}
			}

			因此我们这里需要对A继续改进：
			public final class A {
				private final int[] states;
				public A (int[] states) {
					this.states = states.clone();// 拷贝
				}

				public int[] getStates() {
					return this.states.clone();// 拷贝
				}

				public String toString(){
					return Arrays.toString(states)+"";
				}
			}
			clone()方法是拷贝一个实例在内存中，拷贝的实例与与原有实例一样，但是引用地址不一样，分别指向各自的实例。
			因此，通过深度拷贝屏蔽了states（引用型变量）与外界的交互，从而使类A又变为了不可变类。

		2.1.3 不可变类String
			public final class String
			    implements java.io.Serializable, Comparable<String>, CharSequence
			{
			    /** The value is used for character storage. */
			    private final char value[];
			    /** The offset is the first index of the storage that is used. */
			    private final int offset;
			    /** The count is the number of characters in the String. */
			    private final int count;
			    /** Cache the hash code for the string */
			    private int hash; // Default to 0
			    ....
			    public String(char value[]) {
			         this.value = Arrays.copyOf(value, value.length); // deep copy操作
			     }
			    ...
			     public char[] toCharArray() {
			     // Cannot use Arrays.copyOf because of class initialization order issues
			        char result[] = new char[value.length];
			        System.arraycopy(value, 0, result, 0, value.length);
			        return result;
			    }
			    ...
			}
			如上代码所示，可以观察到以下设计细节:
				1.String类被final修饰，不可继承
				2.string内部所有成员都设置为私有变量
				3.不存在value的setter
				4.并将value和offset设置为final。
				5.当传入可变数组value[]时，进行copy而不是直接将value[]复制给内部变量.
				6.获取value时不是直接返回对象引用，而是返回对象的copy.

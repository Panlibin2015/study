# 实验楼教程编写模板及实例
#投稿须知

1. 教程必须为作者本人原创，不涉及到其他版权问题，有参考他人或开源项目的内容必须在教程中详细列出。
2. 教程必须在实验楼环境可实现（Ubuntu Linux 14.04桌面环境）。
3. 教程应达到边学边练，需在实践操作中穿插理论知识点讲解，至少包含3个知识点。
4. 教程需为 _markdown_ 格式文件，也可直接发送教程博文链接。
5. 有任何不清楚的地方欢迎与我们联系
 QQ：  3185057317
Email: yandan@simplecloud.cn 

#投稿方式

发送 常用QQ号+昵称+教程+授权文字 到yandan@simplecloud.cn快速投稿。
授权文字：为了尊重作者版权、保护作者利益，需要在投稿邮件中附上以下授权文字（粘贴到邮件中即可）：
> 我谨保证我是此教程的著作权合法人，我同意“实验楼”所属网站和媒体发布此教程，允许将此教程发布到实验楼的免费课或会员课中。未经实验楼或本人同意，其他机构、媒体一律不得转载、使用。  

#教程模板

## 一、实验介绍
### 1.1 实验内容
本实验将不可变类的创建，通过JVM相关知识与工具来理解String常量池以及5种String创建方式。
### 1.2 实验知识点 
1.不可变类与可变类
2.JVM运行时内存简介
3.OQL对象查找语言简介
4.String常量池
【实验中的核心知识点（至少包含3个知识点），完成该课程的收获】
### 1.3 实验环境
1.JDK1.6
2.Eclipse

## 二、实验步骤
2.1 不可变类
	2.1.1 什么是不可变类
		不可变类：所谓的不可变类是指这个类的实例一旦创建完成后，就不能改变其成员变量值。如JDK内部自带的很多不可变类：Interger、Long和String等。
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
				
2.2 JVM运行时内存模型
	
## 六、实验总结
【对该实验进行总结】

## 七、课后习题
【针对实验内容留下课程习题提供用户检测学习效果】

## 八、参考链接
【课程制作过程中参考资料链接来源，无则跳过】

#实例：Python基于共现提取《釜山行》人物关系

## 一、实验介绍
### 1.1 实验内容【对整个实验内容的概括介绍】
《釜山行》是一部丧尸灾难片，其人物少、关系简单，非常适合我们学习文本处理。课程将介绍共现在关系中的提取，使用python编写代码实现对《釜山行》文本的人物关系提取，最终利用Gephi软件对提取的人物关系绘制人物关系图。
### 1.2 实验知识点 【实验中的核心知识点，完成该课程的收获】
* 共现网络的基本原理
* Python代码对《釜山行》中人物关系提取的具体实现
* `jieba`库的基本使用
* Gephi软件的基本使用
### 1.3 实验环境【实验使用的实验环境及核心开发及部署软件简单介绍】
* python2.7
* Xfce终端
### 1.4 适合人群
本课程难度为一般，属于初级级别课程，适合具有Python基础的用户，熟悉python基础知识加深巩固。
### 1.5 代码获取
你可以通过下面命令将代码下载到实验楼环境中，作为参照对比进行学习。
```bash
$ wget http://labfile.oss.aliyuncs.com/courses/677/busan.py
```
## 二、实验原理【有则写无则不写】
实验基于简单共现关系，编写 Python 代码从纯文本中提取出人物关系网络，并用`Gephi` 将生成的网络可视化。下面介绍共现网络的基本原理。你可以在我的博客查看对[共现网络简单的英文介绍](https://forec.github.io/2016/10/03/co-occurrence-structure-capture/)。

实体间的共现是一种基于统计的信息提取。关系紧密的人物往往会在文本中多段内同时出现，可以通过识别文本中已确定的实体（人名），计算不同实体共同出现的次数和比率。当比率大于某一阈值，我们认为两个实体间存在某种联系。这种联系可以具体细化，但提取过程也更加复杂。因此在此课程只介绍最基础的共现网络。
## 三、开发准备【有则写无则不写】
打开Xfce终端，进入 `Code` 目录，创建 `work` 文件夹, 将其作为课程的工作目录。下载并安装 `gephi` 。
```bash
$ mkdir work && cd work
$ mkdir gephi && cd gephi
$ wget http://labfile.oss.aliyuncs.com/courses/677/gephi-0.9.1-linux.tar.gz                         #下载
$ tar -zxvf gephi-0.9.1-linux.tar.gz     #解压 
```
下载《釜山行》的中文剧本。
```bash
$ wget http://labfile.oss.aliyuncs.com/courses/677/busan.txt
```
安装`jieba`中文分词。
```bash
$ sudo pip2 install jieba
```## 四、项目文件结构
【针对具有多个文件的项目或者文件之间彼此有依赖关系，若项目仅一个代码文件的可忽略此项】

【整个项目所有文件（依赖文件、代码文件等）的目录结构】

【本实验涉及到的文件（需修改、重新编写等）单独标出】
## 五、实验步骤
### 5.1 观察文本结构、准备词典
《釜山行》剧本非常适合文本处理，语言简洁，大致每一段对应一个关键情节。人物较少且易于识别，所以非常适合文本处理的学习，因此选用了《釜山行》作为课程的样例。

由于《釜山行》人物少、关系简单，所以我们可以通过词典指定人物名称的方式做实体识别。你也可以不建立字典并尝试使用某种分词算法或包装好的分词库（如教程使用的`jieba`），但离开特定词典的针对特定文本的分词效果可能会有很大程度削弱。因此对简单网络而言，建立字典是效率较高的做法。

可以通过各类百科获取《釜山行》的主要人物，你可以在[百度百科](http://baike.baidu.com/item/%E9%87%9C%E5%B1%B1%E8%A1%8C#3)中找到他们的介绍，并将人名写入一个字典中。项目将主要人物的名称保存在文件`dict.txt`中，你可以通过下面的命令下载这个字典，也可以自己新建一个文件保存。字典`dict.txt`需放在文件夹`work`下。
```bash
$ wget http://labfile.oss.aliyuncs.com/courses/677/dict.txt
```### 5.2 确定需要的变量
在`work`文件下创建代码文件`busan.py`,开始进行python代码的编写。

在代码中，我使用字典类型`names`保存人物，该字典的键为人物名称，值为该人物在全文中出现的次数。我使用字典类型`relationships`保存人物关系的有向边，该字典的键为有向边的起点，值为一个字典`edge`，`edge`的键是有向边的终点，值是有向边的权值，代表两个人物之间联系的紧密程度。`lineNames`是一个缓存变量，保存对每一段分词得到当前段中出现的人物名称，`lineName[i]`是一个列表，列表中存储第`i`段中出现过的人物。
```python
# -*-  coding: utf-8 -*-
import os, sys
import jieba, codecs, math
import jieba.posseg as pseg

names = {}			# 姓名字典
relationships = {}	# 关系字典
lineNames = []		# 每段内人物关系
```### 5.3 文本中实体识别
在具体实现过程中，读入《釜山行》剧本的每一行，对其做分词（判断该词的词性是不是“人名”[词性编码：nr]，如果该词的词性不为nr，则认为该词不是人名），提取该行（段）中出现的人物集，存入`lineName`中。之后对出现的人物，更新他们在`names`中的出现次数。
```python
jieba.load_userdict("dict.txt")		# 加载字典
with codecs.open("busan.txt", "r", "utf8") as f:
	for line in f.readlines():
		poss = pseg.cut(line)		# 分词并返回该词词性
		lineNames.append([])		# 为新读入的一段添加人物名称列表
		for w in poss:
			if w.flag != "nr" or len(w.word) < 2:
				continue			# 当分词长度小于2或该词词性不为nr时认为该词不为人名
			lineNames[-1].append(w.word)		# 为当前段的环境增加一个人物
			if names.get(w.word) is None:
				names[w.word] = 0
				relationships[w.word] = {}
			names[w.word] += 1					# 该人物出现次数加 1
```
你可以在 `with` 代码块之后添加以下代码输出生成的 `names` 来观察人物出现的次数：
```python
for name, times in names.items():
    print name, times
```
运行代码
```
python busan.py
```
在实验楼中的显示结果如下图：

此处输入图片的描述
### 5.4 根据识别结果构建网络
对于 `lineNames` 中每一行，我们为该行中出现的所有人物两两相连。如果两个人物之间尚未有边建立，则将新建的边权值设为 1，否则将已存在的边的权值加 1。这种方法将产生很多的冗余边，这些冗余边将在最后处理。
```python
for line in lineNames:					# 对于每一段
	for name1 in line:					
		for name2 in line:				# 每段中的任意两个人
			if name1 == name2:
				continue
			if relationships[name1].get(name2) is None:		# 若两人尚未同时出现则新建项
				relationships[name1][name2]= 1
			else:
				relationships[name1][name2] = relationships[name1][name2]+ 1		# 两人共同出现次数加 1
```### 5.5 过滤冗余边并输出结果
将已经建好的 `names` 和 `relationships` 输出到文本，以方便 `gephi` 可视化处理。输出边的过程中可以过滤可能是冗余的边，这里假设共同出现次数少于 3 次的是冗余边，则在输出时跳过这样的边。输出的节点集合保存为 `busan_node.txt` ，边集合保存为 `busan_edge.node` 。
```python
with codecs.open("busan_node.txt", "w", "gbk") as f:
	f.write("Id Label Weight\r\n")
	for name, times in names.items():
		f.write(name + " " + name + " " + str(times) + "\r\n")

with codecs.open("busan_edge.txt", "w", "gbk") as f:
	f.write("Source Target Weight\r\n")
	for name, edges in relationships.items():
		for v, w in edges.items():
			if w > 3:
			f.write(name + " " + v + " " + str(w) + "\r\n")
```
完成所有代码编写后，运行
```
python busan.py
```
在文件夹`work`下将会生成`busan_node.txt`和`busan_edge.node`。
### 5.6 可视化网络
前面对《釜山行》剧本中的人物关系数据进行了处理，下面我们将使用gephi这个软件来将人物关系可视化，以便展示的更直观，毕竟生硬的数字和文本，或许只有你才懂，其他人可看不明白。

使用 `gephi` 导入生成的网络，并生成简单的可视化布局。执行下面命令启动 `gephi` 。
```bash
$ cd gephi-0.9.1
$ cd bin
$ ./gephi
```
点击 `文件 -> Import SpreadSheets` 。

此处输入图片的描述

分别选择**节点表格**和**边表格**导入上面代码中生成的两个文件，分隔符选择 `空格` ，编码选择 `GB2312` 。

此处输入图片的描述

此处输入图片的描述

导入后 `gephi` 将显示所有节点。此时节点没有合适的布局，如下图。你可以在最上方的 `数据资料` 选项卡中查看图中所有的边和节点，对于分词不准确导致的噪音，可以手动删除。

此处输入图片的描述

分别点击右侧 `统计` 栏中 `平均度` 和 `模块化` 运行计算。模块化运算时 `Resolution` 值填写 `0.5` 。

此处输入图片的描述

点击左上角`外观`中`节点`第一个选项卡，选择`数值设定`，选择`Modularity Class`。

此处输入图片的描述

选中第二个选项卡，选择`数值设定`，选择`连入度`，最小尺寸填10，最大尺寸填40，点击应用为节点染色、放大。

此处输入图片的描述

选择左下角`布局`中的 `Force Atlas` ，斥力强度填写 `20000.0` ，吸引强度填写 `1.0` 。点击运行，稍后点击停止。

此处输入图片的描述

此时布局大致如下图所示。节点染色根据模块化计算结果不定，但染色效果大致相同。

此处输入图片的描述

点击最上方的 `预览` 按钮，选中左侧 `节点标签` 中 `显示标签` 选项，并选择一种字体，这里选择 `文泉驿微米黑` 。

此处输入图片的描述

点击刷新按钮，右侧显示最终的人物关系图。

此处输入图片的描述
## 六、实验总结
基于简单的共现关系，结合jieba库使用python代码从纯文本中提取出人物关系网后，并使用Gephi软件对提取的数据进行了可视化。整个实验让我们熟悉了python基础加深巩固，并且学习了如何使用Gephi软件进可视化。
## 七、课后习题
【针对实验内容留下课程习题提供用户检测】
## 八、参考链接【有则必写无则不写】
【课程制作过程中参考资料链接来源】


Github repository: https://github.com/minebreak28/blackjack
In the zip folder, you will see the following files:


1.	BlackjackBasicStrategy.java and BlackjackCardCounting.java: These program will play 1000 rounds of 1000 hands of blackjack using basic strategy and card counting, respectively, and then print out results onto an Excel worksheet. Note that the functionality to print out to excel sheets stem from the org.apache.poi.xssf imports, which only work if the program is run in Eclipse and has an abundance of jar files installed in the classpath:

![image](https://user-images.githubusercontent.com/78050276/234165251-94070c53-350b-42a2-b3a5-c369fcae48a9.png)
These files will be included in the zip file as well and can be downloaded if you would like to test our code out. Furthermore, our code uses the JRE System Library [JavaSE-11] to compile and use certain syntax and methods.


2.	blackjackTest.java is a simple Junit test case that tests some basic methods in the two main source code. Please install Junit 5 into Eclipse if you wish to run these tests.


4.	blackjack_data.xlsx is a combination of bsDataset.xlsx and ccDataset.xlsx. bsDataset.xlsx is the dataset of 1000 rounds of blackjack using only basic strategy, while ccDataset.xlsx is the dataset of 1000 rounds of blackjack using basic strategy and card counting to decide how much to bet.


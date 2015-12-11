package hm_ML.LGF_build_SVM_data_java;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

public class svmReduce {
public static void main(String[] args) throws Exception {
	   String str = "[640048,672199,834518,835859],[8.141329803083583,7.252008952921531,6.949881455282027,8.95641688629414])";
	   BufferedReader reader1 = new BufferedReader(new FileReader("G://MSGDATA//svm/rssvm1"));
	   BufferedReader reader2 = new BufferedReader(new FileReader("G://MSGDATA//svm/rssvm2"));
	   BufferedReader reader3 = new BufferedReader(new FileReader("G://MSGDATA//svm/rssvm3"));
		
	   BufferedWriter writer = new BufferedWriter(new FileWriter("G://MSGDATA//svm/rssvm"));
	   while((str = reader1.readLine())!=null)
	   {
		   writer.write(str.substring(9,str.length())+"\n");
	   }
	   
	   while((str = reader2.readLine())!=null)
	   {
		   writer.write(str.substring(9,str.length())+"\n");
	   }
	   while((str = reader3.readLine())!=null)
	   {
		   writer.write(str.substring(9,str.length())+"\n");
	   }
	   
	   writer.close();
}

}

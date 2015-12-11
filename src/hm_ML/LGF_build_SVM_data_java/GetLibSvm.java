package hm_ML.LGF_build_SVM_data_java;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GetLibSvm {
public static void main(String[] args) throws Exception {
	   String str = "[640048,672199,834518,835859],[8.141329803083583,7.252008952921531,6.949881455282027,8.95641688629414])";
	   BufferedReader reader1 = new BufferedReader(new FileReader("G://MSGDATA//svm/rssvm"));
	   BufferedWriter writer = new BufferedWriter(new FileWriter("G://MSGDATA//svm/rssvm3.txt"));
	   
	   while((str = reader1.readLine())!=null)
	   {
		   String[] arr = str.split("],");
		   String a1 = arr[0].replaceAll("\\[|\\]\\)", "");
		   String a2 = arr[1].replaceAll("\\[|\\]\\)", "");
		   String[] arr1 = a1.split(","); 
		   String[] arr2 = a2.split(",");
		   for(int i = 0;i < arr1.length;i++)
		   {
			   writer.write(arr1[i] + ":" +arr2[i]+" ");
		   }
		   writer.write("\n");
	   }
	   
	   writer.close();
}
}

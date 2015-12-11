package hm_ML.LGF_build_SVM_data_java;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

public class getKind {
public static void main(String[] args) throws Exception {
	   BufferedReader reader1 = new BufferedReader(new FileReader("G://MSGDATA/rs/train2.txt"));
	   BufferedWriter writer = new BufferedWriter(new FileWriter("G://MSGDATA//svm/kind.txt"));
	   String tmp,kind;
	   while((tmp = reader1.readLine())!=null)
	   {
		   kind = tmp.substring(0, 1);
		   writer.write(kind+"\n");
	   }
	   writer.close();
}
}

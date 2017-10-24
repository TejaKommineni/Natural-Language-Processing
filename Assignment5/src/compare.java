import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class compare {
	
	public boolean compare(String a, String b)
	{
		FileReader f;
		FileReader f1;
		try {
			f = new FileReader(a);
			f1 = new FileReader(b);
			BufferedReader bf = new BufferedReader(f);
			BufferedReader bf1 = new BufferedReader(f1);
			String line1 = "";			
			while((line1 = bf.readLine())!=null)
			{	
				String line2 = bf1.readLine();
				if (line1.equals(line2)) {
					System.out.println(line1 +"   "+ line2);
				}
				else {
					System.out.println("EXIT");
					System.out.println(line1 +"   "+ line2);
					return false;
				}
							
			}
			f.close();
			bf.close();
			
			
		}
		catch(Exception e) {
			
		}
		
		return true;
	}
	
	public static void main(String[] args) {
		compare c = new compare();
		System.out.println(c.compare("test.txt.readable", "test.txt.readable.ALL"));
	}
	

}

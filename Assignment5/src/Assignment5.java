import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

public class Assignment5 {

	private void generateReadable(String[] args) {
		
		String trainFile = args[0];
		String testFile = args[1];
		String locFile = args[2];
		HashSet fTypes = new HashSet<String>();
		HashSet locations = new HashSet<String>();
		HashSet trainingWords = new HashSet<String>();
		HashSet trainingPOS = new HashSet<String>();
		HashMap features = new HashMap<String,Integer>();
		for(int i=3;i<args.length;i++){
			fTypes.add(args[i]);
		}
		FileReader f;
		try {
			f = new FileReader(locFile);
			BufferedReader bf = new BufferedReader(f);
			String currentLine = "";
			while((currentLine = bf.readLine())!=null)
			{
				if("".equals(currentLine) && !locations.contains(currentLine))
				{
					locations.add(currentLine);
				}
			}
			f.close();
			bf.close();
			readable(trainFile,locations,fTypes,trainingWords,trainingPOS,features,false);
			readable(testFile,locations,fTypes,trainingWords,trainingPOS,features,true);
			features.put("capitalized", features.size()+1);
			features.put("abbr", features.size()+1);
			features.put("location", features.size()+1);
			features.put("word-UNK", features.size()+1);
			features.put("prev-word-UNK", features.size()+1);
			features.put("next-word-UNK", features.size()+1);
			features.put("pos-UNKPOS", features.size()+1);
			features.put("prev-pos-UNKPOS", features.size()+1);
			features.put("next-pos-UNKPOS", features.size()+1);
			if(!features.containsKey("prev-word-PHI"))
			 {
				features.put("prev-word-PHI", features.size()+1);
			 }
			if(!features.containsKey("next-word-OMEGA"))
			 {
				features.put("next-word-OMEGA", features.size()+1);
			 }
			if(!features.containsKey("prev-pos-PHIPOS"))
			 {
				features.put("prev-pos-PHIPOS", features.size()+1);
			 }
			if(!features.containsKey("next-pos-OMEGAPOS"))
			 {
				features.put("next-pos-OMEGAPOS", features.size()+1);
			 }
			generateFeatures(trainFile,locations,fTypes,trainingWords,trainingPOS,features,false);
			//featureVector(trainFile,locations,fTypes,trainingWords,trainingPOS,false);
		}
		catch(Exception e)
		{
			
		}		
	}
	
	private void generateFeatures(String trainFile, HashSet locations, HashSet fTypes, HashSet trainingWords,
			HashSet trainingPOS, HashMap<String, Integer> features, boolean flag) {

		FileReader f;
		FileWriter f1;
		try {
			f = new FileReader(trainFile);
			f1 = new FileWriter(trainFile+".vector");
			BufferedReader bf = new BufferedReader(f);
			BufferedWriter bf1 = new BufferedWriter(f1);
			String currentLine = "";
			String prevLine = "";
			List<String> fileContents = new ArrayList<String>();
			while((currentLine = bf.readLine())!=null)
			{				
				fileContents.add(currentLine);
							
			}
			f.close();
			bf.close();
			for(int i=0;i<fileContents.size();i++)
			{
				if(!fileContents.get(i).equals("")) {
				String[] currentLineSplit = split(fileContents.get(i),0,"",trainingWords,flag);				
				TreeSet<Integer> tr = new TreeSet<Integer>(); 
				if(fTypes.contains("WORD"))
				{
					if(flag && !trainingWords.contains(currentLineSplit[2])) {
						tr.add(features.get("word-UNK"));
					}else {
					tr.add(features.get("word-"+currentLineSplit[2]));
					}
				}
				if(fTypes.contains("WORDCON"))
				{
					if(i!=0 && i!=fileContents.size()-1)
					{				
						String[] previousSplit = split(fileContents.get(i-1),-1,"WORDCON",trainingWords,flag);
						String[] nextSplit = split(fileContents.get(i+1),1,"WORDCON",trainingWords,flag);
						tr.add(features.get("prev-word-"+previousSplit[2]));
						tr.add(features.get("next-word-"+nextSplit[2]));
					}
					if(i==0 && i!=fileContents.size()-1)
					{	
					    String[] nextSplit = split(fileContents.get(i+1),1,"WORDCON",trainingWords,flag);
						
						tr.add(features.get("prev-word-PHI"));
						tr.add(features.get("next-word-"+nextSplit[2]));
					}
					if(i!=0 && i==fileContents.size()-1)
					{	
						String[] previousSplit = split(fileContents.get(i-1),-1,"WORDCON",trainingWords,flag);						
						
						tr.add(features.get("next-word-OMEGA"));
						tr.add(features.get("prev-word-"+previousSplit[2]));
					}
				}
				else
				{
					
				}
				if(fTypes.contains("POS"))
				{
					if(flag && !trainingPOS.contains(currentLineSplit[1])) {
						tr.add(features.get("pos-UNKPOS"));
						}
					else
					{
						tr.add(features.get("pos-"+currentLineSplit[1]));
					}					
				}
				else
				{
					
				}
				if(fTypes.contains("POSCON"))
				{
					if(i!=0 && i!=fileContents.size()-1)					{
						String[] previousSplit = split(fileContents.get(i-1),-1,"POSCON",trainingPOS,flag);
						String[] nextSplit = split(fileContents.get(i+1),1,"POSCON",trainingPOS,flag);						
						tr.add(features.get("prev-pos-"+previousSplit[1]));
						tr.add(features.get("next-pos-"+nextSplit[1]));
						
					}
					if(i==0 && i!=fileContents.size()-1)					{					
						
						String[] nextSplit = split(fileContents.get(i+1),1,"POSCON",trainingPOS,flag);
						tr.add(features.get("prev-pos-PHIPOS"));
						tr.add(features.get("next-pos-"+nextSplit[1]));
					}
					if(i!=0 && i==fileContents.size()-1)
					{			
						String[] previousSplit = split(fileContents.get(i-1),-1,"POSCON",trainingPOS,flag);				
						tr.add(features.get("prev-pos-"+previousSplit[1]));
						tr.add(features.get("next-pos-OMEGAPOS"));
					}
				}
				else
				{
				}
				if(fTypes.contains("ABBR"))
				{
					boolean flag1 = true;
					if(currentLineSplit[2].length()>4)
						flag1 = false;
					if(currentLineSplit[2].charAt(currentLineSplit[2].length()-1) != '.')
						flag1 = false;
					for(int j=0;j<currentLineSplit[2].length();j++)
					{
						if(currentLineSplit[2].charAt(j) != '.' &&  !Character.isAlphabetic(currentLineSplit[2].charAt(j)))
							flag1 = false;
					}
					
					if(flag1)
					{
						tr.add(features.get("abbr"));
					}
					else
					{
											
					}
				}
				else
				{
				}
				if(fTypes.contains("CAP"))
				{
					if(currentLineSplit[2].charAt(0) >=65 && currentLineSplit[2].charAt(0)<=90)
					{	
						tr.add(features.get("capitalized"));
					}
					else 
					{
					}
				}
				else
				{
					
				}
				if(fTypes.contains("LOCATION"))
				{
					if(locations.contains(currentLineSplit[2]))
					{
						tr.add(features.get("location"));
					}
					else
					{
						
					}
				}
				else
				{
					
				}		
				
				HashMap<String,Integer> labels = new HashMap();
				labels.put("O", 0); labels.put("B-PER", 1);labels.put("I-PER", 2);labels.put("B-LOC", 3);labels.put("I-LOC", 4);labels.put("B-ORG", 5); labels.put("I-ORG", 6);
				bf1.write(labels.get(currentLineSplit[0]));
				for(int next:tr)
				{
					bf1.write(" "+next+":1");
				}
				bf1.newLine();
			
			}
			
		}
			bf1.close();
			f1.close();
		}	
		catch(Exception e)
		{
			
		}	
				
	}
	
	private String[] split(String currentLine,int flag, String string, HashSet trainingWords,boolean b)
	{
		String[] currentLineSplit = new String[3];
		
		if(currentLine.equals("")) {
			
			if(flag == -1 && string.equals("WORDCON"))
			{
				currentLineSplit[2] = "PHI";
			}
			if(flag == 1 && string.equals("WORDCON"))
			{
				currentLineSplit[2] = "OMEGA";
			}
			if(flag == -1 && string.equals("POSCON"))
			{
				currentLineSplit[1] = "PHIPOS";
			}
			if(flag == 1 && string.equals("POSCON"))
			{
				currentLineSplit[1] = "OMEGAPOS";
			}
			return currentLineSplit;
			
		}
		
		currentLine = currentLine.trim();
		currentLine = currentLine + " ";
		int k = 0;
		while(!currentLine.equals("") && k<3)
		{					
			currentLineSplit[k++] = currentLine.substring(0,currentLine.indexOf(" "));
			currentLine = currentLine.substring(currentLine.indexOf(" "));
			currentLine = currentLine.trim();
			currentLine = currentLine + " ";
		}
		
		if(string.equals("WORDCON") && b && !trainingWords.contains(currentLineSplit[2]))
		{
			currentLineSplit[2] = "UNK";
		}
		if(b && string.equals("POSCON")  && !trainingWords.contains(currentLineSplit[1]))
		{
			currentLineSplit[1] = "UNKPOS";
		}
		
		return currentLineSplit;		
	}
	private HashSet<String> readable(String trainFile, HashSet locations, HashSet fTypes, HashSet trainingWords, HashSet trainingPOS, HashMap features, boolean flag)
	{
		FileReader f;
		FileWriter f1;
		try {
			f = new FileReader(trainFile);
			f1 = new FileWriter(trainFile+".readable");
			BufferedReader bf = new BufferedReader(f);
			BufferedWriter bf1 = new BufferedWriter(f1);
			String currentLine = "";
			String prevLine = "";
			List<String> fileContents = new ArrayList<String>();
			while((currentLine = bf.readLine())!=null)
			{				
				fileContents.add(currentLine);
							
			}
			f.close();
			bf.close();
			for(int i=0;i<fileContents.size();i++)
			{
				if(!fileContents.get(i).equals("")) {
				String[] currentLineSplit = split(fileContents.get(i),0,"",trainingWords, flag);				
				
				if(fTypes.contains("WORD"))
				{
					if(flag && !trainingWords.contains(currentLineSplit[2])) {
					bf1.write("WORD: UNK");
					bf1.newLine();
					}else {
					bf1.write("WORD: "+currentLineSplit[2]);
					bf1.newLine();
					if(!flag && !trainingWords.contains(currentLineSplit[2]))
					 {
						trainingWords.add(currentLineSplit[2]);
						features.put("word-"+currentLineSplit[2], features.size()+1);
					 }
					
					}
				}
				if(fTypes.contains("WORDCON"))
				{
					if(i!=0 && i!=fileContents.size()-1)
					{				
						String[] previousSplit = split(fileContents.get(i-1),-1,"WORDCON",trainingWords,flag);
						String[] nextSplit = split(fileContents.get(i+1),1,"WORDCON",trainingWords,flag);
						bf1.write("WORDCON: "+previousSplit[2] +" "+ nextSplit[2]);
						bf1.newLine();
						if(!flag && !features.containsKey("prev-word-"+previousSplit[2]))
						 {
							features.put("prev-word-"+previousSplit[2], features.size()+1);
						 }
						if(!flag && !features.containsKey("next-word-"+nextSplit[2]))
						 {
							features.put("next-word-"+nextSplit[2], features.size()+1);
						 }
					}
					if(i==0 && i!=fileContents.size()-1)
					{	
					    String[] nextSplit = split(fileContents.get(i+1),1,"WORDCON",trainingWords,flag);
						bf1.write("WORDCON: PHI "+ nextSplit[2]);
						bf1.newLine();
						if(!flag && !features.containsKey("next-word-"+nextSplit[2]))
						 {
							features.put("next-word-"+nextSplit[2], features.size()+1);
						 }
					}
					if(i!=0 && i==fileContents.size()-1)
					{	
						String[] previousSplit = split(fileContents.get(i-1),-1,"WORDCON",trainingWords,flag);						
						bf1.write("WORDCON: "+previousSplit[2] +" OMEGA");
						bf1.newLine();
						if(!flag && !features.containsKey("prev-word-"+previousSplit[2]))
						 {
							features.put("prev-word-"+previousSplit[2], features.size()+1);
						 }
					}
				}
				else
				{
					bf1.write("WORDCON: n/a");
					bf1.newLine();
				}
				if(fTypes.contains("POS"))
				{
					if(flag && !trainingPOS.contains(currentLineSplit[1])) {
						bf1.write("POS: UNKPOS");
						bf1.newLine();
						}
					else {
						bf1.write("POS: "+currentLineSplit[1]);
						bf1.newLine();
					}					
					if(!flag && !trainingPOS.contains(currentLineSplit[1]))
					{
						trainingPOS.add(currentLineSplit[1]);
						features.put("pos-"+currentLineSplit[1], features.size()+1);
					}
				}
				else
				{
					bf1.write("POS: n/a");
					bf1.newLine();
				}
				if(fTypes.contains("POSCON"))
				{
					if(i!=0 && i!=fileContents.size()-1)					{
						String[] previousSplit = split(fileContents.get(i-1),-1,"POSCON",trainingPOS,flag);
						String[] nextSplit = split(fileContents.get(i+1),1,"POSCON",trainingPOS,flag);
						bf1.write("POSCON: "+previousSplit[1] +" "+ nextSplit[1]);
						bf1.newLine();
						if(!flag && !features.containsKey("prev-pos-"+previousSplit[1]))
						 {
							features.put("prev-pos-"+previousSplit[1], features.size()+1);
						 }
						if(!flag && !features.containsKey("next-pos-"+nextSplit[1]))
						 {
							features.put("next-pos-"+nextSplit[1], features.size()+1);
						 }
					}
					if(i==0 && i!=fileContents.size()-1){					
						
						String[] nextSplit = split(fileContents.get(i+1),1,"POSCON",trainingPOS,flag);
						bf1.write("POSCON: PHIPOS "+ nextSplit[1]);
						bf1.newLine();
						if(!flag && !features.containsKey("next-pos-"+nextSplit[1]))
						 {
							features.put("next-pos-"+nextSplit[1], features.size()+1);
						 }
					}
					if(i!=0 && i==fileContents.size()-1)
					{			
						String[] previousSplit = split(fileContents.get(i-1),-1,"POSCON",trainingPOS,flag);				
						bf1.write("POSCON: "+previousSplit[1] +" OMEGAPOS");
						bf1.newLine();
						if(!flag && !features.containsKey("prev-pos-"+previousSplit[1]))
						 {
							features.put("prev-pos-"+previousSplit[1], features.size()+1);
						 }
					}
				}
				else
				{
					bf1.write("POSCON: n/a");
					bf1.newLine();
				}
				if(fTypes.contains("ABBR"))
				{
					boolean flag1 = true;
					if(currentLineSplit[2].length()>4)
						flag1 = false;
					if(currentLineSplit[2].charAt(currentLineSplit[2].length()-1) != '.')
						flag1 = false;
					for(int j=0;j<currentLineSplit[2].length();j++)
					{
						if(currentLineSplit[2].charAt(j) != '.' &&  !Character.isAlphabetic(currentLineSplit[2].charAt(j)))
							flag1 = false;
					}
					
					if(flag1)
					{
						bf1.write("ABBR: yes");
						bf1.newLine();
					}
					else
					{
						bf1.write("ABBR: no");
						bf1.newLine();
					}
				}
				else
				{
					bf1.write("ABBR: n/a");
					bf1.newLine();
				}
				if(fTypes.contains("CAP"))
				{
					if(currentLineSplit[2].charAt(0) >=65 && currentLineSplit[2].charAt(0)<=90)
					{					
						bf1.write("CAP: yes");
						bf1.newLine();
					}
					else 
					{
						bf1.write("CAP: no");
						bf1.newLine();
					}
				}
				else
				{
					bf1.write("CAP: n/a");
					bf1.newLine();
				}
				if(fTypes.contains("LOCATION"))
				{
					if(locations.contains(currentLineSplit[2]))
					{
						bf1.write("LOCATION: yes");
						bf1.newLine();
					}
					else
					{
						bf1.write("LOCATION: no");
						bf1.newLine();
					}
				}
				else
				{
					
				}			
				bf1.newLine();
			
			}
			
		}
			bf1.close();
			f1.close();
		}	
		catch(Exception e)
		{
			
		}	
		
		return trainingWords;
	}
	

	public static void main(String[] args) {

		Assignment5 a5 = new Assignment5();
		a5.generateReadable(args);
	}
}

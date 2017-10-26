import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class eval {

	
	
	public void evaluate(String[] args)
	{
		String predictionFile = args[1];
		String goldFile = args[2];
		HashMap<String,List<String>> prediction= new HashMap();
		HashMap<String,List<String>> gold= new HashMap();
		parseFile(predictionFile,prediction);
		parseFile(goldFile,gold);
		//print(prediction);
		//print(gold);
		try {
			printResults(prediction,gold);
		} catch (IOException e) {			
			e.printStackTrace();
		}
		
	}
	
	private void printResults(HashMap<String, List<String>> prediction, HashMap<String, List<String>> gold) throws IOException {
		
		List<String> temp = new ArrayList<String>();
		FileWriter f = new FileWriter("eval.txt");
		BufferedWriter bf = new BufferedWriter(f);
		for(int i=0;i<prediction.get("per").size();i++)
		{
			if(gold.get("per").contains(prediction.get("per").get(i)))
			 temp.add(prediction.get("per").get(i));
		}
		String listCorrect = convertToString(temp);
		bf.write("Correct PER = "+ listCorrect);
		bf.newLine();
		int avgRecallNum = 0;
		int avgRecallDen = 0;
		int avgPrecisionNum = 0;
		int avgPrecisionDen = 0;
		String recall = "";
		if(gold.get("per").size() == 0)
			recall = "n/a";
		else
			recall =""+ temp.size()+"/"+gold.get("per").size();
		bf.write("Recall PER = "+ recall);
		bf.newLine();
		avgRecallNum = avgRecallNum + temp.size();
		avgRecallDen = avgRecallDen + gold.get("per").size();
		
		String precision = "";
		if(prediction.get("per").size() == 0)
			precision = "n/a";
		else
			precision =""+ temp.size()+"/"+prediction.get("per").size();
		bf.write("Precision PER = "+ precision);
		bf.newLine();
		avgPrecisionNum = avgPrecisionNum +  temp.size();
		avgPrecisionDen = avgPrecisionDen +  prediction.get("per").size();
		bf.newLine();
		
		
		temp = new ArrayList<String>();
		
		for(int i=0;i<prediction.get("loc").size();i++)
		{
			if(gold.get("loc").contains(prediction.get("loc").get(i)))
			 temp.add(prediction.get("loc").get(i));
		}
		listCorrect = convertToString(temp);
		bf.write("Correct LOC = "+ listCorrect);
		bf.newLine();
		recall = "";
		if(gold.get("loc").size() == 0)
			recall = "n/a";
		else
			recall =""+ temp.size()+"/"+gold.get("loc").size();
		bf.write("Recall LOC = "+ recall);
		bf.newLine();
		avgRecallNum = avgRecallNum + temp.size();
		avgRecallDen = avgRecallDen + gold.get("loc").size();
		
		precision = "";
		if(prediction.get("loc").size() == 0)
			precision = "n/a";
		else
			precision =""+ temp.size()+"/"+prediction.get("loc").size();
		bf.write("Precision LOC = "+ precision);
		bf.newLine();
		avgPrecisionNum = avgPrecisionNum +  temp.size();
		avgPrecisionDen = avgPrecisionDen +  prediction.get("loc").size();
		bf.newLine();
		
		
		temp = new ArrayList<String>();
		
		for(int i=0;i<prediction.get("org").size();i++)
		{
			if(gold.get("org").contains(prediction.get("org").get(i)))
			 temp.add(prediction.get("org").get(i));
		}
		listCorrect = convertToString(temp);
		bf.write("Correct ORG = "+ listCorrect);
		bf.newLine();
		recall = "";
		if(gold.get("org").size() == 0)
			recall = "n/a";
		else
			recall =""+ temp.size()+"/"+gold.get("org").size();
		bf.write("Recall ORG = "+ recall);
		bf.newLine();
		avgRecallNum = avgRecallNum + temp.size();
		avgRecallDen = avgRecallDen + gold.get("org").size();
		
		precision = "";
		if(prediction.get("org").size() == 0)
			precision = "n/a";
		else
			precision =""+ temp.size()+"/"+prediction.get("org").size();
		bf.write("Precision ORG = "+ precision);
		bf.newLine();
		avgPrecisionNum = avgPrecisionNum +  temp.size();
		avgPrecisionDen = avgPrecisionDen +  prediction.get("org").size();
		bf.newLine();
		
		bf.write("Average Recall = "+ avgRecallNum+"/"+avgRecallDen);
		bf.newLine();
		bf.write("Average Precision = "+ avgPrecisionNum+"/"+avgPrecisionDen);
		bf.newLine();
		
		bf.close();
		f.close();
	}

	private String convertToString(List<String> temp) {
	
		if(temp.size() == 0)
		{
			return "NONE";
		}
		else {
			String result = "";
			for(int i=0; i<temp.size();i++)
			{
				result = result + temp.get(i) + " | "; 
			}
			
			return result.substring(0, result.length()-2);
		}		
	}

	private void print(HashMap<String, List<String>> prediction) {
		
		for(int i=0;i<prediction.get("loc").size();i++)
		{
			System.out.println(prediction.get("loc").get(i));
		}

		for(int i=0;i<prediction.get("org").size();i++)
		{
			System.out.println(prediction.get("org").get(i));
		}

		for(int i=0;i<prediction.get("per").size();i++)
		{
			System.out.println(prediction.get("per").get(i));
		}
		
	}

	private void parseFile(String goldFile, HashMap<String, List<String>> gold) {
		
		ArrayList<String> l =  new ArrayList<String>();
		ArrayList<String> o =  new ArrayList<String>();
		ArrayList<String> p =  new ArrayList<String>();
		gold.put("loc", l);
		gold.put("org", o);
		gold.put("per", p);	
		boolean loc ,org , per;
		loc = org = per = false;
		FileReader f;
		try {
			f = new FileReader(goldFile);
			BufferedReader bf = new BufferedReader(f);
			String currentLine = "";
			String tracking = "";
			Integer start = 0;
			int i = 0;
			while((currentLine = bf.readLine())!=null)
			{
				i++;
				String [] currentLineSplit = split(currentLine);
				if(currentLineSplit[0].equals("B-LOC") || currentLineSplit[0].equals("B-PER") || currentLineSplit[0].equals("B-ORG") || currentLineSplit[0].equals("O")) {
					if(loc)
					{						
						l.add(tracking + " ["+start +"-"+(i-1) +"]");
						tracking = "";
						loc = false;
					}
					if(org)
					{
						o.add(tracking + " ["+start +"-"+(i-1) +"]");
						tracking = "";
						org = false;
					}
					if(per)
					{
						p.add(tracking + " ["+start +"-"+(i-1) +"]");
						tracking = "";
						per = false;
					}
					if(currentLineSplit[0].equals("B-LOC"))
					{
						loc= true;
						tracking = tracking + " "+ currentLineSplit[1];
						start = i;
					}
					if(currentLineSplit[0].equals("B-ORG"))
					{
						org= true;
						tracking = tracking + " "+  currentLineSplit[1];
						start = i;
					}
					if(currentLineSplit[0].equals("B-PER"))
					{
						per= true;
						tracking = tracking + " "+  currentLineSplit[1];
						start = i;
					}
					
				}
				else if(currentLineSplit[0].equals("I-LOC") && loc)
				{
					tracking = tracking + " "+  currentLineSplit[1];
				}
				else if(currentLineSplit[0].equals("I-PER") && per)
				{
					tracking = tracking + " "+  currentLineSplit[1];
				}
				else if(currentLineSplit[0].equals("I-ORG") && org)
				{
					tracking = tracking + " "+  currentLineSplit[1];
				}
				else
				{
					if(loc)
					{
						l.add(tracking + " ["+start +"-"+(i-1)+"]");
						tracking = "";
						loc = false;
					}
					if(org)
					{
						o.add(tracking + " ["+start +"-"+(i-1)+"]");
						tracking = "";
						org = false;
					}
					if(per)
					{
						p.add(tracking + " ["+start +"-"+(i-1) +"]");
						tracking = "";
						per = false;
					}
				}
				
			}	
			f.close();
			bf.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private String[] split(String currentLine)
	{		
		String[] currentLineSplit = new String[2];
		currentLine = currentLine.trim();
		currentLine = currentLine + " ";
		int k = 0;
		while(!currentLine.equals("") && k<2)
		{					
			currentLineSplit[k++] = currentLine.substring(0,currentLine.indexOf(" "));
			currentLine = currentLine.substring(currentLine.indexOf(" "));
			currentLine = currentLine.trim();
			currentLine = currentLine + " ";
		}		
		return currentLineSplit;		
	
	}

	public static void main(String[] args) {
		eval e = new eval();
		e.evaluate(args);
	
		
	}
}

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class Assignment3 {
	
	
	public HashMap unmarshall(String s, boolean emission)
	{
		FileReader f;

		HashMap<String,Double> emissionProb = new HashMap();
		HashMap<String,Double> transitionProb = new HashMap();	
		try {
			f = new FileReader(s);
			BufferedReader bf = new BufferedReader(f);
			String currentLine;		
			while((currentLine = bf.readLine())!=null)
			{
				String X = currentLine.split(" ")[0].toLowerCase();
				String Y = currentLine.split(" ")[1].toLowerCase();
				double prob = Double.parseDouble(currentLine.split(" ")[2]);
				if(("noun".equals(X) || "verb".equals(X) || "inf".equals(X) || "prep".equals(X) || "phi".equals(X)) && ("noun".equals(Y) || "verb".equals(Y) || "inf".equals(Y) ||"prep".equals(Y) || "phi".equals(Y)))
					transitionProb.put(X+" "+Y, prob);
				else
					emissionProb.put(X+" "+Y, prob);
			}			
			bf.close();
			f.close();
		}
		catch(Exception e)
		{
			
		}
		return emission?emissionProb:transitionProb;
	}
	
	public void initialization(String currentLine, HashMap<String,Double> emissionProb, HashMap<String,Double> transitionProb, double score[][], int backptr[][])
	{
		String tags[] = new String[4];
		tags[0] ="noun";tags[1] ="verb";tags[2] ="inf";tags[3] ="prep";
		String currentLineWords[] = currentLine.split(" ");
		for(int t=0;t<4;t++) 
		{
			String initialWord = currentLineWords[0].toLowerCase();
			double x,y;
			if(emissionProb.containsKey(initialWord+" "+tags[t]))
			{
				x = emissionProb.get(initialWord+" "+tags[t]);
			}
			else
			{
				x = 0.0001;
			}
			if(transitionProb.containsKey(tags[t]+" phi"))
			{
				y = transitionProb.get(tags[t]+" phi");
			}
			else
			{
				y = 0.0001;
			}			
			score[t][0] = (Math.log(x) / Math.log(2)) + (Math.log(y) / Math.log(2));
			backptr[t][0] = Integer.MIN_VALUE;
		}
	}
	
	public void iterative(String currentLine, HashMap<String,Double> emissionProb, HashMap<String,Double> transitionProb, double score[][], int backptr[][])
	{
		String tags[] = new String[4];
		tags[0] ="noun";tags[1] ="verb";tags[2] ="inf";tags[3] ="prep";
		String currentLineWords[] = currentLine.split(" ");
		for(int w =1;w<currentLineWords.length;w++)
		{
			String presentWord = currentLineWords[w].toLowerCase();
			for(int t=0;t<4;t++)
			{
				double x;
				if(emissionProb.containsKey(presentWord+" "+tags[t]))
				{
					x = emissionProb.get(presentWord+" "+tags[t]);
				}
				else
				{
					x = 0.0001;
				}				
				double max = Integer.MIN_VALUE;
				for(int k=0;k<4;k++) 
				{
					double y;
					if(transitionProb.containsKey(tags[t]+" "+tags[k]))
					{
						y = transitionProb.get(tags[t]+" "+tags[k]);
					}
					else
					{
						y = 0.0001;
					}
					
					double temp = score[k][w-1] + (Math.log(y) / Math.log(2));
					if(temp>max)
					{
						max = temp;
						backptr[t][w] = k;
					}
				}
				score[t][w] = (Math.log(x) / Math.log(2)) +max;
			}
		}
	}
	private void sequenceIdentification(String currentLine,double [][] score, int[][] backptr) {
		String tags[] = new String[4];
		tags[0] ="noun";tags[1] ="verb";tags[2] ="inf";tags[3] ="prep";	
		String currentLineWords[] = currentLine.split(" ");
		DecimalFormat df = new DecimalFormat("#.####");
		System.out.println("FINAL VITERBI NETWORK");
		for(int w=0;w<currentLineWords.length;w++)
		{
			for(int t=0;t<4;t++)
			{
			System.out.println("P("+currentLineWords[w].toLowerCase()+"="+tags[t]+") = "+df.format(score[t][w]));
			}
		}
		System.out.println();
		
		System.out.println("FINAL BACKPTR NETWORK");
		for(int w=1;w<currentLineWords.length;w++)
		{
			for(int t=0;t<4;t++)
			{
			System.out.println("Backptr("+currentLineWords[w].toLowerCase()+"="+tags[t]+") = "+tags[backptr[t][w]]);
			}
		}
		System.out.println();
		
		double bestProb = Integer.MIN_VALUE;
		int bestTag = 0;
		for(int t=0;t<4;t++)
		{
			if(score[t][currentLineWords.length-1]>bestProb)
			{
				bestProb = score[t][currentLineWords.length-1]; 
				bestTag = t;
			}
		}
		System.out.println("BEST TAG SEQUENCE HAS LOG PROBABILITY = " + df.format(bestProb));
		int w = currentLineWords.length-1;
		while(bestTag != Integer.MIN_VALUE)
		{
			System.out.println(currentLineWords[w]+" -> "+ tags[bestTag]);
			bestTag = backptr[bestTag][w--];
		}
		System.out.println();
		
	}
	
	public void forwardAlgorithm(String currentLine,  HashMap<String,Double> emissionProb, HashMap<String,Double> transitionProb)
	{
		String tags[] = new String[4];
		tags[0] ="noun";tags[1] ="verb";tags[2] ="inf";tags[3] ="prep";	
		String currentLineWords[] = currentLine.split(" ");		
		double seqsum [][] = new double[4][currentLineWords.length];			
		DecimalFormat df = new DecimalFormat("#.####");
		
		for(int t=0;t<4;t++) 
		{					
			String initialWord = currentLineWords[0].toLowerCase();
			double x,y;
			if(emissionProb.containsKey(initialWord+" "+tags[t]))
			{
				x = emissionProb.get(initialWord+" "+tags[t]);
			}
			else
			{
				x = 0.0001;
			}
			if(transitionProb.containsKey(tags[t]+" phi"))
			{
				y = transitionProb.get(tags[t]+" phi");
			}
			else
			{
				y = 0.0001;
			}			
			seqsum[t][0] = x*y;				
		}
		
		
		
		
		for(int w =1;w<currentLineWords.length;w++)
		{
			String presentWord = currentLineWords[w].toLowerCase();
			for(int t=0;t<4;t++)
			{
				double x,y;
				if(emissionProb.containsKey(presentWord+" "+tags[t]))
				{
					x = emissionProb.get(presentWord+" "+tags[t]);
				}
				else
				{
					x = 0.0001;
				}				
				double sum = 0;
				for(int k=0;k<4;k++) 
				{
					if(transitionProb.containsKey(tags[t]+" "+tags[k]))
					{
						y = transitionProb.get(tags[t]+" "+tags[k]);
					}
					else
					{
						y = 0.0001;
					}					
					double temp = seqsum[k][w-1] * y ; 
					sum = sum + temp;
				}
				seqsum[t][w] = sum * x; 
			}
		}
		
		
		System.out.println("FORWARD ALGORITHM RESULTS");
		for(int w =0;w<currentLineWords.length;w++)
		{
			String presentWord = currentLineWords[w].toLowerCase();
			for(int t=0;t<4;t++)
			{
				double sum =0;
				for(int k=0;k<4;k++) 
				{
					sum = sum + seqsum[k][w];
				}
				System.out.println("P("+currentLineWords[w]+"="+tags[t]+") = "+df.format(seqsum[t][w]/sum));						
			}
		}
		System.out.println();
		
	}		
	

	public void algo(String currentLine, HashMap<String,Double> emissionProb, HashMap<String,Double> transitionProb)
	{		
		String tags[] = new String[4];
		tags[0] ="noun";tags[1] ="verb";tags[2] ="inf";tags[3] ="prep";		
		System.out.println("PROCESSING SENTENCE: "+currentLine);
		System.out.println();
		String currentLineWords[] = currentLine.split(" ");
		double score [][] = new double[4][currentLineWords.length];
		int backptr [][] = new int[4][currentLineWords.length];
		
		initialization(currentLine,emissionProb,transitionProb,score,backptr);
		iterative(currentLine,emissionProb,transitionProb,score,backptr);
		sequenceIdentification(currentLine,score,backptr);		
		forwardAlgorithm(currentLine,emissionProb,transitionProb);
	}
	

	public void viterbi(String s)
	{
		String []input = s.split(" "); 		
		String probFile = input[1];
		String sentFile = input[2];
		HashMap<String,Double> emissionProb = unmarshall(probFile, true);
		HashMap<String,Double> transitionProb = unmarshall(probFile, false);
		FileReader f;
		BufferedReader bf;
		try {			
			f = new FileReader(sentFile);
			bf = new BufferedReader(f);
			String tags[] = new String[4];
			tags[0] ="noun";tags[1] ="verb";tags[2] ="inf";tags[3] ="prep";
			String currentLine;
			while((currentLine = bf.readLine())!=null)
			{
				algo(currentLine,emissionProb,transitionProb);
			}		
			
		} catch (Exception e) {			
			e.printStackTrace();
		}				
	
		
	}
	
	
	public static void main(String[] args) {
		Assignment3 a3 = new Assignment3();	
		Scanner sc = new Scanner(System.in);
		a3.viterbi(sc.nextLine());
	}

}

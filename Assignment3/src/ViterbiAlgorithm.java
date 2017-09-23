import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class ViterbiAlgorithm {
	
	
	public void pos(String s)
	{
		String []input = s.split(" "); 
		String sentFile = input[2];
		String probFile = input[1];
		FileReader f;
		try {
			f = new FileReader(probFile);
			BufferedReader bf = new BufferedReader(f);
			String currentLine;
			HashMap<String,Double> emissionProb = new HashMap();
			HashMap<String,Double> transitionProb = new HashMap();			
			while((currentLine = bf.readLine())!=null)
			{
				String X = currentLine.split(" ")[0].toLowerCase();
				String Y = currentLine.split(" ")[1].toLowerCase();
				double prob = Double.parseDouble(currentLine.split(" ")[2]);
				if(Arrays.asList("noun","verb","inf","prep","phi").contains(X) && Arrays.asList("noun","verb","inf","prep","phi").contains(Y))
					transitionProb.put(X+"|"+Y, prob);
				else
					emissionProb.put(X+"|"+Y, prob);
			}			
			bf.close();
			f.close();
			f = new FileReader(sentFile);
			bf = new BufferedReader(f);
			String tags[] = new String[4];
			tags[0] ="noun";tags[1] ="verb";tags[2] ="inf";tags[3] ="prep";
			while((currentLine = bf.readLine())!=null)
			{
				System.out.println("PROCESSING SENTENCE: "+currentLine);
				System.out.println();
				String currentLineWords[] = currentLine.split(" ");
				double score [][] = new double[4][currentLineWords.length];
				int backptr [][] = new int[4][currentLineWords.length];
				//Initialization step
				String initialWord = currentLineWords[0].toLowerCase();
				for(int t=0;t<4;t++) 
				{
					
					double x = emissionProb.containsKey(initialWord+"|"+tags[t])? emissionProb.get(initialWord+"|"+tags[t]):0.0001;
					double y = transitionProb.containsKey(tags[t]+"|phi")? transitionProb.get(tags[t]+"|phi"):0.0001;
					score[t][0] = (Math.log(x) / Math.log(2)) + (Math.log(y) / Math.log(2));
					backptr[t][0] = -1;
				}
				
				//Iteration Step
				for(int w =1;w<currentLineWords.length;w++)
				{
					String presentWord = currentLineWords[w].toLowerCase();
					for(int t=0;t<4;t++)
					{
						double x = emissionProb.containsKey(presentWord+"|"+tags[t])? emissionProb.get(presentWord+"|"+tags[t]):0.0001;
						double max = Integer.MIN_VALUE;
						for(int k=0;k<4;k++) 
						{
							double y = transitionProb.containsKey(tags[t]+"|"+tags[k])? transitionProb.get(tags[t]+"|"+tags[k]):0.0001;
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
				
				
				//Sequence Identification
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
				while(bestTag != -1)
				{
					System.out.println(currentLineWords[w]+" -> "+ tags[bestTag]);
					bestTag = backptr[bestTag][w--];
				}
				System.out.println();
				
				
				//Forward Algorithm
				double seqsum [][] = new double[4][currentLineWords.length];				
				//Initialization step
				initialWord = currentLineWords[0].toLowerCase();
				for(int t=0;t<4;t++) 
				{					
					double x = emissionProb.containsKey(initialWord+"|"+tags[t])? emissionProb.get(initialWord+"|"+tags[t]):0.0001;
					double y = transitionProb.containsKey(tags[t]+"|phi")? transitionProb.get(tags[t]+"|phi"):0.0001;
					seqsum[t][0] = x*y;//(Math.log(x) / Math.log(2)) + (Math.log(y) / Math.log(2));					
				}
				
				//Iteration Step
				for(w =1;w<currentLineWords.length;w++)
				{
					String presentWord = currentLineWords[w].toLowerCase();
					for(int t=0;t<4;t++)
					{
						double x = emissionProb.containsKey(presentWord+"|"+tags[t])? emissionProb.get(presentWord+"|"+tags[t]):0.0001;
						double sum = 0;
						for(int k=0;k<4;k++) 
						{
							double y = transitionProb.containsKey(tags[t]+"|"+tags[k])? transitionProb.get(tags[t]+"|"+tags[k]):0.0001;
							double temp = seqsum[k][w-1] * y ; //(Math.log(y) / Math.log(2));
							sum = sum + temp;
						}
						seqsum[t][w] = sum * x; // (Math.log(x) / Math.log(2)) ;
					}
				}
				
				// Lexical Probs
				System.out.println("FORWARD ALGORITHM RESULTS");
				for(w =0;w<currentLineWords.length;w++)
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
			
		} catch (Exception e) {			
			e.printStackTrace();
		}				
	}
	
	
	public static void main(String[] args) {
		ViterbiAlgorithm va = new ViterbiAlgorithm();	
		Scanner sc = new Scanner(System.in);
		va.pos(sc.nextLine());
	}

}

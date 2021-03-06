import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class LanguageModels {

	private void languageModels(String str) {
		String input[] = str.split(" ");
		String trainingFile = input[1];
		String testingFile = input[3];
		LinkedHashMap<String, Integer> unigrams = generateNgrams(trainingFile, 1);
		LinkedHashMap<String, Integer> bigrams = generateNgrams(trainingFile, 2);
		if(input[2].equals("-gen")) {
			FileReader f;
			try {
				f = new FileReader(testingFile);
				BufferedReader bf = new BufferedReader(f);
				String currentLine;
				while ((currentLine = bf.readLine()) != null) {
					languageGenerator(currentLine,unigrams,bigrams);
					System.out.println();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else {			
		// print(unigrams);
		print(bigrams);
		FileReader f;
		try {
			f = new FileReader(testingFile);
			BufferedReader bf = new BufferedReader(f);
			String currentLine;
			while ((currentLine = bf.readLine()) != null) {
				System.out.println("S = " + currentLine);
				System.out.println();
				unsmoothedUnigrams(currentLine, unigrams);
				unsmoothedBigrams(currentLine, unigrams, bigrams);
				smoothedBigrams(currentLine, unigrams, bigrams);
				System.out.println();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		}
	}

	// for this problem again the case sensitivity should be considered eg: Mc Donalds?
	private void languageGenerator(String seed, LinkedHashMap<String, Integer> unigrams, LinkedHashMap<String, Integer> bigrams) {						
			System.out.println("Seed = "+seed);
			System.out.println();
			String initialSeed = seed;
			for(int i=1;i<=10;i++)
			{
				String sentence = initialSeed;
				seed = initialSeed;
				int count = 1;
				while(count<=10)
				{
					seed = nextSeed(seed.toLowerCase(),bigrams);					
					if(seed.equals("seed doesn't exist"))
						break;
					seed = seed.substring(seed.indexOf(" ")+1);
					sentence = sentence + " "+seed; 					
					if(seed.equals(".") || seed.equals("!") || seed.equals("?"))
						break;	
					count++;
				}								
				System.out.println("Sentence "+i+": "+sentence);
			}
	}
	
	private String nextSeed(String seed, LinkedHashMap<String, Integer> bigrams)
	{
		LinkedHashMap<String,Integer> seedBigrams= new LinkedHashMap<String,Integer>();
		int totalSum = 0;
		for(Map.Entry<String, Integer> e: bigrams.entrySet())
		{
			if(e.getKey().startsWith(seed.trim()+" "))
			{
				totalSum += e.getValue();
				seedBigrams.put(e.getKey(), e.getValue());
			}
		}
		if(seedBigrams.size() == 0)
			return "seed doesn't exist";
		Random rand = new Random();
		int index = 1 + rand.nextInt(totalSum);
	    int sum = 0;
	    String nextSeed = "";
	    for(Map.Entry<String, Integer> e: seedBigrams.entrySet())
		{
			if(sum > index)
			{
				break;
			}
			else
			{
				nextSeed = e.getKey();
				sum = sum + e.getValue();
			}
		}
	    return nextSeed;
		
	}

	// ? should we add phi for the testing set?
	private void smoothedBigrams(String currentLine, LinkedHashMap<String, Integer> unigrams,
			LinkedHashMap<String, Integer> bigrams) {
		currentLine = "phi " + currentLine;
		double logProb = 0;
		DecimalFormat df = new DecimalFormat("#.####");
		String temp = "";
		for (int i = 1; i < currentLine.length(); i++) {
			if ((currentLine.charAt(i - 1) == currentLine.charAt(i)) && (currentLine.charAt(i) == ' ')) {

			} else {
				temp = temp + currentLine.charAt(i - 1);
			}
		}
		if (currentLine.charAt(currentLine.length() - 1) != ' ')
			temp = temp + currentLine.charAt(currentLine.length() - 1);
		currentLine = temp;
		String currUnigrams[] = currentLine.split(" ");
		for (int i = 1; i < currUnigrams.length; i++) {
			double smoothing = 0;
			if (!bigrams.containsKey(currUnigrams[i - 1].toLowerCase() + " " + currUnigrams[i].toLowerCase())) {
				smoothing = 1;
			} else
				smoothing = 1 + bigrams.get(currUnigrams[i - 1].toLowerCase() + " " + currUnigrams[i].toLowerCase());
			double ratio = smoothing / (unigrams.get(currUnigrams[i - 1].toLowerCase()) + unigrams.size() -1);
			logProb = logProb + (Math.log(ratio) / Math.log(2));
			//System.out.println(smoothing);
			//System.out.println((unigrams.get(currUnigrams[i - 1].toLowerCase()) + unigrams.size() -1));
		}
		// System.out.println("Smoothed Bigrams, logprob(S) = " + logProb);
		
		System.out.println("Smoothed Bigrams, logprob(S) = " + df.format(logProb));

	}

	// even if one bigram is not in the corpus. it will lead to zero/undefined.
	private void unsmoothedBigrams(String currentLine, LinkedHashMap<String, Integer> unigrams,
			LinkedHashMap<String, Integer> bigrams) {
		currentLine = "phi " + currentLine;
		double logProb = 0;
		DecimalFormat df = new DecimalFormat("#.####");
		String temp = "";
		for (int i = 1; i < currentLine.length(); i++) {
			if ((currentLine.charAt(i - 1) == currentLine.charAt(i)) && (currentLine.charAt(i) == ' ')) {

			} else {
				temp = temp + currentLine.charAt(i - 1);
			}
		}
		if (currentLine.charAt(currentLine.length() - 1) != ' ')
			temp = temp + currentLine.charAt(currentLine.length() - 1);
		currentLine = temp;
		String currUnigrams[] = currentLine.split(" ");
		for (int i = 1; i < currUnigrams.length; i++) {

			if (!bigrams.containsKey(currUnigrams[i - 1].toLowerCase() + " " + currUnigrams[i].toLowerCase())) {
				System.out.println("Unsmoothed Bigrams, logprob(S) = undefined");
				return;
			}
			double ratio = (double) bigrams.get(currUnigrams[i - 1].toLowerCase() + " " + currUnigrams[i].toLowerCase())
					/ unigrams.get(currUnigrams[i - 1].toLowerCase());
			logProb = logProb + (Math.log(ratio) / Math.log(2));
		}
		// System.out.println("Unsmoothed Bigrams, logprob(S) = " + logProb);
		System.out.println("Unsmoothed Bigrams, logprob(S) = " + df.format(logProb));
	}

	// expecting that every test unigram is present in training phase ? what if phi is in sentence ?
	private void unsmoothedUnigrams(String currentLine, LinkedHashMap<String, Integer> unigrams) {
		String currUnigrams[] = currentLine.split(" ");
		double logProb = 0;
		DecimalFormat df = new DecimalFormat("#.####");
		for (String unigram : currUnigrams) {
			if (unigram.length() > 0) {
				unigram = unigram.toLowerCase();
				double ratio = (double) unigrams.get(unigram) / unigrams.get("unigram count");
				logProb = logProb + (Math.log(ratio)) / Math.log(2);
			}
		}
		// System.out.println("Unsmoothed Unigrams, logprob(S) = " + logProb);
		System.out.println("Unsmoothed Unigrams, logprob(S) = " + df.format(logProb));
	}

	public void print(LinkedHashMap<String, Integer> ngrams) {
		FileWriter f;

		try {
			f = new FileWriter("out.txt");
			BufferedWriter bf = new BufferedWriter(f);
			for (Map.Entry<String, Integer> e : ngrams.entrySet()) {
				bf.write(e.getKey() + "  : " + e.getValue());
				bf.newLine();
			}
			bf.close();
			f.close();

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private LinkedHashMap<String, Integer> generateNgrams(String trainingFile, int i) {
		LinkedHashMap<String, Integer> ngrams = new LinkedHashMap<String,Integer>();
		FileReader f;
		try {
			f = new FileReader(trainingFile);
			BufferedReader bf = new BufferedReader(f);
			if (i == 1) {
				int count = 0;
				String currentLine;
				int numberOfLines = 0;
				while ((currentLine = bf.readLine()) != null) {
					String unigrams[] = currentLine.split(" ");
					numberOfLines++;
					for (String unigram : unigrams) {
						if (unigram.length() > 0) {
							unigram = unigram.toLowerCase();
							ngrams.put(unigram, ngrams.getOrDefault(unigram, 0) + 1);
							count++;
						}
					}
				}
				ngrams.put("unigram count", count);
				ngrams.put("phi", numberOfLines);
				return ngrams;
			} else {
				String currentLine;
				while ((currentLine = bf.readLine()) != null) {
					String unigrams[] = currentLine.split(" ");
					int count = 0;
					if (unigrams.length >= i - 1) {
						String temp = "";
						int j = 0;
						while (count < i - 1 && j < unigrams.length) {
							if (unigrams[j].length() > 0) {
								temp = temp + " " + unigrams[j].toLowerCase();
								count++;
							}
							j++;
						}
						if (count == i - 1)
							ngrams.put("phi" + temp, ngrams.getOrDefault("phi" + temp, 0) + 1);
					}
					count = 0;
					String prev = "";
					if (unigrams.length >= i) {
						for (String unigram : unigrams) {
							if (unigram.length() > 0) {
								count++;
								if (count == i) {
									prev = prev + unigram.toLowerCase();
									ngrams.put(prev, ngrams.getOrDefault(prev, 0) + 1);
									prev = prev.substring(prev.indexOf(' ') + 1) + " ";
									count--;
								} else {
									prev = prev + unigram.toLowerCase() + " ";
								}
							}
						}
					}
				}
				return ngrams;

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ngrams;
	}

	public static void main(String[] args) {
		LanguageModels l = new LanguageModels();
		Scanner sc = new Scanner(System.in);		
		l.languageModels(sc.nextLine());
		
	}

}

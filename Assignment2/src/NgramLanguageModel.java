import java.io.*;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
public class NgramLanguageModel {
	
	private void ngramLanguageModel(String s) {
		String data[] = s.split(" ");
		String train = data[1];
		String test = data[3];		
		if(data[2].equals("-gen")) {
			HashMap<String, Integer> u = ngramGeneration(train, 1);
			HashMap<String, Integer> b = ngramGeneration(train, 2);
			FileReader fread;
			try {
				String presentl;
				fread = new FileReader(test);
				BufferedReader br = new BufferedReader(fread);
				while ((presentl = br.readLine()) != null) {
					ngramLangGen(presentl,b);
					System.out.println();
				}
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}else {
			HashMap<String, Integer> u = ngramGeneration(train, 1);
			HashMap<String, Integer> b = ngramGeneration(train, 2);
			FileReader fread;
			try {
				fread = new FileReader(test);
				BufferedReader br = new BufferedReader(fread);
				String presentl;
				while ((presentl = br.readLine()) != null) {
					System.out.println("S= " + presentl);
					System.out.println();
					languagemodels(presentl, u,b,1);
					languagemodels(presentl, u,b,2);
					languagemodels(presentl, u,b,3);
					System.out.println();

				}
			}
			catch(Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private void languagemodels(String presentl, HashMap<String, Integer> u, HashMap<String, Integer> b,Integer k) {
		
		if(k ==1) {
		
		String cu[] = presentl.split(" ");
		double lp = 0;
		DecimalFormat decimalFormat = new DecimalFormat("#.####");
		for (String ug : cu) {
			if (ug.length() > 0) {
				ug = ug.toLowerCase();
				double r = (double) u.get(ug) / u.get("TOTAL UNIGRAMS");
				lp = lp + (Math.log(r)) / Math.log(2);
			}
		}
		System.out.println("Unsmoothed Unigrams, logprob(S) = " + decimalFormat.format(lp));
	}
		if(k == 2)
		{
			presentl = "phi " + presentl;
			double lp = 0;
			DecimalFormat decimalFormat = new DecimalFormat("#.####");
			String x = "";
			for (int i = 1; i < presentl.length(); i++) {
				if ((presentl.charAt(i - 1) == presentl.charAt(i)) && (presentl.charAt(i) == ' ')) {
					
				} 
				else
				{
					x = x + presentl.charAt(i - 1);
				}
			}
			if (presentl.charAt(presentl.length() - 1) != ' ')
				x = x + presentl.charAt(presentl.length() - 1);
			presentl = x;
			String cu[] = presentl.split(" ");
			for (int i = 1; i < cu.length; i++) {
				if (!b.containsKey(cu[i - 1].toLowerCase() + " " + cu[i].toLowerCase()))
				{
					System.out.println("Unsmoothed Bigrams, logProb(S) = undefined");
					return;
				}
				
				double r = (double) b.get(cu[i - 1].toLowerCase() + " " + cu[i].toLowerCase())
						/ u.get(cu[i - 1].toLowerCase());
				lp = lp + (Math.log(r)) / Math.log(2);
			}
			System.out.println("Unsmoothed Bigrams, logProb(S) = " + decimalFormat.format(lp));
		}
		
		if(k ==3)
		{
			presentl = "phi " + presentl;
			double lp = 0;
			DecimalFormat decimalFormat = new DecimalFormat("#.####");
			String x = "";			
			for (int i = 1; i < presentl.length(); i++) {
				if ((presentl.charAt(i - 1) == presentl.charAt(i)) && (presentl.charAt(i) == ' ')) {
					
				} 
				else
				{
					x = x + presentl.charAt(i - 1);
				}
				
			}
			if (presentl.charAt(presentl.length() - 1) != ' ')
				x = x + presentl.charAt(presentl.length() - 1);
			presentl = x;
			String cu[] = presentl.split(" ");
			for (int i = 1; i < cu.length; i++) {
				double sm = 0;
				if (!b.containsKey(cu[i - 1].toLowerCase() + " " + cu[i].toLowerCase())) {
					sm = 1;
				} else
					sm = 1 + b.get(cu[i - 1].toLowerCase() + " " + cu[i].toLowerCase());
				double r  =  sm / (u.get(cu[i - 1].toLowerCase()) + u.size()-1);
				lp = lp + (Math.log(r)) / Math.log(2);
			}
			System.out.println("SmoothedBigrams, logProb(S) = " + decimalFormat.format(lp));
		}
		
	}
	
	private HashMap<String, Integer> ngramGeneration(String train, int i){
		HashMap<String, Integer> n = new HashMap();
		FileReader fread;
		try {
			fread = new FileReader(train);
			BufferedReader br = new BufferedReader(fread);
			if(i==1) {
				String presentl;
				int lineCounter = 0;
				int sum = 0;
				while ((presentl = br.readLine()) != null) {
					String ug[] = presentl.split(" ");
					lineCounter++;
					for (String u : ug) {
						if (u.length() > 0) {
							u = u.toLowerCase();
							n.put(u, n.getOrDefault(u, 0) + 1);
							sum++;
						}
					}
				}
				n.put("TOTAL UNIGRAMS", sum);
				n.put("phi", lineCounter);
				return n;
				
			}
			if(i == 2){
				String presentl;
				while ((presentl = br.readLine()) != null) {
					String ug[] = presentl.split(" ");
					int sum = 0;
					if (ug.length >= 1) {
						String x = "";
						int y = 0;
						while (sum < 1 && y < ug.length) {
							if (ug[y].length() > 0) {
								x = x + " " + ug[y].toLowerCase();
								sum++;
							}
							y++;
						}
						if (sum == 1)
							n.put("phi" + x, n.getOrDefault("phi" + x, 0) + 1);
					}
					String p = "";
					sum = 0;
					if (ug.length >= 2) {
						for (String u : ug) {
							if (u.length() > 0) {
								sum++;
								if (sum == 2) {
									p = p + u.toLowerCase();
									n.put(p, n.getOrDefault(p, 0) + 1);
									p = p.substring(p.indexOf(' ') + 1) + " ";
									sum--;
								}else p = p + u.toLowerCase() + " ";
								
							}
						}
						
					}
				}
				return n;
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		return n;
	}
	private void ngramLangGen(String s, HashMap<String, Integer> b) {
		System.out.println("Seed= "+s);
		System.out.println();
		String inits = s;
		for(int i=1;i<=10;i++) {
			String sen = inits;
			s = inits;
			int sum = 1;
			while(sum<=10) {					
				LinkedHashMap<String,Integer> sb= new LinkedHashMap();
				int count = 0;
				for(Map.Entry<String, Integer> e: b.entrySet()) {
					if(e.getKey().startsWith(s.trim().toLowerCase()+" "))
					{
						count += e.getValue();
						sb.put(e.getKey(), e.getValue());
					}
				}
				if(sb.size() == 0)
					break;
				Random r = new Random();
				int ind = 1 + r.nextInt(count);
			    int total = 0;
			    String ns = "";
			    for(Map.Entry<String, Integer> e: sb.entrySet()) {
			    	if(total > ind)
					{
						break;
					}					
						ns = e.getKey();
						total = total + e.getValue();					
			    }
			    s = ns;
				s = s.substring(s.indexOf(" ")+1);
				sen = sen + " "+s; 					
				if(s.equals(".") || s.equals("!") || s.equals("?"))
					break;	
				sum++;
			}
			System.out.println("Sentence "+i+": "+sen);
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Scanner scn = new Scanner(System.in);
		NgramLanguageModel nlm=new NgramLanguageModel();
		nlm.ngramLanguageModel(scn.nextLine());
	}

}

'''
Created on Nov 3, 2017

@author: tejak
'''
import sys
import nltk

class EventExtraction:
        
    def event_extraction(self):
        w = open(self.filename+".template","w+")
        r = open(self.filename,"r")
        prev = ""   
        for line in r:
            if ("DEV-MUC3-" in line) or ("TST1-MUC3-" in line) or ("TST2-MUC4-" in line):            
                if prev != "":
                   print("ID:         "+self.ID+"\n")
                   print("INCIDENT:   "+self.ID+"\n")
                   print("WEAPON:     "+self.ID+"\n")
                   print("PERP INDIV: "+self.ID+"\n")
                   print("PERP ORG:   "+self.ID+"\n")
                   print("TARGET:     "+self.ID+"\n")
                   print("VICTIM:     "+self.ID+"\n")                   
                self.ID = line.strip()
                prev = ""
            else:
                prev = prev + line
        
        print("ID:         "+self.ID+"\n")
        print("INCIDENT:   "+self.ID+"\n")
        print("WEAPON:     "+self.ID+"\n")
        print("PERP INDIV: "+self.ID+"\n")
        print("PERP ORG:   "+self.ID+"\n")
        print("TARGET:     "+self.ID+"\n")
        print("VICTIM:     "+self.ID+"\n")
        print(prev)            
        sentences = nltk.sent_tokenize(prev)
        sentences = [nltk.word_tokenize(sent) for sent in sentences]
        sentences = [nltk.pos_tag(sent) for sent in sentences]
        sentences = [nltk.ne_chunk(sent) for sent in sentences]
        for sent in sentences:
            print(sent)        
         
    
        w.close()
    

    def __init__(self, filename):
        self.filename = filename
        print("File Name is",self.filename)
        self.event_extraction()
        
if __name__ == "__main__":
   e = EventExtraction(sys.argv[1])        
        
        
        
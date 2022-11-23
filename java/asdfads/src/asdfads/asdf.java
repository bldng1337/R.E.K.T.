package asdfads;


	
	public class Bankomatkarte{
	    
	    private static int kartenanzahl = 0;
	    private int kartennummer;
	    private String kartenbesitzer;
	    private static float guthaben = 0;
	    

	    pubilc Bankomatkarte(String name){
	        this.kartenbesitzer = name;
	        this.kartenanzahl++;
	        this.kartennummer = this.kartenanzahl;
	    }    
	    
	    public void druckeInfos(){
	        System.out.println("==========");
	        System.out.println("Karteninfos:");
	        System.out.println("Anzahl der Karten: " + this.kartenanzahl);
	        System.out.println("Kartennummer: " + this.kartennummer);
	        System.out.println("Karteninhaber: " +this.kartenbesitzer);
	        System.out.println("Guthaben: " + this.guthaben);
	        System.out.println("==========");
	    }
	    
	    public void einzahlen(float betrag){
	        if(betrag>0){
	            this.guthaben += betrag;
	            System.out.println("Sie haben "+betrag+" eingezahlt");
	            System.out.println("Ihr Guthaben beträgt: " + this.guthaben);
	        }
	        else
	            System.out.println("Aktion nicht möglich!");
	        
	    }
	    
	    public float abheben(float betrag){
	    
	        
	        if((this.guthaben - betrag) >= 0 && betrag > 0 ){
	            System.out.println("Auszahlung: "+betrag);
	            this.guthaben -=betrag;
	            
	        }
	        else{
	            System.out.println("Auszahlung nicht möglich.");
	            betrag = 0;
	        }
	        System.out.println("Ihr Guthaben beträgt: "+this.guthaben);
	        
	        return betrag;
	    }
	    
	}


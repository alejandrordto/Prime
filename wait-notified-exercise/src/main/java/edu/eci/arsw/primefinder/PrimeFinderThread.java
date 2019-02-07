package edu.eci.arsw.primefinder;


import static edu.eci.arsw.primefinder.Control.SYNCRO;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PrimeFinderThread extends Thread{

	
	int a,b;
	
	private List<Integer> primes;
        private boolean running=true;
	
	public PrimeFinderThread(int a, int b) {
		super();
                this.primes = new LinkedList<>();
		this.a = a;
		this.b = b;
	}

        @Override
	public void run(){
           
            for (int i= a;i < b;i++){	
                if(running){
                if (isPrime(i)){
                    primes.add(i);
                    //System.out.println(i);
                } else{
                    synchronized (SYNCRO) {
                        try {
                            SYNCRO.wait();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(PrimeFinderThread.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
                    
                }
            
            }
	}
	
	boolean isPrime(int n) {
	    boolean ans;
            if (n > 2) { 
                ans = n%2 != 0;
                for(int i = 3;ans && i*i <= n; i+=2 ) {
                    ans = n % i != 0;
                }
            } else {
                ans = n == 2;
            }
	    return ans;
	}

	public List<Integer> getPrimes() {
		return primes;
	}
        public void shutdown(){
            running=false;
        }
        public void lightup(){
            running=true;
        }
      
	
}

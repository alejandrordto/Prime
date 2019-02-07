/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.primefinder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 */
public class Control extends java.lang.Thread {

    private final static int NTHREADS = 3;
    private final static int MAXVALUE = 30000000;
    private final static int TMILISECONDS = 5000;

    private final int NDATA = MAXVALUE / NTHREADS;
    public final static Object SYNCRO = new Object();
    private PrimeFinderThread pft[];

    private Control() {
        super();
        this.pft = new PrimeFinderThread[NTHREADS];
        int i;
        for (i = 0; i < NTHREADS - 1; i++) {
            PrimeFinderThread elem = new PrimeFinderThread(i * NDATA, (i + 1) * NDATA);
            pft[i] = elem;
        }
        pft[i] = new PrimeFinderThread(i * NDATA, MAXVALUE + 1);
    }

    public static Control newControl() {
        return new Control();
    }

    @Override
    public void run() {
        for (int i = 0; i < NTHREADS; i++) {
            pft[i].start();

        }
        //hola
        boolean flag = true;
        while (flag) {
            try {
                int foundPrimes = 0;
                    Control.sleep(TMILISECONDS);
                    for (int i = 0; i < NTHREADS; i++) {

                        pft[i].shutdown();
                        foundPrimes += pft[i].getPrimes().size();
                    }
                    System.out.print("Se han encontrado " + foundPrimes + " Primos.");
                    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                    String input = br.readLine();
                    synchronized (SYNCRO) {
                        SYNCRO.notifyAll();
                    }
                    for (int i = 0; i < NTHREADS; i++) {
                        pft[i].lightup();
                    }
                

            } catch (IOException ex) {
                Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);

            } catch (InterruptedException ex) {
                Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
            }
            flag = pft[0].isAlive();
            
            for (int i = 1; i < NTHREADS; i++) {
                flag = flag || pft[i].isAlive();
                
            }
        }
        flag = pft[0].isAlive();
            int foundPrimes = 0;
            for (int i = 1; i < NTHREADS; i++) {
                flag = flag || pft[i].isAlive();
                foundPrimes += pft[i].getPrimes().size();
            }
            System.out.println(foundPrimes);
    }
}

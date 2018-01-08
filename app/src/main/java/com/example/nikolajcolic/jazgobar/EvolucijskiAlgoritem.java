package com.example.nikolajcolic.jazgobar;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by nikolajcolic on 02/01/2018.
 */

public class EvolucijskiAlgoritem {

    ArrayList<Goba> seznam = new ArrayList<>();
    public double fitness(double x[]){
        //utezi!!(nastavljene z poizkusanjem)
        double utezDolzina=0.2;
        double utezNepravilni=0.5;
        double utezPravilni=0.6;
        double s = 0;
        int stNepravilnihTemp=0, stNepravilnihPritisk=0, stNepravilnihVlaznost=0,stVsehTemp=0, stVsehPritisk=0,stVsehVlaznost=0;

        if(x[1]<1 || x[0]<0 || x[1]>30 || x[3]<990 || x[2]<990 || x[3]>1030 || x[5]<1 || x[4]<0 || x[5]>100){//ce smo izven intervala[0,30] oz [990,1300] oz [0,100] koncamo
            return Double.MAX_VALUE;
        }
        int stNajdenih=0;
        for(int y = 0; y < seznam.size(); y++){
            //temperatura
            if(seznam.get(y).getTemperatura() >= x[0] && seznam.get(y).getTemperatura() <=x [1]){//pogledamo koliko podatkov imamo znotraj intervala
                stVsehTemp++;
                if(seznam.get(y).isNabral() == false){//kolikokrat znotraj intervala nismo nabrali gobe
                    stNepravilnihTemp++;
                }
            }
            //pritisk
            if(seznam.get(y).getPritisk() >= x[2] && seznam.get(y).getPritisk() <=x [3]){//pogledamo koliko podatkov imamo znotraj intervala
                stVsehPritisk++;
                if(seznam.get(y).isNabral() == false){//kolikokrat znotraj intervala nismo nabrali gobe
                    stNepravilnihPritisk++;
                }
            }
            //vlaznost
            if(seznam.get(y).getVlaznost() >= x[4] && seznam.get(y).getVlaznost() <=x [5]){//pogledamo koliko podatkov imamo znotraj intervala
                stVsehVlaznost++;
                if(seznam.get(y).isNabral() == false){//kolikokrat znotraj intervala nismo nabrali gobe
                    stNepravilnihVlaznost++;
                }
            }
            if(seznam.get(y).isNabral() == true){//kolikokrat smo nabrali gobo v splošnem
                stNajdenih++;
            }
        }
        double dolzinaTemp=x[1]-x[0];//dolzina intervala
        double dolzinaPritisk=x[3]-x[2];//dolzina intervala
        double dolzinaVlaznost=x[5]-x[4];//dolzina intervala

        //fitness funkcija!!
        //zracuna glede na produkt utez dolzine in dolzine+produkt utezi nepravilnih podatkov in st nepravilnih podatkov+
        //produkt utezi pravilnih in oklepaja(1 del je odstotek pravilnih najdenih podatkov v intervalu katerim prištejemo(2 del) število pravilnih podatkov ki jih interval ne zajema
        s = utezDolzina*dolzinaTemp + utezNepravilni*stNepravilnihTemp + utezPravilni*(1-((stVsehTemp-stNepravilnihTemp)/stNajdenih) + (stNajdenih-(stVsehTemp-stNepravilnihTemp))*3);
        s += utezDolzina*dolzinaPritisk + utezNepravilni*stNepravilnihPritisk + utezPravilni*(1-((stVsehPritisk-stNepravilnihPritisk)/stNajdenih) + (stNajdenih-(stVsehPritisk-stNepravilnihPritisk))*3);
        s += utezDolzina*dolzinaVlaznost + utezNepravilni*stNepravilnihVlaznost + utezPravilni*(1-((stVsehVlaznost-stNepravilnihVlaznost)/stNajdenih) + (stNajdenih-(stVsehVlaznost-stNepravilnihVlaznost))*3);
        return s;
    }

    private double CR=0.8;
    private double F=1.3;
    private int NP =30; //pop_size
    double x[][]; //pop_size X dimension
    double f[]; //fitness
    double fy;
    //private double max=5.12;
    //private double min=-5.12;
    private int D;
    int eval;
    int Rr;
    int a,b,c;
    private static Random rnd=new Random();
    private void init() {
        seznam.add(new Goba(53,5.3,1009,true));
        seznam.add(new Goba(55,10.2,1010,true));
        seznam.add(new Goba(51,8.4,1013,true));
        seznam.add(new Goba(54,9.9,1011,false));
        seznam.add(new Goba(59,13,1004,false));
        seznam.add(new Goba(62,15,1006,false));
        eval =0;
        D=6;
        x = new double[NP][6];
        f = new double[NP];
        for (int i=0; i<NP; i++) {
            //x[i] = vrniRnd(D, 0, 30);//napolnimo x z random podatki
            //temperatura[0,1]
            Random rnd = new Random();
            x[i][0] = rnd.nextDouble()*(15-0)+0;
            Random rnd2 = new Random();
            x[i][1] = rnd2.nextDouble()*(30-15)+15;
            if(x[i][0]>x[i][1]){
                double tmp=x[i][0];
                x[i][0]=x[i][1];
                x[i][1]=tmp;
            }
            //x[i] = vrniRnd(D, 0, 30);//napolnimo x z random podatki
            //pritisk[2,3]
            Random rnd3 = new Random();
            x[i][2] = rnd3.nextDouble()*(990-0)+0;
            Random rnd4 = new Random();
            x[i][3] = rnd4.nextDouble()*(1030-990)+990;
            if(x[i][2]>x[i][3]){
                double tmp=x[i][2];
                x[i][2]=x[i][3];
                x[i][3]=tmp;
            }

            //x[i] = vrniRnd(D, 0, 30);//napolnimo x z random podatki
            //Vlaznost[4,5]
            Random rnd5 = new Random();
            x[i][4] = rnd5.nextDouble()*(50-0)+0;
            Random rnd6 = new Random();
            x[i][5] = rnd6.nextDouble()*(100-50)+50;
            if(x[i][4]>x[i][5]){
                double tmp=x[i][4];
                x[i][4]=x[i][5];
                x[i][5]=tmp;
            }
            f[i] = fitness(x[i]);//izracunamo vrednost nakljucnih podatkov
            eval++;
        }
    }

    public double[] execute(int maxEval) {
        init();
        while (eval<maxEval) {
            for (int i=0; i<NP; i++) {
                //dolocimo nakljucno indexe za a,b,c
                do {
                    a = rnd.nextInt(NP);
                } while (i==a);
                do {
                    b = rnd.nextInt(NP);
                } while ((i==b) || (a==b));
                do {
                    c = rnd.nextInt(NP);
                } while ((i==c) || (a==c)|| (b==c));
                Rr = rnd.nextInt(D);//izberemo nakljucni index znotraj dimenzije
                double y[] = new double[D];
                for (int ii=0; ii<D; ii++) {
                    if ((rnd.nextDouble()<CR) ||(ii==Rr)) {
                        y[ii] = x[a][ii] + F*(x[b][ii]-x[c][ii]);//ce je pogoj pozitiven izracunamo novo vrednost z funkcijo
                    } else y[ii] = x[i][ii];//drugace prekopiramo
                }//tako dobimo novo pozicijo ki je mesanica a,b,c
                //popravljanje
                if(y[0]>y[1]){//ce je zacetek intervala vecji od konca intervala, potem zamenjamo stevili(temperatura)
                    double tmp=y[0];
                    y[0]=y[1];
                    y[1]=tmp;
                }
                if(y[2]>y[3]){//ce je zacetek intervala vecji od konca intervala, potem zamenjamo stevili(pritisk)
                    double tmp=y[2];
                    y[2]=y[3];
                    y[3]=tmp;
                }
                if(y[4]>y[5]){//ce je zacetek intervala vecji od konca intervala, potem zamenjamo stevili(vlaznost)
                    double tmp=y[4];
                    y[4]=y[5];
                    y[5]=tmp;
                }
                fy = fitness(y);
                eval++;
                if (f[i]>fy) {//pogledamo ce je nova pozicija boljsa od trenutne
                    f[i] = fy;//ce je menjamo, drugace ohranimo staro
                    x[i] = y;
                }
                if (eval>=maxEval) break;
            }
        }
        int bestFitness = 0;
        for(int x=0;x<NP;x++) {//na koncu pogledamo kje je najboljsi fitness
            if(f[bestFitness]>f[x]){
                bestFitness=x;
            }
        }
        return x[bestFitness]; //TODO return best in population
    }
}

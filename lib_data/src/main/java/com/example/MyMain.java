package com.example;

public class MyMain {
    public static void main(String args[]){
        System.out.println("Gobeee!");
        /*Tag a = new Tag("Igle");
        TagList l = new TagList();
        DataAll dataAll = DataAll.scenarijA();
        System.out.println(dataAll);*/
        Goba g = new Goba("Jurček");
        System.out.println(g);
        User u = new User("nikolaj.colic@gmail.com","nikolaj", "nepovem");
        System.out.println(u);
        Lokacija l = new Lokacija("Velenje Tomšičeva cesta 5", 123213213,123123123,"", System.currentTimeMillis(),true,u.getIdUser());
        System.out.println(l);
        GobaList gl = new GobaList();
        System.out.println(gl);
        //LokacijaGoba lg= new LokacijaGoba(l.getIme(),g.getIme(), System.currentTimeMillis(),u.getIdUser());
        //System.out.println(lg);
        System.out.println("----------------------------------------------");
        DataAll da = DataAll.scenarijA();
        System.out.println(da);
    }
}

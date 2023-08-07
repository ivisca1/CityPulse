package com.example.proba;

public class Grad {
    private int id;
    private String naziv;
    private int stanovnika;
    private Drzava drzava;
    private String slika;
    private int postanskiBroj;

    public Grad() {
        drzava = new Drzava();
    }

    public Grad(int id, String naziv, int brojStanovnika, Drzava drzava) {
        this.id = id;
        this.naziv = naziv;
        this.stanovnika = brojStanovnika;
        this.drzava = drzava;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public int getStanovnika() {
        return stanovnika;
    }

    public void setStanovnika(int brojStanovnika) {
        this.stanovnika = brojStanovnika;
    }

    public Drzava getDrzava() {
        return drzava;
    }

    public void setDrzava(Drzava drzava) {
        this.drzava = drzava;
    }

    public String getSlika(){
        return slika;
    }

    public void setSlika(String slika){
        this.slika=slika;
    }

    public int getPostanskiBroj() {
        return postanskiBroj;
    }

    public void setPostanskiBroj(int postanskiBroj) {
        this.postanskiBroj = postanskiBroj;
    }

    @Override
    public String toString(){
        return naziv;
    }
}

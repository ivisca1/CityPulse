package com.example.proba;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class GeografijaDAO {
    private static GeografijaDAO instance = null;
    private Connection conn;
    private PreparedStatement gradoviUpit,drzaveUpit,glavniGradUpit,obrisiDrzavuUpit,dodajGradUpit,dodajDrzavuUpit,izmijeniGradUpit,nadjiDrzavuUpit,nadjiGradUpit,obrisiGradUpit,sljedeciIdGradaUpit,dodajGradBezDrzave,sljedeciIdDrzaveUpit,dodajDrzavuBezGrada,obrisiGradoveIzDrzave;

    private GeografijaDAO(){
        String url = "jdbc:sqlite:baza.db";
        try {
            conn = DriverManager.getConnection(url);
            gradoviUpit = conn.prepareStatement("SELECT * FROM grad ORDER BY broj_stanovnika DESC");
        } catch (SQLException e) {
            generisiBazu();
            try {
                gradoviUpit = conn.prepareStatement("SELECT * FROM grad ORDER BY broj_stanovnika DESC");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        try{
            glavniGradUpit = conn.prepareStatement("SELECT glavni_grad FROM drzava WHERE naziv=?");
            obrisiDrzavuUpit = conn.prepareStatement("DELETE FROM drzava WHERE naziv=?");
            dodajGradUpit = conn.prepareStatement("INSERT INTO grad (id,naziv,broj_stanovnika,drzava,slika,postanski_broj) VALUES (?,?,?,?,?,?)");
            dodajDrzavuUpit = conn.prepareStatement("INSERT INTO drzava (id,naziv,glavni_grad) VALUES (?,?,?)");
            izmijeniGradUpit = conn.prepareStatement("UPDATE grad SET broj_stanovnika=?, naziv=?, drzava=?, slika=?, postanski_broj=? WHERE id=?");
            nadjiDrzavuUpit = conn.prepareStatement("SELECT * FROM drzava WHERE naziv=?");
            nadjiGradUpit = conn.prepareStatement("SELECT * FROM grad WHERE naziv=?");
            drzaveUpit = conn.prepareStatement("SELECT * FROM drzava");
            obrisiGradUpit = conn.prepareStatement("DELETE FROM grad WHERE id=?");
            sljedeciIdGradaUpit = conn.prepareStatement("SELECT MAX(id) FROM grad");
            dodajGradBezDrzave = conn.prepareStatement("INSERT INTO grad (id,naziv,broj_stanovnika,drzava,slika,postanski_broj) VALUES (?,?,?,NULL,?,?)");
            sljedeciIdDrzaveUpit = conn.prepareStatement("SELECT MAX(id) FROM drzava");
            dodajDrzavuBezGrada = conn.prepareStatement("INSERT INTO drzava (id,naziv,glavni_grad) VALUES (?,?,NULL)");
            obrisiGradoveIzDrzave = conn.prepareStatement("DELETE FROM grad WHERE drzava=?");
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public Connection getConnection(){
        return conn;
    }

    private String dajNazivDrzaveId(int id) {
        try {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT naziv FROM drzava WHERE id = " + id);
            if (rs.next())
                return rs.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Grad nadjiGlavniGradDrzave(Drzava drzava) throws SQLException {
        String upit = "SELECT g.id, g.naziv, g.broj_stanovnika, g.drzava FROM grad g, drzava d where g.id = d.glavni_grad AND d.id = " + drzava.getId();
        Statement statement = conn.createStatement();
        ResultSet rs = statement.executeQuery(upit);
        if (rs.next()) {
            return new Grad(rs.getInt(1), rs.getString(2), rs.getInt(3), drzava);
        }
        return null;
    }

    public static GeografijaDAO getInstance() {
        if(instance==null) {
            instance = new GeografijaDAO();
        }
        return instance;
    }

    public static void removeInstance() {
        if(instance!=null) {
            try {
                instance.conn.close();
                instance = null;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void vratiBazuNaDefault() {
        try {
            Statement stmt = null;
            stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM grad");
            stmt.executeUpdate("DELETE FROM drzava");
            generisiBazu();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void generisiBazu(){
        Scanner ulaz = null;
        try {
            ulaz = new Scanner(new FileInputStream("baza.sql"));
            String sqlUpit = "";
            while (ulaz.hasNext()) {
                sqlUpit += ulaz.nextLine();
                if ( sqlUpit.length() > 1 && sqlUpit.charAt( sqlUpit.length()-1 ) == ';') {
                    try {
                        Statement stmt = conn.createStatement();
                        stmt.execute(sqlUpit);
                        sqlUpit = "";
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
            ulaz.close();
        } catch (FileNotFoundException e) {
            System.out.println("Ne postoji SQL datoteka... nastavljam sa praznom bazom");
        }
    }

    public ArrayList<Grad> gradovi(){
        ArrayList<Grad> rezultat = new ArrayList<>();
        try {
            ResultSet resultSet = gradoviUpit.executeQuery();
            while (resultSet.next()) {
                Drzava drzava = nadjiDrzavu(dajNazivDrzaveId(resultSet.getInt(4)));
                Grad grad = new Grad(resultSet.getInt(1), resultSet.getString(2), resultSet.getInt(3), drzava);
                grad.setSlika(resultSet.getString(5));
                grad.setPostanskiBroj(resultSet.getInt(6));
                rezultat.add(grad);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rezultat;
    }

    public Grad glavniGrad(String drzava) {
        try {
            glavniGradUpit.setString(1, drzava);
            ResultSet resultSet = glavniGradUpit.executeQuery();
            if (resultSet.next()) {
                int gradId = resultSet.getInt(1);
                Statement dajNazivZaId = conn.createStatement();
                ResultSet rsGrad = dajNazivZaId.executeQuery("SELECT naziv FROM grad WHERE id = " + gradId);
                if (rsGrad.next())
                    return nadjiGrad(rsGrad.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public Grad nadjiGrad(String grad) {
        try {
            nadjiGradUpit.setString(1, grad);
            ResultSet rs = nadjiGradUpit.executeQuery();
            if (rs.next()) {
                Grad g = new Grad(rs.getInt(1),rs.getString(2),rs.getInt(3),nadjiDrzavu(dajNazivDrzaveId(rs.getInt(4))));
                g.setSlika(rs.getString(5));
                g.setPostanskiBroj(rs.getInt(6));
                return g;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void dodajGrad(Grad grad) {
        try {
            if(grad.getId()==-1){
                ResultSet rs = sljedeciIdGradaUpit.executeQuery();
                grad.setId(rs.getInt(1) + 1);
            }
            if(grad.getDrzava()==null){
                dodajGradBezDrzave.setInt(1,grad.getId());
                dodajGradBezDrzave.setString(2,grad.getNaziv());
                dodajGradBezDrzave.setInt(3,grad.getStanovnika());
                dodajGradBezDrzave.setString(4, grad.getSlika());
                dodajGradBezDrzave.setInt(5,grad.getPostanskiBroj());
                dodajGradBezDrzave.execute();
            }
            else {
                dodajGradUpit.setInt(1,grad.getId());
                dodajGradUpit.setString(2,grad.getNaziv());
                dodajGradUpit.setInt(3,grad.getStanovnika());
                dodajGradUpit.setInt(4, grad.getDrzava().getId());
                dodajGradUpit.setString(5,grad.getSlika());
                dodajGradUpit.setInt(6,grad.getPostanskiBroj());
                dodajGradUpit.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void izmijeniGrad(Grad grad) {
        try {
            izmijeniGradUpit.setInt(1, grad.getStanovnika());
            izmijeniGradUpit.setString(2, grad.getNaziv());
            izmijeniGradUpit.setInt(3, grad.getDrzava().getId());
            izmijeniGradUpit.setString(4, grad.getSlika());
            izmijeniGradUpit.setInt(5, grad.getPostanskiBroj());
            izmijeniGradUpit.setInt(6,grad.getId());
            izmijeniGradUpit.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void dodajDrzavu(Drzava drzava) {
        try {
            if(drzava.getId()==-1){
                ResultSet rs = sljedeciIdDrzaveUpit.executeQuery();
                drzava.setId(rs.getInt(1) + 1);
            }
            if(drzava.getGlavniGrad()==null){
                dodajDrzavuBezGrada.setInt(1,drzava.getId());
                dodajDrzavuBezGrada.setString(2,drzava.getNaziv());
                dodajDrzavuBezGrada.execute();
            }
            else {
                dodajDrzavuUpit.setInt(1, drzava.getId());
                dodajDrzavuUpit.setString(2, drzava.getNaziv());
                dodajDrzavuUpit.setInt(3, drzava.getGlavniGrad().getId());
                dodajDrzavuUpit.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void obrisiDrzavu(String drzava){
        try {
            int id = 0;
            for(Drzava d : drzave()){
                if(d.getNaziv().equals(drzava)){
                    id = d.getId();
                }
            }
            if(id!=0) {
                obrisiGradoveIzDrzave.setInt(1,id);
                obrisiGradoveIzDrzave.execute();
                obrisiDrzavuUpit.setString(1, drzava);
                obrisiDrzavuUpit.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Drzava nadjiDrzavu(String drzava) {
        try {
            nadjiDrzavuUpit.setString(1, drzava);
            ResultSet rs = nadjiDrzavuUpit.executeQuery();
            if (rs.next()) {
                Drzava d = new Drzava();
                d.setId(rs.getInt(1));
                d.setNaziv(rs.getString(2));
                Grad grad = nadjiGlavniGradDrzave(d);
                if (grad != null) {
                    d.setGlavniGrad(grad);
                    return d;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ArrayList<Drzava> drzave() {
        ArrayList<Drzava> rezultat = new ArrayList<>();
        ResultSet rs = null;
        try {
            rs = drzaveUpit.executeQuery();
            while(rs.next()){
                Drzava d = new Drzava();
                d.setId(rs.getInt(1));
                d.setNaziv(rs.getString(2));
                Grad grad = nadjiGlavniGradDrzave(d);
                d.setGlavniGrad(grad);
                rezultat.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rezultat;
    }

    public void obrisiGrad(Grad grad) {
        try {
            obrisiGradUpit.setInt(1,grad.getId());
            obrisiGradUpit.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

package com.example.proba;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRException;

import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import static javafx.scene.layout.Region.USE_COMPUTED_SIZE;

public class GlavnaController {

    private GeografijaDAO model;
    private ObservableList<Grad> gradovi;
    private boolean izmjena;
    public TableView<Grad> tableViewGradovi;
    public TableColumn<Grad,Integer> colGradId;
    public TableColumn<Grad,String> colGradNaziv;
    public TableColumn<Grad,Integer> colGradStanovnika;
    public TableColumn<Grad,Drzava> colGradDrzava;
    public TableColumn<Grad,Integer> colGradPost;
    public Button btnJezik;

    public GlavnaController(){
        model = GeografijaDAO.getInstance();
    }

    @FXML
    public void initialize(){
        izmjena = false;
        gradovi = FXCollections.observableArrayList(model.gradovi());
        tableViewGradovi.setItems(gradovi);
        colGradId.setCellValueFactory(new PropertyValueFactory<>("Id"));
        colGradNaziv.setCellValueFactory(new PropertyValueFactory<>("Naziv"));
        colGradStanovnika.setCellValueFactory(new PropertyValueFactory<>("Stanovnika"));
        colGradDrzava.setCellValueFactory(new PropertyValueFactory<>("Drzava"));
        colGradPost.setCellValueFactory(new PropertyValueFactory<>("postanskiBroj"));
    }

    public void actionDodajGrad() throws IOException {
        izmjena = false;
        Stage stage = new Stage();
        ResourceBundle bundle = ResourceBundle.getBundle("Translation", Locale.getDefault());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/grad.fxml"),bundle);
        GradController gradController = new GradController(null, model.drzave());
        loader.setController(gradController);
        stage.setOnHiding(
                o -> {
                    if(gradController.getTemp()){
                        Grad g = new Grad(-1,gradController.fieldNaziv.getText(),Integer.parseInt(gradController.fieldBrojStanovnika.getText()),gradController.choiceDrzava.getValue());
                        g.setSlika(gradController.imageView.getImage().getUrl());
                        g.setPostanskiBroj(Integer.parseInt(gradController.fldPostanskiBroj.getText()));
                        model.dodajGrad(g);
                        gradovi.setAll(model.gradovi());
                        tableViewGradovi.setItems(gradovi);
                    }
                }
        );
        Parent root = loader.load();
        stage.setTitle("Grad");
        stage.setResizable(false);
        stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        stage.show();
    }

    public void actionIzmijeniGrad() throws IOException {
        izmjena = true;
        Stage stage = new Stage();
        ResourceBundle bundle = ResourceBundle.getBundle("Translation", Locale.getDefault());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/grad.fxml"),bundle);
        Grad grad = tableViewGradovi.getSelectionModel().getSelectedItem();
        GradController gradController = new GradController(grad,model.drzave());
        loader.setController(gradController);
        stage.setOnHiding(
                o -> {
                    if(gradController.getTemp()){
                        grad.setNaziv(gradController.fieldNaziv.getText());
                        grad.setStanovnika(Integer.parseInt(String.valueOf(gradController.fieldBrojStanovnika.getText())));
                        grad.setDrzava(gradController.choiceDrzava.getValue());
                        grad.setSlika(gradController.imageView.getImage().getUrl());
                        grad.setPostanskiBroj(Integer.parseInt(gradController.fldPostanskiBroj.getText()));
                        model.izmijeniGrad(grad);
                        gradovi.setAll(model.gradovi());
                        tableViewGradovi.setItems(gradovi);
                    }
                }
        );
        Parent root = loader.load();
        stage.setTitle("Grad");
        stage.setResizable(false);
        stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        stage.show();
    }

    public void actionObrisiGrad(){
        Grad grad = tableViewGradovi.getSelectionModel().getSelectedItem();
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        if(Locale.getDefault().toString().equals("bs")) {
            alert.setTitle("Potvrda");
            alert.setHeaderText("Izabrali ste brisanje grada");
            alert.setContentText("Ako želite da obrišete izabrani grad pritisnite OK");
        }
        else if(Locale.getDefault().toString().equals("en_US")){
            alert.setTitle("Confirmation");
            alert.setHeaderText("You chose to delete city");
            alert.setContentText("If you want to delete the selected city press OK");
        }
        else if(Locale.getDefault().toString().equals("de_DE")){
            alert.setTitle("Bestätigung");
            alert.setHeaderText("Sie haben sich entschieden, die Stadt zu löschen");
            alert.setContentText("Wenn Sie die ausgewählte Stadt löschen möchten, drücken Sie OK");
        }
        else{
            alert.setTitle("Confirmation");
            alert.setHeaderText("Vous avez choisi de supprimer la ville");
            alert.setContentText("Si vous souhaitez supprimer la ville sélectionnée, appuyez sur OK");
        }
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK && grad!=null){
            model.obrisiGrad(grad);
            gradovi.setAll(model.gradovi());
            tableViewGradovi.setItems(gradovi);
        }
    }

    public void actionDodajDrzavu() throws IOException {
        Stage stage = new Stage();
        ResourceBundle bundle = ResourceBundle.getBundle("Translation", Locale.getDefault());
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/drzava.fxml"),bundle);
        DrzavaController drzavaController = new DrzavaController(null,model.gradovi());
        loader.setController(drzavaController);
        stage.setOnHiding(
                o -> {
                    if(drzavaController.getTemp() && !drzavaController.fieldNaziv.getText().isBlank()){
                        model.dodajDrzavu(new Drzava(-1, drzavaController.fieldNaziv.getText(), drzavaController.choiceGrad.getValue()));
                    }
                }
        );
        Parent root = loader.load();
        stage.setTitle("Država");
        stage.setResizable(false);
        stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        stage.show();
    }

    public Grad getTrenutniGrad(){
        return tableViewGradovi.getSelectionModel().getSelectedItem();
    }

    public void actionStampa(){
        try {
            new GradoviReport().showReport(model.getConnection());
        } catch (JRException e1) {
            e1.printStackTrace();
        }
    }

    public void actionJezik() throws IOException {
        ChoiceDialog<String> dialog;
        Optional<String> rezultat;
        String bos = "";
        String eng = "";
        String ger = "";
        String fra = "";
        if(Locale.getDefault().toString().equals("bs")) {
            bos = "Bosanski";
            eng = "Engleski";
            ger = "Njemački";
            fra = "Francuski";
            dialog = new ChoiceDialog<>("", "Bosanski", "Engleski", "Njemački", "Francuski");
            dialog.setTitle("Jezik");
            dialog.setHeaderText("Odabir jezika");
            dialog.setContentText("Izaberite jezik: ");
            rezultat = dialog.showAndWait();
        }
        else if(Locale.getDefault().toString().equals("en_US")){
            bos = "Bosnian";
            eng = "English";
            ger = "German";
            fra = "French";
            dialog = new ChoiceDialog<>("", "Bosnian", "English", "German", "French");
            dialog.setTitle("Language");
            dialog.setHeaderText("Language choice");
            dialog.setContentText("Choose a language: ");
            rezultat = dialog.showAndWait();
        }
        else if(Locale.getDefault().toString().equals("de_DE")){
            bos = "Bosnisch";
            eng = "Englisch";
            ger = "Deutsch";
            fra = "Französisch";
            dialog = new ChoiceDialog<>("", "Bosnisch", "Englisch", "Deutsch", "Französisch");
            dialog.setTitle("Sprache");
            dialog.setHeaderText("Sprachwahl");
            dialog.setContentText("Wählen Sie eine Sprache: ");
            rezultat = dialog.showAndWait();
        }
        else{
            bos = "Bosniaque";
            eng = "Anglais";
            ger = "Allemand";
            fra = "Français";
            dialog = new ChoiceDialog<>("", "Bosniaque", "Anglais", "Allemand", "Français");
            dialog.setTitle("Langue");
            dialog.setHeaderText("Choix de la langue");
            dialog.setContentText("Choisir une langue: ");
            rezultat = dialog.showAndWait();
        }
        if(rezultat.isPresent()){
            if(rezultat.get().equals(bos)){
                Locale.setDefault(new Locale("bs"));
            }
            else if(rezultat.get().equals(eng)){
                Locale.setDefault(new Locale("en","US"));
            }
            else if(rezultat.get().equals(ger)){
                Locale.setDefault(new Locale("de","DE"));
            }
            else if(rezultat.get().equals(fra)){
                Locale.setDefault(new Locale("fr","FR"));
            }
            ResourceBundle bundle = ResourceBundle.getBundle("Translation", Locale.getDefault());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/glavna.fxml"),bundle);
            loader.setController(new GlavnaController());
            Parent root = loader.load();
            Stage stage = (Stage) btnJezik.getScene().getWindow();
            stage.setTitle(bundle.getString("appname"));
            stage.setScene(new Scene(root, USE_COMPUTED_SIZE, USE_COMPUTED_SIZE));
        }
    }
}

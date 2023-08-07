package com.example.proba;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;

public class GradController {

    private boolean temp;
    private Grad grad;
    private ArrayList<Drzava> drzave;
    public TextField fieldNaziv;
    public TextField fieldBrojStanovnika;
    public TextField fldPostanskiBroj;
    public ChoiceBox<Drzava> choiceDrzava;
    public Button btnOk;
    public Button btnCancel;
    public ImageView imageView;

    public GradController(Grad grad,ArrayList<Drzava> drzave){
        this.drzave=drzave;
        this.grad=grad;
    }

    @FXML
    public void initialize(){
        if(grad!=null){
            fieldNaziv.setText(grad.getNaziv());
            fieldBrojStanovnika.setText(String.valueOf(grad.getStanovnika()));
            fldPostanskiBroj.setText(String.valueOf(grad.getPostanskiBroj()));
            choiceDrzava.setValue(grad.getDrzava());
            fieldNaziv.getStyleClass().add("ispravno");
            fieldBrojStanovnika.getStyleClass().add("ispravno");
            fldPostanskiBroj.getStyleClass().add("ispravno");
            Image img = new Image(grad.getSlika());
            imageView.setImage(img);
        }
        else{
            grad = new Grad();
            fieldNaziv.getStyleClass().add("neispravno");
            fieldBrojStanovnika.getStyleClass().add("neispravno");
            fldPostanskiBroj.getStyleClass().add("neispravno");
            Image img = new Image("C:/Users/Jasmi/IdeaProjects/proba/src/main/resources/pictures/world.jpg");
            imageView.setImage(img);
        }
        temp = false;
        choiceDrzava.getItems().addAll(drzave);
        fieldNaziv.textProperty().addListener(
                (obs,oldValue,newValue) -> {
                    if (newValue.isBlank()) {
                        fieldNaziv.getStyleClass().removeAll("ispravno");
                        fieldNaziv.getStyleClass().add("neispravno");
                    } else {
                        fieldNaziv.getStyleClass().removeAll("neispravno");
                        fieldNaziv.getStyleClass().add("ispravno");
                    }
                }
        );
        fieldBrojStanovnika.textProperty().addListener(
                (obs,oldValue,newValue) -> {
                    if(!newValue.isBlank() && newValue.chars().allMatch(Character::isDigit) && Integer.parseInt(newValue)>0){
                        fieldBrojStanovnika.getStyleClass().removeAll("neispravno");
                        fieldBrojStanovnika.getStyleClass().add("ispravno");
                    } else {
                        fieldBrojStanovnika.getStyleClass().removeAll("ispravno");
                        fieldBrojStanovnika.getStyleClass().add("neispravno");
                    }
                }
        );
        fldPostanskiBroj.textProperty().addListener(
                (obs,oldValue,newValue) -> {
                    if(!newValue.isBlank() && newValue.chars().allMatch(Character::isDigit) && Integer.parseInt(newValue)>0){
                        fldPostanskiBroj.getStyleClass().removeAll("neispravno");
                        fldPostanskiBroj.getStyleClass().add("ispravno");
                    } else {
                        fldPostanskiBroj.getStyleClass().removeAll("ispravno");
                        fldPostanskiBroj.getStyleClass().add("neispravno");
                    }
                }
        );
    }

    public void actionCancel(){
        temp = false;
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    public void actionOk(){
        temp = true;
        if(!fieldNaziv.getText().isBlank() && !fieldBrojStanovnika.getText().isBlank() && fieldBrojStanovnika.getText().chars().allMatch(Character::isDigit) && Integer.parseInt(fieldBrojStanovnika.getText())>0
        && !fldPostanskiBroj.getText().isBlank() && fldPostanskiBroj.getText().chars().allMatch(Character::isDigit) && Integer.parseInt(fldPostanskiBroj.getText())>0) {
            Stage stage = (Stage) btnOk.getScene().getWindow();
            stage.close();
        }
    }

    public boolean getTemp() {
        return temp;
    }

    public Grad getGrad(){
        grad.setId(-1);
        grad.setNaziv(fieldNaziv.getText());
        grad.setStanovnika(Integer.parseInt(fieldBrojStanovnika.getText()));
        grad.setDrzava(choiceDrzava.getValue());
        grad.setSlika(imageView.getImage().getUrl());
        return grad;
    }

    public void actionPromjena(){
        TextInputDialog dialog = new TextInputDialog();
        if(Locale.getDefault().toString().equals("bs")) {
            dialog.setTitle("Promjena slike");
            dialog.setHeaderText("Izabrali ste promjenu slike");
            dialog.setContentText("Unesite aspolutni put do slike koju želite (primjer: C:/Users/name/desktop)");
        }
        else if(Locale.getDefault().toString().equals("en_US")){
            dialog.setTitle("Picture change");
            dialog.setHeaderText("You chose to change the picture");
            dialog.setContentText("Enter the absolute path to the image you want (example: C:/Users/name/desktop)");
        }
        else if(Locale.getDefault().toString().equals("de_DE")){
            dialog.setTitle("Bildwechsel");
            dialog.setHeaderText("Sie haben sich entschieden, das Bild zu ändern");
            dialog.setContentText("Geben Sie den absoluten Pfad zum gewünschten Bild ein (Beispiel: C:/Users/name/desktop)");
        }
        else{
            dialog.setTitle("Changement d'image");
            dialog.setHeaderText("Vous avez choisi de changer l'image");
            dialog.setContentText("Entrez le chemin absolu vers l'image que vous voulez (exemple: C:/Users/name/desktop)");
        }

        Optional<String> rezultat = dialog.showAndWait();
        if(rezultat.isPresent()){
            Image img = new Image(rezultat.get());
            imageView.setImage(img);
        }
    }
}

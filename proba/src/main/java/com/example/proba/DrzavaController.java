package com.example.proba;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;

public class DrzavaController {

    private boolean temp;
    private ArrayList<Grad> gradovi;
    public TextField fieldNaziv;
    public Button btnOk;
    public Button btnCancel;
    public ChoiceBox<Grad> choiceGrad;

    public DrzavaController(Drzava d, ArrayList<Grad> gradovi){
        this.gradovi=gradovi;
    }

    @FXML
    public void initialize(){
        temp = false;
        choiceGrad.getItems().addAll(gradovi);
        fieldNaziv.getStyleClass().add("neispravno");
        fieldNaziv.textProperty().addListener(
                (obs,oldValue,newValue) -> {
                    if(newValue.isBlank()){
                        fieldNaziv.getStyleClass().removeAll("ispravno");
                        fieldNaziv.getStyleClass().add("neispravno");
                    }
                    else{
                        fieldNaziv.getStyleClass().removeAll("neispravno");
                        fieldNaziv.getStyleClass().add("ispravno");
                    }
        });
    }

    public void actionOk(){
        temp = true;
        if(!fieldNaziv.getText().isBlank()) {
            Stage stage = (Stage) btnOk.getScene().getWindow();
            stage.close();
        }
    }

    public void actionCancel(){
        temp = false;
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }

    public boolean getTemp(){
        return temp;
    }
}

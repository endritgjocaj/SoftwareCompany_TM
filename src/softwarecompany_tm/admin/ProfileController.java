/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softwarecompany_tm.admin;

import com.jfoenix.controls.JFXTextField;
import controllers.TechnologiesJpaController;
import controllers.exceptions.NonexistentEntityException;
import entities.Employees;
import entities.Technologies;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * FXML Controller class
 *
 * @author My-Pc
 */
public class ProfileController implements Initializable {
    
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("SoftwareCompany_TMPU");
    
    private TechnologiesJpaController jpa = new TechnologiesJpaController(emf); 
    private List<Technologies> list = new ArrayList<>();
    
    @FXML
    private TextField searchField;
    @FXML
    private Label employeeProfile;
    @FXML
    private TableView<Technologies> tableView;
    @FXML
    private JFXTextField idField;
    @FXML
    private JFXTextField firstNameField;
    @FXML
    private JFXTextField lastNameField;
    @FXML
    private JFXTextField technologyField;
    @FXML
    private TableColumn<Technologies, String> technologyColumn;
    
    private static ScrollPane scrollPane;
    
    private List<Technologies> techList = new ArrayList<>();
    
    private Employees myEmployee;
    private Technologies selectedTechnology;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        list = jpa.findTechnologiesEntities();
        
        technologyColumn.setCellValueFactory(new PropertyValueFactory<>("technology"));
        
        idField.setEditable(false);
        firstNameField.setEditable(false);
        lastNameField.setEditable(false);
    }

    public void getEmployee(Employees employee){
        myEmployee = employee;
        
        employeeProfile.setText("Employee: " + employee.getFirstName() + " " + employee.getLastName());
        
        idField.setText(employee.getId() + "");
        firstNameField.setText(employee.getFirstName());
        lastNameField.setText(employee.getLastName());
    }
    
    public void getPane(ScrollPane scrPane){
        scrollPane = scrPane;
        search();
        loadData();
    }
    
    public void loadData(){
        list = jpa.findTechnologiesEntities();
        
        techList.clear();
        for(Technologies tech : list){
            if(myEmployee.getTechnologiesList().contains(tech)){
                techList.add(tech);
            }
        }
        
        tableView.getItems().setAll(techList);
        tableView.refresh();
    }
    
    public void clearFields(){
        idField.setText(myEmployee.getId().toString());
        firstNameField.setText(firstNameField.getText());
        lastNameField.setText(lastNameField.getText());
        technologyField.setText(null);
    }
    
    @FXML
    private void profileRowSelected(MouseEvent event) {
        Technologies technology = tableView.getSelectionModel().getSelectedItem();
        
        idField.setText(technology.getId() + "");
        technologyField.setText(technology.getTechnology());
        
        selectedTechnology = technology;
    }

    @FXML
    private void addTechnology(ActionEvent event) {
        String errorMessage = "";
        
        Integer id = Integer.parseInt(idField.getText());
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String strTechnology = technologyField.getText();
        
        if(!checkNullAndEmpty(strTechnology)){
            errorMessage += "Technology field is empty!\n\n";
        }
        
        for(Technologies tech : myEmployee.getTechnologiesList()){
            if(tech.getTechnology().equals(strTechnology)){
                errorMessage += "This technology exists in list!\n\n";
            }
        }
        
        Technologies technology = new Technologies(strTechnology);
        
        if(!errorMessage.equals("")){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText(errorMessage);
            a.show();
            return;
        }
        
        boolean techExists = false;
        for(Technologies tech : list){
            if(tech.getTechnology().equals(strTechnology)){
               technology = tech;
               techExists = true;
               break;
            }
        }
        
        if(techExists == false){
            List<Employees> employeesList = new ArrayList<>();
            employeesList.add(myEmployee);
            technology.setEmployeesList(employeesList);
            jpa.create(technology);
        }
        else{
            try {
                technology.getEmployeesList().add(myEmployee);
                jpa.edit(technology);
            } catch (Exception ex) {
            }
        }
        
        myEmployee.getTechnologiesList().add(technology);
        
        loadData();
        clearFields();
    }

    @FXML
    private void deleteTechnology(ActionEvent event) {
        Technologies technology = jpa.findTechnologies(selectedTechnology.getId());
        
        if(selectedTechnology.getEmployeesList().size() == 1){
            try {
                jpa.destroy(selectedTechnology.getId());
            } catch (NonexistentEntityException ex) {
                
            }
            
            loadData();
            clearFields();
            
        }
        else{
            try {
                technology.getEmployeesList().remove(myEmployee);
                jpa.edit(technology);
                myEmployee.getTechnologiesList().remove(technology);
                
            } catch (Exception ex) {
                Logger.getLogger(ProfileController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }    
        
        loadData();
        clearFields();
    }

    @FXML
    private void getBackImage(MouseEvent event) {
        Parent parent = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Employees.fxml"));
        
        try {
            parent = loader.load();
        } 
        catch (IOException ex) {
            
        }
        scrollPane.setContent(parent);
    }
    
    public boolean checkNullAndEmpty(String s){
        return s != null && !s.trim().isEmpty();
    }
    
    public void search(){
        loadData();
        ObservableList<Technologies> observableData = FXCollections.observableArrayList(techList);
        FilteredList<Technologies> filteredData = new FilteredList<>(observableData, b -> true);
        
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(technology -> {
                if(newValue == null || newValue.isEmpty()){
                    return true;
                }
                 
                String lowerCaseFilter = newValue.toLowerCase();
                
                if(technology.getTechnology().toLowerCase().contains(lowerCaseFilter)){
                    return true;
                }
                 
                 
                tableView.setItems(filteredData);

                return false;
             });
        });
    }

    @FXML
    private void onCleanClicked(MouseEvent event) {
        clearFields();
    }
}
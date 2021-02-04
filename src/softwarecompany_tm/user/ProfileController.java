/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softwarecompany_tm.user;

import com.jfoenix.controls.JFXTextField;
import controllers.TechnologiesJpaController;
import entities.Employees;
import entities.Technologies;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
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
    
    private List<Technologies> techList = new ArrayList<>();
    
    private static ScrollPane scrollPane;
    
    private Employees myEmployee;
    
    
    
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
        technologyField.setEditable(false);
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



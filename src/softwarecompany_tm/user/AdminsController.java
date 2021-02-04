/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softwarecompany_tm.user;

import com.jfoenix.controls.JFXTextField;
import controllers.AdminJpaController;
import entities.Admin;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * FXML Controller class
 *
 * @author My-Pc
 */
public class AdminsController implements Initializable {
    
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("SoftwareCompany_TMPU");
    
    private AdminJpaController jpa = new AdminJpaController(emf);
    private List<Admin> list = new ArrayList<>();
    
    @FXML
    private AnchorPane anchorPane1;
    @FXML
    private TextField searchField;
    @FXML
    private TableView<Admin> tableView;
    @FXML
    private TableColumn<Admin, Integer> idColumn;
    @FXML
    private TableColumn<Admin, String> firstNameColumn;
    @FXML
    private TableColumn<Admin, String> lastNameColumn;
    @FXML
    private TableColumn<Admin, String> usernameColumn;
    @FXML
    private TableColumn<Admin, String> emailColumn;
    @FXML
    private JFXTextField idField;
    @FXML
    private JFXTextField firstNameField;
    @FXML
    private JFXTextField lastNameField;
    @FXML
    private JFXTextField usernameField;
    @FXML
    private JFXTextField emailField;
    
    private static ScrollPane scrollPane;
    
    String selectedUsername = null;
    String selectedEmail = null;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        list = jpa.findAdminEntities();
        
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        firstNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z]*")) {
                firstNameField.setText(newValue.replaceAll("[^a-zA-Z]", ""));
            }
        });
        
        lastNameField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z]*")) {
                lastNameField.setText(newValue.replaceAll("[^a-zA-Z]", ""));
            }
        });
        
        idField.setEditable(false);
        firstNameField.setEditable(false);
        lastNameField.setEditable(false);
        usernameField.setEditable(false);
        emailField.setEditable(false);
        
        loadData();
        clearFields();
    }    
    
    public void getPane(ScrollPane scrPane){
        scrollPane = scrPane;
        search();
    }
    
    public void loadData(){
        list = jpa.findAdminEntities();
        tableView.getItems().setAll(list);
        tableView.refresh();
        search();
    }
    
    public void clearFields(){
        idField.setText(null);
        firstNameField.setText(null);
        lastNameField.setText(null);
        usernameField.setText(null);
        emailField.setText(null);
    }
    
    @FXML
    private void adminRowSelected(MouseEvent event) {
        Admin admin = tableView.getSelectionModel().getSelectedItem();
        
        idField.setText(admin.getId() + "");
        firstNameField.setText(admin.getFirstName());
        lastNameField.setText(admin.getLastName());
        usernameField.setText(admin.getUsername());
        emailField.setText(admin.getEmail());
        
        selectedUsername = usernameField.getText();
        selectedEmail = emailField.getText();
    }
    
    public boolean checkNullAndEmpty(String s){
        return s != null && !s.trim().isEmpty();
    }
    
    public void search(){
        ObservableList<Admin> observableData = FXCollections.observableArrayList(list);
        FilteredList<Admin> filteredData = new FilteredList<>(observableData, b -> true);
        
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
             filteredData.setPredicate(admin -> {
                 if(newValue == null || newValue.isEmpty()){
                     return true;
                 }
                 
                 int i = admin.getId().intValue();
                 String lowerCaseFilter = newValue.toLowerCase();

                 String id = i + "";
                 
                 if((id.contains(newValue))){
                     return true;
                 }
                 else if(admin.getFirstName().toLowerCase().contains(lowerCaseFilter)){
                     return true;
                 }
                 else if(admin.getLastName().toLowerCase().contains(lowerCaseFilter)){
                     return true;
                 }
                 else if(admin.getUsername().toLowerCase().contains(lowerCaseFilter)){
                     return true;
                 }
                 else if(admin.getEmail().toLowerCase().contains(lowerCaseFilter)){
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

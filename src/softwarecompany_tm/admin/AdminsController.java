/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softwarecompany_tm.admin;

import com.jfoenix.controls.JFXTextField;
import controllers.AdminJpaController;
import controllers.exceptions.NonexistentEntityException;
import entities.Admin;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
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
public class AdminsController implements Initializable {
    
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("SoftwareCompany_TMPU");
    
    private AdminJpaController jpa = new AdminJpaController(emf);
    private List<Admin> list = new ArrayList<>();
    
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
    
    private String selectedUsername = null;
    private String selectedEmail = null;
    
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
        
        idField.setEditable(false);
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
        Admin a = tableView.getSelectionModel().getSelectedItem();
        
        idField.setText(a.getId() + "");
        firstNameField.setText(a.getFirstName());
        lastNameField.setText(a.getLastName());
        usernameField.setText(a.getUsername());
        emailField.setText(a.getEmail());
        
        selectedUsername = usernameField.getText();
        selectedEmail = emailField.getText();
    }
    
    @FXML
    private void addAdmin(ActionEvent event) {
        try{
            String errorMessage = "";
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String username = usernameField.getText();
            String password = "123456";
            String email = emailField.getText();

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            String encoded = Base64.getEncoder().encodeToString(hash);
            
            Admin adm = new Admin(firstName, lastName, username, encoded, email);
            
            for(Admin admin : list){
                if(admin.getUsername().equals(adm.getUsername())){
                    errorMessage += "This username is not available!\n\n";
                    break;
                }
            }
            
            for(Admin admin : list){
                if(admin.getEmail().equals(adm.getEmail())){
                    errorMessage += "This email exists!\n\n";
                    break;
                }
            }
            
            if(!errorMessage.equals("")){
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText(errorMessage);
                a.show();
                return;
            }
            
            jpa.create(adm);
        }
        catch(NoSuchAlgorithmException | UnsupportedEncodingException ex){
            
        }
        
        loadData();
        clearFields();
    }
    

    @FXML
    private void deleteAdmin(ActionEvent event) {
        try {
            jpa.destroy(Integer.parseInt(idField.getText()));
        } catch (NonexistentEntityException ex) {
            
        }
        loadData();
        clearFields();
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

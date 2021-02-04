/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softwarecompany_tm.admin;

import com.jfoenix.controls.JFXTextField;
import controllers.AdminJpaController;
import entities.Admin;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * FXML Controller class
 *
 * @author My-Pc
 */
public class AdminAccountController implements Initializable {
    
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("SoftwareCompany_TMPU");
    
    private AdminJpaController jpa = new AdminJpaController(emf);
    private List<Admin> list = new ArrayList<>();
    
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
    private TableColumn<Admin, String> passwordColumn;
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
    @FXML
    private Label adminData;
    @FXML
    private Button changePassword;
    
    private static ScrollPane scrollPane;
    private static Admin myAdmin;

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
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
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
    
    void getPane(ScrollPane scrPane) {
        scrollPane = scrPane;
        loadData();
        clearFields();
    }
    
    void getAdmin(Admin admin) {
        adminData.setText(admin.getFirstName() + " " + admin.getLastName());
        myAdmin = admin;
    }
    
    public void loadData(){
        list = jpa.findAdminEntities();
        
        if(myAdmin != null){
            for(Admin admin : list){
                if(myAdmin.getId().equals(admin.getId())){
                    tableView.getItems().setAll(admin);
                    tableView.refresh();
                    break;
                }
            }
        }
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
    
    @FXML
    private void editAdmin(ActionEvent event) {
        try {
            Admin admin = jpa.findAdmin(Integer.parseInt(idField.getText()));
            
            String errorMessage = "";
            
            if(!checkNullAndEmpty(firstNameField.getText())){
                errorMessage += "Name field is empty!\n";
            }
            
            if(!checkNullAndEmpty(lastNameField.getText())){
                errorMessage += "Last Name field is empty!\n";
            }
            
            if(!checkNullAndEmpty(usernameField.getText())){
                errorMessage += "Username field is empty!\n";
            }
            
            if(!checkNullAndEmpty(emailField.getText())){
                errorMessage += "Email field is empty!\n";
            }
            
            for (Admin adm : list) {
                if (!selectedUsername.equals(usernameField.getText()) && adm.getEmail().equals(usernameField.getText())) {
                    errorMessage += "Username: " + usernameField.getText() + " exists!\n\n";
                    break;
                }
            }
            
            for (Admin adm : list) {
                if (!selectedEmail.equals(emailField.getText()) && adm.getEmail().equals(emailField.getText())) {
                    errorMessage += "Email: " + emailField.getText() + " exists!\n\n";
                    break;
                }
            }
            
            admin.setFirstName(firstNameField.getText());
            admin.setLastName(lastNameField.getText());
            admin.setUsername(usernameField.getText());
            admin.setEmail(emailField.getText());
            
            if(!errorMessage.equals("")){
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText(errorMessage);
                a.show();
                return;
            }
            
            jpa.edit(admin);
            getAdmin(admin);
            loadData();
            clearFields();
            
        } catch (Exception ex) {
            
        }
    }
    
    @FXML
    private void changePassword(ActionEvent event) {
        Parent parent = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ChangePassword.fxml"));
        
        try {
            parent = loader.load();
            ChangePasswordController changePasswordController = (ChangePasswordController)loader.getController();
            changePasswordController.getAdmin(myAdmin);
            changePasswordController.getPane(scrollPane);
        } 
        catch (IOException ex) {
            
        }
        scrollPane.setContent(parent);
    }

    @FXML
    private void getBackImage(MouseEvent event) {
        Parent parent = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Home.fxml"));
        
        try {
            parent = loader.load();
            HomeController homeController = (HomeController)loader.getController();
            homeController.getAdmin(myAdmin);
        } 
        catch (IOException ex) {
            
        }
        scrollPane.setContent(parent);
    }
    
    public boolean checkNullAndEmpty(String s){
        return s != null && !s.trim().isEmpty();
    }
}
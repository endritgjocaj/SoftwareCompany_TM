/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softwarecompany_tm.user;

import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import controllers.EmployeesJpaController;
import controllers.UserJpaController;
import entities.Employees;
import entities.Tasks;
import entities.User;
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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
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
public class UserAccountController implements Initializable {
    
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("SoftwareCompany_TMPU");
    
    private UserJpaController jpa = new UserJpaController(emf);
    private List<User> list = new ArrayList<>();
    
    private EmployeesJpaController jpaEmployees = new EmployeesJpaController(emf);
    private List<Employees> employeesList = new ArrayList<>();
    
    @FXML
    private AnchorPane anchorPane1;
    @FXML
    private Label userData;
    @FXML
    private TableView<Employees> tableView;
    @FXML
    private TableColumn<Employees, Integer> idColumn;
    @FXML
    private TableColumn<Employees, String> firstNameColumn;
    @FXML
    private TableColumn<Employees, String> lastNameColumn;
    @FXML
    private TableColumn<Employees, String> usernameColumn;
    @FXML
    private TableColumn<Employees, String> emailColumn;
    @FXML
    private TableColumn<Employees, Character> genderColumn;
    @FXML
    private TableColumn<Employees, String> sectorColumn;
    @FXML
    private TableColumn<Employees, String> positionColumn;
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
    private JFXRadioButton maleRadioButton;
    @FXML
    private ToggleGroup genderRadio;
    @FXML
    private JFXRadioButton femaleRadioButton;
    @FXML
    private ChoiceBox<String> sectorChoiceBox;
    @FXML
    private ChoiceBox<String> positionChoiceBox;
    @FXML
    private Button changePassword;
    
    private static ScrollPane scrollPane;
    
    private static User myUser;
    
    private RadioButton sRadioButton;
    private Employees selectedEmployee = null;
    private String selectedEmail = null;
    
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        list = jpa.findUserEntities();
        employeesList = jpaEmployees.findEmployeesEntities();
        
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        sectorColumn.setCellValueFactory(new PropertyValueFactory<>("sector"));
        positionColumn.setCellValueFactory(new PropertyValueFactory<>("position"));
        
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
        
        sectorChoiceBox.getItems().addAll("Mobile App","Web App","Desktop App");
        positionChoiceBox.getItems().addAll("Front-End","Back-End","Full Stack");
        
        loadData();
        clearFields();
    }    
    
    public void getPane(ScrollPane scrPane){
        scrollPane = scrPane;
        loadData();
        clearFields();
    }
    
    void getUser(User user) {
        userData.setText("User: " + user.getEmployeesId().getFirstName() + " " + user.getEmployeesId().getLastName());
        myUser = user;
    }
    
    public void loadData(){
        employeesList = jpaEmployees.findEmployeesEntities();
        
        if(myUser != null){
            for(Employees emp : employeesList){
                if(emp.getId().equals(myUser.getId())){
                    tableView.getItems().setAll(emp);
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
        emailField.setText(null);
        usernameField.setText(null);
        genderRadio.selectToggle(maleRadioButton);
        sectorChoiceBox.setValue("Mobile App");
    }
    
    @FXML
    private void userRowSelected(MouseEvent event) {
        Employees employee = tableView.getSelectionModel().getSelectedItem();
        
        idField.setText(employee.getId() + "");
        firstNameField.setText(employee.getFirstName());
        lastNameField.setText(employee.getLastName());
        usernameField.setText(employee.getUsername());
        emailField.setText(employee.getEmail());
        
        if(employee.getGender().equals('M')){
            genderRadio.selectToggle(maleRadioButton);
        }
        if(employee.getGender().equals('F')){
            genderRadio.selectToggle(femaleRadioButton);
        }
        
        sectorChoiceBox.setValue(employee.getSector());
        positionChoiceBox.setValue(employee.getPosition());
        
        selectedEmployee = employee;
        selectedEmail = emailField.getText();
    }
    
    @FXML
    private void editUser(ActionEvent event) {
        Employees employee = jpaEmployees.findEmployees(Integer.parseInt(idField.getText()));
                
        String errorMessage = "";
        
        if(!checkNullAndEmpty(firstNameField.getText())){
            errorMessage += "First name field is empty!\n\n";
        }
        
        if(!checkNullAndEmpty(lastNameField.getText())){
            errorMessage += "Last name field is empty!\n\n";
        }
        
        if(!checkNullAndEmpty(emailField.getText())){
            errorMessage += "Email field is empty!\n\n";
        }
        
        for (Employees emp : employeesList) {
            if (!selectedEmail.equals(emailField.getText()) && emp.getEmail().equals(emailField.getText())) {
                errorMessage += "Email: " + emailField.getText() + " exists!\n\n";
                break;
            }
        }
        
        if(selectedEmployee.getTasksList().size() > 0 && !selectedEmployee.getSector().equals(sectorChoiceBox.getValue())){
            for(Tasks d : selectedEmployee.getTasksList()){
                if(!d.getStatus().equals("Done")){
                    errorMessage += "You cannot change the sector if it has tasks assigned!\n\n";
                    break;
                }
            }
        }
        
        sRadioButton = (RadioButton) genderRadio.getSelectedToggle();

        String genderX = sRadioButton.getText();
        
        char gender = sRadioButton.getText().charAt(0);

        employee.setFirstName(firstNameField.getText());
        employee.setLastName(lastNameField.getText());
        employee.setUsername(usernameField.getText());
        employee.setEmail(emailField.getText());
        employee.setGender(gender);
        employee.setSector(sectorChoiceBox.getValue());
        employee.setPosition(positionChoiceBox.getValue());
        
        employee.getUser().setUsername(usernameField.getText());
        
        if(!errorMessage.equals("")){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText(errorMessage);
            a.show();
            return;
        }
        
        try {
            jpaEmployees.edit(employee);
//            jpa.edit(myUser);
            getUser(employee.getUser());
        }
        catch (Exception ex) {}
        
        loadData();
        clearFields();
    }
    
    @FXML
    private void changePassword(ActionEvent event) {
        Parent parent = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ChangePassword.fxml"));
        
        try {
            parent = loader.load();
            ChangePasswordController changePasswordController = (ChangePasswordController)loader.getController();
            changePasswordController.getUser(myUser);
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
            homeController.getUser(myUser);
        } 
        catch (IOException ex) {
            
        }
        scrollPane.setContent(parent);
    }
    
    public boolean checkNullAndEmpty(String s){
        return s != null && !s.trim().isEmpty();
    }
    
}

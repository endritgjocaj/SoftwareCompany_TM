    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softwarecompany_tm.admin;

import java.io.IOException;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import controllers.EmployeesJpaController;
import controllers.TasksJpaController;
import controllers.TechnologiesJpaController;
import controllers.UserJpaController;
import controllers.exceptions.IllegalOrphanException;
import controllers.exceptions.NonexistentEntityException;
import entities.Employees;
import entities.Tasks;
import entities.Technologies;
import entities.User;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
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
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * FXML Controller class
 *
 * @author My-Pc
 */
public class EmployeesController implements Initializable {
    
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("SoftwareCompany_TMPU");
    
    private EmployeesJpaController jpa = new EmployeesJpaController(emf);
    private List<Employees> list = new ArrayList<>();
    
    @FXML
    private TextField searchField;
    @FXML
    private TableView<Employees> tableView;
    @FXML
    private TableColumn<Employees, Integer> idColumn;
    @FXML
    private TableColumn<Employees, String> firstNameColumn;
    @FXML
    private TableColumn<Employees, String> lastNameColumn;
    @FXML
    private TableColumn<Employees, String> emailColumn;
    @FXML
    private TableColumn<Employees, Character> genderColumn;
    @FXML
    private TableColumn<Employees, String> sectorColumn;
    @FXML
    private TableColumn<Employees, List<Technologies>> technologiesColumn;
    @FXML
    private TableColumn<Employees, String> positionColumn;
    @FXML
    private JFXTextField idField;
    @FXML
    private JFXTextField firstNameField;
    @FXML
    private JFXTextField lastNameField;
    @FXML
    private JFXTextField emailField;
    @FXML
    private ToggleGroup genderRadio;
    @FXML
    private JFXRadioButton maleRadioButton;
    @FXML
    private JFXRadioButton femaleRadioButton;
    @FXML
    private ChoiceBox<String> sectorChoiceBox;
    @FXML
    private ChoiceBox<String> positionChoiceBox;
    @FXML
    private Label profileField;
    @FXML
    private Label tasksField;
    
    private UserJpaController jpaUser = new UserJpaController(emf);
    private List<User> userList = new ArrayList<>();
    
    private TasksJpaController jpaTasks = new TasksJpaController(emf);
    private List<Tasks> tasksList = new ArrayList<>();
    
    private TechnologiesJpaController jpaTechnologies = new TechnologiesJpaController(emf);
    private List<Technologies> technologiesList = new ArrayList<>();
    
    private static ScrollPane scrollPane;
    
    private RadioButton sRadioButton;
    
    private Employees selectedEmployee = null;
    private String selectedEmail = null;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
//        jpa = new EmployeesJpaController(emf);
        list = jpa.findEmployeesEntities();
        technologiesList = jpaTechnologies.findTechnologiesEntities();
        tasksList = jpaTasks.findTasksEntities();
        userList = jpaUser.findUserEntities();
        
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        sectorColumn.setCellValueFactory(new PropertyValueFactory<>("sector"));
        technologiesColumn.setCellValueFactory(new PropertyValueFactory<>("technologiesList"));
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
        
        profileField.setVisible(false);
        tasksField.setVisible(false);
        
        sectorChoiceBox.getItems().addAll("Mobile App","Web App","Desktop App");
        sectorChoiceBox.setValue("Mobile App");
        positionChoiceBox.getItems().addAll("Front-End","Back-End","Full Stack");
        
        loadData();
        clearFields();
    }    
    
    public void getPane(ScrollPane scrPane){
        scrollPane = scrPane;
        search();
    }
    
    public void loadData(){
        list = jpa.findEmployeesEntities();
        userList = jpaUser.findUserEntities();
        tableView.getItems().setAll(list);
        tableView.refresh();
        search();
    }
    
    public void clearFields(){
        idField.setText(null);
        firstNameField.setText(null);
        lastNameField.setText(null);
        emailField.setText(null);
        genderRadio.selectToggle(maleRadioButton);
        sectorChoiceBox.setValue("Mobile App");
        
        profileField.setVisible(false);
        tasksField.setVisible(false);
    }

    @FXML
    private void employeeRowSelected(MouseEvent event) {
        Employees employee = tableView.getSelectionModel().getSelectedItem();
        
        idField.setText(employee.getId() + "");
        firstNameField.setText(employee.getFirstName());
        lastNameField.setText(employee.getLastName());
        emailField.setText(employee.getEmail());
        
        profileField.setVisible(true);
        tasksField.setVisible(true);
        
        if(employee.getGender().equals('M')){
            genderRadio.selectToggle(maleRadioButton);
        }
        if(employee.getGender().equals('F')){
            genderRadio.selectToggle(femaleRadioButton);
        }
        
        emailField.setText(employee.getEmail());
        sectorChoiceBox.setValue(employee.getSector());
        positionChoiceBox.setValue(employee.getPosition());
        
        selectedEmployee = employee;
        selectedEmail = emailField.getText();
    }
    
    @FXML
    private void addEmployee(ActionEvent event) {
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
        
        for (Employees emp : list) {
            if (emp.getEmail().equals(emailField.getText())) {
                errorMessage += "Email: " + emailField.getText() + " exists!\n\n";
                break;
            }
        }
        
        if(!errorMessage.equals("")){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText(errorMessage);
            a.show();
            return;
        }
        
        sRadioButton = (RadioButton) genderRadio.getSelectedToggle();
        
        Employees employee = new Employees(firstNameField.getText(), lastNameField.getText(),
                                    emailField.getText(), sRadioButton.getText().charAt(0),  
                                    sectorChoiceBox.getValue(), positionChoiceBox.getValue());
        
        if(!errorMessage.equals("")){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText(errorMessage);
            a.show();
            return;
        }
        
        jpa.create(employee);
        
        String username = employee.getFirstName() + employee.getId();
        employee.setUsername(username);
        
        try {
            jpa.edit(employee);
        } catch (Exception ex) {}
        
        loadData();
        
        String password = "123456";
        String encoded = null;
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes("UTF-8"));
            encoded = Base64.getEncoder().encodeToString(hash);
        }
        catch(Exception ex){}
        
        User user = new User(employee.getId(), username, encoded, employee);    //Employee/User is created with default values: 
                                                                                //username: firstname+id     eg: Endrit135
                                                                                //password: 123456
        
        try {
            jpaUser.create(user);
            loadData();
            clearFields();
        } catch (Exception ex) {}
    }

    @FXML
    private void editEmployee(ActionEvent event) {
        Employees employee = jpa.findEmployees(Integer.parseInt(idField.getText()));
                
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
        
        for (Employees emp : list) {
            if (!selectedEmail.equals(emailField.getText()) && emp.getEmail().equals(emailField.getText())) {
                errorMessage += "Email: " + emailField.getText() + " exists!\n\n";
                break;
            }
        }
        
        if(selectedEmployee.getTasksList().size() > 0 && !selectedEmployee.getSector().equals(sectorChoiceBox.getValue())){
            for(Tasks task : selectedEmployee.getTasksList()){
                if(!task.getStatus().equals("Done")){
                    errorMessage += "You cannot change the sector because " + selectedEmployee + " has tasks assigned to this sector!\n\n";
                    break;
                }
            }
        }
        
        sRadioButton = (RadioButton) genderRadio.getSelectedToggle();

        String genderX = sRadioButton.getText();
        
        char gender = sRadioButton.getText().charAt(0);
        
        employee.setFirstName(firstNameField.getText());
        employee.setLastName(lastNameField.getText());
        employee.setEmail(emailField.getText());
        employee.setGender(gender);
        employee.setSector(sectorChoiceBox.getValue());
        employee.setPosition(positionChoiceBox.getValue());
        
        if(!errorMessage.equals("")){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText(errorMessage);
            a.show();
            return;
        }
        
        try {
            jpa.edit(employee);
            loadData();
            clearFields();
        }
        catch(Exception ex){
            
        }
    }

    @FXML
    private void deleteEmployee(ActionEvent event) {
        for(Tasks task : selectedEmployee.getTasksList()){
            if(task.getEmployeesList().size() > 1){
                task.getEmployeesList().remove(selectedEmployee);
            } 
        }
            
        try {
            jpa.destroy(Integer.parseInt(idField.getText()));
        } catch (NonexistentEntityException ex) {
            Logger.getLogger(EmployeesController.class.getName()).log(Level.SEVERE, null, ex);
        }        
        
        loadData();
        clearFields();
    }
    
    public boolean checkNullAndEmpty(String s){
        return s != null && !s.trim().isEmpty();
    }
    
    public void search(){
        ObservableList<Employees> observableData = FXCollections.observableArrayList(list);
        FilteredList<Employees> filteredData = new FilteredList<>(observableData, b -> true);
        
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
             filteredData.setPredicate(employee -> {
                if(newValue == null || newValue.isEmpty()){
                    return true;
                }
             
                int i = employee.getId().intValue();
                String lowerCaseFilter = newValue.toLowerCase();

                String id = i + "";
                 
                if((id.contains(newValue))){
                    return true;
                }
                else if(employee.getFirstName().toLowerCase().contains(lowerCaseFilter)){
                    return true;
                }
                else if(employee.getLastName().toLowerCase().contains(lowerCaseFilter)){
                    return true;
                }
                else if(employee.getSector().toLowerCase().contains(lowerCaseFilter)){
                    return true;
                }
                else if(employee.getPosition().toLowerCase().contains(lowerCaseFilter)){
                    return true;
                }
                else{
                    for(Technologies technology : employee.getTechnologiesList()){
                        if(technology.getTechnology().toLowerCase().contains(lowerCaseFilter)){
                            return true;
                        }
                    }
                }
                 
                 tableView.setItems(filteredData);
                 
                 return false;
             });
        });
    }

    @FXML
    private void onProfileClick(MouseEvent event) {
        Parent parent = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Profile.fxml"));
        
        try {
            parent = loader.load();
            
            ProfileController profiliController = (ProfileController)loader.getController();
            profiliController.getEmployee(selectedEmployee);
            profiliController.getPane(scrollPane);
        
        } catch (IOException ex) {

        }
        
        scrollPane.setContent(parent);
    }

    @FXML
    private void onTasksClick(MouseEvent event) {
        Parent parent = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("EmployeeTasks.fxml"));
        
        try {    
            parent = loader.load();
            
            EmployeeTasksController employeeTasksController = (EmployeeTasksController)loader.getController();
            employeeTasksController.getEmployeeProjects(selectedEmployee);
            employeeTasksController.getPane(scrollPane);
        
        } catch (IOException ex) {

        }
        scrollPane.setContent(parent);
    }

    @FXML
    private void onCleanClicked(MouseEvent event) {
        clearFields();
    }
}
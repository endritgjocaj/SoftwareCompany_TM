    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softwarecompany_tm.user;

import java.io.IOException;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import controllers.EmployeesJpaController;
import controllers.TasksJpaController;
import controllers.UserJpaController;
import entities.Employees;
import entities.Tasks;
import entities.Technologies;
import entities.User;
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
    private TableColumn<Employees, String> positionColumn;
    @FXML
    private TableColumn<Employees, List<Technologies>> technologiesColumn;
    
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
    private JFXTextField sectorField;
    @FXML
    private JFXTextField positionField;
    @FXML
    private Label profileField;
    @FXML
    private Label tasksField;
    
    private UserJpaController jpaUser = new UserJpaController(emf);
    private List<User> userList = new ArrayList<>();
    
    private TasksJpaController jpaTasks = new TasksJpaController(emf);
    private List<Tasks> tasksList = new ArrayList<>();
    
    private RadioButton sRadioButton;
    private static ScrollPane scrollPane;
    
    private Employees selectedEmployee = null;
    private String selectedEmail = null;
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        list = jpa.findEmployeesEntities();
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
        emailField.setEditable(false);
        genderRadio.selectedToggleProperty();
        sectorField.setEditable(false);
        technologiesColumn.setEditable(false);
        positionField.setEditable(false);
        
        profileField.setVisible(false);
        tasksField.setVisible(false);
        
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
        sectorField.setText(null);
        
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
        
        sectorField.setText(employee.getSector());
        positionField.setText(employee.getPosition());
        
        selectedEmployee = employee;
        selectedEmail = emailField.getText();
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
//                 String id = employee.getId().toString();
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
            employeeTasksController.getEmployee(selectedEmployee);
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
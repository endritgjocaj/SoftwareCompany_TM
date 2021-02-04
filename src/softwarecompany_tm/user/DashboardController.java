/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softwarecompany_tm.user;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXToggleNode;
import controllers.EmployeesJpaController;
import controllers.UserJpaController;
import entities.Employees;
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
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * FXML Controller class
 *
 * @author My-Pc
 */
public class DashboardController implements Initializable {
    
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("SoftwareCompany_TMPU");
    
    private UserJpaController jpa = new UserJpaController(emf);
    private List<User> list = new ArrayList<>();
    
    private EmployeesJpaController jpaEmployees = new EmployeesJpaController(emf);
    private List<Employees> employeesList = new ArrayList<>();
    
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private JFXComboBox<String> comboBox;
    @FXML
    private Label userLabelName;
    @FXML
    private ToggleGroup sideBarToggle;
    @FXML
    private JFXToggleNode homeButton;
    @FXML
    private JFXToggleNode myTasksButton;
    @FXML
    private JFXToggleNode myProfileButton;
    @FXML
    private JFXToggleNode employeesButton;
    @FXML
    private JFXToggleNode projectsButton;
    @FXML
    private JFXToggleNode tasksButton;
    @FXML
    private JFXToggleNode adminsButton;
    
    private static User myUser;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        comboBox.getItems().add("My account");
        comboBox.getItems().add("Log out");
    }    
    
    void getEditedUser(){
        list = jpa.findUserEntities();
        employeesList = jpaEmployees.findEmployeesEntities();
        
        if(myUser != null){
            for(User user : list){
                if(myUser.getId().equals(user.getId())){
                    myUser = user;
                    break;
                }
            }
        }
    }
    
    void getUser(User user) {
        userLabelName.setText(user.getEmployeesId().getFirstName()+ " " + user.getEmployeesId().getLastName());
        myUser = user;
        homeClick();
    }

    @FXML
    private void onAccountClick(ActionEvent event) {
        Parent parent = null;
        
        if(comboBox.getValue().equals("My account")){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserAccount.fxml"));
            
            try {
                parent = loader.load();
                UserAccountController userAccountController = (UserAccountController)loader.getController();
                userAccountController.getUser(myUser);
                userAccountController.getPane(scrollPane);
            } 
            catch (IOException ex) {
            
            }
                scrollPane.setContent(parent);
            }
        else if(comboBox.getValue().equals("Log out")){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UserLoginPage.fxml"));
            try {
                parent = loader.load();
                  Scene scene = new Scene(parent);
                  Stage stage = (Stage)anchorPane.getScene().getWindow();
                  stage.setScene(scene);
                  stage.show();
            } catch (IOException ex) {
            
            }
        }
    }

    @FXML
    private void onHomeClick(MouseEvent event) {
        homeClick();
    }
    
    public void homeClick(){
        sideBarToggle.selectToggle(homeButton);
        Parent parent = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Home.fxml"));
        
        try {
            parent = loader.load();
        } 
        catch (IOException ex) {
            
        }
        scrollPane.setContent(parent);
        getEditedUser();
        
        HomeController home = (HomeController)loader.getController();
        home.getUser(myUser);
        home.getPane(scrollPane);
    }
    
    @FXML
    private void onMyTasksClick(MouseEvent event) {
        sideBarToggle.selectToggle(myTasksButton);
        Parent parent = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MyTasks.fxml"));
        
        try {    
            parent = loader.load();
        } catch (IOException ex) {

        }
        scrollPane.setContent(parent);
        getEditedUser();
        
        MyTasksController myTasksController = (MyTasksController)loader.getController();
        myTasksController.getUser(myUser);
        myTasksController.getPane(scrollPane);
    }
    
    @FXML
    private void onMyProfileClick(MouseEvent event) {
        sideBarToggle.selectToggle(myProfileButton);
        Parent parent = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("MyProfile.fxml"));
        
        try {    
            parent = loader.load();
        } catch (IOException ex) {

        }
        scrollPane.setContent(parent);
        getEditedUser();
        
        MyProfileController myProfileController = (MyProfileController)loader.getController();
        myProfileController.getUser(myUser);
        myProfileController.getPane(scrollPane);
    }

    @FXML
    private void onEmployeesClick(MouseEvent event) {
        sideBarToggle.selectToggle(employeesButton);
        Parent parent = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Employees.fxml"));
        
        try {
            parent = loader.load();
        } 
        catch (IOException ex) {
            
        }
        scrollPane.setContent(parent);
        
        EmployeesController employeesController = (EmployeesController)loader.getController();
        employeesController.getPane(scrollPane);
    }

    @FXML
    private void onProjectsClick(MouseEvent event) {
        sideBarToggle.selectToggle(projectsButton);
        Parent parent = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Projects.fxml"));
        try {
            parent = loader.load();
        } 
        catch (IOException ex) {
            
        }
        scrollPane.setContent(parent);
        
        ProjectsController projectsController = (ProjectsController)loader.getController();
        projectsController.getPane(scrollPane);
    }

    @FXML
    private void onTasksClick(MouseEvent event) {
        sideBarToggle.selectToggle(tasksButton);
        Parent parent = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Tasks.fxml"));
        
        try {
            parent = loader.load();
        } 
        catch (IOException ex) {
            
        }
        
        scrollPane.setContent(parent);
        
        TasksController tasksController = (TasksController)loader.getController();
        tasksController.getPane(scrollPane);
    }

    @FXML
    private void onAdminsClick(MouseEvent event) {
        sideBarToggle.selectToggle(adminsButton);
        Parent parent = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Admins.fxml"));
        
        try {
            parent = loader.load();
        } 
        catch (IOException ex) {
            
        }
        scrollPane.setContent(parent);
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softwarecompany_tm.user;

import com.jfoenix.controls.JFXComboBox;
import controllers.EmployeesJpaController;
import controllers.ProjectsJpaController;
import controllers.TasksJpaController;
import controllers.UserJpaController;
import entities.Employees;
import entities.Projects;
import entities.Tasks;
import entities.User;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * FXML Controller class
 *
 * @author My-Pc
 */
public class HomeController implements Initializable {
    
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("SoftwareCompany_TMPU");
    
    private UserJpaController jpa = new UserJpaController(emf);
    private List<User> list = new ArrayList<>();
    
    private EmployeesJpaController jpaEmployees = new EmployeesJpaController(emf);
    private List<Employees> employeesList = new ArrayList<>();
        
    private TasksJpaController jpaTasks = new TasksJpaController(emf);
    private List<Tasks> tasksList = new ArrayList<>();
        
    private ProjectsJpaController jpaProjects = new ProjectsJpaController(emf);
    private List<Projects> projectsList = new ArrayList<>();
        
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Label userLabelName;
    @FXML
    private PieChart pieChartTasks;
    @FXML
    private BarChart<String, Integer> pieChartProjectTasks;
    @FXML
    private NumberAxis y;
    @FXML
    private CategoryAxis x;
    @FXML
    private JFXComboBox<String> comboBox;
        
    private static ScrollPane scrollPane;
        
    private static User myUser;
        
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        list = jpa.findUserEntities();
        employeesList = jpaEmployees.findEmployeesEntities();
        projectsList = jpaProjects.findProjectsEntities();
        tasksList = jpaTasks.findTasksEntities();
        
        comboBox.getItems().add("My account");
        comboBox.getItems().add("Log out");
        
        if(myUser != null){
            loadView();
        }
    }
    
    void getPane(ScrollPane scrPane) {
        scrollPane = scrPane;
    }
    
    void getUser(User user) {
        userLabelName.setText(user.getEmployeesId().getFirstName()+ " " + user.getEmployeesId().getLastName());
        if(myUser == null){
            myUser = user;
            loadView();
        }
        myUser = user;
    }
    
    void loadView(){
        pieChartTasks();
        pieChartProjectTasks();
    }
    
    void pieChartTasks(){
        int failedToFinish = 0;
        int inProgress = 0;
        int done = 0;
        int notStarted = 0;
        
        for(Tasks task : myUser.getEmployeesId().getTasksList()){
            if(task.getStatus().equals("Failed To Finish")){
                failedToFinish++;
            }
            else if(task.getStatus().equals("In Progress")){
                inProgress++;
            }
            else if(task.getStatus().equals("Done")){
                done++;
            }
            else if(task.getStatus().equals("Not Started")){
                notStarted++;
            }
        }
        
        ObservableList<PieChart.Data> status = FXCollections.observableArrayList(
            new PieChart.Data("Failed To Finish", failedToFinish),
            new PieChart.Data("In Progress", inProgress),
            new PieChart.Data("Done", done),
            new PieChart.Data("Not Started", notStarted)
        );
        
        pieChartTasks.setData(status);
        
        for (Node node : pieChartTasks.lookupAll(".chart-pie-label")) {
            if (node instanceof Text) {        
                for(PieChart.Data data : pieChartTasks.getData()){
                    if(((Text) node).getText().equals(data.getName())){
                        ((Text) node).setText((int) data.getPieValue() + "");
                    }
                }
            }
        }
    }
    
    void pieChartProjectTasks(){        
        XYChart.Series<String, Integer> set2 = new XYChart.Series<>();
        for(Projects project : projectsList){
            int projectTasks = 0;
                    
            for(Tasks task : myUser.getEmployeesId().getTasksList()){
                if(project.getTasksList().contains(task)){
                    projectTasks++;
                }
            }
            set2.getData().add(new XYChart.Data<String, Integer>(project.getProjectName(), (Integer) projectTasks));
        }
        pieChartProjectTasks.getData().addAll(set2);
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
}
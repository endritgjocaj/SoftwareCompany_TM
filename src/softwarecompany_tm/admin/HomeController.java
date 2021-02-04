/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softwarecompany_tm.admin;

import com.jfoenix.controls.JFXComboBox;
import controllers.EmployeesJpaController;
import controllers.ProjectsJpaController;
import controllers.TasksJpaController;
import entities.Admin;
import entities.Employees;
import entities.Projects;
import entities.Tasks;
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
 * @author My-Pc    //NOT FINISHED
 */
public class HomeController implements Initializable {
    
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("SoftwareCompany_TMPU");
    
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
    private PieChart pieChartSectors;
    @FXML
    private PieChart pieChartTasks;
    @FXML
    private NumberAxis y;
    @FXML
    private CategoryAxis x;
    @FXML
    private BarChart<?, ?> pieChartEmployeeTasks;
    @FXML
    private BarChart<?, ?> pieChartProjectTasks;
    @FXML
    private NumberAxis y1;
    @FXML
    private CategoryAxis x1;
    @FXML
    private JFXComboBox<String> comboBox;
    
    private static ScrollPane scrollPane;
    
    private static Admin admin;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        employeesList = jpaEmployees.findEmployeesEntities();
        tasksList = jpaTasks.findTasksEntities();
        projectsList = jpaProjects.findProjectsEntities();
        
        pieChartSectors();
        pieChartTasks();
        
        pieChartEmployeeTasks();
        pieChartProjectTasks();
        
        comboBox.getItems().add("My account");
        comboBox.getItems().add("Log out");
    }    
    
    public void getPane(ScrollPane scrPane){
        scrollPane = scrPane;
    }
    
    void getAdmin(Admin adm) {
        userLabelName.setText(adm.getFirstName()+ " " + adm.getLastName());
        admin = adm;
    }
    
    void pieChartSectors(){
        int mobileApp = 0;
        int webApp = 0;
        int desktopApp = 0;
        int gameApp = 0;
        
        for(Employees p : employeesList){
            if(p.getSector().equals("Mobile App")){
                mobileApp++;
            }
            else if(p.getSector().equals("Web App")){
                webApp++;
            }
            else if(p.getSector().equals("Desktop App")){
                desktopApp++;
            }
        }
        
        ObservableList<PieChart.Data> sectors = FXCollections.observableArrayList(
            new PieChart.Data("Mobile App", mobileApp),
            new PieChart.Data("Web App", webApp),
            new PieChart.Data("Desktop App", desktopApp)
        );
        
        pieChartSectors.setData(sectors);
        
        for (Node node : pieChartSectors.lookupAll(".chart-pie-label")) {
            if (node instanceof Text) {        
                for(PieChart.Data d : pieChartSectors.getData()){
                    if(((Text) node).getText().equals(d.getName())){
                        ((Text) node).setText((int) d.getPieValue() + "");
                    }
                }
            }
        }
    }
    
    void pieChartTasks(){
        int failedToFinish = 0;
        int inProgress = 0;
        int done = 0;
        int notStarted = 0;
        
        for(Tasks d : tasksList){
            if(d.getStatus().equals("Failed To Finish")){
                failedToFinish++;
            }
            else if(d.getStatus().equals("In Progress")){
                inProgress++;
            }
            else if(d.getStatus().equals("Done")){
                done++;
            }
            else if(d.getStatus().equals("Not Started")){
                notStarted++;
            }
        }
        
        ObservableList<PieChart.Data> sectors = FXCollections.observableArrayList(
            new PieChart.Data("Failed To Finish", failedToFinish),
            new PieChart.Data("In Progress", inProgress),
            new PieChart.Data("Done", done),
            new PieChart.Data("Not Started", notStarted)
        );
        
        pieChartTasks.setData(sectors);
        
        for (Node node : pieChartTasks.lookupAll(".chart-pie-label")) {
            if (node instanceof Text) {        
                for(PieChart.Data d : pieChartTasks.getData()){
                    if(((Text) node).getText().equals(d.getName())){
                        ((Text) node).setText((int) d.getPieValue() + "");
                    }
                }
            }
        }
    }
    
    void pieChartEmployeeTasks(){
        XYChart.Series set1 = new XYChart.Series<>();
        for(Employees p : employeesList){
            set1.getData().add(new XYChart.Data(p.getFirstName() + " " + p.getLastName(), p.getTasksList().size()));
        }
        pieChartEmployeeTasks.getData().addAll(set1);
    }
    
    void pieChartProjectTasks(){
        XYChart.Series set2 = new XYChart.Series<>();
        for(Projects project : projectsList){
            set2.getData().add(new XYChart.Data(project.getProjectName(), project.getTasksList().size()));
        }
        pieChartProjectTasks.getData().addAll(set2);
    }
    
    @FXML
    private void onAccountClick(ActionEvent event) {
        Parent parent = null;
        
        if(comboBox.getValue().equals("My account")){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminAccount.fxml"));
        
            try {
                parent = loader.load();
                AdminAccountController adminAccountController = (AdminAccountController)loader.getController();
                adminAccountController.getAdmin(admin);
                adminAccountController.getPane(scrollPane);
            } 
            catch (IOException ex) {
            
            }
                scrollPane.setContent(parent);
            }
        else if(comboBox.getValue().equals("Log out")){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AdminLoginPage.fxml"));
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

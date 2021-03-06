/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softwarecompany_tm.user;

import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import controllers.EmployeesJpaController;
import controllers.ProjectsJpaController;
import controllers.TasksJpaController;
import controllers.TechnologiesJpaController;
import entities.Employees;
import entities.Projects;
import entities.Tasks;
import entities.Technologies;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
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
public class TasksController implements Initializable {
    
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("SoftwareCompany_TMPU");
    
    private TasksJpaController jpa = new TasksJpaController(emf); 
    private List<Tasks> list = new ArrayList<>();
    
    private List<Tasks> tasksList = new ArrayList<>();
    
    @FXML
    private TextField searchField;
    @FXML
    private TableView<Tasks> tableView;
    @FXML
    private TableColumn<Tasks, Integer> idColumn;
    @FXML
    private TableColumn<Tasks, String> descriptionColumn;
    @FXML
    private TableColumn<Tasks, Projects> projectColumn;
    @FXML
    private TableColumn<Tasks, List<Employees>> employeesColumn;
    @FXML
    private TableColumn<Tasks, String> startDateColumn;
    @FXML
    private TableColumn<Tasks, String> endDateColumn;
    @FXML
    private TableColumn<Tasks, String> statusColumn;
    @FXML
    private JFXTextField idField;
    @FXML
    private JFXTextField descriptionField;
    @FXML
    private JFXTextField projectField;
    @FXML
    private JFXListView<Employees> frontEndListView;
    @FXML
    private JFXListView<Employees> backEndListView;
    @FXML
    private JFXTextField startDateField;
    @FXML
    private JFXTextField endDateField;
    @FXML
    private JFXTextField statusField;
    
    private ProjectsJpaController jpaProjects = new ProjectsJpaController(emf);
    private List<Projects> projectsList = new ArrayList<>();
    
    private EmployeesJpaController jpaEmployees = new EmployeesJpaController(emf);
    private List<Employees> employeesList = new ArrayList<>();
    
    private TechnologiesJpaController jpaTechnologies = new TechnologiesJpaController(emf);
    private List<Technologies> technologiesList = new ArrayList<>();
    
    private static ScrollPane scrollPane;
    
    private String projectDueDate = null;
    private Projects selectedProject = null;
    
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
    Date acDate = new Date(System.currentTimeMillis());
    private String actualDate = sdf.format(acDate);
    
    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        list = jpa.findTasksEntities();
        
        projectsList = jpaProjects.findProjectsEntities();
        employeesList = jpaEmployees.findEmployeesEntities();
        technologiesList = jpaTechnologies.findTechnologiesEntities();
                
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        projectColumn.setCellValueFactory(new PropertyValueFactory<>("project"));
        employeesColumn.setCellValueFactory(new PropertyValueFactory<>("employeesList"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        idField.setEditable(false);
        startDateField.setEditable(false);      
        endDateField.setEditable(false);        
        statusField.setEditable(false);
        
        for(Tasks task : list){
            Projects project = task.getProject();
            projectDueDate = project.getDueDate();
            
            if(get(projectDueDate) < get(actualDate) && !task.getStatus().equals("Done")){
                statusField.setText("Failed To Finish");
                task.setStatus(statusField.getText());
            }
            else if(get(projectDueDate) > get(actualDate) && task.getStatus().equals("Failed To Finish")){
                statusField.setText("Not Started");
                task.setStatus(statusField.getText());
            }
            
            try{
                jpa.edit(task);
            }
            catch(Exception ex){
            }
        }
        
        for(Tasks task : list){
            for(Employees employee : task.getEmployeesList()){
                boolean isCommon = Collections.disjoint(task.getProject().getTechnologiesList(),employee.getTechnologiesList());
                if(isCommon == true){
                    task.getEmployeesList().remove(employee);
                    
                    try {
                        jpa.edit(task);
                        loadData();
                    } catch (Exception ex) {}
                }
            }
        }
        customiseFactory(statusColumn, 1);
        clearFields();
    }
    
    public void getPane(ScrollPane scrPane){
        list = jpa.findTasksEntities();
        scrollPane = scrPane;
        search();
        loadData();
    }
    
    public void loadData(){
        list = jpa.findTasksEntities();
        tableView.getItems().setAll(list);
        tableView.refresh();
    }
    
    public void clearFields(){
        idField.setText(null);
        descriptionField.setText(null);
        projectField.setText(null);
        frontEndListView.getItems().clear();
        backEndListView.getItems().clear();
        startDateField.setText(null);
        endDateField.setText(null);
        statusField.setText(null);
    }
    
    @FXML
    private void taskRowSelected(MouseEvent event) {
        Tasks task = tableView.getSelectionModel().getSelectedItem();
        
        idField.setText(task.getId() + "");
        descriptionField.setText(task.getDescription());
        
        if(get(actualDate) < get(task.getProject().getDueDate())){
            projectField.setText(task.getProject().toString());
        }
        
        startDateField.setText(task.getStartDate());
        endDateField.setText(task.getEndDate());
        statusField.setText(task.getStatus());
        
        selectedProject = task.getProject();
        
        frontEndListView.getItems().clear();
        backEndListView.getItems().clear();
        
        for(Employees employee : task.getEmployeesList()){
            if(employee.getPosition().equals("Full Stack")){
                frontEndListView.getItems().addAll(employee);
                backEndListView.getItems().addAll(employee);
            }
            else if(employee.getPosition().equals("Front-End")){
                frontEndListView.getItems().addAll(employee);
            }
            else if(employee.getPosition().equals("Back-End")){
                backEndListView.getItems().addAll(employee);
            }
        }
    }
    
    public void search(){
        loadData();
        
        if(tasksList == null || tasksList.isEmpty()){
            tasksList = list;
        }
        
        ObservableList<Tasks> observableData = FXCollections.observableArrayList(tasksList);
        FilteredList<Tasks> filteredData = new FilteredList<>(observableData, b -> true);
        
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
             filteredData.setPredicate(task -> {
                 if(newValue == null || newValue.isEmpty()){
                     return true;
                 }
                 
                 String lowerCaseFilter = newValue.toLowerCase();
                 String id = task.getId().toString();
                 
                 if((id.trim().contains(newValue))){
                     return true;
                 }
                 else if(task.getDescription().toLowerCase().contains(lowerCaseFilter)){
                     return true;
                 }
                 else if(task.getProject().getProjectName().toLowerCase().contains(lowerCaseFilter)){
                     return true;
                 }
                 else if(task.getStatus().toLowerCase().contains(lowerCaseFilter)){
                     return true;
                 }
                 else if(true){
                     for(Employees employee : task.getEmployeesList()){
                         if(employee.toString().toLowerCase().contains(lowerCaseFilter)){
                             return true;
                         }
                     }
                 }             
                 tableView.setItems(filteredData);
                 return false;
             });
        });
    }
    
    public long get(String date){
        
        String [] array = new String [8];
            array[0] = date.charAt(6) + "";
            array[1] = date.charAt(7) + "";
            array[2] = date.charAt(8) + "";
            array[3] = date.charAt(9) + "";
            array[4] = date.charAt(3) + "";
            array[5] = date.charAt(4) + "";
            array[6] = date.charAt(0) + "";
            array[7] = date.charAt(1) + "";
        
        String strDate = "";
        
        for(String value : array){
            strDate += value;
        }
        
        long longDate = Long.parseLong(strDate);
        
        return longDate;
    }
    
    private void customiseFactory(TableColumn<Tasks, String> calltypel, int x) {
        calltypel.setCellFactory(column -> {
            return new TableCell<Tasks, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    setText(empty ? "" : getItem().toString());
                    setGraphic(null);

                    TableRow<Tasks> currentRow = getTableRow();

                    if (!isEmpty() && x == 1) {

                        if(item.equals("Done")) 
                            currentRow.setStyle("-fx-background-color:#dff0d8;");
                        if(item.equals("In Progress")) 
                            currentRow.setStyle("-fx-background-color:#fcf8e3");
                        if(item.equals("Failed To Finish")) 
                            currentRow.setStyle("-fx-background-color:#f2dede");
                        if(item.equals("Not Started")) 
                            currentRow.setStyle("-fx-background-color:#f5f5f5");
                        if(item.equals("To Do"))
                            currentRow.setStyle("-fx-background-color:#d9edf7");
                    }
                    else if (x  == 0){
                        currentRow.setStyle(item);
                    }
                }
            };
        });
}

    @FXML
    private void onCleanClicked(MouseEvent event) {
        clearFields();
    }
}
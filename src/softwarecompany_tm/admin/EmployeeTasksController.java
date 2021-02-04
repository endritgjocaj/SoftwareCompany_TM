/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softwarecompany_tm.admin;

import com.jfoenix.controls.JFXTextField;
import controllers.EmployeesJpaController;
import controllers.ProjectsJpaController;
import controllers.TasksJpaController;
import controllers.TechnologiesJpaController;
import controllers.exceptions.NonexistentEntityException;
import entities.Employees;
import entities.Projects;
import entities.Tasks;
import entities.Technologies;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
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
import org.controlsfx.control.CheckComboBox;

/**
 * FXML Controller class
 *
 * @author My-Pc
 */
public class EmployeeTasksController implements Initializable {
    
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("SoftwareCompany_TMPU");
    
    private TasksJpaController jpa = new TasksJpaController(emf); 
    private List<Tasks> list = new ArrayList<>();
    
    private List<Tasks> tasksList = new ArrayList<>();
    
    @FXML
    private Label employeeTasks;
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
    private ChoiceBox<Projects> projectChoiceBox;
    @FXML
    private CheckComboBox<Employees> frontEndComboBox;
    @FXML
    private CheckComboBox<Employees> backEndComboBox;
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
    
    private String projectStartDate = null;
    private String projectDueDate = null;
    
    private Projects chosenProject = null;
    private Employees selectedEmployee = null;
    
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
            Projects projek = task.getProject();
            projectDueDate = projek.getDueDate();
            
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
    
    public void getEmployeeProjects(Employees employee){
        selectedEmployee = employee;
        employeeTasks.setText("Employee: " + employee.getFirstName()+ " " + employee.getLastName());
        
        frontEndComboBox.getItems().clear();
        backEndComboBox.getItems().clear();
        
        projectChoiceBox.getItems().clear();
        
        for(Projects project : projectsList){
            if(employee.getSector().equals(project.getSector())){
                for(Technologies technology: employee.getTechnologiesList()){
                    if(project.getTechnologiesList().contains(technology) && !projectChoiceBox.getItems().contains(project)){
                        projectChoiceBox.getItems().add(project);
                    }
                }
            }
        }
        
        projectChoiceBox.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Projects> observable, Projects oldValue, Projects newValue) -> {
            setEmployeesToTask(newValue);
        }
        );

        loadData();
        clearFields();
    }
    
    void setEmployeesToTask(Projects slcProject){
        frontEndComboBox.getCheckModel().clearChecks();
        frontEndComboBox.getItems().clear();
        
        backEndComboBox.getCheckModel().clearChecks();
        backEndComboBox.getItems().clear();
        
        for(Employees employee : employeesList){
            if(employee.getSector().equals(slcProject.getSector())){
                
                for(Technologies technology : employee.getTechnologiesList()){
                    if(slcProject.getTechnologiesList().contains(technology) && 
                        !frontEndComboBox.getItems().contains(employee) && !backEndComboBox.getItems().contains(employee)){
                        if(employee.getPosition().equals("Full Stack")){
                            frontEndComboBox.getItems().addAll(employee);
                            backEndComboBox.getItems().addAll(employee);
                        }
                        else if(employee.getPosition().equals("Front-End")){
                            frontEndComboBox.getItems().addAll(employee);
                        }
                        else if(employee.getPosition().equals("Back-End")){
                            backEndComboBox.getItems().addAll(employee);
                        }
                    }
                }
            }
        }
    }
    
    public void loadData(){
        list = jpa.findTasksEntities();
        tasksList.clear();
        
        for(Tasks task : list){
            for(Employees p : task.getEmployeesList()){
                if(p.getId().equals(selectedEmployee.getId())){
                    tasksList.add(task);
                }
            }
        }
        
        tableView.getItems().setAll(tasksList);
        tableView.refresh();
    }
    
    public void clearFields(){
        idField.setText(null);
        descriptionField.setText(null);
        projectChoiceBox.setValue(null);
        frontEndComboBox.getCheckModel().clearChecks();
        backEndComboBox.getCheckModel().clearChecks();
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
            projectChoiceBox.setValue(task.getProject());
        }
        
        statusField.setText(task.getStatus());

        startDateField.setText(task.getStartDate());
        endDateField.setText(task.getEndDate());
        
        chosenProject = task.getProject();
        
        frontEndComboBox.getCheckModel().clearChecks();
        backEndComboBox.getCheckModel().clearChecks();
        
        List<Integer> frontList = new ArrayList<>();
        List<Integer> backList = new ArrayList<>();

        List<Employees> frontEndBox = frontEndComboBox.getItems();
        List<Employees> backEndBox = backEndComboBox.getItems();
        
        for(int i = 0; i < frontEndBox.size(); i++){
            if(task.getEmployeesList().contains(frontEndBox.get(i))){
                frontList.add(i);
            }
        }
        
        for(int i = 0; i < backEndBox.size(); i++){
            if(task.getEmployeesList().contains(backEndBox.get(i))){
                backList.add(i);
            }
        }
        
        int [] frontArray = frontList.stream().mapToInt(Integer::intValue).toArray();
        int [] backArray = backList.stream().mapToInt(Integer::intValue).toArray();
        
        frontEndComboBox.getCheckModel().checkIndices(frontArray);
        backEndComboBox.getCheckModel().checkIndices(backArray);
        
    }

    @FXML
    private void addTask(ActionEvent event) {
        String errorMessage = "";
        
        String description = descriptionField.getText();
        Projects project = projectChoiceBox.getValue();
            
        if(description == null || description.trim().equals("")){
            errorMessage += "Description field cannot be empty!\n\n";
        }
        
        if(projectChoiceBox.getValue() == null){
            errorMessage += "Project field cannot be empty!\n\n";
        }
        
        String startDate = actualDate;  
        String endDate = null;      
        statusField.setText("Not Started"); 

        
        for(Projects projek : projectsList){
            if(projek.equals(projectChoiceBox.getValue())){
                projectStartDate = projek.getStartDate();
                projectDueDate = projek.getDueDate();
            }
        }
        
        if(get(actualDate) < get(projectStartDate)){
            startDate = projectStartDate;
        }
        
        if(get(actualDate) > get(projectDueDate)){
            errorMessage += "You cannot create new tasks because project has ended!\n\n";
        }
        
        List<Employees> employeesAssigned = new ArrayList<>();                          
        for(Employees p : frontEndComboBox.getCheckModel().getCheckedItems()){
            employeesAssigned.add(p);
        }
        for(Employees p : backEndComboBox.getCheckModel().getCheckedItems()){
            if(!employeesAssigned.contains(p)){
                employeesAssigned.add(p);
            }                                                                              
        }

        Tasks task = new Tasks(description, project, employeesAssigned, startDate, endDate, statusField.getText());
        
        if(!errorMessage.equals("")){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText(errorMessage);
            a.show();
            return;
        }
        
        jpa.create(task);
        
        loadData();
        clearFields();
    }
    

    @FXML
    private void editTask(ActionEvent event) {
        Tasks task = jpa.findTasks(Integer.parseInt(idField.getText()));
        
        String errorMessage = "";
        
        String description = descriptionField.getText();
        Projects project = projectChoiceBox.getValue();
        
        List<Employees> employeesAssigned = new ArrayList<>();                          
        for(Employees employee : frontEndComboBox.getCheckModel().getCheckedItems()){
            employeesAssigned.add(employee);
        }
        for(Employees employee : backEndComboBox.getCheckModel().getCheckedItems()){
            if(!employeesAssigned.contains(employee)){
                employeesAssigned.add(employee);
            }                                                                               
        }
        
        String status = statusField.getText();
        
        if(statusField.getText().equals("Done")){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("You cannot change anything because the task is done!\n\n");
            a.show();
            return;
        }
        
        if(statusField.getText().equals("Failed to finish")){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("You cannot change anything because the deadline has already past!\n\n");
            a.show();
            return;
        }
        
        if(chosenProject != null && !chosenProject.equals(project)){
            errorMessage += "You cannot change the project!\n\n";
        }
        
        if(!errorMessage.equals("")){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText(errorMessage);
            a.show();
            return;
        }
        
        task.setDescription(description);
        task.setProject(project);
        task.setEmployeesList(employeesAssigned);
        
        try{
            jpa.edit(task);
            loadData();
            clearFields();
        }
        catch(Exception ex){
            
        }
    }

    @FXML
    private void deleteTask(ActionEvent event) {
        try{
            jpa.destroy(Integer.parseInt(idField.getText()));
            loadData();
            clearFields();
        }
        catch(NonexistentEntityException ex){
            
        }
    }
    
    @FXML
    private void getBackImage(MouseEvent event) {
        Parent parent = null;
        FXMLLoader loader = loader = new FXMLLoader(getClass().getResource("Employees.fxml"));
        
        try {
            parent = loader.load();
        } catch (IOException ex) {

        }    
        scrollPane.setContent(parent);
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
    
    private void customiseFactory(TableColumn<Tasks, String> calltype, int x) {
        calltype.setCellFactory(column -> {
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
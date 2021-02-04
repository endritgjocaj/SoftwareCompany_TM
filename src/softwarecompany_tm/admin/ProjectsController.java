/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softwarecompany_tm.admin;

import java.util.*;                             
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTextField;
import controllers.ProjectsJpaController;
import controllers.TasksJpaController;
import controllers.TechnologiesJpaController;
import controllers.exceptions.NonexistentEntityException;
import entities.Projects;
import entities.Tasks;
import entities.Technologies;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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
public class ProjectsController implements Initializable {
    
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("SoftwareCompany_TMPU");
    
    private ProjectsJpaController jpa = new ProjectsJpaController(emf); 
    private List<Projects> list = new ArrayList<>();
    
    @FXML
    private TextField searchField;
    @FXML
    private TableView<Projects> tableView;
    @FXML
    private TableColumn<Projects, Integer> idColumn;
    @FXML
    private TableColumn<Projects, String> projectNameColumn;
    @FXML
    private TableColumn<Projects, String> sectorColumn;
    @FXML
    private TableColumn<Projects, List<Technologies>> technologiesColumn;
    @FXML
    private TableColumn<Projects, String> clientColumn;
    @FXML
    private TableColumn<Projects, String> startDateColumn;
    @FXML
    private TableColumn<Projects, String> dueDateColumn;
    @FXML
    private JFXTextField idField;
    @FXML
    private JFXTextField projectNameField;
    @FXML
    private ChoiceBox<String> sectorChoiceBox;
    @FXML
    private TextField technologiesField;
    @FXML
    private ListView<Technologies> listView;
    @FXML
    private JFXTextField clientField;
    @FXML
    private JFXDatePicker startDateDatePicker;
    @FXML
    private JFXDatePicker dueDateDatePicker;
    @FXML
    private Label tasksField;
    
    private TechnologiesJpaController jpaTechnologies = new TechnologiesJpaController(emf);
    private List<Technologies> technologiesList = new ArrayList<>();
    
    private TasksJpaController jpaTasks = new TasksJpaController(emf);
    
    private static ScrollPane scrollPane;
    
    private String selectedDueDate = null;
    private Projects selectedProject = null;
    private Technologies selectedTechnology = null;
    
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
    Date acDate = new Date(System.currentTimeMillis());
    private String actualDate = sdf.format(acDate);
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        list = jpa.findProjectsEntities();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        projectNameColumn.setCellValueFactory(new PropertyValueFactory<>("projectName"));
        sectorColumn.setCellValueFactory(new PropertyValueFactory<>("sector"));
        technologiesColumn.setCellValueFactory(new PropertyValueFactory<>("technologiesList"));
        clientColumn.setCellValueFactory(new PropertyValueFactory<>("client"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        
        idField.setEditable(false);
        tasksField.setVisible(false);
        
        sectorChoiceBox.getItems().addAll("Mobile App","Web App","Desktop App");
        
        for(Technologies tech : technologiesList){
            if(tech.getProjectsList().isEmpty()){
                try {
                    jpaTechnologies.destroy(tech.getId());
                } catch (NonexistentEntityException ex) {
                }
            }
        }
        
        search();
        loadData();
        clearFields();
    }
    
    public void getPane(ScrollPane scrPane){
        scrollPane = scrPane;
        loadData();
    }
    
    public void loadData(){
        list = jpa.findProjectsEntities();
        tableView.getItems().setAll(list);
        tableView.refresh();
    }
    
    public void clearFields(){
        idField.setText(null);
        projectNameField.setText(null);
        sectorChoiceBox.setValue(null);
        listView.getItems().clear();
        clientField.setText(null);
        startDateDatePicker.setValue(null);
        dueDateDatePicker.setValue(null);
        tasksField.setVisible(false);
    }
    
    @FXML
    private void projectRowSelected(MouseEvent event) {
        Projects project = tableView.getSelectionModel().getSelectedItem();
        
        idField.setText(project.getId() + "");
        projectNameField.setText(project.getProjectName());
        sectorChoiceBox.setValue(project.getSector());
        clientField.setText(project.getClient());
        technologiesField.setText("");
        tasksField.setVisible(true);
        
        String strStartDate = project.getStartDate();
        LocalDate localStartDate = LocalDate.parse(strStartDate, formatter);
        startDateDatePicker.setValue(localStartDate);
        
        String strDueDate = project.getDueDate();
        LocalDate localDueDate = LocalDate.parse(strDueDate, formatter);
        dueDateDatePicker.setValue(localDueDate);
        
        selectedProject = project;
        selectedDueDate = strDueDate;
        
        ObservableList<Technologies> obsList = FXCollections.observableArrayList(project.getTechnologiesList());
        listView.setItems(obsList);
    }
    
    @FXML
    private void addProject(ActionEvent event) {
        String errorMessage = "";
        String projectName = projectNameField.getText();
        String sector = sectorChoiceBox.getValue();
        String client = clientField.getText();
        
        List<Technologies> techList = new ArrayList<>();
        for(Technologies tech : listView.getItems()){
            techList.add(tech);
        }
        
        if(!checkNullAndEmpty(projectName)){
            errorMessage += "Project name field is empty!\n\n";
        }
        else{
            for(Projects project : list) {
                if(project.getProjectName().equalsIgnoreCase(projectName)) {
                        errorMessage += "Project \"" + projectName + "\" exists!\n\n";
                }
            }
        }
        if(!checkNullAndEmpty(client)){
            errorMessage += "Client field is empty!\n\n";
        } 
        
        if(sector == null){
            errorMessage += "Sector field is empty!\n\n";
        }
        
        String startDateP = null;
        LocalDate localDate1 = startDateDatePicker.getValue();
        if(localDate1 != null){
            startDateP = formatter.format(localDate1);
            startDateDatePicker.setValue(localDate1);
        }
        
        String dueDateP = null;
        LocalDate localDate2 = dueDateDatePicker.getValue();
        if(localDate2 != null){
            dueDateP = formatter.format(localDate2);
            dueDateDatePicker.setValue(localDate2);
        }
        
        if(startDateP == null){
            errorMessage += "Start date field is empty!\n\n";
        }
        else if(get(startDateP) < get(actualDate)){
            errorMessage += "The new project cannot start before: " + actualDate + "!\n\n";
        }
        
        if(dueDateP == null){
            errorMessage += "Due date is empty!\n\n";
        }
        else if(get(startDateP) > get(dueDateP)){
            errorMessage += "Project cannot start after due date!\n\n";
        }
        
        if(!errorMessage.equals("")){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText(errorMessage);
            a.show();
            return;
        }
        
        Projects project = new Projects(projectName, sector, techList, client, startDateP, dueDateP);
        
        jpa.create(project);
        loadData();
        clearFields();
    }
    
    @FXML
    private void editProject(ActionEvent event) {
        Projects project = jpa.findProjects(Integer.parseInt(idField.getText()));
        
        String errorMessage = "";
        String projectName = projectNameField.getText();
        String sector = sectorChoiceBox.getValue();
        String client = clientField.getText();
        
        List<Technologies> techList = new ArrayList<>();
        for(Technologies tech : listView.getItems()){
            techList.add(tech);
        }
        
        if(!checkNullAndEmpty(projectName)){
            errorMessage += "Project name field is empty!\n\n";
        }
        else {
            if(!projectName.equals(selectedProject.getProjectName())){
                for(Projects projek : list) {
                    if(projek.getProjectName().equalsIgnoreCase(projectName)) {
                        errorMessage += "Project \"" + projectName + "\" exists!\n\n";
                    }
                }
            }
        }
        
        if(!checkNullAndEmpty(client)){
            errorMessage += "Client field is empty!\n\n";
        }
        if(!checkNullAndEmpty(sector)){
            errorMessage += "Sector field is empty!\n\n";
        }    
            
        if(selectedProject.getTasksList().size() > 0 && !selectedProject.getSector().equals(sector)){
            errorMessage += "You cannot change the sector if it has tasks assigned!\n\n";
        }
        
        String startDateP = null;
        LocalDate localStartDate = startDateDatePicker.getValue();
        if(localStartDate != null){
            startDateP = formatter.format(localStartDate);
            startDateDatePicker.setValue(localStartDate);
        }
        
        String dueDateP = null;
        LocalDate localDueDate = dueDateDatePicker.getValue();
        if(localDueDate != null){
            dueDateP = formatter.format(localDueDate);
            dueDateDatePicker.setValue(localDueDate);
        }
        
        
        if(startDateP == null || dueDateP == null){
            if(startDateP == null){
                errorMessage += "Start date field is empty!\n\n";
            }
            if(dueDateP == null){
                errorMessage += "Due date field is empty!\n\n";
            }
        }
        
        else{
            if(!selectedProject.getStartDate().equals(startDateP)){
                if(selectedProject.getTasksList().size() > 0){
                    errorMessage += "You cannot change the start date if the project has already tasks assigned!\n\n";
                }
                else if(get(actualDate) > get(startDateP)){
                    errorMessage += "Project's start date cannot be in the past days!\n\n";
                }
            }
            if(!selectedProject.getDueDate().equals(dueDateP) && get(startDateP) > get(dueDateP)){
                errorMessage += "Due date cannot be before start date!\n\n";
            }
        }
        
        if(!errorMessage.equals("")){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText(errorMessage);
            a.show();
            return;
        }
        
        project.setProjectName(projectName);
        project.setSector(sector);
        project.setTechnologiesList(techList);
        project.setClient(client);
        project.setStartDate(startDateP);
        project.setDueDate(dueDateP);
        
        try {
            jpa.edit(project);
            search();
            loadData();
            clearFields();
        }
        catch (Exception ex){   
        }
    }
    
    @FXML
    private void deleteProject(ActionEvent event) {
        try {
            if(selectedProject.getTasksList().size() > 0) {
                
                Alert a = new Alert(Alert.AlertType.CONFIRMATION);
                a.setTitle("Confirmation Dialog");
                a.setHeaderText("Delete confirmation!");
                a.setContentText("If you delete project, you will delete all tasks assigned to this project!\n\n");
                Optional<ButtonType> result = a.showAndWait();
                
                if(result.get() == ButtonType.OK){
                     for(Tasks detyra : selectedProject.getTasksList()){
                         jpaTasks.destroy(detyra.getId());
                     }
                    jpa.destroy(Integer.parseInt(idField.getText()));
                }
            }
            else{
                jpa.destroy(Integer.parseInt(idField.getText()));    
            }
            
            loadData();
            clearFields();
        } 
        
        catch (NonexistentEntityException ex) {
        }
    }
    
    public boolean checkNullAndEmpty(String s){
        return s != null && !s.trim().isEmpty();
    }
    
    public void search(){
        ObservableList<Projects> observableData = FXCollections.observableArrayList(list);
        FilteredList<Projects> filteredData = new FilteredList<>(observableData, b -> true);
        
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
             filteredData.setPredicate(project -> {
                 if(newValue == null || newValue.isEmpty()){
                     return true;
                 }
                 
                 String lowerCaseFilter = newValue.toLowerCase();
                 String id = project.getId().toString();
                 
                 if((id.trim().contains(newValue))){
                     return true;
                 }
                 else if(project.getProjectName().toLowerCase().contains(lowerCaseFilter)){
                     return true;
                 }
                 else if(project.getSector().toLowerCase().contains(lowerCaseFilter)){
                     return true;
                 }
                 else if(project.getClient().toLowerCase().contains(lowerCaseFilter)){
                     return true;
                 }
                 
                 tableView.setItems(filteredData);
  
                 return false;
             });
        });
    }
    
    public int get(String date){
        
        date = date.replace("/","");
        
        String [] array = new String [8];
            array[0] = date.charAt(4) + "";
            array[1] = date.charAt(5) + "";
            array[2] = date.charAt(6) + "";
            array[3] = date.charAt(7) + "";
            array[4] = date.charAt(2) + "";
            array[5] = date.charAt(3) + "";
            array[6] = date.charAt(0) + "";
            array[7] = date.charAt(1) + "";
        
        String strDate = "";
        
        for(String value : array){
            strDate += value;
        }
        
        int intDate = Integer.parseInt(strDate);
        
        return intDate;
    }

    @FXML
    private void onTasksClick(MouseEvent event) {
        Parent parent = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ProjectTasks.fxml"));
        
        try {    
            parent = loader.load();
            
            ProjectTasksController projectTasksController = (ProjectTasksController)loader.getController();
            projectTasksController.getProject(selectedProject);
            projectTasksController.getPane(scrollPane);
            
        } catch (IOException ex) {

        }
        scrollPane.setContent(parent);
        
    }

    @FXML
    private void onCleanClicked(MouseEvent event) {
        clearFields();
    }

    @FXML
    private void technologyRowSelected(MouseEvent event) {
        technologiesField.setText(null);
        
        Technologies tech = listView.getSelectionModel().getSelectedItem();
        selectedTechnology = tech;
        
        technologiesField.setText(tech.getTechnology());
    }

    @FXML
    private void addTechnology(MouseEvent event) {
        String errorMessage = "";
        technologiesList = jpaTechnologies.findTechnologiesEntities();
        
        Technologies technology = new Technologies(technologiesField.getText());
        
        for(Technologies tech : technologiesList){
            if(tech.getTechnology().equals(technology.getTechnology())){
                technology.setId(tech.getId());
                break;
            }
        }
        
        technologiesField.setText(null);
        
        if(technology.getTechnology() == null || technology.getTechnology().trim().equals("")){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Technology field is empty!\n");
            a.show();
            return;
        }
        
        for(Technologies tech : listView.getItems()){
            if(tech.getTechnology().equals(technology.getTechnology())){   
                Alert a = new Alert(Alert.AlertType.ERROR);
                a.setContentText("This technology exists!\n");
                a.show();
                return;
            }
        }
        
        listView.getItems().add(technology);
        
        if(!technologiesList.contains(technology)){
            jpaTechnologies.create(technology);
        }
    }

    @FXML
    private void deleteTechnology(MouseEvent event) {
        listView.getItems().remove(selectedTechnology);
        technologiesField.setText(null);
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softwarecompany_tm.user;

import java.util.*;                             
import com.jfoenix.controls.JFXTextField;
import controllers.ProjectsJpaController;
import controllers.TechnologiesJpaController;
import controllers.exceptions.NonexistentEntityException;
import entities.Projects;
import entities.Technologies;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
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
    private JFXTextField sectorField;
    @FXML
    private TextField technologiesField;
    @FXML
    private ListView<Technologies> listView;
    @FXML
    private JFXTextField clientField;
    @FXML
    private JFXTextField startDateField;
    @FXML
    private JFXTextField dueDateField;
    @FXML
    private Label tasksField;
    
    private TechnologiesJpaController jpaTechnologies = new TechnologiesJpaController(emf);
    private List<Technologies> technologiesList = new ArrayList<>();
    
    private static ScrollPane scrollPane;
    
    private Projects selectedProject = null;
    
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
        projectNameField.setEditable(false);
        sectorField.setEditable(false);
        technologiesField.setEditable(false);
        clientField.setEditable(false);
        startDateField.setEditable(false);
        dueDateField.setEditable(false);
        
        tasksField.setVisible(false);
        
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
        sectorField.setText(null);
        listView.getItems().clear();
        clientField.setText(null);
        startDateField.setText(null);
        dueDateField.setText(null);
        tasksField.setVisible(false);
    }
    
    @FXML
    private void projectRowSelected(MouseEvent event) {
        Projects project = tableView.getSelectionModel().getSelectedItem();
        
        idField.setText(project.getId() + "");
        projectNameField.setText(project.getProjectName());
        sectorField.setText(project.getSector());
        clientField.setText(project.getClient());
        technologiesField.setText("");
        startDateField.setText(project.getStartDate());
        dueDateField.setText(project.getDueDate());
        tasksField.setVisible(true);
        
        selectedProject = project;
        
        ObservableList<Technologies> obsList = FXCollections.observableArrayList(project.getTechnologiesList());
        listView.setItems(obsList);
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
}

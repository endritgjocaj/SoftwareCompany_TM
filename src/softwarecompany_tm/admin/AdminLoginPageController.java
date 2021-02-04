/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softwarecompany_tm.admin;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import controllers.AdminJpaController;
import entities.Admin;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 *
 * @author My-Pc
 */
public class AdminLoginPageController implements Initializable {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("SoftwareCompany_TMPU");
    
    private AdminJpaController jpa = new AdminJpaController(emf);
    private List<Admin> list = new ArrayList<>();
    
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private JFXTextField usernameField;
    @FXML
    private JFXPasswordField passwordField;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        list = jpa.findAdminEntities();
    }

    @FXML
    private void onLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        try {
            Admin admin = jpa.findByUsernameAndPassword(username, password);
            
            Parent parent = null;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
            parent = loader.load();

            DashboardController dashboardController = (DashboardController)loader.getController();
            dashboardController.getAdmin(admin);
            
            Scene scene = new Scene(parent);
            Stage stage = (Stage)anchorPane.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
        catch(Exception ex) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setHeaderText("The email and password you entered did not match our records.\n\n");
            a.show();
            
            ex.getStackTrace();
        }
    }

    @FXML
    private void onUserLogin(ActionEvent event) {
        Parent parent = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/softwarecompany_tm/user/UserLoginPage.fxml"));
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

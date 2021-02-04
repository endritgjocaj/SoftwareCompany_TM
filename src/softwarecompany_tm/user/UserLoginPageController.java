/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softwarecompany_tm.user;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import controllers.UserJpaController;
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
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * FXML Controller class
 *
 * @author My-Pc
 */
public class UserLoginPageController implements Initializable {
    
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("SoftwareCompany_TMPU");
    
    private UserJpaController jpa = new UserJpaController(emf);
    private List<User> list = new ArrayList<>();
    
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private JFXTextField usernameField;
    @FXML
    private JFXPasswordField passwordField;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //To do
    }

    @FXML
    private void onAdminLogin(ActionEvent event) {
        Parent parent = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/softwarecompany_tm/admin/AdminLoginPage.fxml"));
        try {
            parent = loader.load();
            Scene scene = new Scene(parent);
            Stage stage = (Stage)anchorPane.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException ex) {
            
        }
    }

    @FXML
    private void onLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        
        try {
            User user = jpa.findByUsernameAndPassword(username, password);

            Parent parent = null;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
            parent = loader.load();
            
            DashboardController dashboardController = (DashboardController)loader.getController();
            dashboardController.getUser(user);
            
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
}

















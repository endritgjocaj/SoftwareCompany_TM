/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softwarecompany_tm.user;

import com.jfoenix.controls.JFXPasswordField;
import controllers.UserJpaController;
import entities.User;
import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * FXML Controller class
 *
 * @author My-Pc
 */
public class ChangePasswordController implements Initializable {
    
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("SoftwareCompany_TMPU");
    
    private UserJpaController jpa = new UserJpaController(emf);
    private List<User> list = new ArrayList<>();
    
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private JFXPasswordField currentPasswordField;
    @FXML
    private JFXPasswordField newPasswordField;
    @FXML
    private JFXPasswordField confirmPasswordField;
    
    private static ScrollPane scrollPane;
    
    private static User myUser;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public void getPane(ScrollPane scrPane){
        scrollPane = scrPane;
    }
    
    void getUser(User user) {
        myUser = user;
    }
    
    public void clearFields(){
        currentPasswordField.setText(null);
        newPasswordField.setText(null);
        confirmPasswordField.setText(null);
    }
    
    @FXML
    private void onSubmit(ActionEvent event) {
        try {
            String currentPassword = currentPasswordField.getText();
            String newPassword = newPasswordField.getText();
            String confirmPassword = confirmPasswordField.getText();
            
            User user = jpa.findUser(myUser.getId());
            
            String errorMessage = "";
            
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            
            byte[] hashCurrentPsw = digest.digest(currentPassword.getBytes("UTF-8"));
            String currentPsw = Base64.getEncoder().encodeToString(hashCurrentPsw);
            
            byte[] hashNewPsw = digest.digest(newPassword.getBytes("UTF-8"));
            String newPsw = Base64.getEncoder().encodeToString(hashNewPsw);
            
            if(!checkNullAndEmpty(currentPassword) || !checkNullAndEmpty(newPassword) || !checkNullAndEmpty(confirmPassword)){
                if(!checkNullAndEmpty(currentPassword)){
                    errorMessage += "Current password field is empty!\n\n";
                }
                
                if(!checkNullAndEmpty(newPassword)){
                    errorMessage += "New password field is empty\n\n";
                }
                
                if(!checkNullAndEmpty(confirmPassword)){
                    errorMessage += "Confirm password field is empty!\n\n";
                }
            }
            
            else if (checkNullAndEmpty(currentPassword) && checkNullAndEmpty(newPassword) && checkNullAndEmpty(confirmPassword)){
                if(!user.getPassword().equals(currentPsw)){
                    errorMessage += "This is not your current password!\n\n";
                }
                
                else {
                    if(currentPsw.equals(newPsw)){
                        errorMessage += "Your new password cannot be the same as your current password!\n\n";
                    }
                    if(!newPassword.equals(confirmPassword)){
                        errorMessage += "New password and the confirmation password don't match!\n\n";
                    }
                }
            }    
            
            user.setPassword(newPsw);
            
            Alert a = new Alert(Alert.AlertType.ERROR);
            if(!errorMessage.equals("")){
                a.setContentText(errorMessage);
            }
            else{
                jpa.edit(user);
                a.setContentText("You changed your password correctly!\n\n");
            }
            a.show();
            
        } catch (Exception ex) {
            
        }
            clearFields();
    }
    
    @FXML
    private void getBackImage(MouseEvent event) {
        Parent parent = null;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("UserAccount.fxml"));
        
        try {
            parent = loader.load();
            UserAccountController userAccountController = (UserAccountController)loader.getController();
            userAccountController.getUser(myUser);
        } 
        catch (IOException ex) {
            
        }
        scrollPane.setContent(parent);
    }
    
    public boolean checkNullAndEmpty(String s){
        return s != null && !s.trim().isEmpty();
    }
    
}








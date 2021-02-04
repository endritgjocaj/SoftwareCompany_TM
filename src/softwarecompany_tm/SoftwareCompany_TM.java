/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package softwarecompany_tm;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author My-Pc
 */
public class SoftwareCompany_TM extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/softwarecompany_tm/user/UserLoginPage.fxml"));      
                                                                                    
                                                                                    //Login with admin:
                                                                                    //username: Endrit123
                                                                                    //password: 123456
                                                                                    
                                                                                    //Login as employee/user:
                                                                                    //username: firstname+id     eg. Dua121
                                                                                    //password: 123456
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        root.requestFocus();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
            launch(args);
    }
}

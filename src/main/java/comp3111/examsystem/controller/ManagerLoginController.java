package comp3111.examsystem.controller;

import comp3111.examsystem.Main;
import comp3111.examsystem.database.DatabaseConnection;
import comp3111.examsystem.model.ManagerControllerModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ManagerLoginController implements Initializable {
    @FXML
    private TextField usernameTxt;
    @FXML
    private PasswordField passwordTxt;
    @FXML
    private Label errorMessageLbl;

    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void login(ActionEvent e) {

        ManagerControllerModel dataModel = new ManagerControllerModel();

        String username = usernameTxt.getText();
        String password = passwordTxt.getText();

        String sql = "SELECT * FROM manager WHERE username = ? AND password = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            //match if manager exist in db
            if (rs.next()) {
                FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("ManagerMainUI.fxml"));
                fxmlLoader.setControllerFactory(param -> {
                    ManagerMainController controller = new ManagerMainController();
                    controller.setDataModel(dataModel);
                    return controller;
                });

                Stage stage = new Stage();
                stage.setTitle("Hi " + usernameTxt.getText() + ", Welcome to HKUST Examination System");
                stage.setScene(new Scene(fxmlLoader.load()));
                stage.show();

                ((Stage) ((Button) e.getSource()).getScene().getWindow()).close();
                errorMessageLbl.setVisible(false);
            } else {
                errorMessageLbl.setVisible(true);
            }
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
            errorMessageLbl.setVisible(true); // Show the error message on exception
        }
    }
}

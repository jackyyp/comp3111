package comp3111.examsystem;

import comp3111.examsystem.model.StudentControllerModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The main class of the exam system.
 * This class is the entry point of the application.
 * It is responsible for starting the application and loading the login UI.
 */
public class Main extends Application {
	/**
	 * Starts the application and loads the login UI.
	 *
	 * @param primaryStage the primary stage of the application
	 */
	public void start(Stage primaryStage) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("LoginUI.fxml"));
			Scene scene = new Scene(fxmlLoader.load(), 640, 480);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * The entry point of the application.
	 *
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}

}
package main;

import DAO.UserDAO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import DAO.JDBC;
import controller.LoginController;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * The Main class for the application.
 */
public class Main extends Application {

    /**
     * The start method that launches application. We open the connection to the database, initialize the UserDAO,
     * load the Login.fxml, we get the controller for the login, set the UserDAO, set the scene and stage, and close the connection upon exit.
     *
     * @param primaryStage The primary stage for the application.
     * @throws Exception If there is an error during application startup.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        JDBC.openConnection();

        UserDAO userDAO = new UserDAO(JDBC.connection);

        Locale locale = Locale.getDefault();
        ResourceBundle bundle = ResourceBundle.getBundle("bundles.Login", locale);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Login.fxml"));
        loader.setResources(bundle);
        Parent root = loader.load();

        LoginController loginController = loader.getController();
        loginController.setUserDAO(userDAO);

        Scene scene = new Scene(root, 400, 300);

        primaryStage.setTitle(bundle.getString("title"));
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> JDBC.closeConnection());
    }

    /**
     * The main method that launches the application.
     *
     * @param args The command-line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
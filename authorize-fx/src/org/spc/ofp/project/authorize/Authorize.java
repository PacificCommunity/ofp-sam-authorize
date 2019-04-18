/***********************************************************************
 *  Copyright - Secretariat of the Pacific Community                   *
 *  Droit de copie - Secrétariat Général de la Communauté du Pacifique *
 *  http://www.spc.int/                                                *
 ***********************************************************************/
package org.spc.ofp.project.authorize;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.spc.ofp.project.authorize.scene.main.MainUIController;

/**
 * This pet project is used to digitally sign main OFP projects.
 * @author Fabrice Bouyé (fabriceb@spc.int)c
 */
public final class Authorize extends Application {

    /**
     * Reference to the UI controller.
     */
    private MainUIController uiController = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public void start(final Stage primaryStage) throws Exception {
        final var fxmlURL = getClass().getResource("scene/main/MainUI.fxml");
        final var fxmlLoader = new FXMLLoader(fxmlURL, I18N.INSTANCE.getResourceBundle());
        final var ui = (Node) fxmlLoader.load();
        uiController = (MainUIController) fxmlLoader.getController();
        //
        final var root = new StackPane();
        root.setId("root"); // NOI18N.
        root.getChildren().setAll(ui);
        final var scene = new Scene(root);
        final var cssURL = getClass().getResource("Authorize.css"); // NOI18N.
        scene.getStylesheets().add(cssURL.toExternalForm());
        primaryStage.setTitle(I18N.INSTANCE.getString("app.title")); // NOI18N.
        primaryStage.setWidth(900);
        primaryStage.setHeight(500);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Main entry point.
     * @param args the command line arguments
     */
    public static void main(final String... args) {
        launch(args);
    }
}

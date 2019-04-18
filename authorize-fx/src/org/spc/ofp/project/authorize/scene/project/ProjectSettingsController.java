/***********************************************************************
 *  Copyright - Secretariat of the Pacific Community                   *
 *  Droit de copie - Secrétariat Général de la Communauté du Pacifique *
 *  http://www.spc.int/                                                *
 ***********************************************************************/
package org.spc.ofp.project.authorize.scene.project;

import java.io.File;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import org.spc.ofp.project.authorize.signature.jsign.JSignParametersBuilder;
import org.spc.ofp.project.authorize.signature.jarsigner.JarSignerParametersBuilder;

/**
 * The controller for the project settings UI.
 * @author Fabrice Bouyé (fabriceb@spc.int)
 */
public final class ProjectSettingsController implements Initializable {

    @FXML
    private AnchorPane rootPane;
    @FXML
    private TextField pathField;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(final URL url, final ResourceBundle resources) {

    }

    /**
     * Load new settings in the UI.
     * @param properties Source properties.
     * @param project project type.
     */
    public void load(final Properties properties, final String project) {
        final boolean invalid = properties == null || project == null;
        final var path = (invalid) ? null : properties.getProperty(project + ".path.to.sign"); // NOI18N.
        pathField.setText(path);
    }

    /**
     * Apply parameters to JarSigner.    
     * @param parametersBuilder The parameters builder.
     */
    public void apply(final JarSignerParametersBuilder parametersBuilder) {
        if (parametersBuilder == null) {
            return;
        }
        parametersBuilder.pathToSign(pathField.getText());
    }

    /**
     * Apply parameters to JSign.    
     * @param parametersBuilder The parameters builder.
     */
    public void apply(final JSignParametersBuilder parametersBuilder) {
        if (parametersBuilder == null) {
            return;
        }
        parametersBuilder.filename(pathField.getText());
    }

    /**
     * Called whenever the path button is clicked.
     */
    @FXML
    private void handlePathButton() {
        final var initialDirectory = new File(pathField.getText());
        final var dialog = new DirectoryChooser();
        dialog.setInitialDirectory(initialDirectory);
        final var directory = dialog.showDialog(rootPane.getScene().getWindow());
        if (directory != null) {
            pathField.setText(directory.getAbsolutePath());
        }
    }
}

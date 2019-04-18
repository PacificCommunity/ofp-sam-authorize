/***********************************************************************
 *  Copyright - Secretariat of the Pacific Community                   *
 *  Droit de copie - Secrétariat Général de la Communauté du Pacifique *
 *  http://www.spc.int/                                                *
 ***********************************************************************/
package org.spc.ofp.project.authorize.scene.keystore;

import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import org.spc.ofp.project.authorize.signature.jsign.JSignParametersBuilder;
import org.spc.ofp.project.authorize.signature.jarsigner.JarSignerParametersBuilder;

/**
 * The controller for the project settings UI.
 * @author Fabrice Bouyé (fabriceb@spc.int)
 */
public final class KeyStoreSettingsController implements Initializable {

    @FXML
    private AnchorPane rootPane;
    @FXML
    private TextField pathField;
    @FXML
    private PasswordField storePassField;
    @FXML
    private TextField aliasField;
    @FXML
    private PasswordField keyPassField;
    @FXML
    private CheckBox samePassCheck;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        samePassCheck.selectedProperty().addListener(samePassChangeListener);
    }

    /**
     * Called whenever the user checks {@code samePassCheck}.
     */
    private final ChangeListener<Boolean> samePassChangeListener = (observable, oldValue, newValue) -> {
        if (newValue) {
            keyPassField.textProperty().bind(storePassField.textProperty());
            keyPassField.setEditable(false);
            keyPassField.setDisable(true);
        } else {
            keyPassField.textProperty().unbind();
            keyPassField.setEditable(true);
            keyPassField.setDisable(false);
        }
    };

    /**
     * Load new settings in the UI.
     * @param properties Source properties.
     * @param project project type.
     */
    public void load(final Properties properties, final String project) {
        final boolean invalid = properties == null || project == null;
        final var path = (invalid) ? null : properties.getProperty(project + ".key.store"); // NOI18N.
        pathField.setText(path);
        final var alias = (invalid) ? null : properties.getProperty(project + ".alias"); // NOI18N.
        aliasField.setText(alias);
        final var keyPass = (invalid) ? null : properties.getProperty(project + ".key.pass"); // NOI18N.
        final var storePass = (invalid) ? null : properties.getProperty(project + ".store.password"); // NOI18N.
        final boolean samePassword = Objects.equals(storePass, keyPass);
        samePassCheck.setSelected(samePassword);
        storePassField.setText(storePass);
        if (!samePassword) {
            keyPassField.setText(keyPass);
        }
    }

    /**
     * Apply parameters to JarSigner.    
     * @param parametersBuilder The parameters builder.
     */
    public void apply(final JarSignerParametersBuilder parametersBuilder) {
        if (parametersBuilder == null) {
            return;
        }
        parametersBuilder.keyStore(pathField.getText())
                .storePassword(storePassField.getText())
                .alias(aliasField.getText())
                .keyPass(keyPassField.getText());
    }

    /**
     * Apply parameters to JSign.    
     * @param parametersBuilder The parameters builder.
     */
    public void apply(final JSignParametersBuilder parametersBuilder) {
        if (parametersBuilder == null) {
            return;
        }
        parametersBuilder.keystoreFilename(pathField.getText())
                .password(storePassField.getText())
                .alias(aliasField.getText())
                .keypass(keyPassField.getText());
    }

    /**
     * Called when the path button is clicked.
     */
    @FXML
    private void handlePathButton() {
        final var initialFile = new File(pathField.getText());
        final var dialog = new FileChooser();
        final var initialDirectory = initialFile.getParentFile();
        dialog.setInitialDirectory(initialDirectory);
        dialog.setInitialFileName(initialFile.getName());
        final var file = dialog.showOpenDialog(rootPane.getScene().getWindow());
        if (file != null) {
            pathField.setText(file.getAbsolutePath());
        }
    }
}

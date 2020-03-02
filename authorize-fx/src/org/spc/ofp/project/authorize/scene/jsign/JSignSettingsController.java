/***********************************************************************
 *  Copyright - Secretariat of the Pacific Community                   *
 *  Droit de copie - Secrétariat Général de la Communauté du Pacifique *
 *  http://www.spc.int/                                                *
 ***********************************************************************/
package org.spc.ofp.project.authorize.scene.jsign;

import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import org.spc.ofp.project.authorize.signature.jsign.JSignParametersBuilder;

/**
 * The controller for the project settings UI.
 * @author Fabrice Bouyé (fabriceb@spc.int)
 */
public final class JSignSettingsController implements Initializable {

    @FXML
    private TextField programNameField;
    @FXML
    private TextField programUrlField;
    @FXML
    private TextField programEmailField;

    /**
     * Creates a new instance.
     */
    public JSignSettingsController() {
    }

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
        final var programName = (invalid) ? null : properties.getProperty(project + ".program.name"); // NOI18N.
        programNameField.setText(programName);
        final var programUrl = (invalid) ? null : properties.getProperty(project + ".program.url"); // NOI18N.
        programUrlField.setText(programUrl);
        final var programEmail = (invalid) ? null : properties.getProperty(project + ".program.email"); // NOI18N.
        programEmailField.setText(programEmail);
    }

    /**
     * Apply parameters to JSign.    
     * @param parametersBuilder The parameters builder.
     */
    public void apply(final JSignParametersBuilder parametersBuilder) {
        if (parametersBuilder == null) {
            return;
        }
        parametersBuilder.programName(programNameField.getText())
                .programURL(programUrlField.getText())
                .programEmail(programEmailField.getText());
    }
}

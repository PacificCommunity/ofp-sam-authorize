/***********************************************************************
 *  Copyright - Secretariat of the Pacific Community                   *
 *  Droit de copie - Secrétariat Général de la Communauté du Pacifique *
 *  http://www.spc.int/                                                *
 ***********************************************************************/
package org.spc.ofp.project.authorize.scene.main;

import org.spc.ofp.project.authorize.signature.SignMethod;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.spc.ofp.project.authorize.Authorize;
import org.spc.ofp.project.authorize.I18N;
import org.spc.ofp.project.authorize.scene.jsign.JSignSettingsController;
import org.spc.ofp.project.authorize.scene.keystore.KeyStoreSettingsController;
import org.spc.ofp.project.authorize.scene.project.ProjectSettingsController;
import org.spc.ofp.project.authorize.task.jsign.JSignSignTask;
import org.spc.ofp.project.authorize.signature.jsign.JSignParametersBuilder;
import org.spc.ofp.project.authorize.task.jarsigner.JarSignerSignTask;
import org.spc.ofp.project.authorize.signature.jarsigner.JarSignerParametersBuilder;

/**
 * The controller for the project main UI.
 * @author Fabrice Bouyé (fabriceb@spc.int)
 */
public final class MainUIController implements Initializable {

    private static final Logger LOGGER = Logger.getLogger(Authorize.class.getName());
    private static final String IMPLICIT_SIGN_METHOD = "jarsigner"; // NOI18N.
    public static final String DEFAULT_CONFIG_FILE = "settings.properties"; // NOI18N.
    private static final Preferences PREFS = Preferences.userNodeForPackage(Authorize.class);

    @FXML
    private BorderPane rootPane;
    @FXML
    private ListView<String> projectList;
    @FXML
    private Button signButton;
    @FXML
    private VBox centerVBox;
    @FXML
    private ProjectSettingsController projectSettingsController;
    @FXML
    private KeyStoreSettingsController keyStoreSettingsController;
    @FXML
    private ProgressBar progressBar;

    /**
     * Contains app settings.
     */
    private final Properties properties = new Properties();

    /**
     * Creates a new instance.
     */
    public MainUIController() throws Exception {
        projectProperty().addListener(projectChangeListener);
        loadProjectsAsync();
    }

    /**
     * Reference to the resource bundle used to load this FXML.
     * <br>Will be reused when loading other panes.
     */
    private ResourceBundle resources;

    /**
     * {@inheritDoc}
     */
    @Override
    public void initialize(final URL url, final ResourceBundle resources) {
        this.resources = resources;
        //
        signButton.disableProperty().bind(projectList.getSelectionModel().selectedItemProperty().isNull().or(signingProperty()));
        //
        projectList.itemsProperty().bind(projectsProperty());
        projectProperty().bind(projectList.getSelectionModel().selectedItemProperty());
    }

    /**
     * Update UI when selected project changes.
     */
    private final ChangeListener<String> projectChangeListener = (observable, oldValue, newValue) -> {
        updateUIForProject();
    };

    /**
     * Reference to the optional JSign controller.
     */
    private JSignSettingsController jsignSettingsController = null;

    /**
     * Update UI when a new project has been selected.
     */
    private void updateUIForProject() {
        final var currentProject = getProject();
        projectSettingsController.load(properties, currentProject);
        keyStoreSettingsController.load(properties, currentProject);
        centerVBox.getChildren().remove(4, centerVBox.getChildren().size());
        jsignSettingsController = null;
        if (currentProject == null) {
            return;
        }
        final var signMethodStr = properties.getProperty(String.format("%s.sign.method", currentProject), IMPLICIT_SIGN_METHOD); // NOI18N.
        final var signMethod = SignMethod.parse(signMethodStr);
        switch (signMethod) {
            case JSIGN: {
                try {
                    final var jsignLabel = new Label(resources.getString("windows.exec.sign.label")); // NOI18N.
                    jsignLabel.getStyleClass().add("h1");
                    centerVBox.getChildren().add(jsignLabel);
                    // Load JSign pane.
                    final var fxmlURL = Authorize.class.getResource("scene/jsign/JSignSettings.fxml"); // NOI18N.
                    final var fxmlLoader = new FXMLLoader(fxmlURL, resources);
                    final var node = (Node) fxmlLoader.load();
                    jsignSettingsController = (JSignSettingsController) fxmlLoader.getController();
                    centerVBox.getChildren().add(node);
                    jsignSettingsController.load(properties, currentProject);
                } catch (Throwable ex) {
                    LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
                }
                break;
            }
            default:
        }
    }

    /**
     * Load projects asynchronously.
     */
    private void loadProjectsAsync() {
        final var loadService = new Service<String[]>() {
            @Override
            protected Task<String[]> createTask() {
                return new Task<String[]>() {
                    @Override
                    protected String[] call() throws Exception {
                        final var settingsPath = Paths.get(DEFAULT_CONFIG_FILE);
                        var result = new String[0];
                        if (Files.exists(settingsPath) && Files.isReadable(settingsPath)) {
                            try (final var input = Files.newInputStream(settingsPath)) { // NOI18N.
                                properties.load(input);
                            }
                            result = properties.getProperty("projects", "").split("\\s+"); // NOI18N.
                        }
                        return result;
                    }
                };
            }
        };
        loadService.setOnSucceeded(event -> {
            final var message = I18N.INSTANCE.getString("task.load.success"); // NOI18N.
            LOGGER.log(Level.INFO, message); // NOI18N.
            runningServices.remove(loadService);
            final var allProjects = (String[]) event.getSource().getValue();
            final var allProjectsList = FXCollections.observableArrayList(allProjects);
            Platform.runLater(() -> MainUIController.this.projects.set(allProjectsList));
        });
        loadService.setOnCancelled(event -> {
            final var message = I18N.INSTANCE.getString("task.load.cancel"); // NOI18N.
            LOGGER.log(Level.WARNING, message);
            runningServices.remove(loadService);
        });
        loadService.setOnFailed(event -> {
            final var ex = event.getSource().getException();
            final var message = I18N.INSTANCE.getString("task.load.fail"); // NOI18N.
            LOGGER.log(Level.INFO, message, ex.getMessage());
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            runningServices.remove(loadService);
        });
        runningServices.add(loadService);
        loadService.start();
    }

    /**
     * Sign selected project.
     */
    public void doSignProject() {
        final var currentProject = getProject();
        if (currentProject == null) {
            return;
        }
        final var signMethodStr = properties.getProperty(String.format("%s.sign.method", currentProject), IMPLICIT_SIGN_METHOD); // NOI18N.
        final var signMethod = SignMethod.parse(signMethodStr);
        switch (signMethod) {
            case JSIGN: {
                signWithJSignAsync();
                break;
            }
            case JARSIGNER: {
                signWithJarSignerAsync();
                break;
            }
            default:
        }
    }

    private final List<Service> runningServices = new LinkedList<>();

    /**
     * Sign with JAR signer asynchronously.
     */
    private void signWithJarSignerAsync() {
        final var signService = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                final var parametersBuilder = JarSignerParametersBuilder.create()
                        .debugDirectoryWalk(Boolean.parseBoolean(properties.getProperty("debug.directory.walk"))) // NOI18N.
                        .debugSignature(Boolean.parseBoolean(properties.getProperty("debug.signature"))) // NOI18N.
                        .debugCommand(Boolean.parseBoolean(properties.getProperty("debug.command"))) // NOI18N.
                        .javaHome(properties.getProperty("java.home")) // NOI18N.
                        .jarSignerExec(properties.getProperty("jar.signer")) // NOI18N.
                        .useProxy(Boolean.parseBoolean(properties.getProperty("use.proxy"))) // NOI18N.
                        .proxyHost(properties.getProperty("proxy.host")) // NOI18N.
                        .proxyPort(properties.getProperty("proxy.port")) // NOI18N.
                        .logger(LOGGER);
                projectSettingsController.apply(parametersBuilder);
                keyStoreSettingsController.apply(parametersBuilder);
                final var parameters = parametersBuilder.build();
                final var task = new JarSignerSignTask(parameters);
                return task;
            }
        };
        final ChangeListener<String> messageListener = (observable, oldValue, newValue) -> LOGGER.log(Level.INFO, newValue);
        signService.messageProperty().addListener(messageListener);
        signService.setOnSucceeded(event -> {
            final var message = I18N.INSTANCE.getString("task.sign.success"); // NOI18N.
            LOGGER.log(Level.INFO, message); // NOI18N.
            runningServices.remove(signService);
            signService.messageProperty().removeListener(messageListener);
            progressBar.progressProperty().unbind();
            displaySuccessMessage(I18N.INSTANCE.getString("app.title"), I18N.INSTANCE.getString("digital.signature.label"), message);
            signing.set(false);
        });
        signService.setOnCancelled(event -> {
            final var message = I18N.INSTANCE.getString("task.sign.cancel"); // NOI18N.
            LOGGER.log(Level.WARNING, message);
            runningServices.remove(signService);
            signService.messageProperty().removeListener(messageListener);
            progressBar.progressProperty().unbind();
            signing.set(false);
        });
        signService.setOnFailed(event -> {
            final var ex = event.getSource().getException();
            final var message = I18N.INSTANCE.getString("task.sign.fail"); // NOI18N.
            LOGGER.log(Level.INFO, message, ex.getMessage());
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            runningServices.remove(signService);
            signService.messageProperty().removeListener(messageListener);
            progressBar.progressProperty().unbind();
            signing.set(false);
        });
        progressBar.progressProperty().bind(signService.progressProperty());
        signing.set(true);
        runningServices.add(signService);
        signService.start();
    }

    /**
     * Sign with JSign asynchronously.
     */
    private void signWithJSignAsync() {
        final var signService = new Service<Void>() {
            @Override
            protected Task<Void> createTask() {
                final var parametersBuilder = JSignParametersBuilder.create()
                        .debugDirectoryWalk(Boolean.parseBoolean(properties.getProperty("debug.directory.walk"))) // NOI18N.
                        .debugSignature(Boolean.parseBoolean(properties.getProperty("debug.signature"))) // NOI18N.
                        .debugCommand(Boolean.parseBoolean(properties.getProperty("debug.command"))) // NOI18N.
                        .useProxy(Boolean.parseBoolean(properties.getProperty("use.proxy"))) // NOI18N.
                        .proxyHost(properties.getProperty("proxy.host")) // NOI18N.
                        .proxyPort(properties.getProperty("proxy.port")) // NOI18N.                       
                        .logger(LOGGER);
                projectSettingsController.apply(parametersBuilder);
                keyStoreSettingsController.apply(parametersBuilder);
                jsignSettingsController.apply(parametersBuilder);
                final var parameters = parametersBuilder.build();
                final var task = new JSignSignTask(parameters);
                return task;
            }
        };
        final ChangeListener<String> messageListener = (observable, oldValue, newValue) -> LOGGER.log(Level.INFO, newValue);
        signService.messageProperty().addListener(messageListener);
        signService.setOnSucceeded(event -> {
            final var message = I18N.INSTANCE.getString("task.sign.success"); // NOI18N.
            LOGGER.log(Level.INFO, message); // NOI18N.
            runningServices.remove(signService);
            signService.messageProperty().removeListener(messageListener);
            progressBar.progressProperty().unbind();
            displaySuccessMessage(I18N.INSTANCE.getString("app.title"), I18N.INSTANCE.getString("digital.signature.label"), message);
            signing.set(false);
        });
        signService.setOnCancelled(event -> {
            final var message = I18N.INSTANCE.getString("task.sign.cancel"); // NOI18N.
            LOGGER.log(Level.WARNING, message);
            runningServices.remove(signService);
            signService.messageProperty().removeListener(messageListener);
            progressBar.progressProperty().unbind();
            signing.set(false);
        });
        signService.setOnFailed(event -> {
            final var ex = event.getSource().getException();
            final var message = I18N.INSTANCE.getString("task.sign.fail"); // NOI18N.
            LOGGER.log(Level.INFO, message, ex.getMessage());
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            runningServices.remove(signService);
            signService.messageProperty().removeListener(messageListener);
            progressBar.progressProperty().unbind();
            signing.set(false);
        });
        progressBar.progressProperty().bind(signService.progressProperty());
        signing.set(true);
        runningServices.add(signService);
        signService.start();
    }

    private void displaySuccessMessage(final String title, final String header, final String message) {
        if (PREFS.getBoolean("show.success.dialog", true)) { // NOI18N.
            final var alert = new Alert(AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(header);
            final var textFlow = new TextFlow(new Text(message));
            final var showAgainCheckBox = new CheckBox(I18N.INSTANCE.getString("dialog.show.again")); // NOI18N.
            showAgainCheckBox.setSelected(true);
            final var content = new VBox(textFlow, showAgainCheckBox);
            content.getStyleClass().add("vbox-alert"); // NOI18N.
            alert.getDialogPane().setContent(content);
            content.getStylesheets().setAll(rootPane.getScene().getStylesheets());
            alert.showAndWait();
            PREFS.putBoolean("show.success.dialog", showAgainCheckBox.isSelected()); // NOI18N.
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    private final ReadOnlyListWrapper<String> projects = new ReadOnlyListWrapper<>(this, "projects"); // NOI18N.

    public final ObservableList<String> getProjects() {
        return FXCollections.unmodifiableObservableList(projects);
    }

    public final ReadOnlyListProperty<String> projectsProperty() {
        return projects.getReadOnlyProperty();
    }
    private final StringProperty project = new SimpleStringProperty(this, "project", null); // NOI18N.

    public final String getProject() {
        return project.get();
    }

    public final void setProject(final String value) {
        project.set(value);
    }

    public final StringProperty projectProperty() {
        return project;
    }
    private final ReadOnlyBooleanWrapper signing = new ReadOnlyBooleanWrapper(this, "signing", false);

    public final boolean isSigning() {
        return signing.get();
    }

    public final ReadOnlyBooleanProperty signingProperty() {
        return signing.getReadOnlyProperty();
    }

    /**
     * Called whenever the sign button is clicked.
     */
    @FXML
    private void handleSignButton() {
        try {
            doSignProject();
        } catch (Throwable ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}

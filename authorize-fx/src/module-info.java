/***********************************************************************
 *  Copyright - Secretariat of the Pacific Community                   *
 *  Droit de copie - Secrétariat Général de la Communauté du Pacifique *
 *  http://www.spc.int/                                                *
 ***********************************************************************/
module authorize.fx {
    exports org.spc.ofp.project.authorize;
    opens org.spc.ofp.project.authorize.scene.jsign to javafx.fxml;
    opens org.spc.ofp.project.authorize.scene.keystore to javafx.fxml;
    opens org.spc.ofp.project.authorize.scene.main to javafx.fxml;
    opens org.spc.ofp.project.authorize.scene.project to javafx.fxml;
    requires java.logging;
    requires java.prefs;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires authorize.core;
    requires jsign;
}

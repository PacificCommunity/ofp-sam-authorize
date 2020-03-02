/***********************************************************************
 *  Copyright - Secretariat of the Pacific Community                   *
 *  Droit de copie - Secrétariat Général de la Communauté du Pacifique *
 *  http://www.spc.int/                                                *
 ***********************************************************************/
package org.spc.ofp.project.authorize.signature.jsign;

import java.util.logging.Logger;

/**
 * The parameters to the sign signature using JSign.
 * @author Fabrice Bouyé (fabriceb@spc.int)
 */
public class JSignParameters {

    String keystoreFilename = "";
    String filename = "";
    String alias = "";
    String password = "";
    String keypass = "";
    String programName = "";
    String programURL = "";
    String programEmail = "";
    boolean useProxy = false;
    String proxyHost = "";
    String proxyPort = "";
    boolean useTimeStamp = true;
    String timeStampHost = JSignUtils.DEFAULT_TIME_STAMP_HOST;
    // Debug.
    boolean debugDirectoryWalk = false;
    boolean debugSignature = false;
    boolean debugCommand = true;
    Logger logger = Logger.getGlobal();
}

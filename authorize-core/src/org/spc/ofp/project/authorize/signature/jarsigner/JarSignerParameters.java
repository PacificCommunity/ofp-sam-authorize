/***********************************************************************
 *  Copyright - Secretariat of the Pacific Community                   *
 *  Droit de copie - Secrétariat Général de la Communauté du Pacifique *
 *  http://www.spc.int/                                                *
 ***********************************************************************/
package org.spc.ofp.project.authorize.signature.jarsigner;

import java.util.logging.Logger;

/**
 * The parameters to the sign task using JarSigner.
 * @author Fabrice Bouyé (fabriceb@spc.int)
 */
public final class JarSignerParameters {

    String javaHome = "";
    String jarSignerExec = JarSignerUtils.DEFAULT_JAR_SIGNER;
    String pathToSign = "";
    String keyStore = "";
    String storePassword = "";
    String alias = "";
    String keyPass = "";
    boolean useProxy = false;
    String proxyHost = "";
    String proxyPort = "";
    boolean useTimeStamp = true;
    String timeStampHost = JarSignerUtils.DEFAULT_TIME_STAMP_HOST;
    // Debug.
    boolean debugDirectoryWalk = false;
    boolean debugSignature = false;
    boolean debugCommand = true;
    //
    Logger logger = Logger.getGlobal();
}

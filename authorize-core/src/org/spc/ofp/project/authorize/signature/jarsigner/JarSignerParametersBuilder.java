/***********************************************************************
 *  Copyright - Secretariat of the Pacific Community                   *
 *  Droit de copie - Secrétariat Général de la Communauté du Pacifique *
 *  http://www.spc.int/                                                *
 ***********************************************************************/
package org.spc.ofp.project.authorize.signature.jarsigner;

import java.util.logging.Logger;
import org.spc.ofp.project.authorize.signature.jsign.JSignParametersBuilder;

/**
 * Builder class for the parameters of the sign task.
 * @author Fabrice Bouyé (fabriceb@spc.int)
 */
public final class JarSignerParametersBuilder {

    private final JarSignerParameters delegated = new JarSignerParameters();

    /**
     * Hidden constructor.
     */
    private JarSignerParametersBuilder() {
    }

    ////////////////////////////////////////////////////////////////////////////
    public static JarSignerParametersBuilder create() {
        return new JarSignerParametersBuilder();
    }

    ////////////////////////////////////////////////////////////////////////////
    public JarSignerParameters build() {
        final var result = new JarSignerParameters();
        result.javaHome = delegated.javaHome;
        result.jarSignerExec = delegated.jarSignerExec;
        result.pathToSign = delegated.pathToSign;
        result.keyStore = delegated.keyStore;
        result.storePassword = delegated.storePassword;
        result.alias = delegated.alias;
        result.keyPass = delegated.keyPass;
        result.useProxy = delegated.useProxy;
        result.proxyHost = delegated.proxyHost;
        result.proxyPort = delegated.proxyPort;
        result.useTimeStamp = delegated.useTimeStamp;
        result.timeStampHost = delegated.timeStampHost;
        // Debug.
        result.debugDirectoryWalk = delegated.debugDirectoryWalk;
        result.debugSignature = delegated.debugSignature;
        result.debugCommand = delegated.debugCommand;
        // 
        result.logger = delegated.logger;
        return result;
    }

    ////////////////////////////////////////////////////////////////////////////
    // Builder methods.
    public JarSignerParametersBuilder javaHome(final String value) {
        delegated.javaHome = value;
        return this;
    }

    public JarSignerParametersBuilder jarSignerExec(final String value) {
        delegated.jarSignerExec = value;
        return this;
    }

    public JarSignerParametersBuilder pathToSign(final String value) {
        delegated.pathToSign = value;
        return this;
    }

    public JarSignerParametersBuilder keyStore(final String value) {
        delegated.keyStore = value;
        return this;
    }

    public JarSignerParametersBuilder storePassword(final String value) {
        delegated.storePassword = value;
        return this;
    }

    public JarSignerParametersBuilder alias(final String value) {
        delegated.alias = value;
        return this;
    }

    public JarSignerParametersBuilder keyPass(final String value) {
        delegated.keyPass = value;
        return this;
    }

    public JarSignerParametersBuilder useProxy(final boolean value) {
        delegated.useProxy = value;
        return this;
    }

    public JarSignerParametersBuilder proxyHost(final String value) {
        delegated.proxyHost = value;
        return this;
    }

    public JarSignerParametersBuilder proxyPort(final String value) {
        delegated.proxyPort = value;
        return this;
    }

    public JarSignerParametersBuilder useTimeStamp(final boolean value) {
        delegated.useTimeStamp = value;
        return this;
    }

    public JarSignerParametersBuilder timeStampHost(final String value) {
        delegated.timeStampHost = value;
        return this;
    }
    // Debug.

    public JarSignerParametersBuilder debugDirectoryWalk(final boolean value) {
        delegated.debugDirectoryWalk = value;
        return this;
    }

    public JarSignerParametersBuilder debugSignature(final boolean value) {
        delegated.debugSignature = value;
        return this;
    }

    public JarSignerParametersBuilder debugCommand(final boolean value) {
        delegated.debugCommand = value;
        return this;
    }

    public JarSignerParametersBuilder logger(final Logger value) {
        delegated.logger = (value == null) ? Logger.getGlobal() : value;
        return this;
    }
}

/***********************************************************************
 *  Copyright - Secretariat of the Pacific Community                   *
 *  Droit de copie - Secrétariat Général de la Communauté du Pacifique *
 *  http://www.spc.int/                                                *
 ***********************************************************************/
package org.spc.ofp.project.authorize.signature.jsign;

import java.util.logging.Logger;

/**
 * Builder class for the parameters of the sign process.
 * @author Fabrice Bouyé (fabriceb@spc.int)
 */
public final class JSignParametersBuilder {

    private final JSignParameters delegated = new JSignParameters();

    /**
     * Hidden constructor.
     */
    private JSignParametersBuilder() {
    }

    ////////////////////////////////////////////////////////////////////////////
    /**
     * Creates a new instance of this builder.
     * @return A {@code JSignSignTaskParametersBuilder} instance, never {@code null}.
     */
    public static JSignParametersBuilder create() {
        return new JSignParametersBuilder();
    }

    ////////////////////////////////////////////////////////////////////////////
    /**
     * Build the task parameter from this builder.
     * @return A {@code JSignSignTaskParameters} instance, never {@code null}.
     */
    public JSignParameters build() {
        final var result = new JSignParameters();
        result.keystoreFilename = delegated.keystoreFilename;
        result.filename = delegated.filename;
        result.alias = delegated.alias;
        result.password = delegated.password;
        result.keypass = delegated.keypass;
        result.programName = delegated.programName;
        result.programURL = delegated.programURL;
        result.programEmail = delegated.programEmail;
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
    public JSignParametersBuilder keystoreFilename(final String value) {
        delegated.keystoreFilename = value;
        return this;
    }

    public JSignParametersBuilder filename(final String value) {
        delegated.filename = value;
        return this;
    }

    public JSignParametersBuilder alias(final String value) {
        delegated.alias = value;
        return this;
    }

    public JSignParametersBuilder password(final String value) {
        delegated.password = value;
        return this;
    }

    public JSignParametersBuilder keypass(final String value) {
        delegated.keypass = value;
        return this;
    }

    public JSignParametersBuilder programName(final String value) {
        delegated.programName = value;
        return this;
    }

    public JSignParametersBuilder programURL(final String value) {
        delegated.programURL = value;
        return this;
    }

    public JSignParametersBuilder programEmail(final String value) {
        delegated.programEmail = value;
        return this;
    }
    
    public JSignParametersBuilder useProxy(final boolean value) {
        delegated.useProxy = value;
        return this;
    }

    public JSignParametersBuilder proxyHost(final String value) {
        delegated.proxyHost = value;
        return this;
    }

    public JSignParametersBuilder proxyPort(final String value) {
        delegated.proxyPort = value;
        return this;
    }

    public JSignParametersBuilder useTimeStamp(final boolean value) {
        delegated.useTimeStamp = value;
        return this;
    }

    public JSignParametersBuilder timeStampHost(final String value) {
        delegated.timeStampHost = value;
        return this;
    }
    // Debug.

    public JSignParametersBuilder debugDirectoryWalk(final boolean value) {
        delegated.debugDirectoryWalk = value;
        return this;
    }

    public JSignParametersBuilder debugSignature(final boolean value) {
        delegated.debugSignature = value;
        return this;
    }

    public JSignParametersBuilder debugCommand(final boolean value) {
        delegated.debugCommand = value;
        return this;
    }

    public JSignParametersBuilder logger(final Logger value) {
        delegated.logger = (value == null) ? Logger.getGlobal() : value;
        return this;
    }
}

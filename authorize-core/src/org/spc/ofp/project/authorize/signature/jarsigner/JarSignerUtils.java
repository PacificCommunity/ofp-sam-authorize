/***********************************************************************
 *  Copyright - Secretariat of the Pacific Community                   *
 *  Droit de copie - Secrétariat Général de la Communauté du Pacifique *
 *  http://www.spc.int/                                                *
 ***********************************************************************/
package org.spc.ofp.project.authorize.signature.jarsigner;

import org.spc.ofp.project.authorize.signature.SignUtils;

/**
 * Base definitions for signing with JarSigner.
 * @author Fabrice Bouyé (fabriceb@spc.int)
 */
public enum JarSignerUtils {
    /**
     * Unique instance of this class.
     */
    INSTANCE;

    /**
     * Default timestamp host, value is "{@value}".
     */
    public static final String DEFAULT_TIME_STAMP_HOST = SignUtils.DEFAULT_TIME_STAMP_HOST;
    /**
     * Default timestamp host, value is determined at run time.
     */
    public static final String DEFAULT_JAR_SIGNER = nameDefaultJarSigner();

    /**
     * Generate default jar signer executable name for current platform.
     * @return A {@code String} instance, never {@code null}.
     */
    private static String nameDefaultJarSigner() {
        String result = "jarsigner"; // NOI18N.
        if (System.getProperty("os.name").toLowerCase().contains("windows")) { // NOI18N.
            result += ".exe"; // NOI18N.
        }
        return result;
    }
}

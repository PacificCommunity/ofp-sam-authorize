/***********************************************************************
 *  Copyright - Secretariat of the Pacific Community                   *
 *  Droit de copie - Secrétariat Général de la Communauté du Pacifique *
 *  http://www.spc.int/                                                *
 ***********************************************************************/
package org.spc.ofp.project.authorize.signature;

/**
 * Supported sign methods.
 * @author Fabrice Bouyé (fabriceb@spc.int)
 */
public enum SignMethod {
    /**
     * Use Jsign to sign Windows native executables.
     */
    JSIGN,
    /**
     * Use JarSigner to sign Java JAR files (default sign method).
     */
    JARSIGNER,
    /**
     * Unsupported sign method.
     */
    UNKNOWN;

    /**
     * Parse value and return a sign method.
     * @param value The value.
     * @return A {@code SignMethod} instance, never {@code null}.
     */
    public static SignMethod parse(final String value) {
        var result = UNKNOWN;
        if (value != null) {
            final var name = value.toUpperCase();
            for (final SignMethod method : values()) {
                if (name.equals(method.name())) {
                    result = method;
                    break;
                }
            }
        }
        return result;
    }
}

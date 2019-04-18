/***********************************************************************
 *  Copyright - Secretariat of the Pacific Community                   *
 *  Droit de copie - Secrétariat Général de la Communauté du Pacifique *
 *  http://www.spc.int/                                                *
 ***********************************************************************/
package org.spc.ofp.project.authorize.cli;

/**
 * Error codes.
 * @author Fabrice Bouyé (fabriceb@spc.int)c
 */
public enum ErrorCode {
    /**
     * Unique instance of this class.
     */
    INSTANCE;
    /**
     * Exits the application successfully, equals to {@value}.
     */
    public static final int EXIT_SUCCESS = 0;
    /**
     * Exits the application on failure, equals to {@value}.
     */
    public static final int EXIT_FAILURE = 1;
}

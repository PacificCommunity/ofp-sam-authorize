/***********************************************************************
 *  Copyright - Secretariat of the Pacific Community                   *
 *  Droit de copie - Secrétariat Général de la Communauté du Pacifique *
 *  http://www.spc.int/                                                *
 ***********************************************************************/
package org.spc.ofp.project.authorize.signature.jarsigner;

/**
 * Defines action to execute during code signing.
 * @author Fabrice Bouyé (fabriceb@spc.int)
 */
enum SignAction {

    /**
     * Walk through each individual signing steps without doing anything.
     * Used to debug and to estimate amount of steps required to do the whole signing.
     */
    WALK,
    /**
     * Sign files.
     */
    SIGN;

}

/***********************************************************************
 *  Copyright - Secretariat of the Pacific Community                   *
 *  Droit de copie - Secrétariat Général de la Communauté du Pacifique *
 *  http://www.spc.int/                                                *
 ***********************************************************************/
package org.spc.ofp.project.authorize.signature.jsign;

import org.spc.ofp.project.authorize.signature.SignUtils;

/**
 * Base definitions for signing with JSign.
 * @author Fabrice Bouyé (fabriceb@spc.int)
 */
public enum JSignUtils {
    /**
     * Unique instance of this class.
     */
    INSTANCE;
    
    /**
     * Default timestamp host, value is "{@value}".
     */
    public static final String DEFAULT_TIME_STAMP_HOST = SignUtils.DEFAULT_TIME_STAMP_HOST;
}

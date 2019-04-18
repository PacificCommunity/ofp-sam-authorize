/***********************************************************************
 *  Copyright - Secretariat of the Pacific Community                   *
 *  Droit de copie - Secrétariat Général de la Communauté du Pacifique *
 *  http://www.spc.int/                                                *
 ***********************************************************************/
package org.spc.ofp.project.authorize;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * I18N support.
 * @author Fabrice Bouyé (fabriceb@spc.int)
 */
public enum I18N {
    /**
     * The unique instance of this class.
     */
    INSTANCE;

    /**
     * Delegated resource bundle.
     */
    private final ResourceBundle bundle;

    /**
     * Creates a new instance.
     */
    private I18N() {
        final var packageName = getClass().getPackage().getName();
        final var bundlePath = String.format("%s/%s", packageName.replaceAll("\\.", "/"), "strings"); // NOI18N.
        bundle = ResourceBundle.getBundle(bundlePath);
    }

    /**
     * Gets the delegated resource bundle.
     * @return A {@code ResourceBundle} instance, never {@code null}.
     */
    public ResourceBundle getResourceBundle() {
        return bundle;
    }

    /**
     * Gets string for given key.
     * @param key The key.
     * @return A {@code String} instance, may be {@code null}.
     * <br>If no value is found, the key is returned.
     */
    public String getString(final String key) {
        return getString(key, key);
    }

    /**
     * Gets string for given key.
     * @param key The key.
     * @param defaultValue The default value.
     * @return A {@code String} instance, may be {@code null}.
     * <br>If no value is found, the default value is returned.
     */
    public String getString(final String key, final String defaultValue) {
        var result = defaultValue;
        if (key != null) {
            try {
                result = bundle.getString(key);
            } catch (MissingResourceException ex) {
                final var message = String.format("Missing resource %s", key); // NOI18N.
                Logger.getLogger(Authorize.class.getName()).log(Level.WARNING, message);
            }
        }
        return result;
    }
}

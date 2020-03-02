/***********************************************************************
 *  Copyright - Secretariat of the Pacific Community                   *
 *  Droit de copie - Secrétariat Général de la Communauté du Pacifique *
 *  http://www.spc.int/                                                *
 ***********************************************************************/
package org.spc.ofp.project.authorize.cli;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Scanner;

/**
 * Check certificate validity
 * @author Fabrice Bouyé (fabriceb@spc.int)c
 */
public final class CheckValidity {
    
    /**
    * Program entry point.
    * @param args Arguments from the command line.
    */
    public static void main(final String... args) {
        final var scanner = new Scanner(System.in);
        System.out.print("Store: ");
        final var keyStore = Paths.get(scanner.next());
        System.out.print("Store Password: ");
        final var keyStorePassword = scanner.next();
        new CheckValidity().check(keyStore, keyStorePassword);
    }

    final void check(final Path keyStore, final String keyStorePassword) {
        try {
            final var keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            try (final var input = Files.newInputStream(keyStore)) {
                keystore.load(input, keyStorePassword.toCharArray());
                final var aliases = keystore.aliases();
                while (aliases.hasMoreElements()) {
                    final var alias = aliases.nextElement();
                    if (keystore.getCertificate(alias).getType().equals("X.509")) {
                        System.out.println(alias + " expires " + ((X509Certificate) keystore.getCertificate(alias)).getNotAfter());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

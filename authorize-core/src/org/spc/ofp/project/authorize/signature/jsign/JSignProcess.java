/***********************************************************************
 *  Copyright - Secretariat of the Pacific Community                   *
 *  Droit de copie - Secrétariat Général de la Communauté du Pacifique *
 *  http://www.spc.int/                                                *
 ***********************************************************************/
package org.spc.ofp.project.authorize.signature.jsign;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.util.Objects;
import net.jsign.AuthenticodeSigner;
import net.jsign.pe.PEFile;
import org.spc.ofp.project.authorize.io.IOUtils;
import org.spc.ofp.project.authorize.signature.SignatureProcessBase;
import org.spc.ofp.project.authorize.signature.SignatureProcessMonitor;

/**
 * Handles file signing Windows native exec using <a href="http://ebourg.github.io/jsign/">JSign</a>.
 * @author Fabrice Bouyé (fabriceb@spc.int)
 */
public final class JSignProcess extends SignatureProcessBase {

    /**
     * The parameters object.
     */
    private final JSignParameters parameters;

    /**
     * Creates a new instance.
     * @param parameters The parameters object.
     * @param monitor A monitor interested in the progress of this process, may be {@code null}.
     * @throws NullPointerException If {@code parameters} is {@code null}.
     */
    public JSignProcess(final JSignParameters parameters, final SignatureProcessMonitor monitor) throws NullPointerException {
        super(monitor);
        Objects.requireNonNull(parameters);
        this.parameters = parameters;
    }

    @Override
    public void sign() throws Exception {
        // Nothing to do!
        if (Objects.isNull(parameters.filename) || parameters.filename.isEmpty() || parameters.filename.isBlank()) {
            return;
        }
        updateMessage("Initializing."); // NOI18N.
        final int totalProgress = 5;
        int currentProgress = 0;
        // Create keystore.
        updateMessage("Create key store.");
        final var keyStore = KeyStore.getInstance("JKS"); // NOI18N.
        updateProgress(++currentProgress, totalProgress);
        if (isCancelled()) {
            return;
        }
        // Open & load keystore file.
        updateMessage("Loading key store."); // NOI18N.
        final var keystoreFile = Paths.get(parameters.keystoreFilename);
        try (final var input = Files.newInputStream(keystoreFile)) {
            keyStore.load(input, parameters.password.toCharArray());
        }
        updateProgress(++currentProgress, totalProgress);
        if (isCancelled()) {
            return;
        }
        // Create signer.
        updateMessage("Creating signer."); // NOI18N.
        var signer = new AuthenticodeSigner(keyStore, parameters.alias, parameters.keypass)
                .withProgramName(parameters.programName)
                .withProgramURL(parameters.programURL)
//                .withContactEmail(parameters.programEmail)
                .withTimestamping(parameters.useTimeStamp);
        if (parameters.useTimeStamp) {
            signer = signer.withTimestampingAuthority(parameters.timeStampHost);
        }
        updateProgress(++currentProgress, totalProgress);
        if (isCancelled()) {
            return;
        }
        // Create file to be signed.
        updateMessage("Preparing target file."); // NOI18N.
        final var targetFile = Paths.get(parameters.filename);
        if (!Files.isWritable(targetFile)) {
            IOUtils.INSTANCE.setWritable(targetFile);
        }
        final var pefTargetFile = new PEFile(targetFile.toFile());
        updateProgress(++currentProgress, totalProgress);
        if (isCancelled()) {
            return;
        }
        // Sign file.
        updateMessage("Signing file."); // NOI18N.
        signer.sign(pefTargetFile);
        updateProgress(++currentProgress, totalProgress);
        // @todo Check signature?
        if (isCancelled()) {
            return;
        }
    }
}

/***********************************************************************
 *  Copyright - Secretariat of the Pacific Community                   *
 *  Droit de copie - Secrétariat Général de la Communauté du Pacifique *
 *  http://www.spc.int/                                                *
 ***********************************************************************/
package org.spc.ofp.project.authorize.task.jarsigner;

import java.util.Objects;
import javafx.concurrent.Task;
import org.spc.ofp.project.authorize.signature.SignatureProcessMonitor;
import org.spc.ofp.project.authorize.signature.jarsigner.JarSignerParameters;
import org.spc.ofp.project.authorize.signature.jarsigner.JarSignerProcess;

/**
 * The task that handles JAR file signing using the JDK's JarSigner tool.
 * @author Fabrice Bouyé (fabriceb@spc.int)
 */
public final class JarSignerSignTask extends Task<Void> {

    /**
     * The parameters object.
     */
    private final JarSignerParameters parameters;

    /**
     * Creates a new instance.
     * @param parameters The parameters object.
     * @throws NullPointerException If {@code parameters} is {@code null}.
     */
    public JarSignerSignTask(final JarSignerParameters parameters) throws NullPointerException {
        Objects.requireNonNull(parameters);
        this.parameters = parameters;
    }

    @Override
    protected Void call() throws Exception {
        final var process = new JarSignerProcess(parameters, new SignatureProcessMonitor() {
            @Override
            public void updateProgress(final double progress, final double totalProgess) {
                JarSignerSignTask.this.updateProgress(progress, totalProgess);
            }

            @Override
            public void updateMessage(final String message) {
                JarSignerSignTask.this.updateMessage(message);
            }

            @Override
            public void updateTitle(final String title) {
                JarSignerSignTask.this.updateTitle(title);
            }

            @Override
            public boolean isCancelled() {
                return JarSignerSignTask.this.isCancelled();
            }
        });
        process.sign();
        return null;
    }
}

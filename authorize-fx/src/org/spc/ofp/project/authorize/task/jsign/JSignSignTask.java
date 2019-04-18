/***********************************************************************
 *  Copyright - Secretariat of the Pacific Community                   *
 *  Droit de copie - Secrétariat Général de la Communauté du Pacifique *
 *  http://www.spc.int/                                                *
 ***********************************************************************/
package org.spc.ofp.project.authorize.task.jsign;

import java.util.Objects;
import javafx.concurrent.Task;
import org.spc.ofp.project.authorize.signature.SignatureProcessMonitor;
import org.spc.ofp.project.authorize.signature.jsign.JSignParameters;
import org.spc.ofp.project.authorize.signature.jsign.JSignProcess;

/**
 * The task that handles file signing native exec using <a href="http://ebourg.github.io/jsign/">JSign</a>.
 * @author Fabrice Bouyé (fabriceb@spc.int)
 */
public final class JSignSignTask extends Task<Void> {

    /**
     * The parameters object.
     */
    private final JSignParameters parameters;

    /**
     * Creates a new instance.
     * @param parameters The parameters object.
     * @throws NullPointerException If {@code parameters} is {@code null}.
     */
    public JSignSignTask(final JSignParameters parameters) throws NullPointerException {
        Objects.requireNonNull(parameters);
        this.parameters = parameters;
    }

    @Override
    protected Void call() throws Exception {
        final var process = new JSignProcess(parameters, new SignatureProcessMonitor() {
            @Override
            public void updateProgress(final double progress, final double totalProgess) {
                JSignSignTask.this.updateProgress(progress, totalProgess);
            }

            @Override
            public void updateMessage(final String message) {
                JSignSignTask.this.updateMessage(message);
            }

            @Override
            public void updateTitle(final String title) {
                JSignSignTask.this.updateTitle(title);
            }

            @Override
            public boolean isCancelled() {
                return JSignSignTask.this.isCancelled();
            }
        });
        process.sign();
        return null;
    }
}

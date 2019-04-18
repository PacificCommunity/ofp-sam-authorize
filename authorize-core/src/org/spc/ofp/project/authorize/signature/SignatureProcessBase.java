/***********************************************************************
 *  Copyright - Secretariat of the Pacific Community                   *
 *  Droit de copie - Secrétariat Général de la Communauté du Pacifique *
 *  http://www.spc.int/                                                *
 ***********************************************************************/
package org.spc.ofp.project.authorize.signature;

import java.util.Optional;

/**
 * Base class for a signature process
 * @author Fabrice Bouyé (fabriceb@spc.int)
 */
public abstract class SignatureProcessBase {

    private Optional<SignatureProcessMonitor> observer = Optional.empty();

    /**
     * Creates a new instance.
     * @param monitor A monitor interested in the progress of this process, may be {@code null}.
     */
    public SignatureProcessBase(final SignatureProcessMonitor monitor) {
        this.observer = Optional.ofNullable(monitor);
    }

    public abstract void sign() throws Exception;

    /**
     * Update the progress of this process.
     * @param progress Current progress.
     * @param totalProgess Total progress.
     */
    protected final void updateProgress(final double progress, final double totalProgess) {
        observer.ifPresent(obs -> obs.updateProgress(progress, totalProgess));
    }

    /**
     * Update the message of this process.
     * @param message The message.
     */
    protected final void updateMessage(final String message) {
        observer.ifPresent(obs -> obs.updateMessage(message));
    }

    /**
     * Update the title of this process.
     * @param title The title.
     */
    protected final void updateTitle(final String title) {
        observer.ifPresent(obs -> obs.updateTitle(title));
    }

    /**
     * Test wether the process is cancelled.
     * @return {@code True} if the process if verified, {@code otherwise}.
     */
    protected final boolean isCancelled() {
        return observer.isPresent() ? observer.get().isCancelled() : false;
    }
}

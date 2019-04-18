/***********************************************************************
 *  Copyright - Secretariat of the Pacific Community                   *
 *  Droit de copie - Secrétariat Général de la Communauté du Pacifique *
 *  http://www.spc.int/                                                *
 ***********************************************************************/
package org.spc.ofp.project.authorize.signature;

/**
 * Entity interested into monitoring a signature process.
 * @author Fabrice Bouyé (fabriceb@spc.int)
 */
public interface SignatureProcessMonitor {

    /**
     * Update the progress of this process.
     * @param progress Current progress.
     * @param totalProgess Total progress.
     */
    void updateProgress(double progress, double totalProgess);

    /**
     * Update the message of this process.
     * @param message The message.
     */
    void updateMessage(String message);

    /**
     * Update the title of this process.
     * @param title The title.
     */
    void updateTitle(String title);

    /**
     * Test wether the process is cancelled.
     * @return {@code True} if the process if verified, {@code otherwise}.
     */
    boolean isCancelled();
}

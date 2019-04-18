/***********************************************************************
 *  Copyright - Secretariat of the Pacific Community                   *
 *  Droit de copie - Secrétariat Général de la Communauté du Pacifique *
 *  http://www.spc.int/                                                *
 ***********************************************************************/
package org.spc.ofp.project.authorize.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A class that consumes a stream in a separate {@code Thread}.
 * <br/>Typically this class is used to consume the output stream and error output stream of a {@code Process}.
 *
 * @author Fabrice Bouyé (fabriceb@spc.int)
 */
public final class StreamConsumer implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(StreamConsumer.class.getName());

    private Thread thread;
    private final InputStream in;
    private final PrintStream out;

    /**
     * Creates a new instance with not redirection.
     * <br/>All data coming from the stream will be lost.
     *
     * @param in The source stream.
     */
    public StreamConsumer(final InputStream in) {
        this(in, null);
    }

    /**
     * Creates a new instance with not redirection.
     *
     * @param in  The source stream.
     * @param out The target stream.
     *            If {@code null}, all data coming from the stream will be lost.
     */
    public StreamConsumer(final InputStream in, final OutputStream out) {
        this.in = in;
        this.out = (out != null) ? new PrintStream(out) : null;
        thread = new Thread(this);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        try {
            try (final InputStreamReader source = new InputStreamReader(in);
                 final LineNumberReader reader = new LineNumberReader(source)) {
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    publish(line);
                }
            }
            done();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        } finally {
            thread = null;
        }
    }

    /**
     * Publish intermediate result.
     *
     * @param chunk The chunk to publish.
     */
    protected void publish(final String chunk) {
        if (out != null) {
            out.println(chunk);
        }
    }

    /**
     * Called when the stream has been consummed.
     */
    protected void done() {
    }

    /**
     * Creates a new {@code StreamConsumer} wich will publish to the standard output.
     *
     * @param in The source stream.
     * @return A {@code StreamConsumer} instance.
     */
    public static StreamConsumer toOut(final InputStream in) {
        return new StreamConsumer(in, System.out);
    }

    /**
     * Creates a new {@code StreamConsumer} wich will publish to the standard error output.
     *
     * @param in The source stream.
     * @return A {@code StreamConsumer} instance.
     */
    public static StreamConsumer toErr(final InputStream in) {
        return new StreamConsumer(in, System.err);
    }
}

/***********************************************************************
 *  Copyright - Secretariat of the Pacific Community                   *
 *  Droit de copie - Secrétariat Général de la Communauté du Pacifique *
 *  http://www.spc.int/                                                *
 ***********************************************************************/
package org.spc.ofp.project.authorize.signature.jarsigner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.spc.ofp.project.authorize.io.IOUtils;
import org.spc.ofp.project.authorize.io.StreamConsumer;
import org.spc.ofp.project.authorize.signature.SignatureProcessBase;
import org.spc.ofp.project.authorize.signature.SignatureProcessMonitor;

/**
 * Process that handles JAR file signing using the JDK's JarSigner tool.
 * @author Fabrice Bouyé (fabriceb@spc.int)
 */
public class JarSignerProcess extends SignatureProcessBase {

    /**
     * Current action.
     */
    private SignAction action;

    /**
     * The parameters object.
     */
    private final JarSignerParameters parameters;

    /**
     * Creates a new instance.
     * @param parameters The parameters object.
     * @param monitor A monitor interested in the progress of this process, may be {@code null}.
     * @throws NullPointerException If {@code parameters} is {@code null}.
     */
    public JarSignerProcess(final JarSignerParameters parameters, final SignatureProcessMonitor monitor) throws NullPointerException {
        super(monitor);
        Objects.requireNonNull(parameters);
        this.parameters = parameters;
    }

    @Override
    public void sign() throws Exception {
        // Calculate number of steps in this task.
        updateMessage("Initializing.");
        action = SignAction.WALK;
        impl_sign();
        // Run signing task.
        updateMessage("Running task.");
        updateProgress(currentProgress, totalProgress);
        if (isCancelled()) {
            return;
        }
        action = SignAction.SIGN;
        impl_sign();
    }

    /**
     * Total progress; will be computed after a WALK.
     */
    private int totalProgress = 0;
    /**
     * Current progress; will only increase during a SIGN.
     */
    private int currentProgress;

    /**
     * The current signing process.
     * <ul>
     * <li>If current file is a directory, list its content and add it to the list of files to sign.</li>
     * <li>If current file is a file list, check if it's a JAR file:</li>
     * <ul>
     * <li>If true, check if the file is already signed:</li>
     * <ul>
     * <li>If true, do nothing.</li>
     * <li>If false, sign the file.</li>
     * </ul>
     * <li>If false, do nothing.</li>
     * </ul>
     * </ul>
     * @throws Exception In case of errors.
     */
    private void impl_sign() throws Exception {
        // Nothing to do!
        if (parameters.pathToSign == null || parameters.pathToSign.isEmpty() || parameters.pathToSign.isBlank()) {
            return;
        }
        final var filesToSign = new LinkedList<Path>();
        final var directory = Paths.get(parameters.pathToSign);
        filesToSign.add(directory);
        while (!filesToSign.isEmpty()) {
            final var file = filesToSign.remove(0);
            if (parameters.debugDirectoryWalk) {
                final var message = String.format("File \"%s\"", file.toString());
                parameters.logger.log(Level.INFO, message);
                updateMessage(message);
            }
            // Add folder content to the list.
            if (Files.isDirectory(file)) {
                final var children = Files.list(file)
                        .collect(Collectors.toList());
                filesToSign.addAll(children);
            } else if (Files.isRegularFile(file)) {
                // Need to sign jar files.
                final String filename = file.getFileName()
                        .toString();
                if (filename.endsWith(".jar")) { // NOI18N.
                    switch (action) {
                        case WALK:
                            totalProgress++;
                            break;
                        case SIGN:
                        default:
                            updateMessage(file.toString());
                            final boolean fileIsSigned = isFileSigned(file);
                            if (parameters.debugSignature) {
                                final var message = String.format("File \"%s\" signed: %s.", file.toString(), fileIsSigned);
                                parameters.logger.log(Level.INFO, message);
                                updateMessage(message);
                            }
                            if (!fileIsSigned) {
                                if (!Files.isWritable(file)) {
                                    IOUtils.INSTANCE.setWritable(file);
                                }
                                signFile(file);
                                final var message = String.format("File \"%s\" signed.", file.toString());
                                updateMessage(message);
                            }
                            //
                            updateProgress(++currentProgress, totalProgress);
                            updateMessage("DONE");
                            if (isCancelled()) {
                                return;
                            }
                    }
                }
            }
        }
    }
    ////////////////////////////////////////////////////////////////////////////

    /**
     * Check if provided file is already signed.
     * @param file The target file.
     * @return {@code True} if {@code file} is already signed, {@code false} otherwise.
     * @throws IOException In case of IO errors.
     * @throws InterruptedException If the thread was interrupted.
     */
    private boolean isFileSigned(final Path file) throws IOException, InterruptedException {
        final var args = new LinkedList<String>();
        args.add("-verify"); // NOI18N.
        args.add(String.format("\"%s\"", file.toString())); // NOI18N.
        final var exec = Paths.get(parameters.javaHome, parameters.jarSignerExec);
        final var command = String.format("\"%s\"", exec.toString()); // NOI18N.
        final var sstream = new ByteArrayOutputStream();
        final int commandResult = executeCommand(sstream, command, args.toArray(new String[0]));
        // @todo Check command result.
        if (commandResult > 0) {
            final var message = String.format("Command failed with error %d.", commandResult);
            System.err.println(message);
            parameters.logger.log(Level.SEVERE, message);
        }
        final var signing = sstream.toString().trim();
        // If the file is not already signed, we need to sign it.
        final boolean result = "jar verified.".equals(signing);// NOI18N.
        return result;
    }

    /**
     * Sign provided file.
     * @param file The target file.
     * @throws IOException In case of IO errors.
     * @throws InterruptedException If the thread was interrupted.
     */
    private void signFile(final Path file) throws IOException, InterruptedException {
        final var args = new LinkedList<String>();
        if (parameters.useTimeStamp) {
            args.add("-tsa"); // NOI18N.
            args.add(parameters.timeStampHost);
        }
        if (parameters.useProxy) {
            final var proxyHost = parameters.proxyHost;
            if (proxyHost != null && !proxyHost.isEmpty()) {
                args.add(String.format("-J-Dhttp.proxyHost=%s", proxyHost)); // NOI18N.
            }
            final var proxyPort = parameters.proxyPort;
            if (proxyPort != null && !proxyPort.isEmpty()) {
                args.add(String.format("-J-Dhttp.proxyPort=%s", proxyPort)); // NOI18N.
            }
        }
        final var keyStore = parameters.keyStore;
        if (!keyStore.isEmpty()) {
            args.add("-keystore"); // NOI18N.
            args.add(keyStore);
        }
        final var storePassword = parameters.storePassword;
        args.add("-storepass"); // NOI18N.
        args.add(storePassword);
        final var keyPass = parameters.keyPass;
        if (!keyPass.equals(storePassword)) {
            args.add("-keypass"); // NOI18N.
            args.add(keyPass);
        }
        args.add(String.format("\"%s\"", file.toString())); // NOI18N.
        final var alias = parameters.alias;
        args.add(alias);
        //
        final var exec = Paths.get(parameters.javaHome, parameters.jarSignerExec);
        final var command = String.format("\"%s\"", exec.toString()); // NOI18N.
        final var sstream = new ByteArrayOutputStream();
        final int commandResult = executeCommand(sstream, command, args.toArray(new String[0]));
        // @todo Check command result.
        if (commandResult > 0) {
            // We had an issue with a JAR with several duplicate entries that could not be signed.
            final var message = String.format("Command failed with error %d.", commandResult);
            System.err.println(message);
            parameters.logger.log(Level.SEVERE, message);
        }
    }

    ////////////////////////////////////////////////////////////////////////////
    /**
     * Execute an external command.
     * @param command The command to execute.
     * @return The result of the command.
     * @throws IOException In case of I/O error.
     * @throws InterruptedException
     */
    private int executeCommand(final String command, final String... args) throws IOException, InterruptedException {
        return executeCommand(parameters.debugCommand ? System.out : null, parameters.debugCommand ? System.err : null, command, args);
    }

    /**
     * Execute an external command.
     * @param output The stream on which to redirect the standard output of this program.
     * @param command The command to execute.
     * @return The result of the command.
     * @throws IOException In case of I/O error.
     * @throws InterruptedException
     */
    private int executeCommand(final OutputStream output, final String command, final String... args) throws IOException, InterruptedException {
        return executeCommand(output, parameters.debugCommand ? System.err : null, command, args);
    }

    /**
     * Execute an external command.
     * @param output The stream on which to redirect the standard output of this program.
     * @param errorOutput The stream on which to redirect the standard error output of this program.
     * @param command The command to execute.
     * @return The result of the command.
     * @throws IOException In case of I/O error.
     * @throws InterruptedException
     */
    private int executeCommand(final OutputStream output, final OutputStream errorOutput, final String command, final String... args) throws IOException, InterruptedException {
        final var processArgs = new String[args.length + 1];
        processArgs[0] = command;
        System.arraycopy(args, 0, processArgs, 1, args.length);
        if (parameters.debugCommand) {
            var fullCommand = Arrays.stream(processArgs)
                    .collect(Collectors.joining(" ")); // NOI18N.
            parameters.logger.log(Level.INFO, "Executing: {0}", fullCommand);
        }
        final var processBuilder = new ProcessBuilder(processArgs);
        final var process = processBuilder.start();
//        final Process process = Runtime.getRuntime().exec(command);
        final var out = new StreamConsumer(process.getInputStream(), output);
        final var err = new StreamConsumer(process.getErrorStream(), errorOutput);
        final int returnValue = process.waitFor();
        if (parameters.debugCommand) {
            parameters.logger.log(Level.INFO, "Process exited with error code: {0}", returnValue);
        }
        return returnValue;
    }
}

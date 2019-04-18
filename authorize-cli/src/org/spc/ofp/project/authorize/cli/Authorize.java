/***********************************************************************
 *  Copyright - Secretariat of the Pacific Community                   *
 *  Droit de copie - Secrétariat Général de la Communauté du Pacifique *
 *  http://www.spc.int/                                                *
 ***********************************************************************/
package org.spc.ofp.project.authorize.cli;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.spc.ofp.project.authorize.signature.SignMethod;
import org.spc.ofp.project.authorize.signature.SignUtils;
import org.spc.ofp.project.authorize.signature.jarsigner.JarSignerParametersBuilder;
import org.spc.ofp.project.authorize.signature.jarsigner.JarSignerProcess;
import org.spc.ofp.project.authorize.signature.jarsigner.JarSignerUtils;
import org.spc.ofp.project.authorize.signature.jsign.JSignParametersBuilder;
import org.spc.ofp.project.authorize.signature.jsign.JSignProcess;
import org.spc.ofp.project.authorize.signature.jsign.JSignUtils;

/**
 * Authorize command line interface.
 * @author Fabrice Bouyé (fabriceb@spc.int)c
 */
public final class Authorize {

    public static final String DEFAULT_CONFIG_FILE = "settings.properties";
    /**
     * Prefix of flags, equals to "{@value}".
     */
    public static final String FLAG_PREXIT = "--"; // NOI18N.
    /**
     * Key value separator, equals to "{@value}".
     */
    public static final String FLAG_SEPARATOR = "="; // NOI18N.
    /**
     * Path to the JDK use for signing, equals to "{@value}".
     */
    public static final String JAVA_HOME_FLAG = "java-home";
    /**
     * Path a configuration file, equals to "{@value}".
     */
    public static final String CONFIG_FLAG = "config";
    /**
     * Name of jar signer executable, equals to "{@value}".
     * <ul>
     *  <li>On Windows default jarsigner name is {@code jarsigner.exe}.
     *  <li>On Linux and macOS default jarsigner name is {@code jarsigner}.
     * </ul>
     */
    public static final String JAR_SIGNER_FLAG = "jar-signer";
    /**
     * Help flag, equals to "{@value}".
     */
    public static final String HELP_FLAG = "help"; // NOI18N.
    public static final String DEBUG_COMMAND_FLAG = "debug-command"; // NOI18N.
    public static final String DEBUG_SIGNATURE_FLAG = "debug-signature"; // NOI18N.
    public static final String DEBUG_DIRECTORY_WALK_FLAG = "debug-directory-walk"; // NOI18N.
    public static final String PATH_TO_SIGN = "path-to-sign"; // NOI18N.
    /**
     * Sign method flag, equals to "{@value}".
     */
    public static final String SIGN_METHOD_FLAG = "sign-method"; // NOI18N.
    /**
     * Alias flag, equals to "{@value}".
     */
    public static final String ALIAS_FLAG = "alias"; // NOI18N.
    /**
     * Path to key store file, equals to "{@value}".
     */
    public static final String KEY_STORE_FLAG = "key-store"; // NOI18N.
    /**
     * Password to the store, equals to "{@value}".
     */
    public static final String STORE_PASSWORD_FLAG = "store-password"; // NOI18N.
    /**
     * Pass to the key, equals to "{@value}".
     */
    public static final String KEY_PASS_FLAG = "key-pass"; // NOI18N.
    /**
     * Proxy host flag, equals to "{@value}".
     */
    public static final String PROXY_HOST_FLAG = "proxy-host"; // NOI18N.
    /**
     * Proxy port flag, equals to "{@value}".
     */
    public static final String PROXY_PORT_FLAG = "proxy-port"; // NOI18N.
    /**
     * Timestamp host flag, equals to "{@value}".
     */
    public static final String TIMESTAMP_HOST_FLAG = "time-stamp-host"; // NOI18N.
    public static final String PROGRAM_NAME_FLAG = "program-name"; // NOI18N.
    public static final String PROGRAM_URL_FLAG = "program-url"; // NOI18N.

    /**
     * Main entry point.
     * @param args The command line arguments.
     */
    public static void main(String... args) {
        // Catch early command line hickups.
        final var arguments = parseArgs(args);
        validateConfiguration(arguments);
        // Initialize default config.
        final var config = initializeArgs();
        // Override with config file.
        loadConfigFile(config, null);
        // Override with user provided config file.
        if (arguments.containsKey(CONFIG_FLAG)) {
            loadConfigFile(config, arguments.remove(CONFIG_FLAG));
        }
        // Override with command line arguments.
        mergeConfigurations(arguments, config);
        config.entrySet()
                .stream()
                .forEach(System.out::println);
        validateConfiguration(config);
        // Start sign job.
        sign(config);
        System.exit(ErrorCode.EXIT_SUCCESS);
    }

    /**
     * Prints the application manual.
     * @param out The output stream.
     */
    private static void printManual(final PrintStream out) {
        out.printf("%s %s%n", I18N.INSTANCE.getString("app.title", "Authorize"), I18N.INSTANCE.getString("app.copyright"));
        out.printf("%s%n", I18N.INSTANCE.getString("app.description")); // NOI18N.
        out.println("Usage:"); // NOI18N.
        out.printf("  %s%s\t\t\t%s%n", FLAG_PREXIT, HELP_FLAG, I18N.INSTANCE.getString("flag.help.description")); // NOI18N.
        out.printf("  %s%s=<path>\t\t%s%n", FLAG_PREXIT, CONFIG_FLAG, I18N.INSTANCE.getString("flag.config.description")); // NOI18N.
        out.printf("  %s%s=<method>\t%s%n", FLAG_PREXIT, SIGN_METHOD_FLAG, I18N.INSTANCE.getString("flag.sign-method.description")); // NOI18N.
        out.printf("\t%s\t\t%s%n", SignMethod.JARSIGNER, I18N.INSTANCE.getString("sign-method.jarsigner.description")); // NOI18N.
        out.printf("\t%s\t\t\t%s%n", SignMethod.JSIGN, I18N.INSTANCE.getString("sign-method.jsign.description")); // NOI18N.
        out.printf("  %s%s=<alias>\t\t%s%n", FLAG_PREXIT, ALIAS_FLAG, I18N.INSTANCE.getString("flag.alias.description")); // NOI18N.
        out.printf("  %s%s=<path>\t\t%s%n", FLAG_PREXIT, KEY_STORE_FLAG, I18N.INSTANCE.getString("flag.key-store.description")); // NOI18N.
        out.printf("  %s%s=<path>\t\t%s%n", FLAG_PREXIT, STORE_PASSWORD_FLAG, I18N.INSTANCE.getString("flag.store-password.description")); // NOI18N.
        out.printf("  %s%s=<path>\t\t%s%n", FLAG_PREXIT, KEY_PASS_FLAG, I18N.INSTANCE.getString("flag.key-pass.description")); // NOI18N.
        out.printf("  %s%s=<url>\t\t%s%n", FLAG_PREXIT, PROXY_HOST_FLAG, I18N.INSTANCE.getString("flag.proxy-host.description")); // NOI18N.
        out.printf("  %s%s=<port>\t\t%s%n", FLAG_PREXIT, PROXY_PORT_FLAG, I18N.INSTANCE.getString("flag.proxy-port.description")); // NOI18N.
        out.printf("  %s%s=<url>\t%s%n", FLAG_PREXIT, TIMESTAMP_HOST_FLAG, I18N.INSTANCE.getString("flag.timestamp-host.description")); // NOI18N.
        out.println();
        out.println(I18N.INSTANCE.getString("usage.jar-signer-options")); // NOI18N.
        out.printf("  %s%s=<path>\t\t%s%n", FLAG_PREXIT, JAVA_HOME_FLAG, I18N.INSTANCE.getString("flag.java-home.description")); // NOI18N.
        out.printf("  %s%s=<filename>\t%s%n", FLAG_PREXIT, JAR_SIGNER_FLAG, I18N.INSTANCE.getString("flag.jar-signer.description")); // NOI18N.
        out.println();
        out.println(I18N.INSTANCE.getString("usage.jsign-options")); // NOI18N.
        out.printf("  %s%s=<name>\t\t%s%n", FLAG_PREXIT, PROGRAM_NAME_FLAG, I18N.INSTANCE.getString("flag.program-name.description")); // NOI18N.
        out.printf("  %s%s=<url>\t\t%s%n", FLAG_PREXIT, PROGRAM_URL_FLAG, I18N.INSTANCE.getString("flag.program-url.description")); // NOI18N.
    }

    /**
     * Prints the application manual and exits with {@core ErrorCode.EXIT_SUCCESS}.
     */
    private static void help() {
        printManual(System.out);
        System.exit(ErrorCode.EXIT_SUCCESS);
    }

    /**
     * Prints the application manual and exits with {@core ErrorCode.EXIT_FAILURE}.
     */
    private static void usage() {
        printManual(System.out);
        System.exit(ErrorCode.EXIT_FAILURE);
    }

    /**
     * Initialize argument map with default values.
     * @return A {@code LinkedHashMap<String, String>} instance, never {@code null}.
     */
    private static LinkedHashMap<String, String> initializeArgs() {
        final var result = new LinkedHashMap<String, String>();
        // Fill in default values.
        result.put(DEBUG_COMMAND_FLAG, "false");
        result.put(DEBUG_SIGNATURE_FLAG, "false");
        result.put(DEBUG_DIRECTORY_WALK_FLAG, "false");
        result.put(PROXY_HOST_FLAG, null);
        result.put(PROXY_PORT_FLAG, null);
        result.put(TIMESTAMP_HOST_FLAG, SignUtils.DEFAULT_TIME_STAMP_HOST);
        result.put(KEY_STORE_FLAG, null);
        result.put(ALIAS_FLAG, null);
        result.put(JAR_SIGNER_FLAG, JarSignerUtils.DEFAULT_JAR_SIGNER);
        return result;
    }

    /**
     * Load configuration file.
     * @param result The result map.
     * @param configPath Path to the configuration file, may be {@code null}.
     * <br>If {@code null}, the default configuration file is loaded.
     * @return A {@code LinkedHashMap<String, String>} instance, never {@code null}.
     */
    private static LinkedHashMap<String, String> loadConfigFile(final LinkedHashMap<String, String> result, String configStr) {
        final var settingsStr = (configStr == null) ? DEFAULT_CONFIG_FILE : configStr; // NOI18N.
        try {
            final var settingsPath = Paths.get(settingsStr);
            final var properties = new Properties();
            if (Files.exists(settingsPath) && Files.isReadable(settingsPath)) {
                try (final var input = Files.newInputStream(settingsPath)) {
                    properties.load(input);
                    mergeConfigurations(properties, result);
                } catch (IOException ex) {
                    Logger.getLogger(Authorize.class.getName()).log(Level.WARNING, ex.getMessage(), ex);
                }
            } else if (configStr != null) {
                System.err.printf(I18N.INSTANCE.getString("error.config-invalid.message"), configStr);
                System.exit(ErrorCode.EXIT_FAILURE);
            }
        } catch (InvalidPathException ex) {

        }
        return result;
    }

    /**
     * Merge configurations.
     * @param source Source configuration.
     * @param destination Destination configuration.
     */
    private static void mergeConfigurations(final Map<?, ?> source, final Map<String, String> destination) {
        source.entrySet()
                .stream()
                .forEach(entry -> {
                    final var key = (String) entry.getKey();
                    var value = (String) entry.getValue();
                    value = (value == null || value.isEmpty() || value.isBlank()) ? null : value.trim();
                    destination.put(key, value);
                });
    }

    /**
     * Parses command line arguments.
     * @param result The result map.
     * @param args The command line arguments.
     * @return A {@code LinkedHashMap<String, String>} instance, never {@code null}.
     */
    private static LinkedHashMap<String, String> parseArgs(final String... args) {
        final var result = new LinkedHashMap<String, String>();
        Arrays.stream(args)
                .map(Authorize::splitArg)
                .filter(pair -> !Objects.isNull(pair))
                .forEach(pair -> result.put(pair[0], pair[1]));
        return result;
    }

    /**
     * Splits a command line argument into a (key, value) pair.
     * @param arg The command line argument.
     * @return A {@code String[]} of size 2, never {@code null}.
     */
    private static String[] splitArg(final String arg) {
        if (!arg.startsWith(FLAG_PREXIT)) {
            usage();
        }
        final int index = arg.indexOf(FLAG_SEPARATOR);
        var key = arg.replace(FLAG_PREXIT, ""); // NOI18N.
        var value = (String) null;
        if (index > 0) {
            key = arg.substring(0, index).replace(FLAG_PREXIT, ""); // NOI18N.
            value = arg.substring(index + 1, arg.length());
            value = (value.isEmpty() || value.isBlank()) ? null : value.trim();
        }
        final var result = new String[2];
        result[0] = key;
        result[1] = value;
        return result;
    }

    /**
     * Validate configuration.
     * @param arguments The argument map.
     */
    private static void validateConfiguration(final LinkedHashMap<String, String> arguments) {
        arguments.entrySet()
                .stream()
                .forEach(entry -> {
                    final var key = entry.getKey();
                    final var value = entry.getValue();
                    switch (key) {
                        case HELP_FLAG: {
                            help();
                            break;
                        }
                        case CONFIG_FLAG: {
                            // Check if jarsigner valid.
                            final var configStr = value;
                            try {
                                final var configFile = Paths.get(configStr);
                                if (!Files.exists(configFile) || !Files.isRegularFile(configFile) || !Files.isReadable(configFile)) {
                                    System.err.printf(I18N.INSTANCE.getString("error.config-invalid.message"), configFile.toAbsolutePath().toString());
                                    System.exit(ErrorCode.EXIT_FAILURE);
                                }
                            } catch (InvalidPathException ex2) {
                                System.err.printf(I18N.INSTANCE.getString("error.config-invalid.message"), configStr);
                                System.exit(ErrorCode.EXIT_FAILURE);
                            }
                            break;
                        }
                        case JAVA_HOME_FLAG: {
                            final var javaHomeStr = arguments.get(JAVA_HOME_FLAG);
                            if (javaHomeStr == null) {
                                System.err.print(I18N.INSTANCE.getString("error.java-home-null.message")); // NOI18N.
                                System.exit(ErrorCode.EXIT_FAILURE);
                            }
                            try {
                                final var javaHome = Paths.get(javaHomeStr);
                                if (!Files.exists(javaHome) || !Files.isDirectory(javaHome)) {
                                    System.err.printf(I18N.INSTANCE.getString("error.java-home-invalid.message"), javaHome.toAbsolutePath().toString());
                                    System.exit(ErrorCode.EXIT_FAILURE);
                                }
                            } catch (InvalidPathException ex1) {
                                System.err.printf(I18N.INSTANCE.getString("error.java-home-invalid.message"), javaHomeStr);
                                System.exit(ErrorCode.EXIT_FAILURE);
                            }
                            break;
                        }
                        case JAR_SIGNER_FLAG: {
                            break;
                        }
                        case PATH_TO_SIGN:
                        case SIGN_METHOD_FLAG: {
                            final var signMethodStr = arguments.getOrDefault(SIGN_METHOD_FLAG, SignMethod.JARSIGNER.name());
                            final var signMethod = SignMethod.parse(signMethodStr);
                            switch (signMethod) {
                                case JARSIGNER: {
                                    final var pathToSignStr = arguments.get(PATH_TO_SIGN);
                                    try {
                                        final var pathToSign = Paths.get(pathToSignStr);
                                        if (!Files.exists(pathToSign) || !Files.isReadable(pathToSign)) {
                                            System.err.printf(I18N.INSTANCE.getString("error.java-home-invalid.message"), pathToSign.toAbsolutePath().toString());
                                            System.exit(ErrorCode.EXIT_FAILURE);
                                        }
                                    } catch (InvalidPathException ex2) {
                                        System.err.printf(I18N.INSTANCE.getString("error.jar-signer-invalid.message"), pathToSignStr);
                                        System.exit(ErrorCode.EXIT_FAILURE);
                                    }
                                    break;
                                }
                                case JSIGN: {
                                    final var pathToSignStr = arguments.get(PATH_TO_SIGN);
                                    try {
                                        final var pathToSign = Paths.get(pathToSignStr);
                                        if (!Files.exists(pathToSign) || !Files.isRegularFile(pathToSign) || !Files.isReadable(pathToSign)) {
                                            System.err.printf(I18N.INSTANCE.getString("error.java-home-invalid.message"), pathToSign.toAbsolutePath().toString());
                                            System.exit(ErrorCode.EXIT_FAILURE);
                                        }
                                    } catch (InvalidPathException ex2) {
                                        System.err.printf(I18N.INSTANCE.getString("error.jar-signer-invalid.message"), pathToSignStr);
                                        System.exit(ErrorCode.EXIT_FAILURE);
                                    }
                                    break;
                                }
                                case UNKNOWN:
                                default: {
                                    System.err.printf(I18N.INSTANCE.getString("error.sign-method-unknown.message"), signMethodStr); // NOI18N.
                                    usage();
                                }
                            }
                            break;
                        }
                        case ALIAS_FLAG: {
                            if (value == null) {
                                System.err.print(I18N.INSTANCE.getString("error.alias-null.message"));
                                System.exit(ErrorCode.EXIT_FAILURE);
                            }
                            break;
                        }
                        case KEY_STORE_FLAG: {
                            if (value == null) {
                                System.err.print(I18N.INSTANCE.getString("error.key-store-null.message")); // NOI18N.
                                System.exit(ErrorCode.EXIT_FAILURE);
                            }
                            break;
                        }
                        case STORE_PASSWORD_FLAG:
                        case KEY_PASS_FLAG:
                        case PROXY_HOST_FLAG:
                        case PROXY_PORT_FLAG:
                        case TIMESTAMP_HOST_FLAG:
                            break;
                        case PROGRAM_NAME_FLAG: {
                            if (value == null || value.isEmpty() || value.isBlank()) {
                                System.err.print(I18N.INSTANCE.getString("error.program-name-null.message")); // NOI18N.
                                System.exit(ErrorCode.EXIT_FAILURE);
                            }
                            break;
                        }
                        case PROGRAM_URL_FLAG: {
                            if (value == null || value.isEmpty() || value.isBlank()) {
                                System.err.print(I18N.INSTANCE.getString("error.program-name-null.message")); // NOI18N.
                                System.exit(ErrorCode.EXIT_FAILURE);
                            }
                            break;
                        }
                        case DEBUG_COMMAND_FLAG: {
                            if (value == null) {
                                arguments.put(DEBUG_COMMAND_FLAG, "true");
                            }
                            break;
                        }
                        case DEBUG_SIGNATURE_FLAG: {
                            if (value == null) {
                                arguments.put(DEBUG_SIGNATURE_FLAG, "true");
                            }
                            break;
                        }
                        case DEBUG_DIRECTORY_WALK_FLAG: {
                            if (value == null) {
                                arguments.put(DEBUG_DIRECTORY_WALK_FLAG, "true");
                            }
                            break;
                        }
                        default: { // Unsupported argument.
                            final String pattern = (value == null) ? "error.unsupported-argument.message" : "error.unsupported-argument-pair.message"; // NOI18N.
                            System.err.printf(I18N.INSTANCE.getString(pattern), key, value);
                            usage();
                        }
                    }
                });
    }

    /**
     * Do the sign job.
     * @param arguments The argument map.
     */
    private static void sign(final LinkedHashMap<String, String> arguments) {
        final var signMethod = SignMethod.parse(arguments.getOrDefault(SIGN_METHOD_FLAG, SignMethod.JARSIGNER.name()));
        try {
            switch (signMethod) {
                case JARSIGNER: {
                    signWithJarSigner(arguments);
                    break;
                }
                case JSIGN: {
                    signWithJSign(arguments);
                    break;
                }
            }
        } catch (Throwable ex) {
            Logger.getLogger(Authorize.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    /**
     * sign with JarSigner.
     * @param arguments The argument map.
     */
    private static void signWithJarSigner(final LinkedHashMap<String, String> arguments) throws Exception {
        final boolean useProxy = (arguments.get(PROXY_HOST_FLAG)) != null && (arguments.get(PROXY_PORT_FLAG) != null);
        final boolean useTimeStamp = (arguments.get(TIMESTAMP_HOST_FLAG) != null);
        final var parameters = JarSignerParametersBuilder.create()
                .debugCommand(Boolean.parseBoolean(arguments.get(DEBUG_COMMAND_FLAG)))
                .debugSignature(Boolean.parseBoolean(arguments.get(DEBUG_SIGNATURE_FLAG)))
                .debugDirectoryWalk(Boolean.parseBoolean(arguments.get(DEBUG_DIRECTORY_WALK_FLAG)))
                .javaHome(arguments.get(JAVA_HOME_FLAG))
                .jarSignerExec(arguments.getOrDefault(JAR_SIGNER_FLAG, JarSignerUtils.DEFAULT_JAR_SIGNER))
                .keyStore(arguments.get(KEY_STORE_FLAG))
                .alias(arguments.get(ALIAS_FLAG))
                .storePassword(arguments.get(STORE_PASSWORD_FLAG))
                .keyPass(arguments.get(KEY_PASS_FLAG))
                .useProxy(useProxy)
                .proxyHost(arguments.get(PROXY_HOST_FLAG))
                .proxyPort(arguments.get(PROXY_PORT_FLAG))
                .useTimeStamp(useTimeStamp)
                .timeStampHost(arguments.getOrDefault(TIMESTAMP_HOST_FLAG, JarSignerUtils.DEFAULT_TIME_STAMP_HOST))
                .pathToSign(arguments.get(PATH_TO_SIGN))
                .build();
        final var process = new JarSignerProcess(parameters, null);
        process.sign();
    }

    /**
     * sign with JSign.
     * @param arguments The argument map.
     */
    private static void signWithJSign(final LinkedHashMap<String, String> arguments) throws Exception {
        final boolean useProxy = (arguments.get(PROXY_HOST_FLAG)) != null && (arguments.get(PROXY_PORT_FLAG) != null);
        final boolean useTimeStamp = (arguments.get(TIMESTAMP_HOST_FLAG) != null);
        final var parameters = JSignParametersBuilder.create()
                .debugCommand(Boolean.parseBoolean(arguments.get(DEBUG_COMMAND_FLAG)))
                .debugSignature(Boolean.parseBoolean(arguments.get(DEBUG_SIGNATURE_FLAG)))
                .debugDirectoryWalk(Boolean.parseBoolean(arguments.get(DEBUG_DIRECTORY_WALK_FLAG)))
                .keystoreFilename(arguments.get(KEY_STORE_FLAG))
                .alias(arguments.get(ALIAS_FLAG))
                .password(arguments.get(STORE_PASSWORD_FLAG))
                .keypass(arguments.get(KEY_PASS_FLAG))
                .useProxy(useProxy)
                .proxyHost(arguments.get(PROXY_HOST_FLAG))
                .proxyPort(arguments.get(PROXY_PORT_FLAG))
                .useTimeStamp(useTimeStamp)
                .timeStampHost(arguments.getOrDefault(TIMESTAMP_HOST_FLAG, JSignUtils.DEFAULT_TIME_STAMP_HOST))
                .programName("Foo")
                .programURL("Fii")
                .filename(arguments.get(PATH_TO_SIGN))
                .build();
        final var process = new JSignProcess(parameters, null);
        process.sign();
    }
}

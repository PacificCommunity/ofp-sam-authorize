/***********************************************************************
 *  Copyright - Secretariat of the Pacific Community                   *
 *  Droit de copie - Secrétariat Général de la Communauté du Pacifique *
 *  http://www.spc.int/                                                *
 ***********************************************************************/
package org.spc.ofp.project.authorize.io;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * IO utility class.
 * @author Fabrice Bouyé (fabriceb@spc.int)
 */
public enum IOUtils {
    INSTANCE;

    /**
     * Sets a file to be writable.
     * @param path The path.
     * @throws NullPointerException If {@code path} is {@code null}.
     * @throws IOException In case of IO error.
     */
    public void setWritable(final Path path) throws NullPointerException, IOException {
        Objects.requireNonNull(path);
        final FileSystem fileSystem = path.getFileSystem();
        final Set<String> fileSystemViews = fileSystem.supportedFileAttributeViews();
        if (fileSystemViews.contains("dos")) { // NOI18N.
            Files.setAttribute(path, "dos:readonly", false); // NOI18N.
        } else if (fileSystemViews.contains("posix")) { // NOI18N.
            final Set<PosixFilePermission> posixPermissions = new HashSet<>();
            posixPermissions.add(PosixFilePermission.OWNER_READ);
            posixPermissions.add(PosixFilePermission.OWNER_WRITE);
            Files.setPosixFilePermissions(path, posixPermissions);
        } else {
            throw new IOException("Unsupported file permissions"); // NOI18N.
        }
    }
}

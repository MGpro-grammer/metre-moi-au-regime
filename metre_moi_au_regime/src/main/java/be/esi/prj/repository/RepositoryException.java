package be.esi.prj.repository;

/**
 * Custom runtime exception for repository operations.
 * Thrown when errors occur during data access or manipulation in repositories.
 */
public class RepositoryException extends RuntimeException {

    /**
     * Creates a new repository exception with a detailed message and underlying cause.
     *
     * @param message The error message describing the exception
     * @param cause The underlying cause of this exception
     */
    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}

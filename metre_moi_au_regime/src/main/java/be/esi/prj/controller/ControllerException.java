package be.esi.prj.controller;

/**
 * Custom exception for the application's controller layer.
 * This class extends RuntimeException to represent controller-specific errors.
 */
public class ControllerException extends RuntimeException {

    /**
     * Creates a new instance of ControllerException with the specified error message.
     *
     * @param message Description of the error that occurred in the controller
     */
    public ControllerException(String message) {
        super(message);
    }
}

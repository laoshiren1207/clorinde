package io.github.clorinde.core.exception;

/**
 * @author: laoshiren
 * @date: 2024/05/28 10:50
 **/
public class NotInterfaceException extends Exception {
    public NotInterfaceException(String message)
    {
        super(message);
    }

    public NotInterfaceException(Exception e)
    {
        super(e.getMessage());
    }
}

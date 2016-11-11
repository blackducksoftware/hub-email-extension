package com.blackducksoftware.integration.email.mock;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;

import com.blackducksoftware.integration.email.model.ExtensionProperties;
import com.blackducksoftware.integration.email.model.JavaMailWrapper;

public class MockMailWrapper extends JavaMailWrapper {

    public final static String MESSAGE_EXCEPTION = "Mock thrown exception";

    private final boolean throwException;

    public MockMailWrapper(final boolean throwException) {
        this.throwException = throwException;
    }

    @Override
    public void sendMessage(final ExtensionProperties customerProperties, final Session session, final Message message)
            throws MessagingException {
        if (throwException) {
            throw new MessagingException(MESSAGE_EXCEPTION);
        } else {
            System.out.println("MockMailWrapper: sendMessage called");
        }
    }
}

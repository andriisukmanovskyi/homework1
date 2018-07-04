package com.epam.lab.custom.appender;

import com.epam.lab.external.api.Smsc;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Plugin(name = "SMSAppender", category = "Core", elementType = "appender", printObject = true)

public class SMSAppender extends AbstractAppender {

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();
    private final String LOGIN = "antilopa";
    private final String PASSWORD = "antilopa";

    private String phoneNumbers;
    private Smsc smsc;

    private SMSAppender(String name, Filter filter, Layout<? extends Serializable> layout,
                        String phoneNumbers, boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
        this.phoneNumbers = phoneNumbers;
    }

    @PluginFactory
    public static SMSAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginElement("Filter") final Filter filter,
            @PluginAttribute("phoneNumbers") String phoneNumbers) {
        if (name == null) {
            LOGGER.error("No name provided for SMSAppender");
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        return new SMSAppender(name, filter, layout, phoneNumbers, true);
    }

    public void append(LogEvent logEvent) {
        readLock.lock();
        try {
            final byte[] bytes = getLayout().toByteArray(logEvent);
            smsc = new Smsc(LOGIN, PASSWORD, "UTF-8", true);
            smsc.send_sms(phoneNumbers, new String(bytes, "UTF-8"), 1,
                    "", "", 0, "", "");
        } catch (Exception ex) {
            if (!ignoreExceptions()) {
                throw new AppenderLoggingException(ex);
            }
        } finally {
            readLock.unlock();
        }
    }
}

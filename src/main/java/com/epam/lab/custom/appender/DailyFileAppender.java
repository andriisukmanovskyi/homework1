package com.epam.lab.custom.appender;

import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Plugin(name = "DailyFileAppender", category = "Core", elementType = "appender", printObject = true)

public class DailyFileAppender extends AbstractAppender {

    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
    private final Lock readLock = rwLock.readLock();

    private String fileName;
    private String datePattern;

    protected DailyFileAppender(String name, Filter filter, Layout<? extends Serializable> layout,
                                String fileName, String datePattern, boolean ignoreExceptions) {
        super(name, filter, layout, ignoreExceptions);
        this.fileName = fileName;
        this.datePattern = datePattern;
    }

    @PluginFactory
    public static DailyFileAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginElement("Filter") final Filter filter,
            @PluginAttribute("fileName") String fileName,
            @PluginAttribute("datePattern") String datePattern) {
        if (name == null) {
            LOGGER.error("No name provided for DailyFileAppender");
            return null;
        }
        if (layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        return new DailyFileAppender(name, filter, layout, fileName, datePattern, true);
    }

    public void append(LogEvent logEvent) {
        readLock.lock();
        try {
            final byte[] bytes = getLayout().toByteArray(logEvent);
            writeToFile(bytes);
        } catch (Exception ex) {
            if (!ignoreExceptions()) {
                throw new AppenderLoggingException(ex);
            }
        } finally {
            readLock.unlock();
        }
    }

    private void writeToFile(byte[] bytes) {
        FileOutputStream fileOutputStream = null;
        try {
            String fileNameWithDate = this.fileName + getCurrentData();
            fileOutputStream = new FileOutputStream(fileNameWithDate, true);
            byte[] buffer = bytes;
            fileOutputStream.write(buffer, 0, buffer.length);
        } catch (Exception ex) {

        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getCurrentData() {
        Date dateNow = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(this.datePattern);
        return simpleDateFormat.format(dateNow);
    }
}

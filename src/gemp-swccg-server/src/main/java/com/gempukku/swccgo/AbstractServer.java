package com.gempukku.swccgo;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public abstract class AbstractServer {
    private static Logger _logger = LogManager.getLogger(AbstractServer.class);
    private static ServerCleaner _cleaningTask = new ServerCleaner();

    private boolean _started;

    public void startServer() {
        if (!_started) {
            _cleaningTask.addServer(this);
            _started = true;
            _logger.debug("Started: "+getClass().getSimpleName());
            doAfterStartup();
        }
    }

    protected void doAfterStartup() {

    }

    public void stopServer() {
        if (_started) {
            _cleaningTask.removeServer(this);
            _started = false;
            _logger.debug("Stopped: "+getClass().getSimpleName());
        }
    }

    protected abstract void cleanup();
}

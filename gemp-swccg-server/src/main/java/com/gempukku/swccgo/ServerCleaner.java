package com.gempukku.swccgo;

import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ServerCleaner {
    private static final Logger LOG = Logger.getLogger(ServerCleaner.class);
    private final Set<AbstractServer> _servers = Collections.synchronizedSet(new HashSet<AbstractServer>());
    private CleaningThread _thr;

    public synchronized void addServer(AbstractServer server) {
        LOG.debug("Adding server: " + server.getClass());
        _servers.add(server);
        if (_thr == null) {
            _thr = new CleaningThread();
            _thr.start();
        }
    }

    public synchronized void removeServer(AbstractServer server) {
        _servers.remove(server);
        if (_servers.size() == 0 && _thr != null) {
            _thr.pleaseStop();
            _thr = null;
        }
    }

    private class CleaningThread extends Thread {
        private boolean _stopped;

        public void run() {
            try {
                while (!_stopped) {
                    synchronized (ServerCleaner.this) {
                        for (AbstractServer server : _servers) {
                            try {
                                server.cleanup();
                            } catch (Exception exp) {
                                // We can't do much about it
                                LOG.error("Error while cleaning up a server", exp);
                            }
                        }
                    }
                    Thread.sleep(1000);
                }
            } catch (InterruptedException exp) {
                // Thread interrupted - get lost
            }
        }

        public void pleaseStop() {
            _stopped = true;
        }
    }
}

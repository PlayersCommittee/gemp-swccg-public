package com.gempukku.polling;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;

public class LongPollingSystem {
    private static final Logger _log = LogManager.getLogger(LongPollingSystem.class);

    private final Set<ResourceWaitingRequest> _waitingActions = Collections.synchronizedSet(new HashSet<ResourceWaitingRequest>());

    private final long _pollingInterval = 1000;
    private final long _pollingLength = 2500;

    private ProcessingRunnable _timeoutRunnable;
    private final ExecutorService _executorService = new ThreadPoolExecutor(10, Integer.MAX_VALUE,
            60L, TimeUnit.SECONDS,
            new SynchronousQueue<Runnable>());

    public void start() {
        _timeoutRunnable = new ProcessingRunnable();
        Thread thr = new Thread(_timeoutRunnable);
        thr.start();
    }

    public void processLongPollingResource(LongPollingResource resource, LongPollableResource pollableResource) {
        ResourceWaitingRequest request = new ResourceWaitingRequest(pollableResource, resource, System.currentTimeMillis());
        if (pollableResource.registerRequest(request)) {
            execute(resource);
        } else {
            _waitingActions.add(request);
        }
    }

    private void pause() {
        try {
            Thread.sleep(_pollingInterval);
        } catch (InterruptedException exp) {
            // Ignore
        }
    }

    private void processWaitingRequest(final ResourceWaitingRequest request) {
        _waitingActions.remove(request);
        execute(request.getLongPollingResource());
    }

    private void execute(final LongPollingResource resource) {
        _executorService.submit(
                new Runnable() {
                    @Override
                    public void run() {
                        resource.processIfNotProcessed();
                    }
                });
    }

    private class ProcessingRunnable implements Runnable {
        @Override
        public void run() {
            while (true) {
                Set<ResourceWaitingRequest> resourcesCopy;
                synchronized (_waitingActions) {
                    resourcesCopy = new HashSet<ResourceWaitingRequest>(_waitingActions);
                }
                
                long now = System.currentTimeMillis();
                Iterator<ResourceWaitingRequest> iterator = resourcesCopy.iterator();
                while (iterator.hasNext()) {
                    ResourceWaitingRequest waitingRequest = iterator.next();
                    if (waitingRequest.getLongPollingResource().wasProcessed())
                        _waitingActions.remove(waitingRequest);
                    else {
                        if (waitingRequest.getStart() + _pollingLength < now) {
                            waitingRequest.getLongPollableResource().unregisterRequest(waitingRequest);
                            _waitingActions.remove(waitingRequest);
                            execute(waitingRequest.getLongPollingResource());
                        }
                    }
                }

                pause();
            }
        }
    }

    private class ResourceWaitingRequest implements WaitingRequest {
        private final LongPollingResource _longPollingResource;
        private final LongPollableResource _longPollableResource;
        private final long _start;

        private ResourceWaitingRequest(LongPollableResource longPollableResource, LongPollingResource longPollingResource, long start) {
            _longPollableResource = longPollableResource;
            _longPollingResource = longPollingResource;
            _start = start;
        }

        @Override
        public void processRequest() {
            processWaitingRequest(this);
        }

        public LongPollableResource getLongPollableResource() {
            return _longPollableResource;
        }

        public LongPollingResource getLongPollingResource() {
            return _longPollingResource;
        }

        public long getStart() {
            return _start;
        }
    }
}

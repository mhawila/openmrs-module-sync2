package org.openmrs.module.sync2.api.sync;

import org.openmrs.module.sync2.api.model.TemporaryQueue;

import java.util.LinkedList;
import java.util.List;

/**
 * @uthor Willa Mhawila<a.mhawila@gmail.com> on 11/13/19.
 */
public class TemporaryDataIntegrityException extends Exception {
    private List<TemporaryQueue> temporaryQueueList = new LinkedList<>();
    public TemporaryDataIntegrityException(TemporaryQueue temporaryQueue) {
        super("expected records missing in target");
        temporaryQueueList.add(temporaryQueue);
    }

    public TemporaryDataIntegrityException(List<TemporaryQueue> temporaryQueueList) {
        super("expected records missing in target");
        this.temporaryQueueList = temporaryQueueList;
    }

    public List<TemporaryQueue> getTemporaryQueueList() {
        return temporaryQueueList;
    }
}

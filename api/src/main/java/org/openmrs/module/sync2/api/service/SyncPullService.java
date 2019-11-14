package org.openmrs.module.sync2.api.service;

import com.sun.corba.se.impl.orbutil.concurrent.Sync;
import org.openmrs.module.sync2.api.model.SyncCategory;
import org.openmrs.module.sync2.api.model.TemporaryQueue;
import org.openmrs.module.sync2.api.model.audit.AuditMessage;

import java.util.List;
import java.util.Map;

public interface SyncPullService {

    AuditMessage pullAndSaveObjectFromParent(SyncCategory category, Map<String, String> resourceLinks,
                                           String action);

    AuditMessage pullAndSaveObjectFromParent(SyncCategory category, Map<String, String> resourceLinks,
            String action, String clientName, String uuid);

    AuditMessage retrySynchingPendingObjectFromParent(TemporaryQueue temporaryQueue);

    List<AuditMessage> pullAndSaveObjectFromParent(SyncCategory category, String uuid);

    void pullAndSaveObjectsFromParent(SyncCategory category);
}

package org.openmrs.module.sync2.api.dao;

import org.junit.Test;
import org.openmrs.module.sync2.api.model.SyncCategory;
import org.openmrs.module.sync2.api.model.TemporaryQueue;
import org.openmrs.module.sync2.api.model.enums.OpenMRSSyncInstance;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * This test class really should NOT be in this omod module, however some very messed configuration involving rest module makes running
 * context sensitive in API module really difficult.
 * @uthor Willa Mhawila<a.mhawila@gmail.com> on 11/7/19.
 */
public class TemporaryQueueDaoImplTest extends BaseModuleWebContextSensitiveTest {
    @Autowired
    private TemporaryQueueDao temporaryQueueDao;

    @Test
    public void saveTemporaryQueueShouldWork() {
        TemporaryQueue item = temporaryQueueDao.save(createTemporaryQueue());
        assertNotNull(item.getId());
    }

    @Test
    public void getByIdShouldReturnEntityIfPresent() {
        TemporaryQueue item = temporaryQueueDao.save(createTemporaryQueue());
        Long id = item.getId();
        item = null;
        item = temporaryQueueDao.getById(id);
        assertNotNull(item);
        assertEquals(id, item.getId());
    }

    @Test
    public void deleteByIdShouldDeleteValidId() {
        TemporaryQueue item = temporaryQueueDao.save(createTemporaryQueue());
        temporaryQueueDao.deleteById(item.getId());
        TemporaryQueue again = temporaryQueueDao.getById(item.getId());
        assertNull(again);
    }

    private TemporaryQueue createTemporaryQueue() {
        TemporaryQueue tq = new TemporaryQueue();
        SyncCategory sc = new SyncCategory("encounter", org.openmrs.Encounter.class);
        tq.setSyncCategory(sc);
        tq.setAction("create");
        tq.setStatus(TemporaryQueue.Status.PENDING);
        tq.setReason("Did not sync because parent encounter object with uuid test-uuid does not exist");
        tq.setDateCreated(new Date());
        tq.setInstance(OpenMRSSyncInstance.CHILD);
        tq.setObject(createSimpleObject());
        return tq;
    }

    private SimpleObject createSimpleObject() {
        SimpleObject object = new SimpleObject().add("mtu", new LinkedHashMap<>()).add("umri", 20).add("hobbies", new ArrayList<String>());
        ((Map) object.get("mtu")).put("jina", "jitu bingwa");
        ((List) object.get("hobbies")).addAll(Arrays.asList("bangi", "vitombo", "bata"));

        return object;
    }
}

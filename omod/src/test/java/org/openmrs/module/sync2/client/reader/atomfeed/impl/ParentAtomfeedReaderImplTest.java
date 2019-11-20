package org.openmrs.module.sync2.client.reader.atomfeed.impl;

import org.junit.Test;
import org.openmrs.module.sync2.SyncConstants;
import org.openmrs.module.sync2.client.reader.ParentFeedReader;
import org.openmrs.web.test.BaseModuleWebContextSensitiveTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.reflect.Field;

import static org.junit.Assert.assertNotNull;

/**
 * @uthor Willa Mhawila<a.mhawila@gmail.com> on 11/15/19.
 */
public class ParentAtomfeedReaderImplTest extends BaseModuleWebContextSensitiveTest {
    @Autowired
    @Qualifier("sync2.parentFeedReader." + SyncConstants.ATOMFEED_EVENT_HANDLER)
    private ParentFeedReader reader;

    // I am doing this ridiculous test here to see whether dependencies are injected properly (Silly me!)
    @Test
    public void ensureDependencies() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException {
        assertNotNull(reader);
        Field tempQueueServiceField = reader.getClass().getDeclaredField("temporaryQueueService");
        tempQueueServiceField.setAccessible(true);
        assertNotNull(tempQueueServiceField.get(reader));
    }
}

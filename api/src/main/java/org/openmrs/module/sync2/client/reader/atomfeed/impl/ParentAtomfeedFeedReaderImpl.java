package org.openmrs.module.sync2.client.reader.atomfeed.impl;

import org.openmrs.module.sync2.SyncConstants;
import org.openmrs.module.sync2.api.exceptions.SyncException;
import org.openmrs.module.sync2.api.model.SyncCategory;
import org.openmrs.module.sync2.api.model.TemporaryQueue;
import org.openmrs.module.sync2.api.model.configuration.SyncMethodConfiguration;
import org.openmrs.module.sync2.api.service.SyncPullService;
import org.openmrs.module.sync2.api.service.TemporaryQueueService;
import org.openmrs.module.sync2.client.reader.ParentFeedReader;
import org.openmrs.module.sync2.client.reader.atomfeed.ParentAtomfeedFeedWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("sync2.parentFeedReader." + SyncConstants.ATOMFEED_EVENT_HANDLER)
public class ParentAtomfeedFeedReaderImpl extends AbstractAtomfeedFeedReader implements ParentFeedReader {
	@Autowired private SyncPullService syncPullService;
	@Autowired private TemporaryQueueService temporaryQueueService;


	public ParentAtomfeedFeedReaderImpl() {
		super(new ParentAtomfeedFeedWorker());
	}

	@Override
	protected SyncMethodConfiguration getSyncMethodConf() {
		return configurationService.getSyncConfiguration().getPull();
	}

	@Override
	protected String getBaseUri() {
		return configurationService.getSyncConfiguration().getGeneral().getParentFeedLocation();
	}

	@Override
	public void pullAndProcessAllFeeds() {
		Date startDate = new Date();
		readAndProcessAllFeeds();

		long countOfPendingEvents = temporaryQueueService.getCountOfAllTemporaryQueue(startDate, TemporaryQueue.Status.PENDING);
		if(countOfPendingEvents > 0) {
			// Let us deal with pending stuff.
			List<TemporaryQueue> pendingItems = temporaryQueueService.getAllTemporaryQueue(startDate, TemporaryQueue.Status.PENDING);

			for(TemporaryQueue queueItem: pendingItems) {
				// Attempt again.
			}
		}
	}

	@Override
	public void pullAndProcessFeeds(SyncCategory category) throws SyncException {
		readAndProcessFeedByCategory(category.getCategory());
	}
}

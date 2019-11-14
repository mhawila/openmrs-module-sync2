package org.openmrs.module.sync2.api.dao;

import org.openmrs.module.sync2.api.model.TemporaryQueue;

import java.util.Date;
import java.util.List;

/**
 * @uthor Willa Mhawila<a.mhawila@gmail.com> on 11/7/19.
 */
public interface TemporaryQueueDao {
    TemporaryQueue save(TemporaryQueue temporaryQueue);
    TemporaryQueue deleteById(Long id);
    TemporaryQueue getById(Long id);

    /**
     * Returns a list of all TemporaryQueue items between startDate (inclusive) and endDate(exclusive)
     * @param status
     * @param startDate
     * @param endDate
     * @param startIndex where to start counting (the first result)
     * @param pageSize (Maximum number of records to return from the startIndex
     * @return
     */
    List<TemporaryQueue> getAll(TemporaryQueue.Status status, Date startDate, Date endDate, Integer startIndex, Integer pageSize);

    Long getCountOfAll(TemporaryQueue.Status status, Date startDate, Date endDate);
}

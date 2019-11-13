package org.openmrs.module.sync2.api.dao;

import org.openmrs.module.sync2.api.model.TemporaryQueue;

import java.util.Date;
import java.util.List;

/**
 * @uthor Willa Mhawila<a.mhawila@gmail.com> on 11/7/19.
 */
public interface TemporaryQueueDao {
    TemporaryQueue saveTemporaryQueue(TemporaryQueue temporaryQueue);
    TemporaryQueue deleteById(Long id);
    TemporaryQueue getById(Long id);
    List<TemporaryQueue> getAll();
    List<TemporaryQueue> getAll(Date startDate, Date endDate, Integer startIndex, Integer pageSize);
}

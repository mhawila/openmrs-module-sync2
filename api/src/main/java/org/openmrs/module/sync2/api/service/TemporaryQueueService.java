package org.openmrs.module.sync2.api.service;

import org.openmrs.module.sync2.api.model.TemporaryQueue;

import java.util.Date;
import java.util.List;

/**
 * @uthor Willa Mhawila<a.mhawila@gmail.com> on 11/13/19.
 */
public interface TemporaryQueueService {
    TemporaryQueue saveTemporaryQueue(TemporaryQueue temporaryQueue);
    void saveTemporaryQueueList(List<TemporaryQueue> temporaryQueueList);
    TemporaryQueue getTemporaryQueueById(Long id);
    TemporaryQueue deleteTemporaryQueueById(Long id);
    void deleteTemporaryQueue(TemporaryQueue temporaryQueue);
    List<TemporaryQueue> getAllTemporaryQueue();
    List<TemporaryQueue> getAllTemporaryQueue(TemporaryQueue.Status status);
    List<TemporaryQueue> getAllTemporaryQueue(Date startDate, Date endDate);
    List<TemporaryQueue> getAllTemporaryQueue(Date startDate, Date endDate, TemporaryQueue.Status status);
    List<TemporaryQueue> getAllTemporaryQueue(Date startDate, TemporaryQueue.Status status);
    List<TemporaryQueue> getAllTemporaryQueue(Date startDate, Date endDate, Integer startIndex, Integer pageSize);
    List<TemporaryQueue> getAllTemporaryQueue(Date startDate, Date endDate, TemporaryQueue.Status status, Integer startIndex, Integer pageSize);

    Long getCountOfAllTemporaryQueue();
    Long getCountOfAllTemporaryQueue(TemporaryQueue.Status status);
    Long getCountOfAllTemporaryQueue(Date startDate, Date endDate);
    Long getCountOfAllTemporaryQueue(Date startDate, Date endDate, TemporaryQueue.Status status);
    Long getCountOfAllTemporaryQueue(Date startDate, TemporaryQueue.Status status);
}

package org.openmrs.module.sync2.api.service.impl;

import org.openmrs.module.sync2.api.dao.TemporaryQueueDao;
import org.openmrs.module.sync2.api.model.TemporaryQueue;
import org.openmrs.module.sync2.api.service.TemporaryQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @uthor Willa Mhawila<a.mhawila@gmail.com> on 11/13/19.
 */
@Service("sync2.temporaryQueueService")
@Transactional
public class TemporaryQueueServiceImpl implements TemporaryQueueService {
    @Autowired
    private TemporaryQueueDao temporaryQueueDao;

    @Override
    public TemporaryQueue saveTemporaryQueue(@NotNull final TemporaryQueue temporaryQueue) {
        return temporaryQueueDao.save(temporaryQueue);
    }

    @Override
    public void saveTemporaryQueueList(@NotNull final List<TemporaryQueue> temporaryQueueList) {
        for(TemporaryQueue tq: temporaryQueueList) {
            saveTemporaryQueue(tq);
        }
    }

    @Override
    public TemporaryQueue getTemporaryQueueById(@NotNull final Long id) {
        return temporaryQueueDao.getById(id);
    }

    @Override
    public TemporaryQueue deleteTemporaryQueueById(final Long id) {
        return temporaryQueueDao.deleteById(id);
    }

    @Override
    public void deleteTemporaryQueue(TemporaryQueue temporaryQueue) {
        temporaryQueueDao.deleteById(temporaryQueue.getId());
    }

    @Override
    public List<TemporaryQueue> getAllTemporaryQueue() {
        return temporaryQueueDao.getAll(null,null, null, null, null);
    }

    @Override
    public List<TemporaryQueue> getAllTemporaryQueue(final TemporaryQueue.Status status) {
        return temporaryQueueDao.getAll(status,null, null, null, null);
    }

    @Override
    public List<TemporaryQueue> getAllTemporaryQueue(Date startDate, Date endDate) {
        return temporaryQueueDao.getAll(null, startDate, endDate, null, null);
    }

    @Override
    public List<TemporaryQueue> getAllTemporaryQueue(Date startDate, TemporaryQueue.Status status) {
        return temporaryQueueDao.getAll(status, startDate, null, null, null);
    }

    @Override
    public List<TemporaryQueue> getAllTemporaryQueue(Date startDate, Date endDate, TemporaryQueue.Status status) {
        return temporaryQueueDao.getAll(status, startDate, endDate, null, null);
    }

    @Override
    public List<TemporaryQueue> getAllTemporaryQueue(Date startDate, Date endDate, Integer startIndex, Integer pageSize) {
        return temporaryQueueDao.getAll(null, startDate, endDate, startIndex, pageSize);
    }

    @Override
    public List<TemporaryQueue> getAllTemporaryQueue(Date startDate, Date endDate, TemporaryQueue.Status status, Integer startIndex, Integer pageSize) {
        return temporaryQueueDao.getAll(status, startDate, endDate, startIndex, pageSize);
    }

    @Override
    public Long getCountOfAllTemporaryQueue() {
        return temporaryQueueDao.getCountOfAll(null,null, null);
    }

     @Override
    public Long getCountOfAllTemporaryQueue(@NotNull final TemporaryQueue.Status status) {
        return temporaryQueueDao.getCountOfAll(status,null, null);
    }

     @Override
    public Long getCountOfAllTemporaryQueue(@NotNull final Date startDate, @NotNull final Date endDate) {
        return temporaryQueueDao.getCountOfAll(null,startDate, endDate);
    }

    @Override
    public Long getCountOfAllTemporaryQueue(@NotNull final Date startDate, @NotNull final Date endDate, @NotNull final TemporaryQueue.Status status) {
        return temporaryQueueDao.getCountOfAll(status,startDate, endDate);
    }

    @Override
    public Long getCountOfAllTemporaryQueue(@NotNull final Date startDate, @NotNull final TemporaryQueue.Status status) {
        return temporaryQueueDao.getCountOfAll(status,startDate, null);
    }
}

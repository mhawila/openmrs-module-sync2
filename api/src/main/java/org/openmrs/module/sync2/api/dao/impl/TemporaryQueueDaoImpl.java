package org.openmrs.module.sync2.api.dao.impl;

import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.sync2.api.dao.TemporaryQueueDao;
import org.openmrs.module.sync2.api.model.TemporaryQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @uthor Willa Mhawila<a.mhawila@gmail.com> on 11/7/19.
 */
@Repository
public class TemporaryQueueDaoImpl implements TemporaryQueueDao {
    @Autowired
    private DbSessionFactory sessionFactory;

    private DbSession getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public TemporaryQueue saveTemporaryQueue(TemporaryQueue temporaryQueue) {
        getSession().saveOrUpdate(temporaryQueue);
        return temporaryQueue;
    }

    @Override
    public TemporaryQueue deleteById(Long id) {
        return null;
    }

    @Override
    public TemporaryQueue getById(Long id) {
        return null;
    }

    @Override
    public List<TemporaryQueue> getAll() {
        return null;
    }

    @Override
    public List<TemporaryQueue> getAll(Date startDate, Date endDate, Integer startIndex, Integer pageSize) {
        return null;
    }
}

package org.openmrs.module.sync2.api.dao.impl;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.openmrs.api.db.hibernate.DbSession;
import org.openmrs.api.db.hibernate.DbSessionFactory;
import org.openmrs.module.sync2.api.dao.TemporaryQueueDao;
import org.openmrs.module.sync2.api.model.TemporaryQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
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
    public TemporaryQueue save(@NotNull final TemporaryQueue temporaryQueue) {
        getSession().saveOrUpdate(temporaryQueue);
        return temporaryQueue;
    }

    @Override
    public TemporaryQueue deleteById(@NotNull final Long id) {
        TemporaryQueue tp = getById(id);
        if(tp != null) {
            getSession().delete(tp);
        }
        return tp;
    }

    @Override
    public TemporaryQueue getById(@NotNull final Long id) {
        return (TemporaryQueue) getSession().get(TemporaryQueue.class, id);
    }

    @Override
    public List<TemporaryQueue> getAll(Date startDate, Date endDate, Integer startIndex, Integer pageSize) {
        Criteria criteria = createCriteria(startDate, endDate, startIndex, pageSize);
        criteria.addOrder(Order.desc("dateCreated"));
        return criteria.list();
    }

    @Override
    public Long getCountOfAll(Date startDate, Date endDate) {
        Criteria criteria = createCriteria(startDate, endDate, null, null);
        criteria.setProjection(Projections.rowCount());
        return (Long) criteria.uniqueResult();
    }

    protected Criteria createCriteria(Date startDate, Date endDate, Integer startIndex, Integer pageSize) {
        Criteria criteria = getSession().createCriteria(TemporaryQueue.class, "tq");

        if(startDate != null) {
            criteria.add(Restrictions.ge("dateCreated", startDate));
        }

        if(endDate != null) {
            criteria.add(Restrictions.lt("dateCreated", endDate));
        }

        if(startIndex != null) {
            criteria.setFirstResult(startIndex);
        }

        if(pageSize != null) {
            criteria.setMaxResults(pageSize);
        }

        return criteria;
    }
}

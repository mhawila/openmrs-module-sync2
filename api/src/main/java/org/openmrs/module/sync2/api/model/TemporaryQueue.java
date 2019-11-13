package org.openmrs.module.sync2.api.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.openmrs.module.sync2.api.model.enums.OpenMRSSyncInstance;
import org.openmrs.module.webservices.rest.SimpleObject;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

/**
 * @uthor Willa Mhawila<a.mhawila@gmail.com> on 11/7/19.
 */
@Entity
@Table(name = "sync2_temporary_queue")
@Access(AccessType.PROPERTY)
public class TemporaryQueue implements Serializable {
    public enum Status {
        COMPLETED, PENDING
    }

    private Long id;
    private Date dateCreated;
    private SyncCategory syncCategory;
    private SimpleObject object;
    private String resourceUrl;
    private String action;
    private OpenMRSSyncInstance instance;
    private Status status;
    private String pendingReason;

    private static SimpleObjectConverter CONVERTER = new SimpleObjectConverter();

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "date_created")
    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    @Embedded
    public SyncCategory getSyncCategory() {
        return syncCategory;
    }

    public void setSyncCategory(SyncCategory syncCategory) {
        this.syncCategory = syncCategory;
    }

    @Transient
    public SimpleObject getObject() {
        return object;
    }

    public void setObject(SimpleObject object) {
        this.object = object;
    }

    @Column(name = "object_json")
    public String getObjectJson() {
        return CONVERTER.convertToDatabaseColumn(object);
    }

    public void setObjectJson(String objectJson) {
        this.setObject(CONVERTER.convertToEntityAttribute(objectJson));
    }

    @Column(name = "resource_url")
    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @Enumerated(EnumType.STRING)
    public OpenMRSSyncInstance getInstance() {
        return instance;
    }

    public void setInstance(OpenMRSSyncInstance instance) {
        this.instance = instance;
    }

    @Enumerated(EnumType.STRING)
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Column(name = "pending_reason")
    public String getPendingReason() {
        return pendingReason;
    }

    public void setPendingReason(String pendingReason) {
        this.pendingReason = pendingReason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        TemporaryQueue that = (TemporaryQueue) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .toHashCode();
    }
}

package org.openmrs.module.sync2.api.model;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.codehaus.jackson.map.ObjectMapper;
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
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * @uthor Willa Mhawila<a.mhawila@gmail.com> on 11/7/19.
 */
@Entity
@Table(name = "sync2_temporary_queue")
@Access(AccessType.PROPERTY)
public class TemporaryQueue implements Serializable {
    public enum Status {
        COMPLETED, PENDING, RETRIED_FAILED
    }

    private Long id;
    private Date dateCreated;
    private SyncCategory syncCategory;
    private SimpleObject object;
    private Map<String, String> resourceLinksMap;
    private String client;
    private String action;
    private OpenMRSSyncInstance instance;
    private Status status;
    private String reason;
    private String uuid;

    private static SimpleObjectConverter CONVERTER = new SimpleObjectConverter();
    private static ObjectMapper mapper = new ObjectMapper();

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

    @Transient
    public Map<String, String> getResourceLinksMap() {
        return resourceLinksMap;
    }

    public void setResourceLinksMap(Map<String, String> resourceLinks) {
        this.resourceLinksMap = resourceLinks;
    }

    @Column(name = "resource_links")
    public String getResourceLinks() {
        try {
            return mapper.writeValueAsString(getResourceLinksMap());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setResourceLinks(String resourceLinks) {
        try {
            Map<String, String> linksMap = mapper.readValue(resourceLinks, Map.class);
            setResourceLinksMap(linksMap);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
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
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

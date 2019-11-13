package org.openmrs.module.sync2.api.model;

import com.google.common.base.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.io.Serializable;

@Embeddable
@Access(AccessType.PROPERTY)
public class SyncCategory implements Serializable {

	private static final long serialVersionUID = 1424417880407750927L;

	private String category;

	private Class clazz;

	private static ClassConverter CONVERTER = new ClassConverter();

	public SyncCategory() {
	}

	public SyncCategory(String category, Class clazz) {
		this.category = category;
		this.clazz = clazz;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Transient
	public Class getClazz() {
		return clazz;
	}

	public void setClazz(Class clazz) {
		this.clazz = clazz;
	}

	@Column(name = "class_name")
	public String getClassName() {
		return CONVERTER.convertToDatabaseColumn(clazz);
	}

	public void setClassName(String className) {
		this.setClazz(CONVERTER.convertToEntityAttribute(className));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		SyncCategory that = (SyncCategory) o;
		return Objects.equal(category, that.category) &&
				Objects.equal(clazz, that.clazz);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(category, clazz);
	}
}

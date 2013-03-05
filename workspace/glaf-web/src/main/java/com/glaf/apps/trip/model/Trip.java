package com.glaf.apps.trip.model;

import java.io.*;
import java.util.*;
import javax.persistence.*;
import com.alibaba.fastjson.*;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import com.glaf.core.base.*;
import com.glaf.core.util.DateUtils;
import com.glaf.apps.trip.util.*;

@Entity
@Table(name = "X_APP_TRIP")
public class Trip implements Serializable {
	private static final long serialVersionUID = 1L;

        @Id
	@Column(name = "ID_", length = 50, nullable = false)
	protected String id;

	

        /**
        *��ͨ����
        */
        @Column(name = "TRANSTYPE_")
        protected String transType;

        @Transient
        protected String transTypeName;

        /**
        *��������
        */
        @Temporal(TemporalType.TIMESTAMP)
        @Column(name = "APPLYDATE_")
        protected Date applyDate;

        /**
        *��ʼ����
        */
        @Temporal(TemporalType.TIMESTAMP)
        @Column(name = "STARTDATE_")
        protected Date startDate;

        /**
        *��������
        */
        @Temporal(TemporalType.TIMESTAMP)
        @Column(name = "ENDDATE_")
        protected Date endDate;

        /**
        *����
        */
        @Column(name = "DAYS_")
        protected Double days;

        /**
        *����
        */
        @Column(name = "MONEY_")
        protected Double money;

        /**
        *����
        */
        @Column(name = "CAUSE_")
        protected String cause;

        /**
        *��������
        */
        @Temporal(TemporalType.TIMESTAMP)
        @Column(name = "CREATEDATE_")
        protected Date createDate;

        /**
        *������
        */
        @Column(name = "CREATEBY_")
        protected String createBy;

        /**
        *������
        */
        @Transient
        protected String createByName;

        /**
        *�޸�����
        */
        @Temporal(TemporalType.TIMESTAMP)
        @Column(name = "UPDATEDATE_")
        protected Date updateDate;

        /**
        *�Ƿ�����
        */
        @Column(name = "LOCKED_")
        protected Integer locked;

        /**
        *ɾ�����
        */
        @Column(name = "DELETEFLAG_")
        protected Integer deleteFlag;

        /**
        *״̬
        */
        @Column(name = "STATUS_")
        protected Integer status;

        /**
        *��������
        */
        @Column(name = "PROCESSNAME_")
        protected String processName;

        /**
        *����ʵ�����
        */
        @Column(name = "PROCESSINSTANCEID_")
        protected String processInstanceId;

         
	public Trip() {

	}

	public String getId(){
	    return this.id;
	}



	

        public String getTransType(){
            return this.transType;
        }

        public String getTransTypeName(){
            return this.transTypeName;
        }

        public Date getApplyDate(){
            return this.applyDate;
        }

        public Date getStartDate(){
            return this.startDate;
        }

        public Date getEndDate(){
            return this.endDate;
        }

        public Double getDays(){
            return this.days;
        }

        public Double getMoney(){
            return this.money;
        }

        public String getCause(){
            return this.cause;
        }

        public Date getCreateDate(){
            return this.createDate;
        }

        public String getCreateBy(){
            return this.createBy;
        }

        public String getCreateByName(){
            return this.createByName;
        }

        public Date getUpdateDate(){
            return this.updateDate;
        }

        public Integer getLocked(){
            return this.locked;
        }

        public Integer getDeleteFlag(){
            return this.deleteFlag;
        }

        public Integer getStatus(){
            return this.status;
        }

        public String getProcessName(){
            return this.processName;
        }

        public String getProcessInstanceId(){
            return this.processInstanceId;
        }


	public void setId(String id) {
	    this.id = id; 
	}

	

        public void  setTransType(String transType){
              this.transType=transType;
        }

        public void  setTransTypeName(String transTypeName){
              this.transTypeName = transTypeName;
        }

        public void  setApplyDate(Date applyDate){
              this.applyDate=applyDate;
        }

        public void  setStartDate(Date startDate){
              this.startDate=startDate;
        }

        public void  setEndDate(Date endDate){
              this.endDate=endDate;
        }

        public void  setDays(Double days){
              this.days=days;
        }

        public void  setMoney(Double money){
              this.money=money;
        }

        public void  setCause(String cause){
              this.cause=cause;
        }

        public void  setCreateDate(Date createDate){
              this.createDate=createDate;
        }

        public void  setCreateBy(String createBy){
              this.createBy=createBy;
        }

        public void  setCreateByName(String createByName){
              this.createByName=createByName;
        }

        public void  setUpdateDate(Date updateDate){
              this.updateDate=updateDate;
        }

        public void  setLocked(Integer locked){
              this.locked=locked;
        }

        public void  setDeleteFlag(Integer deleteFlag){
              this.deleteFlag=deleteFlag;
        }

        public void  setStatus(Integer status){
              this.status=status;
        }

        public void  setProcessName(String processName){
              this.processName=processName;
        }

        public void  setProcessInstanceId(String processInstanceId){
              this.processInstanceId=processInstanceId;
        }


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Trip other = (Trip) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((id == null) ? 0 : id.hashCode());
		return result;
	}


	public Trip jsonToObject(JSONObject jsonObject) {
            return TripJsonFactory.jsonToObject(jsonObject);
	}


	public JSONObject toJsonObject() {
            return TripJsonFactory.toJsonObject(this);
	}

	public ObjectNode toObjectNode(){
            return TripJsonFactory.toObjectNode(this);
	}


	public String toString() {
		return ToStringBuilder.reflectionToString(this,
				ToStringStyle.MULTI_LINE_STYLE);
	}

}
package com.vmware.irctc.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonInclude;


@Entity
@Table(name = "Coaches")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Coaches {
	
	private Integer coachId;
	private String coachName;
	private Category category;
	private Integer trainId;
	
	public Coaches(){
		// empty
	}

	@Id
    @Column(name = "Coach_Id")
	public Integer getCoachId() {
		return coachId;
	}

	public void setCoachId(Integer coachId) {
		this.coachId = coachId;
	}

	@Column(name = "Coach_Name")
	public String getCoachName() {
		return coachName;
	}

	public void setCoachName(String coachName) {
		this.coachName = coachName;
	}

	@ManyToOne(targetEntity = Category.class)
    @JoinColumn(name = "Category_Id", referencedColumnName = "Category_Id")
	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	@Column(name = "Train_Id")
	public Integer getTrainId() {
		return trainId;
	}

	public void setTrainId(Integer trainId) {
		this.trainId = trainId;
	}
	
	

}

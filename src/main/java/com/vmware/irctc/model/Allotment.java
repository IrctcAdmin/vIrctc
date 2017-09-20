package com.vmware.irctc.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "Allotment")
public class Allotment {
	
	private Integer allotmentId;
	private Integer seatId;
	private Integer coachId;
	private Integer seatNumber;
	private Integer bookingId;
	
	public Allotment(){
		// empty
	}
	
	@Id
	@Column(name = "Allotment_Id")
	public Integer getAllotmentId() {
		return allotmentId;
	}

	public void setAllotmentId(Integer allotmentId) {
		this.allotmentId = allotmentId;
	}

	@Column(name = "Seat_Id")
	public Integer getSeatId() {
		return seatId;
	}

	public void setSeatId(Integer seatId) {
		this.seatId = seatId;
	}

	@ManyToOne(targetEntity = Coaches.class)
    @JoinColumn(name = "Coach_Id", referencedColumnName = "Coach_Id")
	public Integer getCoachId() {
		return coachId;
	}

	public void setCoachId(Integer coachId) {
		this.coachId = coachId;
	}

	@Column(name = "Seat_Number")
	public Integer getSeatNumber() {
		return seatNumber;
	}

	public void setSeatNumber(Integer seatNumber) {
		this.seatNumber = seatNumber;
	}

	@Column(name = "Booking_Id")
	public Integer getBookingId() {
		return bookingId;
	}

	public void setBookingId(Integer bookingId) {
		this.bookingId = bookingId;
	}
	
	

}

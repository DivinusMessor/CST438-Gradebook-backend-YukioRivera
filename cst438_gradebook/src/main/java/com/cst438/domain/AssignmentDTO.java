package com.cst438.domain;

//import java.sql.Date;

public class AssignmentDTO {
	
	public int id;
	public String name;
	public String dueDate;
	public int needsGrading;
	public int courseId;
	
	
	// created the dto that will be used inside the controller
	// used for the getters 
	@Override
	public String toString() {
		return "Assignment [id=" + id + ", course_id=" + courseId + ", name=" + name + ", dueDate=" + dueDate
				+ ", needsGrading=" + needsGrading + "]";
	}

}

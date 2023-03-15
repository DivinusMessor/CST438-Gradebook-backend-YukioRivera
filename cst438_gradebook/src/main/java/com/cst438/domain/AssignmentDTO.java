package com.cst438.domain;

//import java.sql.Date;

public class AssignmentDTO {
	
	public int id;
	public String name;
	public String dueDate;
	public int needsGrading;
	public int courseId;
	
	
	
	@Override
	public String toString() {
		return "Assignment [id=" + id + ", course_id=" + courseId + ", name=" + name + ", dueDate=" + dueDate
				+ ", needsGrading=" + needsGrading + "]";
	}

}

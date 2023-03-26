package com.cst438.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Course;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;

@RestController
public class EnrollmentController {

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	EnrollmentRepository enrollmentRepository;

	/*
	 * endpoint used by registration service to add an enrollment to an existing
	 * course.
	 */
	@PostMapping("/enrollment") // Define a new POST endpoint to handle enrollment requests
	@Transactional // Use @Transactional annotation to ensure the enrollment process is atomic and can be rolled back in case of errors
	public EnrollmentDTO addEnrollment(@RequestBody EnrollmentDTO enrollmentDTO) {
		
	    // Create a new Enrollment object
	    Enrollment e = new Enrollment();
	    // Set the student email from the received enrollmentDTO
	    e.setStudentEmail(enrollmentDTO.studentEmail);
	    // Set the student name from the received enrollmentDTO
	    e.setStudentName(enrollmentDTO.studentName);

	    // Find the course in the courseRepository by the course_id provided in the enrollmentDTO
	    Course c = courseRepository.findById(enrollmentDTO.course_id).orElse(null);
	    // If the course is not found, throw an exception with a BAD_REQUEST status
	    if (c==null) {
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Course id not found");
	    }
	    // Set the found course to the Enrollment object
	    e.setCourse(c);

	    // Save the enrollment in the enrollmentRepository and return the saved instance
	    e = enrollmentRepository.save(e);
	    // Set the id of the enrollmentDTO to the id of the saved Enrollment object
	    enrollmentDTO.id = e.getId();

	    // Return the updated enrollmentDTO to the caller
	    return enrollmentDTO;
		
	}

}

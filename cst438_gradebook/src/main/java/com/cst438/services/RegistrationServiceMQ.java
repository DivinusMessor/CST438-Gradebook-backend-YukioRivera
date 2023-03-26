package com.cst438.services;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cst438.domain.Course;
import com.cst438.domain.CourseDTOG;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.EnrollmentDTO;
import com.cst438.domain.EnrollmentRepository;

public class RegistrationServiceMQ extends RegistrationService {

	@Autowired
	EnrollmentRepository enrollmentRepository;

	@Autowired
	CourseRepository courseRepository;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	public RegistrationServiceMQ() {
		System.out.println("MQ registration service ");
	}

	// ----- configuration of message queues

	@Autowired
	Queue registrationQueue;

	// ----- end of configuration of message queue

	//receiver of messages from Registration service
	@RabbitListener(queues = "gradebook-queue")
	@Transactional
	// Receive an EnrollmentDTO object and process it to create a new enrollment
	public void receive(EnrollmentDTO enrollmentDTO) {
	    // Find the course in the courseRepository using the course_id provided in the enrollmentDTO
	    Course course = courseRepository.findById(enrollmentDTO.course_id).orElse(null);

	    // Create a new Enrollment object
	    Enrollment newEnrollment = new Enrollment();

	    // Set the student email from the received enrollmentDTO
	    newEnrollment.setStudentEmail(enrollmentDTO.studentEmail);
	    // Set the student name from the received enrollmentDTO
	    newEnrollment.setStudentName(enrollmentDTO.studentName);
	    // Set the found course to the Enrollment object
	    newEnrollment.setCourse(course);

	    // Save the new enrollment in the enrollmentRepository
	    enrollmentRepository.save(newEnrollment);    
	}

	// sender of messages to Registration Service
	@Override
	public void sendFinalGrades(int course_id, CourseDTOG courseDTO) {
		
		// Send the courseDTO to the registrationQueue using RabbitMQ
		rabbitTemplate.convertAndSend(registrationQueue.getName(), courseDTO);
	}
}

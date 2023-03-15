package com.cst438.controllers;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.Id;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentDTO;
import com.cst438.domain.AssignmentListDTO;
import com.cst438.domain.AssignmentGrade;
import com.cst438.domain.AssignmentGradeRepository;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseDTOG;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.GradebookDTO;
import com.cst438.services.RegistrationService;

@RestController
@CrossOrigin(origins = {"http://localhost:3000","http://localhost:3001"})
public class AssignmentController {
	
	@Autowired
	AssignmentRepository assignmentRepository;
	
	@Autowired
	CourseRepository courseRepository;
	
	
		// add a new assignment to a course
		@PostMapping("/assignment/add/{name}/{dueDate}/{courseId}")
		@Transactional
		public AssignmentDTO createAssignment (@PathVariable String name, @PathVariable String dueDate, @PathVariable int courseId) {
			Assignment newAssignment;
			Course course = courseRepository.findById(courseId).orElse(null);
			if(course == null) {
				throw new ResponseStatusException (HttpStatus.BAD_REQUEST, "That courseID doesn't exist");
			} else {
			newAssignment = new Assignment();
			newAssignment.setName(name);
			newAssignment.setDueDate(java.sql.Date.valueOf(dueDate));
			newAssignment.setCourse(course);
			newAssignment.setNeedsGrading(1);
				
			Assignment saveAssignment = assignmentRepository.save(newAssignment);
			AssignmentDTO result = createAssignmentDTO(saveAssignment);
				
			return result;
			}
		}
		
		// update an assignment
		// currently only name change is supported
		@PutMapping("/assignment/update/{assignment_id}/{newName}")
		@Transactional
		public AssignmentDTO updateAssignment (@PathVariable int assignment_id, @PathVariable String newName) {
			
			// Assignment object 
			Assignment assignment = assignmentRepository.findById(assignment_id).orElse(null);
			
			// check for assignment_id
			if(null == assignment) {
				throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "AssignmentId not found" );
			}
			assignment.setName(newName);
			
			Assignment saveAssignment = assignmentRepository.save(assignment);
			AssignmentDTO result = createAssignmentDTO(saveAssignment);
			
			return result;
			
		}
		
		// delete an assignment
		@DeleteMapping("/assignment/delete/{assignment_id}")
		@Transactional
		public void deleteAssignment (@PathVariable int assignment_id) {
			
			// Assignment object 
			Assignment assignment = assignmentRepository.findById(assignment_id).orElse(null);
			
			// check for assignment_id
//			if(null == assignment) {
//				throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "AssignmentId not found." );
//			}
			
			// check that assignment has no grades 
			if(assignment.getNeedsGrading() == 0) {
				throw new ResponseStatusException( HttpStatus.BAD_REQUEST, "Assignments with grades cannot be deleted." );
			}
			
			assignmentRepository.delete(assignment);
		}
		
		
		private AssignmentDTO createAssignmentDTO(Assignment assignment) {
			AssignmentDTO assignmentDTO = new AssignmentDTO();
			assignmentDTO.id = assignment.getId();
			assignmentDTO.name = assignment.getName();
			assignmentDTO.dueDate = assignment.getDueDate().toString();
			assignmentDTO.needsGrading = assignment.getNeedsGrading();
			assignmentDTO.courseId = assignment.getCourse().getCourse_id();
			
			
			return assignmentDTO;
		}
}


package com.cst438;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.PostMapping;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.sql.Date;
import java.util.Optional;

import com.cst438.controllers.AssignmentController;
import com.cst438.controllers.GradeBookController;
import com.cst438.domain.Assignment;
import com.cst438.domain.AssignmentDTO;
import com.cst438.domain.AssignmentGrade;
import com.cst438.domain.AssignmentGradeRepository;
import com.cst438.domain.AssignmentRepository;
import com.cst438.domain.Course;
import com.cst438.domain.CourseDTOG;
import com.cst438.domain.CourseRepository;
import com.cst438.domain.Enrollment;
import com.cst438.domain.GradebookDTO;
import com.cst438.services.RegistrationService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.test.context.ContextConfiguration;

/* 
 * Example of using Junit with Mockito for mock objects
 *  the database repositories are mocked with test data.
 *  
 * Mockmvc is used to test a simulated REST call to the RestController
 * 
 * the http response and repository is verified.
 * 
 *   Note: This tests uses Junit 5.
 *  ContextConfiguration identifies the controller class to be tested
 *  addFilters=false turns off security.  (I could not get security to work in test environment.)
 *  WebMvcTest is needed for test environment to create Repository classes.
 */
@ContextConfiguration(classes = { AssignmentController.class})
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest
class JunitTestAssignment {

	static final String URL = "http://localhost:8081";
	public static final String TEST_NAME = "Assignment2";
	public static final int TEST_ASSIGNMENT_ID = 1;
	public static final int TEST_COURSE_ID = 999001;
	static Date TEST_DUE_DATE = new Date(3921-8-02);
	
	public static final String newName = "ChangedName";

	@MockBean
	AssignmentRepository assignmentRepository;

	@MockBean
	CourseRepository courseRepository; // must have this to keep Spring test happy

	@Autowired
	private MockMvc mvc;
	
	@Test
	void createAssignment() throws Exception{
		MockHttpServletResponse response;
		Assignment assignment = new Assignment();
		
		Course course = new Course();
		
		course.setCourse_id(TEST_COURSE_ID);
		
		assignment.setName(TEST_NAME);
		assignment.setId(TEST_ASSIGNMENT_ID);
		assignment.setCourse(course);
		assignment.setDueDate(TEST_DUE_DATE);
	
//		given(assignmentRepository.findById(TEST_ASSIGNMENT_ID)).willReturn(Optional.of(assignment)); // given wrong into to make sure it checks
		
		given(courseRepository.findById(TEST_COURSE_ID)).willReturn(Optional.of(course)); // simulate when rest goes to testdb and tries to get course
		
		given(assignmentRepository.save(any(Assignment.class))).willReturn(assignment); // if this works, new assign will be saved in repo
		
		response = mvc.perform(
                MockMvcRequestBuilders
                .post("/assignment/add/{name}/{dueDate}/{course}", TEST_NAME, TEST_DUE_DATE, TEST_COURSE_ID))
				.andReturn().getResponse(); // checking post 
		
		assertEquals(200, response.getStatus());
		
		AssignmentDTO assignmentDTO = fromJsonString(response.getContentAsString(), AssignmentDTO.class);
		assertEquals(TEST_ASSIGNMENT_ID, assignmentDTO.id);
//        boolean found = false;
//        if(assignmentDTO.id == (TEST_ASSIGNMENT_ID)) {
//            found = true;
//        }
//        assertEquals(true, found, "Assignment not found");
        verify(assignmentRepository, times(1)).save(any());
//
        System.out.println(assignmentDTO);	
	}
	
	@Test
	void updateAssignment() throws Exception{
		MockHttpServletResponse response;
		Assignment assignment = new Assignment();
		
		Course course = new Course();
		
		course.setCourse_id(TEST_COURSE_ID);
		
		assignment.setName(TEST_NAME);
		assignment.setId(TEST_ASSIGNMENT_ID);
		assignment.setCourse(course);
		assignment.setDueDate(TEST_DUE_DATE);
	
		given(assignmentRepository.findById(TEST_ASSIGNMENT_ID)).willReturn(Optional.of(assignment)); // given wrong into to make sure it checks
		
		given(assignmentRepository.save(any(Assignment.class))).willReturn(assignment); // if this works, new assign will be saved in repo
		
		response = mvc.perform(
                MockMvcRequestBuilders
                .put("/assignment/update/{assignment_id}/{newName}", TEST_ASSIGNMENT_ID, newName))
				.andReturn().getResponse(); // checking post 
		
		assertEquals(200, response.getStatus());
		
		AssignmentDTO assignmentDTO = fromJsonString(response.getContentAsString(), AssignmentDTO.class);

        verify(assignmentRepository, times(1)).save(any());

        System.out.println(assignmentDTO);	
	}
	
	@Test
	void deleteAssignment() throws Exception{
		MockHttpServletResponse response;
		Assignment assignment = new Assignment();
		
		Course course = new Course();
		
		course.setCourse_id(TEST_COURSE_ID);
		
		assignment.setName(TEST_NAME);
		assignment.setId(TEST_ASSIGNMENT_ID);
		assignment.setCourse(course);
		assignment.setDueDate(TEST_DUE_DATE);
		assignment.setNeedsGrading(1);
		
		given(assignmentRepository.findById(TEST_ASSIGNMENT_ID)).willReturn(Optional.of(assignment)); // given wrong into to make sure it checks
		
		response = mvc.perform(
                MockMvcRequestBuilders
                .delete("/assignment/delete/{assignment_id}", TEST_ASSIGNMENT_ID))
				.andReturn().getResponse(); // checking post 
		
		assertEquals(200, response.getStatus());
		
        verify(assignmentRepository, times(1)).delete(any());

        System.out.println(assignment);	
		
	}
	
	private static <T> T  fromJsonString(String str, Class<T> valueType ) {
        try {
            return new ObjectMapper().readValue(str, valueType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
package com.app.b_and_t_lms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.b_and_t_lms.models.Program;
import com.app.b_and_t_lms.models.Program.ProgramCategory;
import com.app.b_and_t_lms.models.Program.ProgramStatus;

public interface ProgramRepository extends JpaRepository<Program, Long>     {

    Long countByCategoryAndStatus(ProgramCategory learnership, ProgramStatus inProgress);


    
}

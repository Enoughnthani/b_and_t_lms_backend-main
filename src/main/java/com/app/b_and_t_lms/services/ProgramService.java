package com.app.b_and_t_lms.services;

import com.app.b_and_t_lms.repositories.ProgramRepository;
import org.springframework.stereotype.Service;

import com.app.b_and_t_lms.dto.ApiResponse;
import com.app.b_and_t_lms.dto.ProgramDTO;
import com.app.b_and_t_lms.models.Program;

import java.util.List;
import java.util.Optional;

@Service
public class ProgramService {

    private final ProgramRepository programRepository;

    public ProgramService(ProgramRepository programRepository) {
        this.programRepository = programRepository;
    }

    public ApiResponse<?> addProgram(ProgramDTO programDTO) {
        try {
            Program program = new Program();
            mapDtoToEntity(programDTO, program);

            programRepository.save(program);
            return new ApiResponse<>(true, "Program added successfully", program);

        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to add program: " + e.getMessage(), null);
        }
    }

    public ApiResponse<?> getAllPrograms() {
        try {
            List<Program> programs = programRepository.findAll();
            return new ApiResponse<>(true, "Programs fetched successfully", programs);

        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to fetch programs", null);
        }
    }

    public ApiResponse<?> getProgramById(Long id) {
        try {
            Optional<Program> program = programRepository.findById(id);

            if (program.isPresent()) {
                return new ApiResponse<>(true, "Program found", program.get());
            } else {
                return new ApiResponse<>(false, "Program not found", null);
            }

        } catch (Exception e) {
            return new ApiResponse<>(false, "Error fetching program", null);
        }
    }

    public ApiResponse<?> updateProgram(Long id, ProgramDTO programDTO) {
        try {
            Optional<Program> optionalProgram = programRepository.findById(id);

            if (optionalProgram.isEmpty()) {
                return new ApiResponse<>(false, "Program not found", null);
            }

            Program program = optionalProgram.get();
            mapDtoToEntity(programDTO, program);

            programRepository.save(program);
            return new ApiResponse<>(true, "Program updated successfully", program);

        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to update program", null);
        }
    }

    public ApiResponse<?> deleteProgram(Long id) {
        try {
            Optional<Program> program = programRepository.findById(id);

            if (program.isEmpty()) {
                return new ApiResponse<>(false, "Program not found", null);
            }

            programRepository.deleteById(id);
            return new ApiResponse<>(true, "Program deleted successfully", null);

        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to delete program", null);
        }
    }

    private void mapDtoToEntity(ProgramDTO dto, Program program) {
        program.setName(dto.getName());
        program.setDescription(dto.getDescription());
        program.setProgramType(dto.getProgramType());
        program.setCapacity(dto.getCapacity());
        program.setStatus(dto.getStatus());
        program.setStartDate(dto.getStartDate());
        program.setEndDate(dto.getEndDate());
        // program.setImageUrl(dto.getImageUrl());
    }
}
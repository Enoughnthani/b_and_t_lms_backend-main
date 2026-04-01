package com.app.b_and_t_lms.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.app.b_and_t_lms.dto.ApiResponse;
import com.app.b_and_t_lms.dto.ProgramDTO;
import com.app.b_and_t_lms.models.Program;
import com.app.b_and_t_lms.repositories.ProgramRepository;

@Service
public class ProgramService {

    private final ProgramRepository programRepository;
    private final ImageStorageService imageStorageService;

    public ProgramService(ProgramRepository programRepository, ImageStorageService imageStorageService) {
        this.programRepository = programRepository;
        this.imageStorageService = imageStorageService;
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
            List<ProgramDTO> programs = programRepository.findAll().stream().map(ProgramDTO::new).toList();
            return new ApiResponse<>(true, "Programs fetched successfully", programs);

        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to fetch programs", null);
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
            return new ApiResponse<>(false, "Failed to update program "+e.getMessage(), null);
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
        program.setType(dto.getType());
        program.setCapacity(dto.getCapacity());
        program.setStatus(dto.getStatus());
        program.setStartDate(dto.getStartDate());
        program.setEndDate(dto.getEndDate());
        program.setLocation(dto.getLocation());
        program.setCategory(dto.getCategory());
        program.setCreatedAt(LocalDateTime.now());

        if (dto.getImageBase64() != null && !dto.getImageBase64().equals(program.getImageUrl())) {
            String imageURL = imageStorageService.saveImage(dto.getImageBase64(), "programs");
            program.setImageUrl(imageURL);

        }

    }

    public ApiResponse<?> getProgramById(Long id) {

        try {
            return programRepository.findById(id)
                    .map(program -> new ApiResponse<>(true, "Program fetched successfully", new ProgramDTO(program)))
                    .orElseGet(() -> new ApiResponse<>(false, "Program not found", null));
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to fetch program", null);
        }
    }
}
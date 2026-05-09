package com.app.b_and_t_lms.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.app.b_and_t_lms.dto.ApiResponse;
import com.app.b_and_t_lms.dto.UnitStandardRequestDTO;
import com.app.b_and_t_lms.dto.UnitStandardResponseDTO;
import com.app.b_and_t_lms.dto.UnitStandardStatsDTO;
import com.app.b_and_t_lms.models.Program;
import com.app.b_and_t_lms.models.UnitStandard;
import com.app.b_and_t_lms.models.UnitStandard.UnitStandardType;
import com.app.b_and_t_lms.repositories.ProgramRepository;
import com.app.b_and_t_lms.repositories.UnitStandardRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UnitStandardService {

    private final UnitStandardRepository unitStandardRepository;
    private final ProgramRepository programRepository;

    // Get all unit standards for a program
    public List<UnitStandardResponseDTO> getByProgramId(Long programId) {
        List<UnitStandard> unitStandards = unitStandardRepository.findByProgramId(programId);
        return unitStandards.stream()
                .map(UnitStandardResponseDTO::new)
                .collect(Collectors.toList());
    }

    // Get unit standard by ID
    public UnitStandardResponseDTO getById(Long unitStandardId) {
        UnitStandard unitStandard = unitStandardRepository.findById(unitStandardId)
                .orElseThrow(() -> new RuntimeException("Unit Standard not found with id: " + unitStandardId));
        return new UnitStandardResponseDTO(unitStandard);
    }

    // Get unit standards by program and type
    public List<UnitStandardResponseDTO> getByProgramIdAndType(Long programId, String type) {
        List<UnitStandard> unitStandards = unitStandardRepository.findByProgramIdAndType(programId, type);
        return unitStandards.stream()
                .map(UnitStandardResponseDTO::new)
                .collect(Collectors.toList());
    }

    // Get unit standard with its content
    public UnitStandardResponseDTO getByIdWithContent(Long unitStandardId) {
        UnitStandard unitStandard = unitStandardRepository.findByIdWithContents(unitStandardId)
                .orElseThrow(() -> new RuntimeException("Unit Standard not found with id: " + unitStandardId));
        return new UnitStandardResponseDTO(unitStandard);
    }

    @Transactional
    public ApiResponse<?> create(UnitStandardRequestDTO dto) {
        // Validate required fields
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            throw new RuntimeException("Unit Standard title is required");
        }
        if (dto.getProgramId() == null) {
            throw new RuntimeException("Program ID is required");
        }

        if (unitStandardRepository.existsById(dto.getUnitStandardId())) {
            return new ApiResponse<>(false,
                    "Unit Standard already exists with SAQA ID: " + dto.getUnitStandardId(),
                    null);
        }

        Program program = programRepository.findById(dto.getProgramId())
                .orElseThrow(() -> new RuntimeException("Program not found with id: " + dto.getProgramId()));

        UnitStandard unitStandard = new UnitStandard();
        unitStandard.setUnitStandardId(dto.getUnitStandardId());
        unitStandard.setTitle(dto.getTitle());
        unitStandard.setDescription(dto.getDescription());
        unitStandard.setCredits(dto.getCredits());
        unitStandard.setNqfLevel(dto.getNqfLevel());
        unitStandard.setType(dto.getType());
        unitStandard.setProgram(program);
        unitStandard.setCreatedAt(LocalDateTime.now());

        unitStandardRepository.save(unitStandard);
        return new ApiResponse<>(true,
                "Unit Standard with SAQA ID " + dto.getUnitStandardId() + " was created successfully.",
                null);
    }

    // Update unit standard
    @Transactional
    public UnitStandardResponseDTO update(Long unitStandardId, UnitStandardRequestDTO dto) {
        UnitStandard unitStandard = unitStandardRepository.findById(unitStandardId)
                .orElseThrow(() -> new RuntimeException("Unit Standard not found with id: " + unitStandardId));

        // Update fields if provided
        if (dto.getTitle() != null && !dto.getTitle().trim().isEmpty()) {
            unitStandard.setTitle(dto.getTitle());
        }

        if (dto.getDescription() != null) {
            unitStandard.setDescription(dto.getDescription());
        }

        if (dto.getCredits() != null) {
            unitStandard.setCredits(dto.getCredits());
        }

        if (dto.getNqfLevel() != null) {
            unitStandard.setNqfLevel(dto.getNqfLevel());
        }

        if (dto.getType() != null) {
            unitStandard.setType(dto.getType());
        }

        UnitStandard updated = unitStandardRepository.save(unitStandard);
        return new UnitStandardResponseDTO(updated);
    }

    // Delete unit standard
    @Transactional
    public void delete(Long unitStandardId) {
        UnitStandard unitStandard = unitStandardRepository.findById(unitStandardId)
                .orElseThrow(() -> new RuntimeException("Unit Standard not found with id: " + unitStandardId));

        // Check if unit standard has content
        if (unitStandard.getContents() != null && !unitStandard.getContents().isEmpty()) {
            throw new RuntimeException("Cannot delete Unit Standard with existing content. Delete all content first.");
        }

        unitStandardRepository.delete(unitStandard);
    }

    // Search unit standards by title
    public List<UnitStandardResponseDTO> search(Long programId, String keyword) {
        List<UnitStandard> results = unitStandardRepository.searchByProgramIdAndKeyword(programId, keyword);
        return results.stream()
                .map(UnitStandardResponseDTO::new)
                .collect(Collectors.toList());
    }

    // Get total credits for a program
    public Integer getTotalCreditsByProgramId(Long programId) {
        Integer total = unitStandardRepository.getTotalCreditsByProgramId(programId);
        return total != null ? total : 0;
    }

    // Get statistics for a program
    public UnitStandardStatsDTO getStatsByProgramId(Long programId) {
        List<UnitStandard> unitStandards = unitStandardRepository.findByProgramId(programId);

        int total = unitStandards.size();
        int fundamental = 0;
        int core = 0;
        int elective = 0;
        int totalCredits = 0;
        int totalHours = 0;

        for (UnitStandard us : unitStandards) {
            UnitStandardType type = us.getType();

            switch (type) {
                case CORE:
                    core++;
                    break;
                case FUNDAMENTAL:
                    fundamental++;
                    break;
                default:
                    elective++;
                    break;
            }

            if (us.getCredits() != null) {
                totalCredits += us.getCredits();
            }
        }

        double averageCredits = total > 0 ? (double) totalCredits / total : 0;
        double averageHours = total > 0 ? (double) totalHours / total : 0;

        return new UnitStandardStatsDTO(
                total, fundamental, core, elective, totalCredits,
                0, 0, averageCredits, averageHours);
    }

    // Get unit standards with no content
    public List<UnitStandardResponseDTO> getEmptyUnitStandards(Long programId) {
        List<UnitStandard> emptyStandards = unitStandardRepository.findEmptyUnitStandardsByProgramId(programId);
        return emptyStandards.stream()
                .map(UnitStandardResponseDTO::new)
                .collect(Collectors.toList());
    }

    // Get unit standards that have content
    public List<UnitStandardResponseDTO> getUnitStandardsWithContent(Long programId) {
        List<UnitStandard> standardsWithContent = unitStandardRepository
                .findUnitStandardsWithContentsByProgramId(programId);
        return standardsWithContent.stream()
                .map(UnitStandardResponseDTO::new)
                .collect(Collectors.toList());
    }
}
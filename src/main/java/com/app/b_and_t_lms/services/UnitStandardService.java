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
import com.app.b_and_t_lms.models.UnitStandard.UnitStandardStatus;
import com.app.b_and_t_lms.models.UnitStandard.UnitStandardType;
import com.app.b_and_t_lms.repositories.ProgramRepository;
import com.app.b_and_t_lms.repositories.UnitStandardRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UnitStandardService {

    private final UnitStandardRepository unitStandardRepository;
    private final ProgramRepository programRepository;

    public List<UnitStandardResponseDTO> getByProgramId(Long programId) {
        return unitStandardRepository.findByProgramId(programId)
                .stream()
                .map(UnitStandardResponseDTO::new)
                .collect(Collectors.toList());
    }

    public UnitStandardResponseDTO getById(Long unitStandardId) {
        UnitStandard unitStandard = unitStandardRepository.findById(unitStandardId)
                .orElseThrow(() -> new RuntimeException(
                        "Unit Standard not found with id: " + unitStandardId));
        return new UnitStandardResponseDTO(unitStandard);
    }

    public List<UnitStandardResponseDTO> getByProgramIdAndType(Long programId, String type) {
        return unitStandardRepository.findByProgramIdAndType(programId, type)
                .stream()
                .map(UnitStandardResponseDTO::new)
                .collect(Collectors.toList());
    }

    public UnitStandardResponseDTO getByIdWithContent(Long unitStandardId) {
        UnitStandard unitStandard = unitStandardRepository.findByIdWithContents(unitStandardId)
                .orElseThrow(() -> new RuntimeException(
                        "Unit Standard not found with id: " + unitStandardId));
        return new UnitStandardResponseDTO(unitStandard);
    }

    @Transactional
    public ApiResponse<?> create(UnitStandardRequestDTO dto) {
        if (dto.getTitle() == null || dto.getTitle().trim().isEmpty()) {
            return new ApiResponse<>(false, "Title is required", null);
        }
        if (dto.getProgramId() == null) {
            return new ApiResponse<>(false, "Program ID is required", null);
        }
        if (dto.getUnitStandardId() == null) {
            return new ApiResponse<>(false, "Module code is required", null);
        }

        if (unitStandardRepository.existsById(dto.getUnitStandardId())) {
            return new ApiResponse<>(false,
                    "A module already exists with code: " + dto.getUnitStandardId(),
                    null);
        }

        Program program = programRepository.findById(dto.getProgramId())
                .orElseThrow(() -> new RuntimeException(
                        "Program not found with id: " + dto.getProgramId()));

        UnitStandard us = new UnitStandard();
        us.setUnitStandardId(dto.getUnitStandardId());
        us.setTitle(dto.getTitle());
        us.setDescription(dto.getDescription());
        us.setPurpose(dto.getPurpose());
        us.setLearningAssumed(dto.getLearningAssumed());
        us.setCredits(dto.getCredits());
        us.setNotionalHours(dto.getNotionalHours());
        us.setNqfLevel(dto.getNqfLevel());
        us.setType(dto.getType() != null ? dto.getType() : UnitStandardType.KNOWLEDGE);
        us.setStatus(dto.getStatus() != null ? dto.getStatus() : UnitStandardStatus.ACTIVE);
        us.setModerationBody(dto.getModerationBody());
        us.setRangeStatement(dto.getRangeStatement());
        us.setSpecificOutcomes(dto.getSpecificOutcomes());
        us.setAssessmentCriteria(dto.getAssessmentCriteria());
        us.setCriticalCrossFieldOutcomes(dto.getCriticalCrossFieldOutcomes());
        us.setProgram(program);
        us.setCreatedAt(LocalDateTime.now());
        us.setUpdatedAt(LocalDateTime.now());

        unitStandardRepository.save(us);

        return new ApiResponse<>(true,
                "Module " + dto.getUnitStandardId() + " created successfully.",
                new UnitStandardResponseDTO(us));
    }

    @Transactional
    public ApiResponse<?> update(Long unitStandardId, UnitStandardRequestDTO dto) {
        try {
            UnitStandard us = unitStandardRepository.findById(unitStandardId).orElse(null);

            if (us == null) {
                return new ApiResponse<>(false,
                        "Module not found with id: " + unitStandardId, null);
            }

            if (dto.getTitle() != null && !dto.getTitle().trim().isEmpty()) {
                us.setTitle(dto.getTitle());
            }
            if (dto.getDescription() != null) {
                us.setDescription(dto.getDescription());
            }
            if (dto.getPurpose() != null) {
                us.setPurpose(dto.getPurpose());
            }
            if (dto.getLearningAssumed() != null) {
                us.setLearningAssumed(dto.getLearningAssumed());
            }
            if (dto.getCredits() != null) {
                us.setCredits(dto.getCredits());
            }
            if (dto.getNotionalHours() != null) {
                us.setNotionalHours(dto.getNotionalHours());
            }
            if (dto.getNqfLevel() != null) {
                us.setNqfLevel(dto.getNqfLevel());
            }
            if (dto.getType() != null) {
                us.setType(dto.getType());
            }
            if (dto.getStatus() != null) {
                us.setStatus(dto.getStatus());
            }
            if (dto.getModerationBody() != null) {
                us.setModerationBody(dto.getModerationBody());
            }
            if (dto.getRangeStatement() != null) {
                us.setRangeStatement(dto.getRangeStatement());
            }
            if (dto.getSpecificOutcomes() != null) {
                us.setSpecificOutcomes(dto.getSpecificOutcomes());
            }
            if (dto.getAssessmentCriteria() != null) {
                us.setAssessmentCriteria(dto.getAssessmentCriteria());
            }
            if (dto.getCriticalCrossFieldOutcomes() != null) {
                us.setCriticalCrossFieldOutcomes(dto.getCriticalCrossFieldOutcomes());
            }

            us.setUpdatedAt(LocalDateTime.now());

            UnitStandard updated = unitStandardRepository.save(us);
            return new ApiResponse<>(true, "Module updated successfully",
                    new UnitStandardResponseDTO(updated));

        } catch (Exception e) {
            return new ApiResponse<>(false,
                    "Failed to update module: " + e.getMessage(), null);
        }
    }

    @Transactional
    public ApiResponse<?> delete(Long unitStandardId) {
        UnitStandard us = unitStandardRepository.findById(unitStandardId).orElse(null);

        if (us == null) {
            return new ApiResponse<>(false,
                    "Module not found with id: " + unitStandardId, null);
        }

        if (us.getContents() != null && !us.getContents().isEmpty()) {
            return new ApiResponse<>(false,
                    "Cannot delete module with existing content. Delete all content first.",
                    null);
        }

        unitStandardRepository.delete(us);
        return new ApiResponse<>(true, "Module deleted.", null);
    }

    public List<UnitStandardResponseDTO> search(Long programId, String keyword) {
        return unitStandardRepository.searchByProgramIdAndKeyword(programId, keyword)
                .stream()
                .map(UnitStandardResponseDTO::new)
                .collect(Collectors.toList());
    }

    public Integer getTotalCreditsByProgramId(Long programId) {
        Integer total = unitStandardRepository.getTotalCreditsByProgramId(programId);
        return total != null ? total : 0;
    }

    public UnitStandardStatsDTO getStatsByProgramId(Long programId) {
        List<UnitStandard> unitStandards = unitStandardRepository.findByProgramId(programId);

        int total = unitStandards.size();
        int knowledge = 0;
        int practical = 0;
        int workExperience = 0;
        int active = 0;
        int phasedOut = 0;
        int totalCredits = 0;
        int notionalHours = 0;

        for (UnitStandard us : unitStandards) {
            UnitStandardType type = us.getType();
            if (type != null) {
                switch (type) {
                    case KNOWLEDGE:
                        knowledge++;
                        break;
                    case PRACTICAL:
                        practical++;
                        break;
                    case WORK_EXPERIENCE:
                        workExperience++;
                        break;
                }
            }

            if (us.getStatus() == UnitStandardStatus.ACTIVE) {
                active++;
            }
            if (us.getStatus() == UnitStandardStatus.PHASED_OUT) {
                phasedOut++;
            }

            if (us.getCredits() != null) {
                totalCredits += us.getCredits();
            }
            if (us.getNotionalHours() != null) {
                notionalHours += us.getNotionalHours();
            }
        }

        UnitStandardStatsDTO stats = new UnitStandardStatsDTO();
        stats.setTotal(total);
        stats.setKnowledge(knowledge);
        stats.setPractical(practical);
        stats.setWorkExperience(workExperience);
        stats.setActive(active);
        stats.setPhasedOut(phasedOut);
        stats.setTotalCredits(totalCredits);
        stats.setNotionalHours(notionalHours);
        return stats;
    }

    public List<UnitStandardResponseDTO> getEmptyUnitStandards(Long programId) {
        return unitStandardRepository.findEmptyUnitStandardsByProgramId(programId)
                .stream()
                .map(UnitStandardResponseDTO::new)
                .collect(Collectors.toList());
    }

    public List<UnitStandardResponseDTO> getUnitStandardsWithContent(Long programId) {
        return unitStandardRepository.findUnitStandardsWithContentsByProgramId(programId)
                .stream()
                .map(UnitStandardResponseDTO::new)
                .collect(Collectors.toList());
    }
}
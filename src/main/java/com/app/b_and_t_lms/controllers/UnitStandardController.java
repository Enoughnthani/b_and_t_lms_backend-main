package com.app.b_and_t_lms.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.b_and_t_lms.dto.ApiResponse;
import com.app.b_and_t_lms.dto.UnitStandardRequestDTO;
import com.app.b_and_t_lms.dto.UnitStandardResponseDTO;
import com.app.b_and_t_lms.dto.UnitStandardStatsDTO;
import com.app.b_and_t_lms.services.UnitStandardService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/unit-standards")
@RequiredArgsConstructor
public class UnitStandardController {

    private final UnitStandardService unitStandardService;

    @GetMapping("/program/{programId}")
    public ResponseEntity<List<UnitStandardResponseDTO>> getByProgramId(@PathVariable Long programId) {
        List<UnitStandardResponseDTO> unitStandards = unitStandardService.getByProgramId(programId);
        return ResponseEntity.ok(unitStandards);
    }

    @GetMapping("/{unitStandardId}")
    public ResponseEntity<UnitStandardResponseDTO> getById(@PathVariable Long unitStandardId) {
        UnitStandardResponseDTO unitStandard = unitStandardService.getById(unitStandardId);
        return ResponseEntity.ok(unitStandard);
    }

    @GetMapping("/program/{programId}/type/{type}")
    public ResponseEntity<List<UnitStandardResponseDTO>> getByProgramIdAndType(
            @PathVariable Long programId,
            @PathVariable String type) {
        List<UnitStandardResponseDTO> unitStandards = unitStandardService.getByProgramIdAndType(programId, type);
        return ResponseEntity.ok(unitStandards);
    }

    @PostMapping
    public ApiResponse<?> create(@RequestBody UnitStandardRequestDTO dto) {
        return unitStandardService.create(dto);
    }

    @PutMapping("/{unitStandardId}")
    public ResponseEntity<UnitStandardResponseDTO> update(
            @PathVariable Long unitStandardId,
            @RequestBody UnitStandardRequestDTO dto) {
        UnitStandardResponseDTO updated = unitStandardService.update(unitStandardId, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{unitStandardId}")
    public ApiResponse<?> delete(@PathVariable Long unitStandardId) {
        return unitStandardService.delete(unitStandardId);
    }

    @GetMapping("/{unitStandardId}/with-content")
    public ResponseEntity<UnitStandardResponseDTO> getByIdWithContent(@PathVariable Long unitStandardId) {
        UnitStandardResponseDTO unitStandard = unitStandardService.getByIdWithContent(unitStandardId);
        return ResponseEntity.ok(unitStandard);
    }

    @GetMapping("/program/{programId}/search")
    public ResponseEntity<List<UnitStandardResponseDTO>> search(
            @PathVariable Long programId,
            @RequestParam String keyword) {
        List<UnitStandardResponseDTO> results = unitStandardService.search(programId, keyword);
        return ResponseEntity.ok(results);
    }
    @GetMapping("/program/{programId}/total-credits")
    public ResponseEntity<Integer> getTotalCredits(@PathVariable Long programId) {
        Integer totalCredits = unitStandardService.getTotalCreditsByProgramId(programId);
        return ResponseEntity.ok(totalCredits);
    }

    @GetMapping("/program/{programId}/stats")
    public ResponseEntity<UnitStandardStatsDTO> getStats(@PathVariable Long programId) {
        UnitStandardStatsDTO stats = unitStandardService.getStatsByProgramId(programId);
        return ResponseEntity.ok(stats);
    }
}
package com.app.b_and_t_lms.dto;

import java.util.List;

import com.app.b_and_t_lms.models.AssessmentQuestion;

import lombok.Data;

@Data
public class LearnerQuestionResponseDTO {
    private Long id;
    private String type;
    private String text;
    private Integer marks;
    private Integer displayOrder;
    private List<OptionDTO> options;
    private Integer blanksCount;
    private List<MatchingPairDTO> matchingPairs;

    @Data
    public static class OptionDTO {
        private String text;
        private Integer displayOrder;
    }

    @Data
    public static class MatchingPairDTO {
        private String leftItem;
        private String rightItem;
        private Integer displayOrder;
    }
 

    public LearnerQuestionResponseDTO(AssessmentQuestion question) {
        this.id = question.getId();
        this.type = question.getType() != null ? question.getType().name() : null;
        this.text = question.getText();
        this.marks = question.getMarks();
        this.displayOrder = question.getDisplayOrder();
        this.blanksCount = question.getBlanks().size();

        if (question.getOptions() != null) {
            this.options = question.getOptions().stream()
                    .map(opt -> {
                        OptionDTO dto = new OptionDTO();
                        dto.setText(opt.getText());
                        dto.setDisplayOrder(opt.getDisplayOrder());
                        return dto;
                    })
                    .toList();
        }

        if (question.getMatchingPairs() != null) {
            this.matchingPairs = question.getMatchingPairs().stream()
                    .map(pair -> {
                        MatchingPairDTO dto = new MatchingPairDTO();
                        dto.setLeftItem(pair.getLeftItem());
                        dto.setRightItem(pair.getRightItem());
                        dto.setDisplayOrder(pair.getDisplayOrder());
                        return dto;
                    })
                    .toList();
        }
    }
}
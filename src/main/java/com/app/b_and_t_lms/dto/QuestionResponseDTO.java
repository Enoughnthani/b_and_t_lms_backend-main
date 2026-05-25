package com.app.b_and_t_lms.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.app.b_and_t_lms.models.AssessmentQuestion;
import com.app.b_and_t_lms.models.MatchingPair;

import lombok.Data;

@Data
public class QuestionResponseDTO {
    private Long id;
    private String type;
    private String text;
    private Integer marks;
    private String explanation;
    private String correctAnswer;
    private String sampleAnswer;
    private Integer displayOrder;
    private List<String> options;
    private List<String> blanks;
    private Integer blanksCount;
    private List<MatchingPairResponseDTO> pairs;

    public QuestionResponseDTO(AssessmentQuestion question) {
        this.id = question.getId();
        this.type = question.getType().name();
        this.text = question.getText();
        this.marks = question.getMarks();
        this.explanation = question.getExplanation();
        this.correctAnswer = question.getCorrectAnswer();
        this.sampleAnswer = question.getSampleAnswer();
        this.displayOrder = question.getDisplayOrder();
        this.options = question.getOptions().stream().map(q -> q.getText()).toList();
        this.blanks = question.getBlanks().stream().map(b -> b.getBlank()).toList();
        this.blanksCount = blanks != null ? blanks.size() : 0;

        if (question.getMatchingPairs() != null) {
            this.pairs = question.getMatchingPairs().stream()
                    .map(MatchingPairResponseDTO::new)
                    .collect(Collectors.toList());
        }
    }

    @Data
class MatchingPairResponseDTO {
    private Long id;
    private String left;
    private String right;
    private Integer displayOrder;

    public MatchingPairResponseDTO(MatchingPair pair) {
        this.id = pair.getId();
        this.left = pair.getLeftItem();
        this.right = pair.getRightItem();
        this.displayOrder = pair.getDisplayOrder();
    }
}
}


package com.sivalabs.ft.features.domain;

import com.sivalabs.ft.features.domain.dtos.CommentDto;
import com.sivalabs.ft.features.domain.entities.Comment;
import com.sivalabs.ft.features.domain.exceptions.BadRequestException;
import com.sivalabs.ft.features.domain.exceptions.ResourceNotFoundException;
import com.sivalabs.ft.features.domain.mappers.CommentMapper;
import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommentService {
    private static final String COMMENT_PREFIX = "COM";
    private static final String COMMENT_SEPARATOR = "-";

    private final CommentRepository commentRepository;
    private final FeatureRepository featureRepository;
    private final CommentMapper commentMapper;

    public CommentService(
            CommentRepository commentRepository, FeatureRepository featureRepository, CommentMapper commentMapper) {
        this.commentRepository = commentRepository;
        this.featureRepository = featureRepository;
        this.commentMapper = commentMapper;
    }

    @Transactional
    public String addComment(Commands.AddCommentCommand command) {
        var feature = featureRepository
                .findByCode(command.featureCode())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Feature with code %s not found.".formatted(command.featureCode())));

        String code = COMMENT_PREFIX + COMMENT_SEPARATOR + commentRepository.getNextCommentCode();
        Comment comment = new Comment();
        comment.setContent(command.content());
        comment.setFeatureId(feature.getId());
        comment.setCode(code);
        comment.setUsername(command.createdBy());
        comment.setCreatedAt(Instant.now());
        Comment savedComment = commentRepository.save(comment);
        return savedComment.getCode();
    }

    @Transactional
    public void removeComment(String username, String commentCode) {
        int count = commentRepository.deleteByUserIdAndCommentCode(username, commentCode);
        if (count != 1) {
            throw new BadRequestException("comment with code %s not found.".formatted(commentCode));
        }
    }

    public List<CommentDto> findCommentsByFeatureCode(String featureCode, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        List<Comment> comments = commentRepository.findCommentsByFeatureCode(featureCode, pageRequest);
        return comments.stream().map(commentMapper::toDto).toList();
    }
}

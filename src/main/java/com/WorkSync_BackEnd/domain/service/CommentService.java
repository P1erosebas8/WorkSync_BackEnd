package com.WorkSync_BackEnd.domain.service;

import com.WorkSync_BackEnd.domain.dto.CommentDTO;
import com.WorkSync_BackEnd.domain.dto.CommentRequestDTO;
import com.WorkSync_BackEnd.domain.model.Comment;
import com.WorkSync_BackEnd.domain.repository.CommentRepository;
import com.WorkSync_BackEnd.domain.repository.TaskRepository;
import com.WorkSync_BackEnd.domain.repository.UserRepository;
import com.WorkSync_BackEnd.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public CommentDTO createComment(CommentRequestDTO dto) {
        taskRepository.getById(dto.getTaskId())
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada con ID: " + dto.getTaskId()));

        userRepository.getById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + dto.getUserId()));

        Comment commentDomain = Comment.builder()
                .content(dto.getContent())
                .taskId(dto.getTaskId())
                .userId(dto.getUserId())
                .creationDate(LocalDateTime.now())
                .build();

        Comment savedComment = commentRepository.save(commentDomain);
        return toDto(savedComment);
    }

    public List<CommentDTO> getCommentsByTask(Long taskId) {
        return commentRepository.getByTaskId(taskId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private CommentDTO toDto(Comment comment) {
        return CommentDTO.builder()
                .commentId(comment.getCommentId())
                .content(comment.getContent())
                .creationDate(comment.getCreationDate())
                .taskId(comment.getTaskId())
                .userId(comment.getUserId())
                .userName(comment.getUserName())
                .build();
    }
}

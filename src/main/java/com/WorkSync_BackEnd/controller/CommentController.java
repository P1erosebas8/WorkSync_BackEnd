package com.WorkSync_BackEnd.controller;

import com.WorkSync_BackEnd.domain.dto.CommentDTO;
import com.WorkSync_BackEnd.domain.dto.CommentRequestDTO;
import com.WorkSync_BackEnd.domain.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comentarios")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDTO> createComment(@RequestBody CommentRequestDTO dto) {
        return new ResponseEntity<>(commentService.createComment(dto), HttpStatus.CREATED);
    }

    @GetMapping("/tarea/{taskId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByTask(@PathVariable Long taskId) {
        return ResponseEntity.ok(commentService.getCommentsByTask(taskId));
    }
}

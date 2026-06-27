package com.WorkSync_BackEnd.controller;

import com.WorkSync_BackEnd.domain.dto.ComentarioDTO;
import com.WorkSync_BackEnd.domain.dto.ComentarioRequestDTO;
import com.WorkSync_BackEnd.domain.service.ComentarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comentarios")
@RequiredArgsConstructor
public class ComentarioController {

    private final ComentarioService comentarioService;

    @PostMapping
    public ResponseEntity<ComentarioDTO> createComentario(@RequestBody ComentarioRequestDTO dto) {
        return new ResponseEntity<>(comentarioService.createComentario(dto), HttpStatus.CREATED);
    }

    @GetMapping("/tarea/{idTarea}")
    public ResponseEntity<List<ComentarioDTO>> getComentariosByTarea(@PathVariable Long idTarea) {
        return ResponseEntity.ok(comentarioService.getComentariosByTarea(idTarea));
    }
}

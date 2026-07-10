package com.WorkSync_BackEnd.domain.repository;

import com.WorkSync_BackEnd.domain.model.Comment;
import java.util.List;

public interface CommentRepository {
    List<Comment> getByTaskId(Long taskId);
    Comment save(Comment comment);
}

package com.example.eCommerceApp1.controller;

import com.example.eCommerceApp1.dto.comment.CommentInput;
import com.example.eCommerceApp1.dto.comment.CommentOutput;
import com.example.eCommerceApp1.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/comment")
public class CommentController {
    private final CommentService commentService;

    @Operation(summary = "comment sản phẩm")
    @PostMapping("/create")
    public void commentProduct(@RequestHeader("Authorization") String accessToken,
                               @RequestBody CommentInput commentInput,
                               @RequestParam Long productTemplateId) {
        commentService.commentProduct(accessToken, commentInput, productTemplateId);
    }

    @Operation(summary = "Xóa comment")
    @DeleteMapping("/delete")
    public void deleteComment(@RequestHeader("Authorization") String accessToken,
                              @RequestParam Long commentId) {
        commentService.deleteComment(accessToken, commentId);
    }

    @Operation(summary = "sửa comment")
    @PostMapping("/update")
    public void updateComment(@RequestHeader("Authorization") String accessToken,
                              @RequestParam Long commentId,
                              @RequestBody CommentInput commentInput) {
        commentService.changeComment(accessToken, commentInput, commentId);
    }

    @Operation(summary = "Xem comment sản phẩm")
    @GetMapping("/get")
    public Page<CommentOutput> getComments(@RequestParam Long productTemplateId,
                                           @ParameterObject Pageable pageable) {
        return commentService.getComments(productTemplateId, pageable);
    }
}

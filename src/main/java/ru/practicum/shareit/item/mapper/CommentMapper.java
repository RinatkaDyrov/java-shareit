package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.Collection;
import java.util.List;

public class CommentMapper {
    public static CommentDto mapCommentToDto(Comment comment) {
        return new CommentDto(
                comment.getId(),
                comment.getText(),
                comment.getAuthor().getName(),
                comment.getCreated()
        );
    }


    public static List<CommentDto> mapCommentToDtoList(Collection<Comment> comments) {
        return comments.stream()
                .map(CommentMapper::mapCommentToDto)
                .toList();
    }
}

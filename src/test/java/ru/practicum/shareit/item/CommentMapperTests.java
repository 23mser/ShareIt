package ru.practicum.shareit.item;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.item.dto.CommentMapper;

import java.time.LocalDateTime;
import java.util.Optional;

public class CommentMapperTests {
    @Test
    void toCommentTest() {
        CommentDto commentDto = CommentDto.builder()
                .text("text")
                .build();

        Optional<Comment> comment = Optional.of(CommentMapper.toComment(commentDto, new Item(), new User()));

        Assertions.assertThat(comment)
                .isPresent()
                .hasValueSatisfying(i -> Assertions.assertThat(i)
                        .hasFieldOrPropertyWithValue("text", "text"));
    }

    @Test
    void toCommentDto() {
        Comment comment = fillComment();
        comment.setCreated(LocalDateTime.now());

        Optional<CommentDto> commentDto = Optional.of(CommentMapper.toCommentDto(comment));

        Assertions.assertThat(commentDto)
                .isPresent()
                .hasValueSatisfying(i -> Assertions.assertThat(i)
                        .hasFieldOrPropertyWithValue("id", 1L)
                        .hasFieldOrPropertyWithValue("text", "text")
                        .hasFieldOrPropertyWithValue("authorName", "user_name"));
    }

    private Comment fillComment() {
        Item item = new Item();
        item.setId(1L);
        item.setName("item_name");

        User user = new User();
        user.setId(1L);
        user.setName("user_name");

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setText("text");
        comment.setItem(item);
        comment.setAuthor(user);

        return comment;
    }
}

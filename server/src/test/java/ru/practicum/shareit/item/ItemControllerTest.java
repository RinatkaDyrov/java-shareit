package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.CommentRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnItemsByUser() throws Exception {
        ItemDto dto = new ItemDto(1L,
                "дрель",
                "ударная",
                true,
                null,
                null,
                List.of());
        Mockito.when(itemService.findAllByUser(1L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(dto.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(dto.getName())));
    }

    @Test
    void shouldReturnItemById() throws Exception {
        ItemDto dto = new ItemDto(1L,
                "дрель",
                "ударная",
                true,
                null,
                null,
                List.of());
        Mockito.when(itemService.findItem(1L)).thenReturn(dto);

        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(dto.getId().intValue())))
                .andExpect(jsonPath("$.name", is(dto.getName())));
    }

    @Test
    void shouldSearchItems() throws Exception {
        ItemDto dto = new ItemDto(1L,
                "дрель",
                "ударная",
                true,
                null,
                null,
                List.of());
        Mockito.when(itemService.searchItems("дрель")).thenReturn(List.of(dto));

        mockMvc.perform(get("/items/search")
                        .param("text", "дрель"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(dto.getId().intValue())))
                .andExpect(jsonPath("$[0].name", is(dto.getName())));
    }

    @Test
    void shouldCreateItem() throws Exception {
        Item item = new Item();
        item.setName("дрель");
        item.setDescription("ударная");
        item.setAvailable(true);

        ItemDto dto = new ItemDto(1L, "дрель", "ударная", true, null,
                null,
                List.of());
        Mockito.when(itemService.create(any(Item.class), eq(1L))).thenReturn(dto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("дрель")));
    }

    @Test
    void shouldUpdateItem() throws Exception {
        Item item = new Item();
        item.setName("дрель++");

        ItemDto dto = new ItemDto(1L,
                "дрель++",
                "мощная",
                true,
                null,
                null,
                List.of());
        Mockito.when(itemService.updateItem(eq(1L), eq(1L), any(Item.class))).thenReturn(dto);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("дрель++")));
    }

    @Test
    void shouldDeleteItem() throws Exception {
        mockMvc.perform(delete("/items/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk());

        Mockito.verify(itemService).deleteItem(1L, 1L);
    }

    @Test
    void shouldAddComment() throws Exception {
        CommentRequest req = new CommentRequest();
        req.setText("Отличная вещь");

        CommentDto resp = new CommentDto();
        resp.setId(1L);
        resp.setText("Отличная вещь");
        resp.setAuthorName("user");

        Mockito.when(itemService.addNewComment(eq(1L), eq(2L), any(CommentRequest.class))).thenReturn(resp);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(resp.getId().intValue())))
                .andExpect(jsonPath("$.text", is("Отличная вещь")));
    }
}

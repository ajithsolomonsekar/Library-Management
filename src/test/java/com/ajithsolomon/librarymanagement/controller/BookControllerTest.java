package com.ajithsolomon.librarymanagement.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.ajithsolomon.librarymanagement.entity.Book;
import com.ajithsolomon.librarymanagement.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Test
    public void viewBooksReturnNoBooks() throws Exception {
        when(bookService.findAllBooks()).thenReturn(new ArrayList<>());

        MvcResult mvcResult = mockMvc.perform(get("/v1/books")).andExpect(status().isOk())
                .andReturn();

        assertEquals("[]", mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void viewBooksReturnSomeBooks() throws Exception {

        Book book = new Book();
        book.setId(1L);
        book.setIsbn(UUID.randomUUID().toString());
        book.setName("Steve Jobs");
        book.setPublisher("Simon and Schuster");
        book.setAuthor("Walter Isaacson");
        book.setEdition("2011");

        when(bookService.findAllBooks()).thenReturn(List.of(book));

        MvcResult mvcResult = mockMvc.perform(get("/v1/books")).andExpect(status().isOk())
                .andReturn();

        assertNotEquals("", mvcResult.getResponse().getContentAsString());

    }
}

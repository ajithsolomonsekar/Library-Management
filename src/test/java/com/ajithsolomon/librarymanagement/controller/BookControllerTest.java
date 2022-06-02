package com.ajithsolomon.librarymanagement.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.ajithsolomon.librarymanagement.entity.Books;
import com.ajithsolomon.librarymanagement.entity.BorrowedList;
import com.ajithsolomon.librarymanagement.entity.Users;
import com.ajithsolomon.librarymanagement.model.BorrowBookRequest;
import com.ajithsolomon.librarymanagement.repository.BookRepository;
import com.ajithsolomon.librarymanagement.repository.BorrowedListRepository;
import com.ajithsolomon.librarymanagement.repository.UsersRepository;
import com.ajithsolomon.librarymanagement.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.awt.print.Book;
import java.util.*;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BookController.class)
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookService bookService;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void viewBooksReturnNoBooks() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/v1/books")).andExpect(status().isOk())
                .andReturn();
        assertEquals("[]", mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void viewBooksReturnSomeBooks() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/v1/books")).andExpect(status().isOk())
                .andReturn();
        assertNotEquals("", mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void borrowBookFromLibrary() throws Exception {
        BorrowBookRequest borrowBookRequest = new BorrowBookRequest();
        borrowBookRequest.setBookId(2L);
        borrowBookRequest.setUserId(1L);
        mockMvc.perform(patch("/v1/borrow-book").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(borrowBookRequest))).andExpect(status().isNoContent());
    }


}

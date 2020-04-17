package com.yonyou.iuap.demo.contract.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by zhangxbk on 2019/8/8.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class ContractAssoControllerTest {
    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;
    private MockHttpSession session;

    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setupMockMvc() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        this.session = new MockHttpSession();

    }

    @Test
    public void getAssoVo()  throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/contract/getassovo")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .param("id","be1407dbb93e4850a656f650978e999f")
                .param("wb_at","LMjnpmpujlbnFmMd5xamqQI4DjcwsjbZrmnkdwZlokdknqf")
        )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void saveAssoVo() {
    }

    @Test
    public void deleAssoVo() {
    }

    @Test
    public void list()  throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/contract/list?wb_at=LMjnpmpujlbnFmMd5xamqQI4DjcwsjbZrmnkdwZlokdknqf")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void materiallist() {
    }

    @Test
    public void paytermlist() {
    }

    @Test
    public void termlist() {
    }

    @Test
    public void getSessionContextInfo() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/contract/session_context_info?wb_at=LMjnpmpujlbnFmMd5xamqQI4DjcwsjbZrmnkdwZlokdknqf")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
        )
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    public void getDataForPrint() {
    }

}

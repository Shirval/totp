package com.shirval;

import com.shirval.job.OnStartupListener;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public abstract class CommonIntegrationTest {

    @MockitoBean
    private OnStartupListener startupListener;
}

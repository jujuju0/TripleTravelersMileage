package pretask.triple.mileage.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.filter.CharacterEncodingFilter;
import pretask.triple.mileage.model.dto.ReviewEventRequest;
import pretask.triple.mileage.model.enums.ActionEnum;
import pretask.triple.mileage.model.enums.TypeEnum;
import pretask.triple.mileage.service.ReviewPointService;

import java.util.UUID;

@ExtendWith(SpringExtension.class)
@WebMvcTest(ReviewPointController.class)
@AutoConfigureMockMvc
class ReviewPointControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ReviewPointService reviewPointService;

    @BeforeEach
    void setUp() {
        MockMvcBuilders.standaloneSetup(new ReviewPointController(reviewPointService))
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void reviewEvents() throws Exception {
        // given
        String[] _attachedPhotoList = new String[2];
        _attachedPhotoList[0] = UUID.randomUUID().toString();
        _attachedPhotoList[1] = UUID.randomUUID().toString();

        ReviewEventRequest event = new ReviewEventRequest();
        event.setType(TypeEnum.REVIEW);
        event.setAction(ActionEnum.ADD);
        event.setReviewId(UUID.randomUUID().toString());
        event.setContent("리뷰 테스트 1");
        event.setAttachedPhotoIds(_attachedPhotoList);
        event.setUserId("3ede0ef2-92b7-4817-a5f3-0c575361f745");
        event.setPlaceId("2e4baf1c-5acb-4efb-a1af-eddada31b00f");

        // when
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                .post("/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(objectMapper.writeValueAsString(event))
                .accept(MediaType.APPLICATION_JSON_UTF8)
        );

        // then
        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void getUserPoint() throws Exception {
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                .get("/point/3ede0ef2-92b7-4817-a5f3-0c575361f745")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
        );

        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    void getUserPointDetail() throws Exception {
        ResultActions resultActions = mvc.perform(MockMvcRequestBuilders
                .get("/point/detail/3ede0ef2-92b7-4817-a5f3-0c575361f745")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaType.APPLICATION_JSON_UTF8)
        );

        resultActions
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
    }
}
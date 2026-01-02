package com.digicompass.backend.integration;

import com.digicompass.backend.application.interfaces.RatingService;
import com.digicompass.backend.application.models.route.Rating;
import com.digicompass.backend.controller.dto.RatingDto;
import com.digicompass.backend.controller.mapper.RatingMapperController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@WithMockUser(username = "testuser", roles = "USER")
public class RatingControllerCatchIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RatingMapperController ratingMapper;

    @MockitoBean
    private RatingService ratingService;

    private Long routeId = 1L;

    @Test
    void createRating_whenServiceThrowsException_returns500() throws Exception {

        RatingDto request = new RatingDto();
        request.setRating(5d);

        when(ratingMapper.toModel(any(RatingDto.class))).thenReturn(new Rating());

        when(ratingService.addRating(any())).thenThrow(new RuntimeException("Forced exception"));

        mockMvc.perform(post("/rating")
                        .contentType("application/json")
                        .content("""
                            {
                                "rating": 5
                            }
                        """))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getRatingByRoute_whenServiceThrowsException_returns500() throws Exception {

        when(ratingMapper.toModel(any(RatingDto.class))).thenReturn(new Rating());

        when(ratingService.getRatingsByRouteId(any())).thenThrow(new RuntimeException("Forced exception"));

        mockMvc.perform(get("/rating/route/{id}", routeId)
                        .contentType("application/json")
                        .content("""
                            {
                                
                            }
                        """))
                .andExpect(status().isInternalServerError());
    }


    @Test
    void updateRating_whenServiceThrowsException_returns500() throws Exception {
        when(ratingMapper.toModel(any(RatingDto.class))).thenReturn(new Rating());

        when(ratingService.updateRating(any())).thenThrow(new RuntimeException("Forced exception"));

        mockMvc.perform(put("/rating/update/{id}", routeId)
                        .contentType("application/json")
                        .content("""
                            {
                                
                            }
                        """))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void deleteRating_whenServiceThrowsException_returns500() throws Exception {
        when(ratingMapper.toModel(any(RatingDto.class))).thenReturn(new Rating());

        doThrow(new RuntimeException("Forced exception"))
                .when(ratingService).deleteRating(any());


        mockMvc.perform(put("/rating/update/{id}", routeId)
                        .contentType("application/json")
                        .content("""
                            {
                                
                            }
                        """))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void deleteRating_whenIllegalArgumentException_returns404() throws Exception {

        doThrow(new IllegalArgumentException("not found"))
                .when(ratingService).deleteRating(anyLong());

        mockMvc.perform(delete("/ratings/delete/{id}", 1L))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void deleteRating_whenAccessDeniedException_returns403() throws Exception {

        doThrow(new AccessDeniedException("Access denied"))
                .when(ratingService).deleteRating(anyLong());

        mockMvc.perform(delete("/ratings/delete/{id}", 1L))
                .andExpect(status().isInternalServerError());
    }







//    @DeleteMapping("/delete/{id}")
//    public ResponseEntity<Void> deleteRating(@PathVariable Long id) {
//        log.info("Deleting rating {}", id);
//        try{
//            ratingService.deleteRating(id);
//            return ResponseEntity
//                    .status(HttpStatus.NO_CONTENT)
//                    .build();
//        } catch (IllegalArgumentException ex) {
//            log.warn("[CONTROLLER] Review not found: {}", id);
//            return ResponseEntity
//                    .status(HttpStatus.NOT_FOUND).body(null);
//        } catch (AccessDeniedException ex) {
//            log.warn("[CONTROLLER] Access denied deleting review {}", id);
//            return ResponseEntity
//                    .status(HttpStatus.FORBIDDEN)
//                    .body(null);
//        } catch (Exception ex) {
//            log.error("[CONTROLLER] Error deleting review {}", id, ex);
//            return ResponseEntity
//                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(null);
//        }
//    }

}

package kg.manas.skincare.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import kg.manas.skincare.dto.response.AnalysisHistoryResponse;
import kg.manas.skincare.dto.response.AnalysisResponse;
import kg.manas.skincare.dto.response.SimpleResponse;
import kg.manas.skincare.model.User;
import kg.manas.skincare.service.SkinAnalysisService;
import kg.manas.skincare.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/analysis")
@RequiredArgsConstructor
@Tag(name = "Skin Analysis", description = "Анализ кожи и история")
public class AnalysisController {

    private final SkinAnalysisService analysisService;
    private final UserService userService;

    @PostMapping(value = "/detect", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public AnalysisResponse detect(@RequestParam MultipartFile photo,
                                   @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getByEmail(userDetails.getUsername());
        return analysisService.performAnalysis(user, photo);
    }

    @GetMapping("/history")
    public List<AnalysisHistoryResponse> getHistory(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getByEmail(userDetails.getUsername());
        return analysisService.getUserHistory(user);
    }

    @GetMapping("/{id}")
    public AnalysisResponse getAnalysisDetails(@PathVariable Long id,
                                               @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getByEmail(userDetails.getUsername());
        return analysisService.getAnalysisDetails(id, user);
    }

    @DeleteMapping("/{id}")
    public SimpleResponse delete(@PathVariable Long id,
                                 @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getByEmail(userDetails.getUsername());
        analysisService.deleteAnalysis(id, user);
        return new SimpleResponse(HttpStatus.OK, "Анализ успешно удален");
    }
}
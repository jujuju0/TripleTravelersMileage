package pretask.triple.mileage.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pretask.triple.mileage.model.dto.ResultResponse;
import pretask.triple.mileage.model.dto.ReviewEventRequest;
import pretask.triple.mileage.service.ReviewPointService;

@RequiredArgsConstructor
@RestController
public class ReviewPointController {
    private final ReviewPointService reviewPointService;

    @PostMapping("/events")
    private ResponseEntity ReviewEvents(@RequestBody ReviewEventRequest event)
    {
        ResultResponse _res = new ResultResponse();
        _res.setSuccess(true);
        _res.setType("리뷰 이벤트");
        _res.setResult(reviewPointService.ReviewEvents(event));
        return  ResponseEntity.ok(_res);
    }

    @GetMapping("/point/{userId}")
    public ResponseEntity GetMyPoint(@PathVariable String userId)
    {
        ResultResponse _res = new ResultResponse();
        _res.setSuccess(true);
        _res.setType(userId + "님의 현재 누적 포인트 조회");
        _res.setResult(reviewPointService.GetMyPoint(userId));
        return  ResponseEntity.ok(_res);
    }

    @GetMapping("/point/detail/{userId}")
    public ResponseEntity GetMyPointDetail(@PathVariable String userId)
    {
        ResultResponse _res = new ResultResponse();
        _res.setSuccess(true);
        _res.setType(userId + "님의 포인트 상세내역 조회");
        _res.setResult(reviewPointService.GetMyPointDetail(userId));
        return ResponseEntity.ok(_res);
    }
}

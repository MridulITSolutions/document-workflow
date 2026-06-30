package com.docshare.requestservice.controller;

import com.docshare.requestservice.dto.CreateRequestRequest;
import com.docshare.requestservice.dto.CreateRequestResponse;
import com.docshare.requestservice.dto.RequestSummaryResponse;
import com.docshare.requestservice.service.RequestService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/requests")
@SecurityRequirement(name = "bearerAuth")
public class RequestController {

    private final RequestService requestService;

    public RequestController(RequestService requestService) {

        this.requestService = requestService;
    }

    @PostMapping
    public CreateRequestResponse createRequest(
            @RequestBody CreateRequestRequest request) {

        return requestService.createRequest(request);
    }
    @GetMapping
    public List<RequestSummaryResponse> getRequests() {

        return requestService.getRequests();

    }

}
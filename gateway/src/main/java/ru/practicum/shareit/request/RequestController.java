package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final RequestClient requestClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> addRequest(@RequestHeader(USER_ID_HEADER) @Positive long userId,
                                             @Valid @RequestBody RequestDto dto) {
        log.info("Add new request ({}) userId: {}", dto.getDescription(), userId);
        return requestClient.addNewRequest(userId, dto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestsByUserId(@RequestHeader(USER_ID_HEADER) @Positive long userId) {
        log.info("Find all request by userId: {}", userId);
        return requestClient.getAllRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequestsByUserIdNot(@RequestHeader(USER_ID_HEADER) @Positive long userId,
                                                            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
                                                            @RequestParam(name = "size", defaultValue = "20") @Positive Integer size) {
        log.info("Find all request where userId not {}", userId);
        return requestClient.getAllRequestsByUserIdNot(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(USER_ID_HEADER) @Positive long userId,
                                                 @PathVariable @Positive long requestId) {
        log.info("Find request userId - {}, requestId - {}", userId, requestId);
        return requestClient.getRequestById(userId, requestId);
    }
}

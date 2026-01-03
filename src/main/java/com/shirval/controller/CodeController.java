package com.shirval.controller;

import com.shirval.exception.WrongCode;
import com.shirval.model.Code;
import com.shirval.repository.CodeRepository;
import io.micrometer.core.annotation.Timed;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/v1/code")
public class CodeController {

    private final CodeRepository codeRepository;

    public CodeController(CodeRepository codeRepository) {
        this.codeRepository = codeRepository;
    }

    @GetMapping("/current/byCustomerId/{customerId}")
    @Timed("get.code.by.customer")
    public Code getCodeByCustomer(@PathVariable int customerId) {
        return codeRepository.getCode(customerId, Instant.now());
    }

    @GetMapping("/all/byCustomerId/{customerId}")
    @Timed("get.all.codes.by.customer")
    public CodesResponse getCodesByCustomer(@PathVariable int customerId) {
        return new CodesResponse(Instant.now(), codeRepository.getCodes(customerId));
    }

    @GetMapping("/check")
    @Timed("check.code")
    public void checkCode(@RequestParam int customerId, @RequestParam String code) {
        if (!codeRepository.checkCode(customerId, code)) {
            throw new WrongCode();
        }
    }

    public record CodesResponse(
            Instant currentTime,
            List<Code> codes
    ) {}
}

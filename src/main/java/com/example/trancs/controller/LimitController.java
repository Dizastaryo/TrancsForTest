package com.example.trancs.controller;
import com.example.trancs.model.Limit;
import com.example.trancs.service.LimitService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.http.ResponseEntity;
@RestController
@RequestMapping("/api/limits")
@CrossOrigin(origins = "http://localhost:3000")
public class LimitController {
    private final LimitService limitService;
    public LimitController(LimitService limitService) {
        this.limitService = limitService;
    }
    @GetMapping("/all")
    public ResponseEntity<List<Limit>> getAllLimits() {
        List<Limit> limits = limitService.getAllLimits();
        return ResponseEntity.ok(limits);
    }
}

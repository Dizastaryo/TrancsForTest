package com.example.trancs.service;
import org.springframework.stereotype.Service;
import com.example.trancs.model.Limit;
import com.example.trancs.repository.LimitRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
@Service
public class LimitService {
    @Autowired
    private LimitRepository limitRepository;
    public List<Limit> getAllLimits() {
        return limitRepository.findAll();
    }
}


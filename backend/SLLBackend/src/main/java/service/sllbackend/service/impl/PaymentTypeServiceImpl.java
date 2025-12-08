package service.sllbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import service.sllbackend.entity.PaymentType;
import service.sllbackend.repository.PaymentTypeRepo;
import service.sllbackend.service.PaymentTypeService;

@Service
@RequiredArgsConstructor
public class PaymentTypeServiceImpl implements PaymentTypeService {
    private final PaymentTypeRepo paymentTypeRepo;

    @Override
    public PaymentType findByName(String name) {
        return paymentTypeRepo.findByName(name);
    }
}

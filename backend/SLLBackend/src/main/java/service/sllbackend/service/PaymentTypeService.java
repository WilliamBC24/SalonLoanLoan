package service.sllbackend.service;

import service.sllbackend.entity.PaymentType;

public interface PaymentTypeService {
    PaymentType findByName(String name);
}

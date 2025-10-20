package service.sllbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.sllbackend.entity.Loyalty;
import service.sllbackend.entity.UserAccount;
import service.sllbackend.repository.LoyaltyRepo;
import service.sllbackend.service.LoyaltyService;

@Service
@RequiredArgsConstructor
public class LoyaltyServiceImpl implements LoyaltyService {
    private final LoyaltyRepo loyaltyRepo;

    @Override
    @Transactional(readOnly = true)
    public Loyalty findLoyaltyByUser(UserAccount userAccount) {
        return loyaltyRepo.findByUser(userAccount);
    }
}

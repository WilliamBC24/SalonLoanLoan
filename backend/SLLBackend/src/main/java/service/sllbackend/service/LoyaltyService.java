package service.sllbackend.service;

import service.sllbackend.entity.Loyalty;
import service.sllbackend.entity.UserAccount;

public interface LoyaltyService {
    Loyalty findLoyaltyByUser(UserAccount userAccount);
}

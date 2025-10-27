package service.sllbackend.service;

import service.sllbackend.entity.Loyalty;
import service.sllbackend.entity.UserAccount;
import service.sllbackend.web.dto.LoyaltyListViewDTO;

import java.util.List;

public interface LoyaltyService {
    Loyalty findLoyaltyByUser(UserAccount userAccount);
    List<LoyaltyListViewDTO> findByUsernameToDTO(String username);
}

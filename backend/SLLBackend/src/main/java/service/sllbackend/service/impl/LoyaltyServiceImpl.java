package service.sllbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import service.sllbackend.entity.Loyalty;
import service.sllbackend.entity.UserAccount;
import service.sllbackend.repository.LoyaltyRepo;
import service.sllbackend.service.LoyaltyService;
import service.sllbackend.utils.DTOMapper;
import service.sllbackend.web.dto.LoyaltyListViewDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoyaltyServiceImpl implements LoyaltyService {
    private final LoyaltyRepo loyaltyRepo;
    private final DTOMapper DTOMapper;

    @Override
    @Transactional(readOnly = true)
    public Loyalty findLoyaltyByUser(UserAccount userAccount) {
        return loyaltyRepo.findByUser(userAccount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoyaltyListViewDTO> findByUsernameToDTO(String username) {
        List<Loyalty> loyalties;
        if (username != null && !username.isBlank()) {
            loyalties = loyaltyRepo.findByUsernameContaining(username);
        } else {
            loyalties = loyaltyRepo.findAll();
        }
        return DTOMapper.toLoyaltyListViewDTOList(loyalties);
    }
}

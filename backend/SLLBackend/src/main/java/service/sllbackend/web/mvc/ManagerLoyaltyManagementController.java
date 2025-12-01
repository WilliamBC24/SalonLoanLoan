package service.sllbackend.web.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import service.sllbackend.service.LoyaltyService;
import service.sllbackend.web.dto.LoyaltyListViewDTO;

import java.util.List;

@Controller
@RequestMapping("/manager/loyalty")
@RequiredArgsConstructor
public class ManagerLoyaltyManagementController {
    private final LoyaltyService loyaltyService;

    @GetMapping("/list")
    public String listLoyalty(@RequestParam(value = "username", required = false) String username, Model model){
        List<LoyaltyListViewDTO> loyaltyList;
        loyaltyList = loyaltyService.findByUsernameToDTO(username == null ? username : username.trim());
        model.addAttribute("loyaltyList", loyaltyList);
        return "staff-loyalty-list";
    }
}

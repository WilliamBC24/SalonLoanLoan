package service.sllbackend.web.mvc;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import service.sllbackend.entity.Promotion;
import service.sllbackend.entity.PromotionStatus;
import service.sllbackend.enumerator.DiscountType;
import service.sllbackend.service.PromotionService;

@Controller
@RequestMapping("/manager/promotion")
public class ManagerPromotionController {

    private final PromotionService promotionService;

    public ManagerPromotionController(PromotionService promotionService) {
        this.promotionService = promotionService;
    }

    @GetMapping("/list")
    public String listPromotions(Model model) {
        List<Promotion> promotions = promotionService.getAllPromotions();
        model.addAttribute("promotions", promotions);
        return "manager-promotion-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        List<PromotionStatus> statuses = promotionService.getAllPromotionStatuses();
        model.addAttribute("statuses", statuses);
        return "manager-promotion-create";
    }

    @PostMapping("/create")
    public String createPromotion(
            @RequestParam String promotionName,
            @RequestParam String promotionDescription,
            @RequestParam String discountType,
            @RequestParam Integer discountAmount,
            @RequestParam String effectiveFrom,
            @RequestParam String effectiveTo,
            @RequestParam Integer promotionStatusId,
            RedirectAttributes redirectAttributes) {
        
        try {
            List<PromotionStatus> statuses = promotionService.getAllPromotionStatuses();
            PromotionStatus status = statuses.stream()
                .filter(s -> s.getId().equals(promotionStatusId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Promotion status not found"));
            
            Promotion promotion = Promotion.builder()
                .promotionName(promotionName)
                .promotionDescription(promotionDescription)
                .discountType(DiscountType.valueOf(discountType))
                .discountAmount(discountAmount)
                .effectiveFrom(LocalDateTime.parse(effectiveFrom))
                .effectiveTo(LocalDateTime.parse(effectiveTo))
                .promotionStatus(status)
                .build();
            
            promotionService.createPromotion(promotion);
            redirectAttributes.addFlashAttribute("successMessage", "Promotion created successfully!");
            return "redirect:/manager/promotion/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating promotion: " + e.getMessage());
            return "redirect:/manager/promotion/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Promotion promotion = promotionService.getPromotionById(id);
        if (promotion == null) {
            throw new RuntimeException("Promotion not found");
        }
        
        List<PromotionStatus> statuses = promotionService.getAllPromotionStatuses();
        model.addAttribute("promotion", promotion);
        model.addAttribute("statuses", statuses);
        return "manager-promotion-edit";
    }

    @PostMapping("/edit/{id}")
    public String editPromotion(
            @PathVariable Integer id,
            @RequestParam String promotionName,
            @RequestParam String promotionDescription,
            @RequestParam String discountType,
            @RequestParam Integer discountAmount,
            @RequestParam String effectiveFrom,
            @RequestParam String effectiveTo,
            @RequestParam Integer promotionStatusId,
            RedirectAttributes redirectAttributes) {
        
        try {
            List<PromotionStatus> statuses = promotionService.getAllPromotionStatuses();
            PromotionStatus status = statuses.stream()
                .filter(s -> s.getId().equals(promotionStatusId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Promotion status not found"));
            
            Promotion promotion = Promotion.builder()
                .promotionName(promotionName)
                .promotionDescription(promotionDescription)
                .discountType(DiscountType.valueOf(discountType))
                .discountAmount(discountAmount)
                .effectiveFrom(LocalDateTime.parse(effectiveFrom))
                .effectiveTo(LocalDateTime.parse(effectiveTo))
                .promotionStatus(status)
                .build();
            
            promotionService.updatePromotion(id, promotion);
            redirectAttributes.addFlashAttribute("successMessage", "Promotion updated successfully!");
            return "redirect:/manager/promotion/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating promotion: " + e.getMessage());
            return "redirect:/manager/promotion/edit/" + id;
        }
    }
}

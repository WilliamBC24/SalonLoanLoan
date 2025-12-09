package service.sllbackend.web.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import service.sllbackend.entity.StaffCommission;
import service.sllbackend.repository.StaffCommissionRepo;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/commission")
@RequiredArgsConstructor
public class AdminCommissionsController {

    private final StaffCommissionRepo staffCommissionRepo;

    /**
     * View current commission % for each role (position + commission type).
     */
    @GetMapping
    public String viewCommission(Model model) {
        List<StaffCommission> commissions =
                staffCommissionRepo.findAll(
                        Sort.by("position.positionName").ascending()
                                .and(Sort.by("commissionType").ascending())
                );

        model.addAttribute("commissions", commissions);
        return "admin-commission-list";  // your Thymeleaf view
    }

    /**
     * Accept edits from the commission screen.
     *
     * Form should submit parallel arrays:
     *   <input type="hidden" name="id" value="...">
     *   <input type="number" name="commission" value="...">
     * Spring will bind them into two lists with matching indexes.
     */
    @PostMapping
    public String updateCommission(
            @RequestParam("id") List<Integer> ids,
            @RequestParam("commission") List<Short> commissionValues,
            RedirectAttributes redirectAttributes
    ) {
        if (ids.size() != commissionValues.size()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Mismatch between IDs and commission values.");
            return "redirect:/admin/commission";
        }

        List<StaffCommission> toSave = new ArrayList<>();

        for (int i = 0; i < ids.size(); i++) {
            Integer id = ids.get(i);
            Short newCommission = commissionValues.get(i);

            staffCommissionRepo.findById(id).ifPresent(existing -> {
                existing.setCommission(newCommission);
                toSave.add(existing);
            });
        }

        if (!toSave.isEmpty()) {
            staffCommissionRepo.saveAll(toSave);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Commission percentages updated successfully.");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "No commissions were updated.");
        }

        return "redirect:/admin/commission";
    }
}

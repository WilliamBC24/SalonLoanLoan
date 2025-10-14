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

import service.sllbackend.entity.Voucher;
import service.sllbackend.entity.VoucherStatus;
import service.sllbackend.enumerator.DiscountType;
import service.sllbackend.service.VoucherService;

@Controller
@RequestMapping("/staff/voucher")
public class StaffVoucherController {

    private final VoucherService voucherService;

    public StaffVoucherController(VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    @GetMapping("/list")
    public String listVouchers(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String discountType,
            @RequestParam(required = false) Integer statusId,
            Model model) {
        
        // Get all statuses for filters
        List<VoucherStatus> allStatuses = voucherService.getAllVoucherStatuses();
        model.addAttribute("allStatuses", allStatuses);
        
        // Get vouchers with filters applied in service layer
        List<Voucher> vouchers = voucherService.getVouchers(code, name, discountType, statusId);
        
        model.addAttribute("vouchers", vouchers);
        model.addAttribute("searchCode", code);
        model.addAttribute("searchName", name);
        model.addAttribute("selectedDiscountType", discountType);
        model.addAttribute("selectedStatusId", statusId);
        
        return "staff-voucher-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        List<VoucherStatus> statuses = voucherService.getAllVoucherStatuses();
        model.addAttribute("statuses", statuses);
        return "staff-voucher-create";
    }

    @PostMapping("/create")
    public String createVoucher(
            @RequestParam String voucherName,
            @RequestParam String voucherDescription,
            @RequestParam String voucherCode,
            @RequestParam String discountType,
            @RequestParam Integer discountAmount,
            @RequestParam String effectiveFrom,
            @RequestParam String effectiveTo,
            @RequestParam Integer maxUsage,
            @RequestParam Integer voucherStatusId,
            RedirectAttributes redirectAttributes) {
        
        try {
            List<VoucherStatus> statuses = voucherService.getAllVoucherStatuses();
            VoucherStatus status = statuses.stream()
                .filter(s -> s.getId().equals(voucherStatusId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Voucher status not found"));
            
            Voucher voucher = Voucher.builder()
                .voucherName(voucherName)
                .voucherDescription(voucherDescription)
                .voucherCode(voucherCode)
                .discountType(DiscountType.valueOf(discountType))
                .discountAmount(discountAmount)
                .effectiveFrom(LocalDateTime.parse(effectiveFrom))
                .effectiveTo(LocalDateTime.parse(effectiveTo))
                .maxUsage(maxUsage)
                .usedCount(0)
                .voucherStatus(status)
                .build();
            
            voucherService.createVoucher(voucher);
            redirectAttributes.addFlashAttribute("successMessage", "Voucher created successfully!");
            return "redirect:/staff/voucher/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating voucher: " + e.getMessage());
            return "redirect:/staff/voucher/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Voucher voucher = voucherService.getVoucherById(id);
        if (voucher == null) {
            throw new RuntimeException("Voucher not found");
        }
        
        List<VoucherStatus> statuses = voucherService.getAllVoucherStatuses();
        model.addAttribute("voucher", voucher);
        model.addAttribute("statuses", statuses);
        return "staff-voucher-edit";
    }

    @PostMapping("/edit/{id}")
    public String editVoucher(
            @PathVariable Integer id,
            @RequestParam String voucherName,
            @RequestParam String voucherDescription,
            @RequestParam String voucherCode,
            @RequestParam String discountType,
            @RequestParam Integer discountAmount,
            @RequestParam String effectiveFrom,
            @RequestParam String effectiveTo,
            @RequestParam Integer maxUsage,
            @RequestParam Integer voucherStatusId,
            RedirectAttributes redirectAttributes) {
        
        try {
            List<VoucherStatus> statuses = voucherService.getAllVoucherStatuses();
            VoucherStatus status = statuses.stream()
                .filter(s -> s.getId().equals(voucherStatusId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Voucher status not found"));
            
            Voucher voucher = Voucher.builder()
                .voucherName(voucherName)
                .voucherDescription(voucherDescription)
                .voucherCode(voucherCode)
                .discountType(DiscountType.valueOf(discountType))
                .discountAmount(discountAmount)
                .effectiveFrom(LocalDateTime.parse(effectiveFrom))
                .effectiveTo(LocalDateTime.parse(effectiveTo))
                .maxUsage(maxUsage)
                .voucherStatus(status)
                .build();
            
            voucherService.updateVoucher(id, voucher);
            redirectAttributes.addFlashAttribute("successMessage", "Voucher updated successfully!");
            return "redirect:/staff/voucher/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating voucher: " + e.getMessage());
            return "redirect:/staff/voucher/edit/" + id;
        }
    }
}

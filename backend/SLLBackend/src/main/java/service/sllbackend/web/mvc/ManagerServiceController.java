package service.sllbackend.web.mvc;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import lombok.RequiredArgsConstructor;
import service.sllbackend.entity.Service;
import service.sllbackend.entity.ServiceCategory;
import service.sllbackend.enumerator.ServiceType;
import service.sllbackend.repository.ServiceCategoryRepo;
import service.sllbackend.repository.ServiceRepo;

@Controller
@RequestMapping("/manager/service")
@RequiredArgsConstructor
public class ManagerServiceController {

    private final ServiceRepo serviceRepo;
    private final ServiceCategoryRepo serviceCategoryRepo;

    @GetMapping("/list")
    public String listServices(
            @RequestParam(required = false) List<String> types,
            @RequestParam(required = false) List<Integer> categories,
            @RequestParam(required = false) String name,
            Model model) {
        
        // Get all categories for filters
        List<ServiceCategory> allCategories = serviceCategoryRepo.findAll();
        model.addAttribute("allCategories", allCategories);

        List<Service> services = serviceRepo.findAll();
        services = services.stream().filter(service -> {
            boolean matchesType = (types == null || types.isEmpty()) || types.contains(service.getServiceType().name());
            boolean matchesCategory = (categories == null || categories.isEmpty()) || categories.contains(service.getServiceCategory().getId());
            boolean matchesName = (name == null || name.trim().isEmpty()) || service.getServiceName().toLowerCase().contains(name.trim().toLowerCase());
            return matchesType && matchesCategory && matchesName;
        }).toList();
        
        model.addAttribute("services", services);
        model.addAttribute("selectedTypes", types);
        model.addAttribute("selectedCategories", categories);
        model.addAttribute("searchName", name);
        
        return "manager-service-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        List<ServiceCategory> categories = serviceCategoryRepo.findAll();
        model.addAttribute("categories", categories);
        return "manager-service-create";
    }

    @PostMapping("/create")
    public String createService(
            @RequestParam String serviceName,
            @RequestParam Integer serviceCategoryId,
            @RequestParam String serviceType,
            @RequestParam Integer servicePrice,
            @RequestParam Short durationMinutes,
            @RequestParam(required = false) String serviceDescription,
            @RequestParam(required = false, defaultValue = "false") Boolean activeStatus,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Check for duplicate service name
            if (serviceRepo.existsByServiceNameIgnoreCase(serviceName)) {
                redirectAttributes.addFlashAttribute("errorMessage", "A service with the name '" + serviceName + "' already exists");
                return "redirect:/manager/service/create";
            }

            ServiceCategory category = serviceCategoryRepo.findById(serviceCategoryId)
                .orElseThrow(() -> new RuntimeException("Service category not found"));
            
            Service service = Service.builder()
                .serviceName(serviceName)
                .serviceCategory(category)
                .serviceType(ServiceType.valueOf(serviceType))
                .servicePrice(servicePrice)
                .durationMinutes(durationMinutes)
                .serviceDescription(serviceDescription)
                .activeStatus(activeStatus)
                .build();
            
            serviceRepo.save(service);
            redirectAttributes.addFlashAttribute("successMessage", "Service created successfully!");
            return "redirect:/manager/service/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating service: " + e.getMessage());
            return "redirect:/manager/service/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Service service = serviceRepo.findByIdWithCategory(id)
            .orElseThrow(() -> new RuntimeException("Service not found"));
        
        List<ServiceCategory> categories = serviceCategoryRepo.findAll();
        model.addAttribute("service", service);
        model.addAttribute("categories", categories);
        return "manager-service-edit";
    }

    @PostMapping("/edit/{id}")
    public String editService(
            @PathVariable Integer id,
            @RequestParam String serviceName,
            @RequestParam Integer serviceCategoryId,
            @RequestParam String serviceType,
            @RequestParam Integer servicePrice,
            @RequestParam Short durationMinutes,
            @RequestParam(required = false) String serviceDescription,
            @RequestParam(required = false, defaultValue = "false") Boolean activeStatus,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Check for duplicate service name (excluding current service)
            if (serviceRepo.existsByServiceNameIgnoreCaseAndIdNot(serviceName, id)) {
                redirectAttributes.addFlashAttribute("errorMessage", "A service with the name '" + serviceName + "' already exists");
                return "redirect:/manager/service/edit/" + id;
            }

            Service service = serviceRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));
            
            ServiceCategory category = serviceCategoryRepo.findById(serviceCategoryId)
                .orElseThrow(() -> new RuntimeException("Service category not found"));
            
            service.setServiceName(serviceName);
            service.setServiceCategory(category);
            service.setServiceType(ServiceType.valueOf(serviceType));
            service.setServicePrice(servicePrice);
            service.setDurationMinutes(durationMinutes);
            service.setServiceDescription(serviceDescription);
            service.setActiveStatus(activeStatus);
            
            serviceRepo.save(service);
            redirectAttributes.addFlashAttribute("successMessage", "Service updated successfully!");
            return "redirect:/manager/service/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating service: " + e.getMessage());
            return "redirect:/manager/service/edit/" + id;
        }
    }
}

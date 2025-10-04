package service.sllbackend.web.mvc;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import service.sllbackend.entity.Service;
import service.sllbackend.entity.ServiceCategory;
import service.sllbackend.enumerator.ServiceType;
import service.sllbackend.repository.ServiceCategoryRepo;
import service.sllbackend.repository.ServiceRepo;

@Controller
@RequestMapping("/staff/service")
public class StaffServiceController {

    private final ServiceRepo serviceRepo;
    private final ServiceCategoryRepo serviceCategoryRepo;

    public StaffServiceController(ServiceRepo serviceRepo, ServiceCategoryRepo serviceCategoryRepo) {
        this.serviceRepo = serviceRepo;
        this.serviceCategoryRepo = serviceCategoryRepo;
    }

    @GetMapping("/list")
    @Transactional(readOnly = true)
    public String listServices(
            @RequestParam(required = false) List<String> types,
            @RequestParam(required = false) List<Integer> categories,
            @RequestParam(required = false) String name,
            Model model) {
        
        // Get all categories for filters
        List<ServiceCategory> allCategories = serviceCategoryRepo.findAll();
        model.addAttribute("allCategories", allCategories);
        
        // Get services based on filters
        List<Service> services;
        if (types != null || categories != null || (name != null && !name.trim().isEmpty())) {
            List<ServiceType> serviceTypes = null;
            if (types != null && !types.isEmpty()) {
                serviceTypes = types.stream()
                    .map(ServiceType::valueOf)
                    .toList();
            }
            String searchName = (name != null && !name.trim().isEmpty()) ? name.trim() : null;
            services = serviceRepo.searchServices(serviceTypes, categories, searchName);
        } else {
            services = serviceRepo.findAllWithCategory();
        }
        
        model.addAttribute("services", services);
        model.addAttribute("selectedTypes", types);
        model.addAttribute("selectedCategories", categories);
        model.addAttribute("searchName", name);
        
        return "staff-service-list";
    }

    @GetMapping("/create")
    @Transactional(readOnly = true)
    public String showCreateForm(Model model) {
        List<ServiceCategory> categories = serviceCategoryRepo.findAll();
        model.addAttribute("categories", categories);
        return "staff-service-create";
    }

    @PostMapping("/create")
    @Transactional
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
            return "redirect:/staff/service/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error creating service: " + e.getMessage());
            return "redirect:/staff/service/create";
        }
    }

    @GetMapping("/edit/{id}")
    @Transactional(readOnly = true)
    public String showEditForm(@PathVariable Integer id, Model model) {
        Service service = serviceRepo.findByIdWithCategory(id)
            .orElseThrow(() -> new RuntimeException("Service not found"));
        
        List<ServiceCategory> categories = serviceCategoryRepo.findAll();
        model.addAttribute("service", service);
        model.addAttribute("categories", categories);
        return "staff-service-edit";
    }

    @PostMapping("/edit/{id}")
    @Transactional
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
            return "redirect:/staff/service/list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error updating service: " + e.getMessage());
            return "redirect:/staff/service/edit/" + id;
        }
    }
}

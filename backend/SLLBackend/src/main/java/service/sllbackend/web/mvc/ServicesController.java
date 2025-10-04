package service.sllbackend.web.mvc;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import service.sllbackend.entity.Service;
import service.sllbackend.entity.ServiceCategory;
import service.sllbackend.entity.ServiceCombo;
import service.sllbackend.enumerator.ServiceType;
import service.sllbackend.repository.ServiceCategoryRepo;
import service.sllbackend.repository.ServiceComboRepo;
import service.sllbackend.repository.ServiceRepo;

@Controller
@RequestMapping("/")
public class ServicesController {

    private final ServiceRepo serviceRepo;
    private final ServiceCategoryRepo serviceCategoryRepo;
    private final ServiceComboRepo serviceComboRepo;

    public ServicesController(ServiceRepo serviceRepo, ServiceCategoryRepo serviceCategoryRepo, ServiceComboRepo serviceComboRepo) {
        this.serviceRepo = serviceRepo;
        this.serviceCategoryRepo = serviceCategoryRepo;
        this.serviceComboRepo = serviceComboRepo;
    }

    @GetMapping("services")
    @Transactional(readOnly = true)
    public String listServices(
            @RequestParam(required = false) List<String> types,
            @RequestParam(required = false) List<Integer> categories,
            Model model) {
        
        // Get all categories for filters
        List<ServiceCategory> allCategories = serviceCategoryRepo.findAll();
        model.addAttribute("allCategories", allCategories);
        
        // Get services based on filters
        List<Service> services;
        if (types != null || categories != null) {
            List<ServiceType> serviceTypes = null;
            if (types != null && !types.isEmpty()) {
                serviceTypes = types.stream()
                    .map(ServiceType::valueOf)
                    .toList();
            }
            services = serviceRepo.searchServices(serviceTypes, categories);
        } else {
            services = serviceRepo.findAllWithCategory();
        }
        
        model.addAttribute("services", services);
        model.addAttribute("selectedTypes", types);
        model.addAttribute("selectedCategories", categories);
        
        return "services";
    }

    @GetMapping("services/{id}")
    @Transactional(readOnly = true)
    public String viewServiceDetails(@PathVariable Integer id, Model model) {
        Service service = serviceRepo.findByIdWithCategory(id)
            .orElseThrow(() -> new RuntimeException("Service not found"));
        
        model.addAttribute("service", service);
        
        // If it's a combo, get the combo services
        if (service.getServiceType() == ServiceType.COMBO) {
            List<ServiceCombo> comboServices = serviceComboRepo.findByComboIdWithDetails(id);
            model.addAttribute("comboServices", comboServices);
            return "combo-details";
        }
        
        return "service-details";
    }
}


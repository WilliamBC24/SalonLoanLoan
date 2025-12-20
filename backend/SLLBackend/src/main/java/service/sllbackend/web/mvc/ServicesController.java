package service.sllbackend.web.mvc;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import service.sllbackend.entity.Service;
import service.sllbackend.entity.ServiceCategory;
import service.sllbackend.entity.ServiceCombo;
import service.sllbackend.enumerator.ServiceType;
import service.sllbackend.service.ServicesService;
import service.sllbackend.utils.DTOMapper;
import service.sllbackend.web.dto.SimpleServiceDTO;

@Controller
@RequestMapping("/services")
@RequiredArgsConstructor
public class ServicesController {
    private final ServicesService servicesService;
    private final DTOMapper dtoMapper;

    @GetMapping
    public String listServices(
            @RequestParam(required = false) List<String> types,
            @RequestParam(required = false) List<Integer> categories,
            @RequestParam(required = false) String name,
            Model model) {

        // Get all categories for filters
        List<ServiceCategory> allCategories = servicesService.getAllCategories();

        // Get services based on filters
        List<Service> services = servicesService.getFilteredServices(types, categories, name != null ? name.trim().replaceAll("\\s+", " ") : null);

        model.addAttribute("allCategories", allCategories);
        model.addAttribute("services", services);
        model.addAttribute("selectedTypes", types);
        model.addAttribute("selectedCategories", categories);
        model.addAttribute("searchName", name);
        
        return "services";
    }

    @GetMapping("/{id}")
    public String viewServiceDetails(@PathVariable Integer id, Model model) {
        Service service = servicesService.getServiceDetails(id);
        
        model.addAttribute("service", service);
        
        // If it's a combo, get the combo services
        if (service.getServiceType() == ServiceType.COMBO) {
            List<ServiceCombo> comboServices = servicesService.getComboServices(id);
            model.addAttribute("comboServices", comboServices);
            return "combo-details";
        }
        
        return "service-details";
    }

    @GetMapping("/search")
    @ResponseBody
    public List<SimpleServiceDTO> searchServices(@RequestParam String query) {
        List<Service> serviceList = servicesService.getServices(query != null ? query.trim().replaceAll("\\s+", " ") : null, 5);
        List<SimpleServiceDTO> simpleServiceDTOList = new ArrayList<>();
        for(Service service : serviceList) {
            simpleServiceDTOList.add(dtoMapper.toSimpleServiceDTO(service));
        }
        return simpleServiceDTOList;
    }
}


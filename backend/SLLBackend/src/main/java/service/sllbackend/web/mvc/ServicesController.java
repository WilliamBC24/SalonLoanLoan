package service.sllbackend.web.mvc;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import service.sllbackend.entity.Service;
import service.sllbackend.repository.ServiceRepo;

@Controller
@RequestMapping("/")
public class ServicesController {

    private final ServiceRepo serviceRepo;

    public ServicesController(ServiceRepo serviceRepo) {
        this.serviceRepo = serviceRepo;
    }

    @GetMapping("services")
    @Transactional(readOnly = true)
    public String listServices(Model model){
        List<Service> services = serviceRepo.findAllWithCategory();
        model.addAttribute("services", services);
        return "services";
    }
}

package service.sllbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import service.sllbackend.entity.Service;
import service.sllbackend.entity.ServiceCategory;
import service.sllbackend.entity.ServiceCombo;
import service.sllbackend.enumerator.ServiceType;
import service.sllbackend.repository.ServiceCategoryRepo;
import service.sllbackend.repository.ServiceComboRepo;
import service.sllbackend.repository.ServiceRepo;
import service.sllbackend.service.ServicesService;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServicesServiceImpl implements ServicesService {
    private final ServiceRepo serviceRepo;
    private final ServiceCategoryRepo serviceCategoryRepo;
    private final ServiceComboRepo serviceComboRepo;

    @Override
    @Transactional(readOnly = true)
    public List<ServiceCategory> getAllCategories() {
        return serviceCategoryRepo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Service> getFilteredServices(List<String> types, List<Integer> categories, String name) {
        if (types != null || categories != null || (name != null && !name.trim().isEmpty())) {
            List<ServiceType> serviceTypes = null;
            if (types != null && !types.isEmpty()) {
                serviceTypes = types.stream()
                        .map(ServiceType::valueOf)
                        .toList();
            }
            String searchName = (name != null && !name.trim().isEmpty()) ? name.trim() : null;
            return serviceRepo.searchServices(serviceTypes, categories, searchName);
        } else {
            return serviceRepo.findAllWithCategory();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Service getServiceDetails(Integer id) {
        return serviceRepo.findByIdWithCategory(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceCombo> getComboServices(Integer comboId) {
        return serviceComboRepo.findByComboIdWithDetails(comboId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Service> getTenServices(){
        return serviceRepo.findAllServices(PageRequest.of(0,10));
    }
}

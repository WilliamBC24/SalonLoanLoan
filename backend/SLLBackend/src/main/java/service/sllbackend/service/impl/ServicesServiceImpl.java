package service.sllbackend.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import service.sllbackend.entity.Service;
import service.sllbackend.entity.ServiceCategory;
import service.sllbackend.entity.ServiceCombo;
import service.sllbackend.enumerator.ServiceType;
import service.sllbackend.repository.ServiceCategoryRepo;
import service.sllbackend.repository.ServiceComboRepo;
import service.sllbackend.repository.ServiceRepo;
import service.sllbackend.service.ServicesService;

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
            List<Service> allServices = serviceRepo.findAllWithCategory();
            List<Service> filteredServices = new ArrayList<>();

            for(Service s : allServices) {
                boolean matchesType = (serviceTypes == null || serviceTypes.contains(s.getServiceType()));
                boolean matchesCategory = (categories == null || categories.isEmpty() || (s.getServiceCategory() != null && categories.contains(s.getServiceCategory().getId())));
                boolean matchesName = (searchName == null || s.getServiceName().toLowerCase().contains(searchName.toLowerCase()));

                if (matchesType && matchesCategory && matchesName) {
                    filteredServices.add(s);
                }
            }

            return filteredServices;
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
    public List<Service> getServices(String query, int page) {
        return serviceRepo.findAllServices(query, PageRequest.of(0, page));
    }
}

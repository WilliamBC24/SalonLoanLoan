package service.sllbackend.service;

import service.sllbackend.entity.Service;
import service.sllbackend.entity.ServiceCategory;
import service.sllbackend.entity.ServiceCombo;

import java.util.List;

public interface ServicesService {
    List<ServiceCategory> getAllCategories();

    List<Service> getFilteredServices(List<String> types, List<Integer> categories, String name);

    Service getServiceDetails(Integer id);

    List<ServiceCombo> getComboServices(Integer comboId);

    List<Service> getTenServices();
}

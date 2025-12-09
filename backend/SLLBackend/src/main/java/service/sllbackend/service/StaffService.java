package service.sllbackend.service;

import service.sllbackend.entity.Staff;
import service.sllbackend.enumerator.StaffStatus;

import java.util.List;

public interface StaffService {
    Staff findById(int staffId);
    List<Staff> findAllByStatus(StaffStatus status);
    Staff findByName(String name);
}

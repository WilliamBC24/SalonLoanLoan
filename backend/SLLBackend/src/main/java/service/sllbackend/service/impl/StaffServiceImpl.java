package service.sllbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import service.sllbackend.entity.Staff;
import service.sllbackend.enumerator.StaffStatus;
import service.sllbackend.repository.StaffRepo;
import service.sllbackend.service.StaffService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StaffServiceImpl implements StaffService {
    private final StaffRepo staffRepo;

    @Override
    public Staff findById(int staffId) {
        return staffRepo.findById(staffId).orElse(null);
    }

    @Override
    public List<Staff> findAllByStatus(StaffStatus status) {
        return staffRepo.findByStaffStatus(status);
    }
}

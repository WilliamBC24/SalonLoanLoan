package service.sllbackend.utils;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import service.sllbackend.entity.StaffAccount;
import service.sllbackend.entity.UserAccount;
import service.sllbackend.web.dto.StaffProfileViewDTO;
import service.sllbackend.web.dto.UserProfileViewDTO;

@Mapper(componentModel = "spring")
public interface ProfileMapper {
    UserProfileViewDTO toUserProfileViewDTO(UserAccount userAccount);

    @Mapping(target = "active", expression = "java(staffAccount.getAccountStatus() == service.sllbackend.enumerator.AccountStatus.ACTIVE)")
    @Mapping(target = "name", source = "staff.name")
    @Mapping(target = "email", source = "staff.email")
    @Mapping(target = "dateHired", source = "staff.dateHired")
    @Mapping(target = "socialSecurityNum", source = "staff.socialSecurityNum")
    @Mapping(target = "staffStatus", expression = "java(staffAccount.getStaff().getStaffStatus().toString())")
    StaffProfileViewDTO toStaffProfileViewDTO(StaffAccount staffAccount);
}

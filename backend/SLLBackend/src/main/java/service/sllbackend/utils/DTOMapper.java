package service.sllbackend.utils;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import service.sllbackend.entity.Loyalty;
import service.sllbackend.entity.StaffAccount;
import service.sllbackend.entity.UserAccount;
import service.sllbackend.web.dto.LoyaltyListViewDTO;
import service.sllbackend.web.dto.StaffProfileViewDTO;
import service.sllbackend.web.dto.UserProfileEditDTO;
import service.sllbackend.web.dto.UserProfileViewDTO;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DTOMapper {
    @Mapping(target = "loyalty.level", source = "loyalty.level")
    @Mapping(target = "loyalty.point", source = "loyalty.point")
    UserProfileViewDTO toUserProfileViewDTO(UserAccount userAccount, Loyalty loyalty);

    UserProfileEditDTO toUserProfileEditDTO(UserAccount userAccount);

    @Mapping(target = "active", expression = "java(staffAccount.getAccountStatus() == service.sllbackend.enumerator.AccountStatus.ACTIVE)")
    @Mapping(target = "name", source = "staff.name")
    @Mapping(target = "email", source = "staff.email")
    @Mapping(target = "dateHired", source = "staff.dateHired")
    @Mapping(target = "socialSecurityNum", source = "staff.socialSecurityNum")
    @Mapping(target = "staffStatus", expression = "java(staffAccount.getStaff().getStaffStatus().toString())")
    StaffProfileViewDTO toStaffProfileViewDTO(StaffAccount staffAccount);

    @Mapping(target = "username", source = "loyalty.user.username")
    LoyaltyListViewDTO toLoyaltyListViewDTO(Loyalty loyalty);
    List<LoyaltyListViewDTO> toLoyaltyListViewDTOList(List<Loyalty> loyaltyList);
}

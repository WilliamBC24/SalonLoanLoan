package service.sllbackend.dev;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import service.sllbackend.entity.Staff;
import service.sllbackend.entity.StaffAccount;
import service.sllbackend.entity.StaffCurrentPosition;
import service.sllbackend.entity.StaffPosition;
import service.sllbackend.entity.UserAccount;
import service.sllbackend.entity.Service;
import service.sllbackend.entity.ServiceCategory;
import service.sllbackend.enumerator.Gender;
import service.sllbackend.enumerator.ServiceType;
import service.sllbackend.repository.StaffAccountRepo;
import service.sllbackend.repository.StaffCurrentPositionRepo;
import service.sllbackend.repository.StaffPositionRepo;
import service.sllbackend.repository.StaffRepo;
import service.sllbackend.repository.UserAccountRepo;
import service.sllbackend.repository.ServiceRepo;
import service.sllbackend.repository.ServiceCategoryRepo;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
	private final UserAccountRepo userAccountRepo;
	private final StaffRepo staffRepo;
	private final StaffAccountRepo staffAccountRepo;
	private final StaffPositionRepo staffPositionRepo;
	private final StaffCurrentPositionRepo staffCurrentPositionRepo;
	private final PasswordEncoder passwordEncoder;
	private final ServiceRepo serviceRepo;
	private final ServiceCategoryRepo serviceCategoryRepo;

	@Override
	public void run(String... args) {
		System.out.println("############ \n            Loading initial data\n############");
		registerUser();
		registerStaff();
		registerServices();
	}

	public void registerUser() {
		String username = "alice";
		String rawPassword = "alice";

		String hashedPassword = passwordEncoder.encode(rawPassword);
		userAccountRepo.save(UserAccount.builder()
				.username(username)
				.password(hashedPassword)
				.gender(Gender.MALE)
				.birthDate(LocalDate.of(2004, 9, 6))
				.phoneNumber("0999999999")
				.email("alice@wonderland.com")
				.build());
	}

	public void registerStaff() {
		String name = "admin";
		String role = "Administrator";
		String username = "admin";
		String rawPassword = "admin";

		Staff staff = staffRepo.save(Staff.builder()
				.name(name)
				.birthDate(LocalDate.of(2004, 1, 1))
				.build());

		StaffPosition staffPosition = staffPositionRepo.save(StaffPosition.builder()
				.positionName(role)
				.build());

		staffCurrentPositionRepo.save(StaffCurrentPosition.builder()
				.staff(staff)
				.position(staffPosition)
				.build());

		String hashedPassword = passwordEncoder.encode(rawPassword);

		staffAccountRepo.save(StaffAccount.builder()
				.staff(staff)
				.username(username)
				.password(hashedPassword)
				.activeStatus(Boolean.TRUE)
				.build());
	}

	public void registerServices() {
		// Create service categories
		ServiceCategory hairCategory = serviceCategoryRepo.save(ServiceCategory.builder()
				.name("Hair Care")
				.build());
		
		ServiceCategory nailsCategory = serviceCategoryRepo.save(ServiceCategory.builder()
				.name("Nails")
				.build());
		
		ServiceCategory skinCategory = serviceCategoryRepo.save(ServiceCategory.builder()
				.name("Skin Care")
				.build());
		
		ServiceCategory massageCategory = serviceCategoryRepo.save(ServiceCategory.builder()
				.name("Massage & Spa")
				.build());
		
		ServiceCategory makeupCategory = serviceCategoryRepo.save(ServiceCategory.builder()
				.name("Makeup")
				.build());

		// Hair Care Services (10 services)
		serviceRepo.save(Service.builder()
				.serviceName("Classic Haircut")
				.serviceCategory(hairCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(150000)
				.durationMinutes((short) 30)
				.serviceDescription("Professional haircut with styling")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Hair Coloring")
				.serviceCategory(hairCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(350000)
				.durationMinutes((short) 90)
				.serviceDescription("Full hair coloring service with premium products")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Hair Highlights")
				.serviceCategory(hairCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(400000)
				.durationMinutes((short) 120)
				.serviceDescription("Partial or full highlights for stunning hair")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Hair Perm")
				.serviceCategory(hairCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(500000)
				.durationMinutes((short) 150)
				.serviceDescription("Long-lasting curls or waves")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Hair Straightening")
				.serviceCategory(hairCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(600000)
				.durationMinutes((short) 180)
				.serviceDescription("Keratin treatment for smooth, straight hair")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Deep Conditioning Treatment")
				.serviceCategory(hairCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(200000)
				.durationMinutes((short) 45)
				.serviceDescription("Intensive hair repair and moisture treatment")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Scalp Treatment")
				.serviceCategory(hairCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(250000)
				.durationMinutes((short) 60)
				.serviceDescription("Rejuvenating scalp massage and treatment")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Hair Extensions")
				.serviceCategory(hairCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(800000)
				.durationMinutes((short) 180)
				.serviceDescription("Premium quality hair extensions application")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Blowdry and Style")
				.serviceCategory(hairCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(120000)
				.durationMinutes((short) 40)
				.serviceDescription("Professional blowdry with styling")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Hair Spa Treatment")
				.serviceCategory(hairCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(300000)
				.durationMinutes((short) 90)
				.serviceDescription("Complete hair spa with relaxation")
				.activeStatus(true)
				.build());

		// Nails Services (7 services)
		serviceRepo.save(Service.builder()
				.serviceName("Basic Manicure")
				.serviceCategory(nailsCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(100000)
				.durationMinutes((short) 30)
				.serviceDescription("Classic manicure with polish")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Gel Manicure")
				.serviceCategory(nailsCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(180000)
				.durationMinutes((short) 45)
				.serviceDescription("Long-lasting gel nail polish")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Basic Pedicure")
				.serviceCategory(nailsCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(120000)
				.durationMinutes((short) 45)
				.serviceDescription("Foot care with polish")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Gel Pedicure")
				.serviceCategory(nailsCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(200000)
				.durationMinutes((short) 60)
				.serviceDescription("Spa pedicure with gel polish")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Nail Art Design")
				.serviceCategory(nailsCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(150000)
				.durationMinutes((short) 60)
				.serviceDescription("Custom nail art and designs")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Acrylic Nails")
				.serviceCategory(nailsCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(300000)
				.durationMinutes((short) 90)
				.serviceDescription("Full set of acrylic nail extensions")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Nail Removal Service")
				.serviceCategory(nailsCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(80000)
				.durationMinutes((short) 30)
				.serviceDescription("Safe removal of gel or acrylic nails")
				.activeStatus(true)
				.build());

		// Skin Care Services (6 services)
		serviceRepo.save(Service.builder()
				.serviceName("Basic Facial")
				.serviceCategory(skinCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(200000)
				.durationMinutes((short) 60)
				.serviceDescription("Deep cleansing facial treatment")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Anti-Aging Facial")
				.serviceCategory(skinCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(400000)
				.durationMinutes((short) 90)
				.serviceDescription("Premium anti-aging treatment with collagen")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Acne Treatment")
				.serviceCategory(skinCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(250000)
				.durationMinutes((short) 60)
				.serviceDescription("Specialized treatment for acne-prone skin")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Brightening Facial")
				.serviceCategory(skinCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(350000)
				.durationMinutes((short) 75)
				.serviceDescription("Vitamin C facial for radiant skin")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Hydrating Facial")
				.serviceCategory(skinCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(300000)
				.durationMinutes((short) 60)
				.serviceDescription("Deep moisture treatment for dry skin")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Microdermabrasion")
				.serviceCategory(skinCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(450000)
				.durationMinutes((short) 60)
				.serviceDescription("Exfoliation treatment for smooth skin")
				.activeStatus(true)
				.build());

		// Massage & Spa Services (4 services)
		serviceRepo.save(Service.builder()
				.serviceName("Swedish Massage")
				.serviceCategory(massageCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(300000)
				.durationMinutes((short) 60)
				.serviceDescription("Relaxing full body massage")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Deep Tissue Massage")
				.serviceCategory(massageCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(350000)
				.durationMinutes((short) 60)
				.serviceDescription("Therapeutic massage for muscle tension")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Hot Stone Massage")
				.serviceCategory(massageCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(400000)
				.durationMinutes((short) 75)
				.serviceDescription("Relaxing massage with heated stones")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Aromatherapy Massage")
				.serviceCategory(massageCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(380000)
				.durationMinutes((short) 60)
				.serviceDescription("Massage with essential oils")
				.activeStatus(true)
				.build());

		// Makeup Services (3 services)
		serviceRepo.save(Service.builder()
				.serviceName("Event Makeup")
				.serviceCategory(makeupCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(250000)
				.durationMinutes((short) 60)
				.serviceDescription("Professional makeup for special events")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Bridal Makeup")
				.serviceCategory(makeupCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(500000)
				.durationMinutes((short) 90)
				.serviceDescription("Complete bridal makeup package")
				.activeStatus(true)
				.build());

		serviceRepo.save(Service.builder()
				.serviceName("Makeup Lesson")
				.serviceCategory(makeupCategory)
				.serviceType(ServiceType.SINGLE)
				.servicePrice(200000)
				.durationMinutes((short) 60)
				.serviceDescription("Learn professional makeup techniques")
				.activeStatus(true)
				.build());

		System.out.println("Successfully loaded 30 services across 5 categories");
	}
}
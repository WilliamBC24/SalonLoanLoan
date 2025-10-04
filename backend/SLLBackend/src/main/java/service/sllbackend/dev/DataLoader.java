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
import service.sllbackend.enumerator.Gender;
import service.sllbackend.repository.StaffAccountRepo;
import service.sllbackend.repository.StaffCurrentPositionRepo;
import service.sllbackend.repository.StaffPositionRepo;
import service.sllbackend.repository.StaffRepo;
import service.sllbackend.repository.UserAccountRepo;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {
	private final UserAccountRepo userAccountRepo;
	private final StaffRepo staffRepo;
	private final StaffAccountRepo staffAccountRepo;
	private final StaffPositionRepo staffPositionRepo;
	private final StaffCurrentPositionRepo staffCurrentPositionRepo;
	private final PasswordEncoder passwordEncoder;

	@Override
	public void run(String... args) {
		System.out.println("############ \n            Loading initial data\n############");
		registerUser();
		registerStaff();
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
}
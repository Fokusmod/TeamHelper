package ru.geekbrains.WowVendorTeamHelper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.geekbrains.WowVendorTeamHelper.model.Role;


@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByTitle(String role_user);
}
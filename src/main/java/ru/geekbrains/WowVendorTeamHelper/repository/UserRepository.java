package ru.geekbrains.WowVendorTeamHelper.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.geekbrains.WowVendorTeamHelper.model.User;


import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query(value = "select * from users as u " +
            "right join users_statuses as us on u.id = us.user_id where us.status_id = (select id from statuses where title = :status)", nativeQuery = true)
    List<User> findAllByStatus(String status);

    @Query(value = "select * from users as u " +
            "right join users_roles as ur on u.id = ur.user_id where ur.role_id = (select id from roles where title = :role)", nativeQuery = true)
    List<User> findAllByRole(String role);

    @Query(value = "select * from users as u " +
            "right join users_privileges as up on u.id = up.user_id where up.privilege_id = (select id from privileges where title = :privilege)", nativeQuery = true)
    List<User> findAllByPrivilege(String privilege);
}
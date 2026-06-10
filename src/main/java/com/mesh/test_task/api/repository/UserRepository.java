package com.mesh.test_task.api.repository;

import com.mesh.test_task.api.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from EmailData em join em.user u where em.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("select u from PhoneData ph join ph.user u where ph.phone = :phone")
    Optional<User> findByPhone(@Param("phone") String phone);

    @Query(
            value = "select u.id from User u "
                    + "where (:phone is null or exists ("
                    + "select 1 from PhoneData ph where ph.user = u and ph.phone = :phone)) "
                    + "and (:email is null or exists ("
                    + "select 1 from EmailData em where em.user = u and em.email = :email)) "
                    + "and (:name is null or lower(u.name) like lower(concat(:name, '%'))) "
                    + "order by u.id",
            countQuery = "select count(u.id) from User u "
                    + "where (:phone is null or exists ("
                    + "select 1 from PhoneData ph where ph.user = u and ph.phone = :phone)) "
                    + "and (:email is null or exists ("
                    + "select 1 from EmailData em where em.user = u and em.email = :email)) "
                    + "and (:name is null or lower(u.name) like lower(concat(:name, '%')))"
    )
    Page<Long> searchUserIds(
            @Param("phone") String phone,
            @Param("email") String email,
            @Param("name") String name,
            Pageable pageable
    );

    @Query(
            value = "select u.id from User u "
                    + "where (:phone is null or exists ("
                    + "select 1 from PhoneData ph where ph.user = u and ph.phone = :phone)) "
                    + "and (:email is null or exists ("
                    + "select 1 from EmailData em where em.user = u and em.email = :email)) "
                    + "and (:name is null or lower(u.name) like lower(concat(:name, '%'))) "
                    + "and u.dateOfBirth > :dateOfBirth "
                    + "order by u.id",
            countQuery = "select count(u.id) from User u "
                    + "where (:phone is null or exists ("
                    + "select 1 from PhoneData ph where ph.user = u and ph.phone = :phone)) "
                    + "and (:email is null or exists ("
                    + "select 1 from EmailData em where em.user = u and em.email = :email)) "
                    + "and (:name is null or lower(u.name) like lower(concat(:name, '%'))) "
                    + "and u.dateOfBirth > :dateOfBirth"
    )
    Page<Long> searchUserIdsBornAfter(
            @Param("phone") String phone,
            @Param("email") String email,
            @Param("name") String name,
            @Param("dateOfBirth") LocalDate dateOfBirth,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"emails", "phones"})
    @Query("select distinct u from User u where u.id in :ids order by u.id")
    List<User> findAllWithContactsByIdIn(@Param("ids") List<Long> ids);

}

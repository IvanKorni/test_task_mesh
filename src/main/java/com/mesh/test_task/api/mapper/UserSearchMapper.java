package com.mesh.test_task.api.mapper;

import com.mesh.test_task.api.entity.EmailData;
import com.mesh.test_task.api.entity.PhoneData;
import com.mesh.test_task.api.entity.User;
import com.mesh.test_task.api.generated.model.UserSearchItemResponse;
import com.mesh.test_task.api.generated.model.UserSearchResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING, injectionStrategy = CONSTRUCTOR)
public interface UserSearchMapper {
    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "name", source = "user.name")
    @Mapping(target = "dateOfBirth", source = "user.dateOfBirth")
    @Mapping(target = "emails", expression = "java(toEmails(user.getEmails()))")
    @Mapping(target = "phones", expression = "java(toPhones(user.getPhones()))")
    UserSearchItemResponse toItemResponse(User user);

    default UserSearchResponse toResponse(Page<UserSearchItemResponse> usersPage) {
        UserSearchResponse response = new UserSearchResponse();
        response.setItems(usersPage.getContent());
        response.setPage(usersPage.getNumber());
        response.setSize(usersPage.getSize());
        response.setTotalElements(usersPage.getTotalElements());
        response.setTotalPages(usersPage.getTotalPages());
        return response;
    }

    default List<String> toEmails(Set<EmailData> emails) {
        if (emails == null) {
            return Collections.emptyList();
        }
        return emails.stream()
                .sorted(Comparator.comparing(EmailData::getId))
                .map(EmailData::getEmail)
                .collect(Collectors.toList());
    }

    default List<String> toPhones(Set<PhoneData> phones) {
        if (phones == null) {
            return Collections.emptyList();
        }
        return phones.stream()
                .sorted(Comparator.comparing(PhoneData::getId))
                .map(PhoneData::getPhone)
                .collect(Collectors.toList());
    }
}

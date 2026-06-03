package com.mesh.test_task.api.mapper;

import com.mesh.test_task.api.entity.EmailData;
import com.mesh.test_task.api.entity.PhoneData;
import com.mesh.test_task.api.generated.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.InjectionStrategy.CONSTRUCTOR;
import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Mapper(componentModel = SPRING, injectionStrategy = CONSTRUCTOR)
public interface UserContactMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    EmailData toEmailData(AddEmailRequest request);

    UserEmailResponse toEmailResponse(EmailData entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    PhoneData toPhoneData(AddPhoneRequest request);

    UserPhoneResponse toPhoneResponse(PhoneData entity);
}

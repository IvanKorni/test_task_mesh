package com.mesh.test_task.api.service;

import com.mesh.test_task.api.entity.User;
import com.mesh.test_task.api.generated.model.UserSearchItemResponse;
import com.mesh.test_task.api.generated.model.UserSearchResponse;
import com.mesh.test_task.api.mapper.UserSearchMapper;
import com.mesh.test_task.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserSearchService {
    private static final String USER_SEARCH_CACHE = "userSearch";
    private static final int DEFAULT_PAGE = 0;
    private static final int DEFAULT_SIZE = 20;
    private static final int MAX_SIZE = 100;

    private final UserRepository userRepository;
    private final UserSearchMapper userSearchMapper;

    @Transactional(readOnly = true)
    @Cacheable(
            cacheNames = USER_SEARCH_CACHE,
            key = "T(com.mesh.test_task.api.service.UserSearchService).cacheKey("
                    + "#phone, #email, #name, #dateOfBirth, #page, #size)"
    )
    public UserSearchResponse searchUsers(
            String phone,
            String email,
            String name,
            LocalDate dateOfBirth,
            Integer page,
            Integer size
    ) {
        Pageable pageable = pageRequest(page, size);
        Page<Long> userIdsPage = userRepository.searchUserIds(
                clean(phone),
                clean(email),
                clean(name),
                dateOfBirth,
                pageable);
        List<UserSearchItemResponse> items = loadUsers(userIdsPage.getContent()).stream()
                .map(userSearchMapper::toItemResponse)
                .collect(Collectors.toList());

        Page<UserSearchItemResponse> usersPage = new PageImpl<>(items, pageable, userIdsPage.getTotalElements());
        return userSearchMapper.toResponse(usersPage);
    }

    public static String cacheKey(
            String phone,
            String email,
            String name,
            LocalDate dateOfBirth,
            Integer page,
            Integer size
    ) {
        return String.join(
                "|",
                cleanForKey(phone),
                cleanForKey(email),
                cleanForKey(name).toLowerCase(Locale.ROOT),
                dateOfBirth == null ? "" : dateOfBirth.toString(),
                String.valueOf(pageNumber(page)),
                String.valueOf(pageSize(size))
        );
    }

    private Pageable pageRequest(Integer page, Integer size) {
        return PageRequest.of(pageNumber(page), pageSize(size));
    }

    private List<User> loadUsers(List<Long> userIds) {
        if (userIds.isEmpty()) {
            return Collections.emptyList();
        }
        return userRepository.findAllWithContactsByIdIn(userIds);
    }

    private String clean(String value) {
        return value == null || value.trim().isEmpty() ? null : value.trim();
    }

    private static int pageNumber(Integer page) {
        return page == null || page < 0 ? DEFAULT_PAGE : page;
    }

    private static int pageSize(Integer size) {
        return size == null || size <= 0 ? DEFAULT_SIZE : Math.min(size, MAX_SIZE);
    }

    private static String cleanForKey(String value) {
        return value == null ? "" : value.trim();
    }
}

package com.vladimirpandurov.invoiceManager01B.repository.implementation;

import com.vladimirpandurov.invoiceManager01B.domain.Role;
import com.vladimirpandurov.invoiceManager01B.exception.ApiException;
import com.vladimirpandurov.invoiceManager01B.repository.RoleRepository;
import com.vladimirpandurov.invoiceManager01B.rowmapper.RoleRowMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;

import static com.vladimirpandurov.invoiceManager01B.enumeration.RoleType.ROLE_USER;
import static com.vladimirpandurov.invoiceManager01B.query.RoleQuery.*;
import static java.util.Objects.requireNonNull;

@Repository
@RequiredArgsConstructor
@Slf4j
public class RoleRepositoryImpl implements RoleRepository<Role> {


    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Role create(Role data) {
        return null;
    }

    @Override
    public Collection<Role> list(int page, int pageSize) {
        return null;
    }

    @Override
    public Role get(Long id) {
        return null;
    }

    @Override
    public Role update(Role data) {
        return null;
    }

    @Override
    public Boolean delete(Long id) {
        return null;
    }

    @Override
    public void addRoleToUser(Long userId, String roleName) {
        log.info("Adding role {} to user id: {}", roleName, userId);
        try{
            Role role = jdbc.queryForObject(SELECT_ROLE_BY_NAME_QUERY, Map.of("name", roleName), new RoleRowMapper());
            jdbc.update(INSERT_ROLE_TO_USER_QUERY, Map.of("userId", userId, "roleId", requireNonNull(role).getId()));
        }catch(EmptyResultDataAccessException exception){
            throw new ApiException("No role found by name: " + ROLE_USER.name());
        }catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An error occurred.Please try again.");
        }
    }

    @Override
    public Role getRoleByUserId(Long userId) {
        log.info("Getting role  for user id: {}", userId);
        try{
            Role role = jdbc.queryForObject(SELECT_ROLE_BY_ID_QUERY, Map.of("id", userId), new RoleRowMapper());
            log.info("Permissions for this user : " + role.getPermission());
            return role;

        }catch(EmptyResultDataAccessException exception){
            throw new ApiException("No role found by id: " + userId);
        }catch (Exception exception){
            log.error(exception.getMessage());
            throw new ApiException("An error occurred.Please try again.");
        }

    }

    @Override
    public Role getRoleByUserEmail(String email) {
        return null;
    }

    @Override
    public void updateUserRole(Long userId, String roleName) {

    }
}

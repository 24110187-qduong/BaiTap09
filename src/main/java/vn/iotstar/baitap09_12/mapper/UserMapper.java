package vn.iotstar.baitap09_12.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import vn.iotstar.baitap09_12.dto.UserDTO;
import vn.iotstar.baitap09_12.entity.User;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    // Entity → DTO
    @Mapping(target = "roleId", source = "role.id")
    @Mapping(target = "roleName", source = "role.name")
    @Mapping(target = "productCount", ignore = true) // tính riêng
    UserDTO toDTO(User entity);

    List<UserDTO> toDTOList(List<User> entities);

    // DTO → Entity
    @Mapping(target = "role", ignore = true)       // set riêng từ roleId
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "password", ignore = true)   // mã hóa riêng
    @Mapping(target = "id", ignore = true)         // khi tạo mới
    User toEntity(UserDTO dto);

    // Update entity từ DTO (không tạo mới)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(UserDTO dto, @MappingTarget User entity);
}
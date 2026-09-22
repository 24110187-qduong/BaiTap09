package vn.iotstar.baitap09_12.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import vn.iotstar.baitap09_12.dto.ProductDTO;
import vn.iotstar.baitap09_12.entity.Product;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "userName", source = "user.fullName")
    ProductDTO toDTO(Product entity);

    List<ProductDTO> toDTOList(List<Product> entities);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    Product toEntity(ProductDTO dto);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(ProductDTO dto, @MappingTarget Product entity);
}
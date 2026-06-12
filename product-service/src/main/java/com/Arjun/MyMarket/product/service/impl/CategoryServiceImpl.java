package com.Arjun.MyMarket.product.service.impl;

import com.Arjun.MyMarket.product.dto.CategoryDto;
import com.Arjun.MyMarket.product.dto.ProductDto;
import com.Arjun.MyMarket.product.entity.Category;
import com.Arjun.MyMarket.product.exception.ResourceNotFoundException;
import com.Arjun.MyMarket.product.repository.CategoryRepo;
import com.Arjun.MyMarket.product.service.CategoryService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@Transactional
public class CategoryServiceImpl implements CategoryService {

    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
    private final CategoryRepo categoryRepo;

    public CategoryServiceImpl(CategoryRepo categoryRepo){
        this.categoryRepo = categoryRepo;
    }

    @Override
    //Fetch all categories
    public List<CategoryDto> getAllCategories(){

        //finding all categories using repo which returns list of category entity
        List <Category> categoryEntity = categoryRepo.findAll();

        //converting the list of category entity to the list of category dto
        return categoryEntity.stream().map(this::toCategoryDto).collect(Collectors.toList());
    }

    //fetch category by category id
    @Override
    public CategoryDto getCategoryById(Long id){
        Category category = findCategoryById(id);
        return toCategoryDto(category);
    }

    //fetch all categories in a product using product id
    @Override
    public List<CategoryDto> getCategoriesByProductId(UUID id){

        //finding categories from repo using product id which returns category list of entities
        List<Category> categoryList = categoryRepo.findCategoriesByProductId(id);

        //converting the list of category entity to the list of category dto
        List<CategoryDto> categoryDtoList = categoryList.stream().map(this::toCategoryDto).collect(Collectors.toList());

        return categoryDtoList;
    }

    //create category
    @Override
    public CategoryDto createCategory(CategoryDto categoryDto){

        //converting received categoryDto to category entity
        Category category = toCategoryEntity(categoryDto);

        //saving category entity into db
        Category savedCategory = categoryRepo.save(category);

        //converting saved category to dto and returning
        return toCategoryDto(savedCategory);
    }

    //update category
    @Override
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto){

        Category category = categoryRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        category.setTitle(categoryDto.getTitle());

        return toCategoryDto(categoryRepo.save(category));
    }

    //delete category
    @Override
    public void deleteCategory(Long id){
        Category category = findCategoryById(id);

//        Here the owning entity is Category so if we remove category from produc like we did in following code line
//        it wont matter because hibernate only look for changes related to owning entity
        //category.getProducts().stream().forEach(product -> product.getCategories().remove(category));

        //Now this line changes the owning entity so hibernate automatically updates the join table between categories
        //and products. It will remove category from product as well
        category.getProducts().clear();

        //Now we dont need to save the category is this class is transactional so hibernate will keep the transaction open
        //when we call findByCategory and it tracks every change that owning entities encounter

        //categoryRepo.save(category);
        categoryRepo.delete(category);
    }

    //to convert dto to category entity
    private Category toCategoryEntity(CategoryDto categoryDto){
        Category category = new Category();

        category.setTitle(categoryDto.getTitle());

        category.setProducts(new ArrayList<>());

        return category;
    }

    //to fetch category from categoryRepo
    private Category findCategoryById(Long id){
        return categoryRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    //to convert category entity to dto
    private CategoryDto toCategoryDto(Category category){
        CategoryDto dto = new CategoryDto();

        dto.setId((category.getId()));
        dto.setTitle(category.getTitle());

        dto.setProductDtos(category.getProducts() == null ? new ArrayList<>() : category.getProducts().stream().map(product -> {
            var productDto = new ProductDto();
            productDto.setId(product.getId());
            productDto.setTitle(product.getTitle());
            productDto.setShortDesc(product.getShortDesc());
            productDto.setLongDesc(product.getLongDesc());
            productDto.setPrice(product.getPrice());
            productDto.setLive(product.getLive());
            logger.debug("The code is coming here");
            productDto.setProductImages(product.getProductImages() == null ? new ArrayList<>() : new ArrayList<>(product.getProductImages()));
            productDto.setCategories(new ArrayList<>());
            productDto.setReviews(new ArrayList<>());

            return productDto;
        }).collect(Collectors.toList()));

        return dto;
    }
}

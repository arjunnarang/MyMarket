package com.Arjun.MyMarket.product.service.impl;

import com.Arjun.MyMarket.product.dto.CategoryDto;
import com.Arjun.MyMarket.product.dto.ProductDto;
import com.Arjun.MyMarket.product.dto.ReviewDto;
import com.Arjun.MyMarket.product.entity.Category;
import com.Arjun.MyMarket.product.entity.Product;
import com.Arjun.MyMarket.product.entity.Review;
import com.Arjun.MyMarket.product.exception.ResourceNotFoundException;
import com.Arjun.MyMarket.product.repository.CategoryRepo;
import com.Arjun.MyMarket.product.repository.ProductRepo;
import com.Arjun.MyMarket.product.repository.ReviewRepo;
import com.Arjun.MyMarket.product.service.CategoryService;
import com.Arjun.MyMarket.product.service.ProductService;
import jakarta.persistence.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;
    private final ReviewRepo reviewRepo;
    private final CategoryService categoryService;
    //finding product by id
    public ProductDto getProductById(UUID productId){

        Product product = findProduct(productId);

        return toDto(product);
    }

    @Override
    public void deleteProduct(UUID productId) {
        productRepo.deleteById(productId);
    }

    public List<ProductDto> getAllProducts(){

        //fetching products from db that return list of product entities
        List<Product> productList = productRepo.findAll();

        //creating a list of product dtos and converting those entities into dtos and adding them in dto list
        List<ProductDto> productDtoList = new ArrayList<>();

        for(Product product : productList){
            productDtoList.add(toDto(product));
        }

        return productDtoList;
    }

    //adding category to product
    @Override
    public ProductDto addCategoryToProduct(UUID productId, Long categoryId){

        //fetching product
        Product product = productRepo.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        //fetching category
        Category category = categoryRepo.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        //adding category to product
        if(!product.getCategories().contains(category)){
            product.getCategories().add(category);
        }

        //adding product to category table
        if(!category.getProducts().contains(product)){
            category.getProducts().add(product);
        }

        productRepo.save(product);
        categoryRepo.save(category);

        return toDto(product);
    }

    @Override
    public List<String> getProductImages(UUID productId) {
        return List.of();
    }


    //creating product
    @Override
    public ProductDto createProduct(ProductDto productDto){


        Product product = new Product();

        //converting product dto to product entity
        toProductEntity(product, productDto);

        //if productDto have category list then we need to create a list of category entity and give it to product entity
        resolveCategories(product, productDto);

        //saving the product
        Product savedProduct = productRepo.save(product);

        //if product is not present in the category keywords so we need to add product in category database as well
        syncCategoryLinks(savedProduct, product.getCategories());

        return toDto(product);
    }

    //updating product
    @Override
    public ProductDto updateProduct(UUID id, ProductDto productDto) {
        Product product = findProduct(id);

        applyBasicFields(product, productDto);

        if(productDto.getCategories() != null){
            resolveCategories(product, productDto);

            syncCategoryLinks(product, product.getCategories());
        }
        productRepo.save(product);
        return toDto(product);
    }

    //fetch products in particular category
    public List<ProductDto> getProductsByCategoryId(Long categoryId) {

        //find products using category id
        List<Product> products = productRepo.findByCategoryId(categoryId);

        //creating list of product dto and adding product from previous entity list to dto list
        List<ProductDto> productDtoList = new ArrayList<>();
        for (Product product : products) {
            productDtoList.add(toDto(product));
        }

        return productDtoList;
    }

    //remove category from product
    public ProductDto removeCategoryFromProduct(UUID productId, Long categoryId) {
        Product product = productRepo.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));
        Category category = categoryRepo.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        product.getCategories().remove(category);
        category.getProducts().remove(product);

        categoryRepo.save(category);
        productRepo.save(product);

        return toDto(product);
    }

    //adding review to product
    @Override
    public ReviewDto addReviewToProduct(UUID productId, ReviewDto reviewDto) {
        Product product = findProduct(productId);

        Review review = new Review();
        review.setTitle(reviewDto.getTitle());
        review.setComment(reviewDto.getComment());
        review.setRating(reviewDto.getRating());
        review.setProduct(product);

        Review savedReview = reviewRepo.save(review);

        if(product.getReviews() == null){
            product.setReviews(new ArrayList<>());
        }

        product.getReviews().add(review);
        productRepo.save(product);
        return toDtoForReview(savedReview);

    }

    @Override
    public ProductDto addProductImages(UUID productId, List<MultipartFile> images) {
//       Product product = findProduct(productId);
//
//       List<String> urls = uploadImages(images);
//
//       if(product.getProductImages() == null){
//           product.setProductImages(new ArrayList<>());
//       }
//       product.getProductImages().addAll(urls);
//
//       Product productSaved = productRepo.save(product);
//
//       return toDto(productSaved);

        return null;
    }

//    private List<String> uploadImages(List<MultipartFile> images){
//
//    }
    private void applyBasicFields(Product product, ProductDto productDto){


        product.setTitle(productDto.getTitle());
        product.setShortDesc(productDto.getShortDesc());
        product.setLongDesc(productDto.getLongDesc());
        product.setPrice(productDto.getPrice());
        product.setLive(productDto.getLive());
        if(productDto.getProductImages() != null){
            product.setProductImages(new ArrayList<>(productDto.getProductImages()));
        }
    }

    //categories in product entity need to be saved in db
    private void syncCategoryLinks(Product product, List<Category> categories){
        for(Category category : categories){
            if(!category.getProducts().contains(product)){
                category.getProducts().add(product);
            }
            categoryRepo.save(category);
        }
    }

    //finding product by id from repo
    private Product findProduct(UUID productId){
        return productRepo.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found: " + productId));
    }


    // object mapper for Product entity
    private ProductDto toDto(Product product){
        ProductDto dto = new ProductDto();


        dto.setId(product.getId());
        dto.setTitle(product.getTitle());
        dto.setShortDesc(product.getShortDesc());
        dto.setLongDesc(product.getLongDesc());
        dto.setPrice(product.getPrice());
        dto.setLive(product.getLive());
        dto.setProductImages(product.getProductImages() == null ? new ArrayList<>() : new ArrayList<>(product.getProductImages()));
        dto.setCategories(product.getCategories() == null ? new ArrayList<>(): product.getCategories().stream().map(category -> toDtoForCategory(category)).collect(Collectors.toList()));
        dto.setReviews(dto.getReviews() == null ? new ArrayList<>() : product.getReviews().stream().map(review -> toDtoForReview(review)).collect(Collectors.toList()));
        dto.setCreatedAt(product.getCreatedAt());
        return dto;
    }

    //Object mapper for Category entity
    private CategoryDto toDtoForCategory(Category category){

        CategoryDto categoryDto = new CategoryDto();

        categoryDto.setId(category.getId());
        categoryDto.setTitle(category.getTitle());

        //we didnt set list of product dtos with list of products received in "category"
        //because the products in that list are entity as well then we have to call toDto for each product to
        //convert into entity then it would have been a endless loop so we set new list in categoryDto
        categoryDto.setProductDtos(new ArrayList<>());

        return categoryDto;
    }


    //Object mapper for Review entity
    private ReviewDto toDtoForReview(Review review){
        ReviewDto reviewDto = new ReviewDto();

        reviewDto.setId(review.getId());
        reviewDto.setTitle(review.getTitle());
        reviewDto.setComment(review.getComment());
        reviewDto.setRating(review.getRating());
        reviewDto.setProductDto(null);

        return reviewDto;

    }

    //object mapper for Product entity to Product dto
    private void toProductEntity(Product product, ProductDto productDto){
        product.setId(productDto.getId());
        product.setTitle(productDto.getTitle());
        product.setShortDesc(productDto.getShortDesc());
        product.setLongDesc(productDto.getLongDesc());
        product.setPrice(productDto.getPrice());
        if(productDto.getLive() != null){
            product.setLive(productDto.getLive());
        }

        if(productDto.getProductImages() != null){
            product.setProductImages(productDto.getProductImages());
        }
        else {
            product.setProductImages(new ArrayList<>());
        }
        product.setCreatedAt(productDto.getCreatedAt());
    }

    private void resolveCategories(Product product, ProductDto productDto) {

        List<Category> categoryList = new ArrayList<>();
        if(productDto.getCategories() != null){
            for(CategoryDto categoryDto : productDto.getCategories()){

                //to check if category is present inside category table
                Category category = categoryRepo.findByTitle(categoryDto.getTitle());
                if(category == null){
                    category = new Category();
                    category.setTitle(categoryDto.getTitle());
                    category.setProducts(new ArrayList<>());

                    categoryRepo.save(category);
                    categoryList.add(category);
                }else {
                   // categoryList.add(categoryRepo.findById(categoryDto.getId()).orElseThrow(() -> new ResourceNotFoundException("Category not found" + categoryDto.getId())));
                    categoryList.add(category);
                }
            }
        }

        product.setCategories(categoryList);
    }


}

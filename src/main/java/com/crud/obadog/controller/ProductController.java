package com.crud.obadog.controller;

import com.crud.obadog.models.Product;
import com.crud.obadog.models.ProductDto;
import com.crud.obadog.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;


    @GetMapping({"","/"})
    public String listProducts(Model model) {
        List<Product> products = productRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        model.addAttribute("products", products);
        return "products/index";
    }
    @GetMapping("/add")
    public String addProductForm(Model model) {
        ProductDto productDto = new ProductDto();
        model.addAttribute("productDto", productDto);
        return "products/add";
    }
    @PostMapping("/add")
    public String addProduct(@Valid @ModelAttribute ProductDto productDto,
                             BindingResult result) {
        MultipartFile image = productDto.getImageFile();
        Date createdAt = new Date();
        String storageFile = "No image";

        if (image != null && !image.isEmpty()) {
            storageFile = createdAt.getTime() + "_" + image.getOriginalFilename();
            try {
                String uploadDir = "public/images/";
                Path uploadPath = Paths.get(uploadDir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                try (InputStream inputStream = image.getInputStream()) {
                    Files.copy(inputStream, Paths.get(uploadDir + storageFile),
                            StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (Exception ex) {
                System.out.println("Exception: " + ex.getMessage());
            }
        }
        Product product = new Product();
        product.setName(productDto.getName());
        product.setCategory(productDto.getCategory());
        product.setPrice(productDto.getPrice());
        product.setDescription(productDto.getDescription());
        product.setCreatedAt(createdAt);
        product.setImageFile(storageFile);

        productRepository.save(product);

        return "redirect:/products";
    }
    @GetMapping("/edit")
    public String editProduct(Model model, @RequestParam int id) {
        try {
            Product product = productRepository.findById(id).get();
            model.addAttribute("product", product);

            ProductDto productDto = new ProductDto();
            productDto.setName(product.getName());
            productDto.setCategory(product.getCategory());
            productDto.setPrice(product.getPrice());
            productDto.setDescription(product.getDescription());

            model.addAttribute("productDto", productDto);

        } catch (Exception ex ){
            System.out.println("Exception: " + ex.getMessage());
            return "redirect:/products";
        }
        return "products/edit";
    }
    @PostMapping("/edit")
    public String updateProduct(Model model, @RequestParam int id,
                                @Valid @ModelAttribute ProductDto productDto,
                                BindingResult result) {

        Date createdAt = new Date();
        try {
            Product product = productRepository.findById(id).get();
            model.addAttribute("product", product);

            if (result.hasErrors()){
                return "products/edit";
            }
//            todo - image upload
//            if (!productDto.getImageFile().isEmpty()){
//                String uploadDir = "public/images/";
//                Path oldImagePath = Paths.get(uploadDir + product.getImageFile());
//
//                try {
//                    Files.delete(oldImagePath);
//                } catch(Exception ex){
//                    System.out.println("Exception: " + ex.getMessage());
//                }
//                MultipartFile image = productDto.getImageFile();
//                Date createdAt = new Date();
//                String storageFile = "";
//            }

            product.setName(productDto.getName());
            product.setCategory(productDto.getCategory());
            product.setPrice(productDto.getPrice());
            product.setDescription(productDto.getDescription());
            product.setCreatedAt(createdAt);
            product.setImageFile("No image");

            productRepository.save(product);

        } catch (Exception ex) {
            System.out.println("Exception: " + ex.getMessage());
        }
        return "redirect:/products";
    }
    @GetMapping("/delete")
    public String deleteProduct(@RequestParam int id) {

        try {
            Product product = productRepository.findById(id).get();
            Path imagePath = Paths.get("public/images" + product.getImageFile());

            try {
                Files.delete(imagePath);
            } catch (Exception ex) {
                System.out.println("Exception: " + ex.getMessage());
            }
            productRepository.delete(product);

        }catch(Exception ex){
            System.out.println("Exception: " + ex.getMessage());
        }
        return "redirect:/products";
    }
}

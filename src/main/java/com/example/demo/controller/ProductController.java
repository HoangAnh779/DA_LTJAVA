package com.example.demo.controller;

import com.example.demo.model.Category;
import com.example.demo.model.CategoryImages;
import com.example.demo.model.Product;
import com.example.demo.model.ProductImages;
import com.example.demo.service.CategoryImageService;
import com.example.demo.service.CategoryService;
import com.example.demo.service.ProductImageService;
import com.example.demo.service.ProductService;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.io.File;
import java.util.UUID;

@Controller
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private ProductImageService categoryImageService;
    @Autowired
    private CategoryService categoryService; // Đảm bảo bạn đã injectCategoryService
    // Display a list of all products
//    @GetMapping
//    public String showProductList(Model model) {
//        List<Product>products = productService.getAllProducts();
//        model.addAttribute("products", products);
//        return "/products/product-list";
//    }

    @GetMapping
    public String getAllProducts(@NotNull Model model) {
        Long drinkCategoryId = 6L; // ID của danh mục "Thức Uống"
        Long teaboxCategoryId = 1L; // ID của danh mục "Trà Hộp"
        Long teacansCategoryId = 4L; // ID của danh mục "Trà Lon"
        Long teapackageCategoryId = 2L; // ID của danh mục "Trà Gói"
        model.addAttribute("drink", productService.getProductsByCategoryId(drinkCategoryId));
        model.addAttribute("teabox", productService.getProductsByCategoryId(teaboxCategoryId));
        model.addAttribute("teacans", productService.getProductsByCategoryId(teacansCategoryId));
        model.addAttribute("teapackage", productService.getProductsByCategoryId(teapackageCategoryId));
        return "/products/product-list";
    }

    @GetMapping("/home")
    public String showProductHome(Model model) {
        List<Product>products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "/products/Home";
    }

    @GetMapping("/contact")
    public String showProductContact(Model model) {
        List<Product>products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "/products/contact";
    }

    @GetMapping("/blog")
    public String showProductBlog(Model model) {
        List<Product>products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "/products/blog";
    }
    // For adding a new product
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories()); //Load categories
        return "/products/add-product";
    }

    @GetMapping("/{id}")
    public String showProduct(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Id:" + id));
        model.addAttribute("product", product);
        return "/products/show-product";
    }

    // Process the form for adding a new product
//    @PostMapping("/add")
//    public String addProduct(@Valid Product product, BindingResult result, @RequestParam("image") MultipartFile imageFile) {
//        if (result.hasErrors()) {
//            return "/products/add-product";
//        }
//        if (!imageFile.isEmpty()) {
//            try {
//                String imageName = saveImage(imageFile);
//                product.setThumnail(imageName);
//            } catch (IOException e) {
//                e.printStackTrace();
//                // Handle the error appropriately
//            }
//        }
//        productService.addProduct(product);
//        return "redirect:/products";
//    }
//
//    private String saveImage(MultipartFile imageFile) throws IOException {
//
//        // Đường dẫn tuyệt đối tới thư mục lưu trữ hình ảnh
//        String uploadDir = System.getProperty("user.dir") + "/product-images/";
//        // Đường dẫn tương đối tới thư mục lưu trữ hình ảnh
////        String uploadDir = "category-images/";
//
//        // Tạo thư mục nếu chưa tồn tại
//        Path uploadPath = Paths.get(uploadDir);
//        if (!Files.exists(uploadPath)) {
//            Files.createDirectories(uploadPath);
//        }
//
//        String originalFilename = imageFile.getOriginalFilename();
//        String imagePath = uploadDir + originalFilename;
//        File dest = new File(imagePath);
//        imageFile.transferTo(dest);
//        return originalFilename;
//    }
    @PostMapping("/add")
    public String addProduct(@Valid Product product, BindingResult result,
                              @RequestParam("image") MultipartFile imageFile,
                              @RequestParam("productimages") MultipartFile[] imageList) {
        if (result.hasErrors()) {
            return "/products/add-product";
        }
        //luu hinh dai dien
        if (!imageFile.isEmpty()) {
            try {
                String imageName = saveImageStatic(imageFile);
                product.setThumnail("/images/" +imageName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //luu nhieu hinh anh cua category
        //luu category
        productService.addProduct(product);

        for (MultipartFile image : imageList) {
            if (!image.isEmpty()) {
                try {
                    String imageUrl = saveImageStatic(image);
                    ProductImages productImage = new ProductImages();
                    productImage.setImagePath("/images/" +imageUrl);
                    productImage.setProduct(product);
                    product.getImages().add(productImage);
                    productService.addProductImage(productImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "redirect:/products";
    }

    private String saveImageStatic(MultipartFile image) throws IOException {

        Path dirImages = Paths.get("target/classes/static/images");
        if (!Files.exists(dirImages)) {
            Files.createDirectories(dirImages);
        }

        String newFileName = UUID.randomUUID()+ "." + StringUtils.getFilenameExtension(image.getOriginalFilename());
        Path pathFileUpload = dirImages.resolve(newFileName);
        Files.copy(image.getInputStream(), pathFileUpload,
                StandardCopyOption.REPLACE_EXISTING);
        return newFileName;
    }

    // For editing a product
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id).orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories()); //Load categories
        return "/products/update-product";
    }
    // Process the form for updating a product
    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id, @Valid Product product, BindingResult result, Model model) {
        if (result.hasErrors()) {
            product.setId(id); // set id to keep it in the form in case of errors
            return "/products/update-product";
        }
        productService.updateProduct(product);
        return "redirect:/products";
    }
    // Handle request to delete a product
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return "redirect:/products";
    }
    @GetMapping("/drinks")
    public String showDrinks(@NotNull Model model) {
        Long drinkCategoryId = 6L;
        model.addAttribute("products", productService.getProductsByCategoryId(drinkCategoryId));
        return "products/drinks";
    }
    @GetMapping("/teabox")
    public String showTeabox(@NotNull Model model) {
        Long teaboxCategoryId = 1L;
        model.addAttribute("products", productService.getProductsByCategoryId(teaboxCategoryId));
        return "products/tea-box";
    }
    @GetMapping("/teacans")
    public String showTeacans(@NotNull Model model) {
        Long teacansCategoryId = 4L;
        model.addAttribute("products", productService.getProductsByCategoryId(teacansCategoryId));
        return "products/tea-cans";
    }
    @GetMapping("/teapackage")
    public String showTeapackage(@NotNull Model model) {
        Long teapackageCategoryId = 2L;
        model.addAttribute("products", productService.getProductsByCategoryId(teapackageCategoryId));
        return "products/tea-package";
    }
    @GetMapping("/management")
    public String showProductManagement(Model model) {
        List<Product>products = productService.getAllProducts();
        model.addAttribute("products", products);
        return "/products/product-management";
    }
    @GetMapping("/search")
    public String searchProducts(@RequestParam("query") String query, Model model) {
        List<Product> products;
        if (query == null || query.isEmpty()) {
            products = productService.getAllProducts();
        } else {
            products = productService.searchProductsByName(query);
        }
        model.addAttribute("products", products);
        return "products/drinks";
    }
}
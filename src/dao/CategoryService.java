package dao;

import entity.Category;
import java.util.List;

public interface CategoryService {

    List<Category> getAllCategories();

    boolean addCategory(String name);
}

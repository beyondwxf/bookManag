package com.codex.library.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.codex.library.dto.category.CategorySaveRequest;
import com.codex.library.dto.category.CategoryView;
import com.codex.library.entity.Book;
import com.codex.library.entity.BookCategory;
import com.codex.library.exception.BusinessException;
import com.codex.library.mapper.BookCategoryMapper;
import com.codex.library.mapper.BookMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class CategoryService {

    private final BookCategoryMapper categoryMapper;
    private final BookMapper bookMapper;

    public CategoryService(BookCategoryMapper categoryMapper, BookMapper bookMapper) {
        this.categoryMapper = categoryMapper;
        this.bookMapper = bookMapper;
    }

    public List<CategoryView> listAll() {
        return categoryMapper.selectList(Wrappers.<BookCategory>lambdaQuery().orderByAsc(BookCategory::getSortOrder, BookCategory::getId))
                .stream()
                .map(category -> new CategoryView(category.getId(), category.getName(), category.getCode(), category.getSortOrder(), category.getDescription()))
                .toList();
    }

    @Transactional
    public void create(CategorySaveRequest request) {
        long duplicate = categoryMapper.selectCount(Wrappers.<BookCategory>lambdaQuery()
                .eq(BookCategory::getCode, request.code()));
        if (duplicate > 0) {
            throw new BusinessException("分类编码已存在");
        }
        BookCategory category = new BookCategory();
        category.setName(request.name());
        category.setCode(request.code());
        category.setSortOrder(request.sortOrder() == null ? 0 : request.sortOrder());
        category.setDescription(request.description());
        category.setDeleted(0);
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        categoryMapper.insert(category);
    }

    @Transactional
    public void update(Long id, CategorySaveRequest request) {
        BookCategory category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        long duplicate = categoryMapper.selectCount(Wrappers.<BookCategory>lambdaQuery()
                .eq(BookCategory::getCode, request.code())
                .ne(BookCategory::getId, id));
        if (duplicate > 0) {
            throw new BusinessException("分类编码已存在");
        }
        category.setName(request.name());
        category.setCode(request.code());
        category.setSortOrder(request.sortOrder() == null ? 0 : request.sortOrder());
        category.setDescription(request.description());
        category.setUpdatedAt(LocalDateTime.now());
        categoryMapper.updateById(category);
    }

    @Transactional
    public void delete(Long id) {
        if (categoryMapper.selectById(id) == null) {
            throw new BusinessException("分类不存在");
        }
        long count = bookMapper.selectCount(Wrappers.<Book>lambdaQuery().eq(Book::getCategoryId, id));
        if (count > 0) {
            throw new BusinessException("分类下仍有图书，不能删除");
        }
        categoryMapper.deleteById(id);
    }
}

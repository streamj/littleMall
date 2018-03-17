package cc.freecloudfx.littlemall.service;

import cc.freecloudfx.littlemall.common.ServerResponse;
import cc.freecloudfx.littlemall.pojo.Category;

import java.util.List;

/**
 * @author StReaM on 2/28/2018
 */
public interface ICategoryService {

    ServerResponse addCategory(String categoryName, Integer parentId);

    ServerResponse updateCategoryName(Integer categoryId, String categoryName);

    ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId);

    ServerResponse<List<Integer>> selectCategoryAndChildrenById(Integer categoryId);
}

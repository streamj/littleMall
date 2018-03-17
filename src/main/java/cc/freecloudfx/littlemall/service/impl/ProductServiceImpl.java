package cc.freecloudfx.littlemall.service.impl;

import cc.freecloudfx.littlemall.common.Const;
import cc.freecloudfx.littlemall.common.ResponseCode;
import cc.freecloudfx.littlemall.common.ServerResponse;
import cc.freecloudfx.littlemall.dao.CategoryMapper;
import cc.freecloudfx.littlemall.dao.ProductMapper;
import cc.freecloudfx.littlemall.pojo.Category;
import cc.freecloudfx.littlemall.pojo.Product;
import cc.freecloudfx.littlemall.service.ICategoryService;
import cc.freecloudfx.littlemall.service.IProductService;
import cc.freecloudfx.littlemall.util.DateTimeUtil;
import cc.freecloudfx.littlemall.util.PropertiesUtil;
import cc.freecloudfx.littlemall.vo.ProductDetailVo;
import cc.freecloudfx.littlemall.vo.ProductListVo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static cc.freecloudfx.littlemall.common.Const.IMAGE_HOST_PREFIX;
import static cc.freecloudfx.littlemall.common.Const.IMAGE_HOST_PREFIX_DEF;

/**
 * @author StReaM on 3/10/2018
 */
@Service("iProductService")
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService;

    @Override
    public ServerResponse saveOrUpdateProduct(Product product) {

        if (product != null) {
            if (StringUtils.isNotBlank(product.getSubImages())) {
                String[] subImageArray = product.getSubImages().split(",");
                if (subImageArray.length > 0) {
                    product.setMainImage(subImageArray[0]);
                }
            }
            // 约定如果更新操作，必须有 id
            // todo 这个更新操作很原始，需要增量更新
            if (product.getId() != null) {
                int rowCount = productMapper.updateByPrimaryKey(product);
                if (rowCount > 0) {
                    return ServerResponse.createBySuccess("更新产品信息成功");
                } else {
                    return ServerResponse.createByErrorMessage("更新产品失败");
                }
            } else {
                int rowCount = productMapper.insert(product);
                if (rowCount > 0) {
                    return ServerResponse.createBySuccess("新增产品信息成功");
                } else {
                    return ServerResponse.createByErrorMessage("更新产品失败");
                }
            }
        }
        return ServerResponse.createByErrorMessage("新增或者更新操作的参数错误");
    }

    @Override
    public ServerResponse<String> setSaleStatus(Integer productId, Integer status) {
        if (productId == null || status == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if (rowCount > 0) {
            return ServerResponse.createBySuccess("修改销售状态成功");
        }
        return ServerResponse.createByErrorMessage("修改销售状态失败");
    }

    @Override
    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId) {
        if (productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMessage("未找到相关产品");
        }
        // VO value object
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    @Override
    public ServerResponse<PageInfo> getProductList(int pageNum, int pageSize) {
        // startPage
        PageHelper.startPage(pageNum, pageSize);
        // fill up sql
        List<Product> productList = productMapper.selectList();
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product : productList) {
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        // pageHelper
        PageInfo pageResult = new PageInfo(productList);
        // 重置 dataSet 去掉不需要显示的信息
        pageResult.setList(productListVoList);

        return ServerResponse.createBySuccess(pageResult);
    }

    @Override
    public ServerResponse<PageInfo> searchProduct(String productName, Integer productId,
                                                  int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        if (StringUtils.isNotBlank(productName)) {
            productName = new StringBuilder().append("%").append(productName).append("%").toString();

        }
        List<Product> productList = productMapper.selectByNameAndProductId(productName, productId);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product : productList) {
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        // pageHelper
        PageInfo pageResult = new PageInfo(productList);
        // 重置 dataSet 去掉不需要显示的信息
        pageResult.setList(productListVoList);

        return ServerResponse.createBySuccess(pageResult);
    }

    @Override
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId) {
        if (productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            return ServerResponse.createByErrorMessage("未找到相关产品");
        }
        // 前台产品，会关注是否在线或者下架
        if (product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()) {
            return ServerResponse.createByErrorMessage("未找到相关产品");
        }
        // VO value object
        ProductDetailVo productDetailVo = assembleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    @Override
    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword,
                                                                Integer categoryId,
                                                                int pageNum,
                                                                int pageSize,
                                                                String orderBy) {
        if (StringUtils.isBlank(keyword) && categoryId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> categoryIdList = Lists.newArrayList();
        if (categoryId != null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category == null && StringUtils.isBlank(keyword)) {
                // 返回空集，不报错
                PageHelper.startPage(pageNum, pageSize);
                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            // 递归查询出所有的父子集合
            categoryIdList = iCategoryService
                    .selectCategoryAndChildrenById(category.getId()).getData();
        }
        // 拼接关键字
        if (StringUtils.isNotBlank(keyword)) {
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }
        // 开始分页
        PageHelper.startPage(pageNum, pageSize);
        // 排序
        if (StringUtils.isNotBlank(orderBy)) {
            if (Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)) {
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0] + " " + orderByArray[1]);
            }
        }
        List<Product> productList = productMapper.selectByNameAndCategoryIds(
                StringUtils.isBlank(keyword)? null:keyword, categoryIdList.size()> 0 ? categoryIdList:null);
        List<ProductListVo> productListVoList = Lists.newArrayList();
        for (Product product : productList) {
            ProductListVo productListVo = assembleProductListVo(product);
            productListVoList.add(productListVo);
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ServerResponse.createBySuccess(pageInfo);
    }

    private ProductListVo assembleProductListVo(Product product) {
        ProductListVo vo = new ProductListVo();
        vo.setId(product.getId());
        vo.setSubtitle(product.getSubtitle());
        vo.setPrice(product.getPrice());
        vo.setMainImage(product.getMainImage());
        vo.setCategoryId(product.getCategoryId());
        vo.setName(product.getName());

        // imageHost 从配置文件获取
        vo.setImageHost(PropertiesUtil.getProperty(IMAGE_HOST_PREFIX,
                IMAGE_HOST_PREFIX_DEF));

        return vo;
    }

    private ProductDetailVo assembleProductDetailVo(Product product) {
        ProductDetailVo vo = new ProductDetailVo();
        vo.setId(product.getId());
        vo.setSubtitle(product.getSubtitle());
        vo.setPrice(product.getPrice());
        vo.setMainImage(product.getMainImage());
        vo.setSubImage(product.getSubImages());
        vo.setCategoryId(product.getCategoryId());
        vo.setDetail(product.getDetail());
        vo.setName(product.getName());
        vo.setStock(product.getStock());

        // imageHost 从配置文件获取
        vo.setImageHost(PropertiesUtil.getProperty(IMAGE_HOST_PREFIX,
                IMAGE_HOST_PREFIX_DEF));
        // parentCategory
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null) {
            vo.setParentCategoryId(0); // 设为根节点
        } else {
            vo.setParentCategoryId(category.getParentId());
        }
        // createTime
        vo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        // updateTime
        vo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return vo;
    }
}

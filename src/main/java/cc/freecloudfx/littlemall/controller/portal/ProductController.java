package cc.freecloudfx.littlemall.controller.portal;

import cc.freecloudfx.littlemall.common.ServerResponse;
import cc.freecloudfx.littlemall.service.IProductService;
import cc.freecloudfx.littlemall.vo.ProductDetailVo;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @author StReaM on 3/11/2018
 */

@Controller
@RequestMapping("/product/")
public class ProductController {

    @Autowired
    private IProductService iProductService;

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> detail(Integer productId) {

        return iProductService.getProductDetail(productId);
    }

    @RequestMapping(value = "/{productId}", method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<ProductDetailVo> detailRESTful(@PathVariable Integer productId) {

        return iProductService.getProductDetail(productId);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo>
    list(@RequestParam(value = "keyword", required = false) String keyword,
         @RequestParam(value = "categoryId", required = false) Integer categoryId,
         @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
         @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
         @RequestParam(value = "orderBy", defaultValue = "") String orderBy) {
        return iProductService.getProductByKeywordCategory(keyword, categoryId, pageNum, pageSize, orderBy);
    }

    @RequestMapping(value = "/{keyword}/{categoryId}/{pageNum}/{pageSize}/{orderBy}",
            method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo>
    listRESTful(@PathVariable(value = "keyword") String keyword,
                @PathVariable(value = "categoryId") Integer categoryId,
                @PathVariable(value = "pageNum") Integer pageNum,
                @PathVariable(value = "pageSize") Integer pageSize,
                @PathVariable(value = "orderBy") String orderBy) {
        if (pageNum == null) {
            pageNum = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        if (StringUtils.isBlank(orderBy)) {
            orderBy = "price_asc";
        }
        return iProductService.getProductByKeywordCategory(keyword, categoryId, pageNum, pageSize, orderBy);
    }

    @RequestMapping(value = "/{keyword}/categoryId/{pageNum}/{pageSize}/{orderBy}",
            method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo>
    listRESTful(@PathVariable(value = "keyword") String keyword,
                @PathVariable(value = "pageNum") Integer pageNum,
                @PathVariable(value = "pageSize") Integer pageSize,
                @PathVariable(value = "orderBy") String orderBy) {
        if (pageNum == null) {
            pageNum = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        if (StringUtils.isBlank(orderBy)) {
            orderBy = "price_asc";
        }
        return iProductService.getProductByKeywordCategory(keyword, null, pageNum, pageSize, orderBy);
    }

    @RequestMapping(value = "/keyword/{categoryId}/{pageNum}/{pageSize}/{orderBy}",
            method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<PageInfo>
    listRESTful(@PathVariable(value = "categoryId") Integer categoryId,
                @PathVariable(value = "pageNum") Integer pageNum,
                @PathVariable(value = "pageSize") Integer pageSize,
                @PathVariable(value = "orderBy") String orderBy) {
        if (pageNum == null) {
            pageNum = 1;
        }
        if (pageSize == null) {
            pageSize = 10;
        }
        if (StringUtils.isBlank(orderBy)) {
            orderBy = "price_asc";
        }
        return iProductService.getProductByKeywordCategory(null, categoryId, pageNum, pageSize, orderBy);
    }
}

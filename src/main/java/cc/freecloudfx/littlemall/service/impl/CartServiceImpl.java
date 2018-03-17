package cc.freecloudfx.littlemall.service.impl;

import cc.freecloudfx.littlemall.common.Const;
import cc.freecloudfx.littlemall.common.ResponseCode;
import cc.freecloudfx.littlemall.common.ServerResponse;
import cc.freecloudfx.littlemall.dao.CartMapper;
import cc.freecloudfx.littlemall.dao.ProductMapper;
import cc.freecloudfx.littlemall.pojo.Cart;
import cc.freecloudfx.littlemall.pojo.Product;
import cc.freecloudfx.littlemall.service.ICartService;
import cc.freecloudfx.littlemall.util.BigDecimalUtil;
import cc.freecloudfx.littlemall.util.PropertiesUtil;
import cc.freecloudfx.littlemall.vo.CartProductVo;
import cc.freecloudfx.littlemall.vo.CartVo;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import static cc.freecloudfx.littlemall.common.Const.IMAGE_HOST_PREFIX;

/**
 * @author StReaM on 3/12/2018
 */
@Service("iCartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count) {
        if (productId == null || count == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart == null) {
            // 产品不在购物车里, 新建一个购物车单元
            Cart cartItem = new Cart();
            cartItem.setQuantity(count); // 数目
            cartItem.setChecked(Const.Cart.CHECKED); // 选中
            cartItem.setProductId(productId);        // 产品id
            cartItem.setUserId(userId);              // 用户 id
            cartMapper.insert(cartItem);
        } else {
            count = cart.getQuantity() + count;
            cart.setQuantity(count);                 // 更新产品数量
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return list(userId);
    }

    @Override
    public ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count) {
        if (productId == null || count == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectCartByUserIdAndProductId(userId, productId);
        if (cart != null) {
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return list(userId);
    }

    /*
    难道说，是为了减少网络传输？特地用字符串  */
    @Override
    public ServerResponse<CartVo> deleteProduct(Integer userId, String productId) {
        if (productId == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<String> productIdList = Splitter.on(",").splitToList(productId);
        if (CollectionUtils.isEmpty(productIdList)) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(),
                    ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteByUserIdAndProductIds(userId, productIdList);
        return list(userId);
    }

    @Override
    public ServerResponse<CartVo> list(Integer userId) {
        CartVo cartVo = getCartVoLimit(userId);
        return ServerResponse.createBySuccess(cartVo);
    }

    @Override
    public ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer checked) {
        cartMapper.checkedOrUncheckedProduct(userId, productId, checked);
        return list(userId);
    }

    @Override
    public ServerResponse<Integer> getCartProduct(Integer userId) {
        if (userId == null) {
            return ServerResponse.createBySuccess(0);
        }
        return ServerResponse.createBySuccess(cartMapper.selectCartProductCount(userId));
    }

    private CartVo getCartVoLimit(Integer userId) {
        CartVo cartVo = new CartVo();
        // 他这个购物车，根本不是购物车，只是购物车的一个子项，或者叫购物车单元来的比较恰当
        // 根据用户 id 选出这个用户拥有的所有购物车单元
        List<Cart> cartList = cartMapper.selectCartByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();
        BigDecimal cartTotalPrice = new BigDecimal("0");

        if (CollectionUtils.isNotEmpty(cartList)) {
            // 遍历每个购物车子项
            for (Cart cart : cartList) {
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cart.getId());
                cartProductVo.setUserId(userId);
                cartProductVo.setProductId(cart.getProductId());

                Product product = productMapper.selectByPrimaryKey(cart.getProductId());
                if (product != null) {
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());

                    int buyLimitCount = 0;
                    if (product.getStock() >= cart.getQuantity()) {
                        buyLimitCount = cart.getQuantity();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_SUC);
                    } else {
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(Const.Cart.LIMIT_NUM_FAIL);
                        // update quantity
                        Cart updateQuantityCart = new Cart();
                        updateQuantityCart.setId(cart.getId());
                        updateQuantityCart.setQuantity(buyLimitCount);
                        cartMapper.updateByPrimaryKeySelective(updateQuantityCart);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(),
                            cartProductVo.getQuantity()));
                    cartProductVo.setProductChecked(cart.getChecked());
                }

                if (cart.getChecked() == Const.Cart.CHECKED) {
                    // 累加每个购物车单元的价格
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(),
                            cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(getAllCheckedStatus(userId));
        cartVo.setImageHost(PropertiesUtil.getProperty(IMAGE_HOST_PREFIX));

        return cartVo;
    }

    private boolean getAllCheckedStatus(Integer userId) {
        if (userId == null) {
            return false;
        }
        // 只要能根据 userId 查找到未勾选的，就说明没有全选
        return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;
    }
}

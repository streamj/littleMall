package cc.freecloudfx.littlemall.vo;

import java.math.BigDecimal;
import java.util.List;

/**
 * 从代码中可以推测出，这个 CartVo 是整个购物车
 * @author StReaM on 3/12/2018
 */
public class CartVo {
    // 整个购物车的所有商品
    private List<CartProductVo> cartProductVoList;
    // 购物车总价
    private BigDecimal cartTotalPrice;
    // 是否全选
    private Boolean allChecked;
    private String imageHost;

    public List<CartProductVo> getCartProductVoList() {
        return cartProductVoList;
    }

    public void setCartProductVoList(List<CartProductVo> cartProductVoList) {
        this.cartProductVoList = cartProductVoList;
    }

    public BigDecimal getCartTotalPrice() {
        return cartTotalPrice;
    }

    public void setCartTotalPrice(BigDecimal cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }

    public Boolean getAllChecked() {
        return allChecked;
    }

    public void setAllChecked(Boolean allChecked) {
        this.allChecked = allChecked;
    }

    public String getImageHost() {
        return imageHost;
    }

    public void setImageHost(String imageHost) {
        this.imageHost = imageHost;
    }
}

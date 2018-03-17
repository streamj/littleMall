package cc.freecloudfx.littlemall.service;

import cc.freecloudfx.littlemall.common.ServerResponse;
import cc.freecloudfx.littlemall.vo.CartVo;

/**
 * @author StReaM on 3/12/2018
 */
public interface ICartService {

    ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count);

    ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count);

    ServerResponse<CartVo> deleteProduct(Integer userId, String productId);

    ServerResponse<CartVo> list(Integer userId);

    ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer checked);

    ServerResponse<Integer> getCartProduct(Integer userId);
}

package cc.freecloudfx.littlemall.service;

import cc.freecloudfx.littlemall.common.ServerResponse;
import cc.freecloudfx.littlemall.pojo.Shipping;
import com.github.pagehelper.PageInfo;

/**
 * @author StReaM on 3/15/2018
 */
public interface IShippingService {

    ServerResponse add(Integer userId, Shipping shipping);

    ServerResponse<String> del(Integer userId, Integer shippingId);

    ServerResponse update(Integer userId, Shipping shipping);

    ServerResponse<Shipping> select(Integer userId, Integer shippingId);

    ServerResponse<PageInfo> list(Integer userId, int pageNum, int pageSize);
}

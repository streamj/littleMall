package cc.freecloudfx.littlemall.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * @author StReaM on 2/24/2018
 */
public class Const {

    public final static String CURRENT_USER = "currentUser";

    public final static String EMAIL = "email";

    public final static String USERNAME = "username";

    public final static String IMAGE_HOST_PREFIX = "ftp.server.http.prefix";

    public final static String IMAGE_HOST_PREFIX_DEF = "http://image.imoocfake.com/";

    /**
     * 这么写，既是常量，又比枚举轻
     */
    public interface Role {
        int ROLE_CUSTOMER = 0;
        int ROLE_ADMIN = 1;
    }

    public interface ProductListOrderBy {
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price_asc", "price_desc");
    }

    public interface Cart {
        int CHECKED = 1;
        int UNCHECKED = 0;

        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        String LIMIT_NUM_SUC = "LIMIT_NUM_SUC";
    }

    public enum ProductStatusEnum {
        ON_SALE(1, "在线");

        private String value;
        private int code;

        ProductStatusEnum(int code, String value) {
            this.code = code;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public int getCode() {
            return code;
        }
    }
}

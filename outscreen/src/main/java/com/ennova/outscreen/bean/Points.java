package com.ennova.outscreen.bean;

import java.util.List;

/**
 * @作者 zhouchao
 * @日期 2019/4/9
 * @描述
 */
public class Points {

    /**
     * code : 0
     * data : [{"id":44,"shopName":"龙虎宝藏","shopType":2,"longitude":116.985084,"latitude":28.121462},{"id":43,"shopName":"景德镇瓷器","shopType":2,"longitude":116.984864,"latitude":28.121315},{"id":42,"shopName":"进口商品直销中心","shopType":2,"longitude":116.98468,"latitude":28.121187},{"id":41,"shopName":"三碗","shopType":3,"longitude":116.984644,"latitude":28.121319},{"id":40,"shopName":"古越丝绸","shopType":2,"longitude":116.984455,"latitude":28.121546},{"id":39,"shopName":"有家打铁铺","shopType":0,"longitude":116.986787,"latitude":28.122298},{"id":38,"shopName":"古越邮局","shopType":2,"longitude":116.986602,"latitude":28.122386},{"id":37,"shopName":"泥人张画糖人","shopType":0,"longitude":116.986526,"latitude":28.12243},{"id":36,"shopName":"芝士热狗棒","shopType":0,"longitude":116.986319,"latitude":28.122521},{"id":35,"shopName":"韩式炒年糕","shopType":0,"longitude":116.986288,"latitude":28.122525},{"id":34,"shopName":"山西刀削面","shopType":0,"longitude":116.986252,"latitude":28.122541},{"id":33,"shopName":"北京卤煮","shopType":0,"longitude":116.985628,"latitude":28.122625},{"id":45,"shopName":"龙虎山特产","shopType":1,"longitude":116.985228,"latitude":28.121558},{"id":46,"shopName":"奶茶店","shopType":0,"longitude":116.985246,"latitude":28.121629},{"id":47,"shopName":"古越扇堂","shopType":2,"longitude":116.9853,"latitude":28.121673},{"id":59,"shopName":"嘉乐即食水饺","shopType":3,"longitude":116.984208,"latitude":28.121777},{"id":58,"shopName":"老北京炸酱面","shopType":0,"longitude":116.984249,"latitude":28.121805},{"id":57,"shopName":"武汉热干面","shopType":0,"longitude":116.98433,"latitude":28.121833},{"id":56,"shopName":"俄罗斯汉堡","shopType":0,"longitude":116.984478,"latitude":28.121912},{"id":55,"shopName":"状元帽","shopType":2,"longitude":116.984819,"latitude":28.122175},{"id":54,"shopName":"龙虎山土鸡炒粉","shopType":3,"longitude":116.985102,"latitude":28.122338},{"id":53,"shopName":"七修良品","shopType":2,"longitude":116.985376,"latitude":28.121864},{"id":52,"shopName":"石语轩","shopType":2,"longitude":116.985318,"latitude":28.121848},{"id":51,"shopName":"长念夫人","shopType":2,"longitude":116.985232,"latitude":28.121793},{"id":50,"shopName":"蜡像馆","shopType":2,"longitude":116.985174,"latitude":28.121741},{"id":49,"shopName":"东北特产","shopType":1,"longitude":116.985426,"latitude":28.121733},{"id":48,"shopName":"傩文化艺术馆","shopType":2,"longitude":116.985367,"latitude":28.121705},{"id":32,"shopName":"上清铁板豆腐","shopType":0,"longitude":116.98561,"latitude":28.122629},{"id":31,"shopName":"革命小串","shopType":0,"longitude":116.985551,"latitude":28.122617},{"id":30,"shopName":"香脆大薯条","shopType":0,"longitude":116.985538,"latitude":28.122625},{"id":15,"shopName":"龙虎山栗香源","shopType":1,"longitude":116.98433,"latitude":28.122036},{"id":14,"shopName":"深海巨无霸","shopType":0,"longitude":116.984321,"latitude":28.12202},{"id":13,"shopName":"老北京爆肚","shopType":0,"longitude":116.984289,"latitude":28.122012},{"id":12,"shopName":"赵记大肉串","shopType":0,"longitude":116.984267,"latitude":28.122},{"id":11,"shopName":"长沙臭豆腐","shopType":0,"longitude":116.984249,"latitude":28.12198},{"id":10,"shopName":"冰糖雪梨","shopType":0,"longitude":116.984226,"latitude":28.121976},{"id":9,"shopName":"手工酸辣粉","shopType":0,"longitude":116.98419,"latitude":28.12196},{"id":8,"shopName":"西施豆腐","shopType":0,"longitude":116.984172,"latitude":28.121936},{"id":7,"shopName":"武大郎烧饼","shopType":0,"longitude":116.984155,"latitude":28.121932},{"id":6,"shopName":"古越非遗草编","shopType":2,"longitude":116.984155,"latitude":28.121932},{"id":5,"shopName":"山楂坊","shopType":0,"longitude":116.984141,"latitude":28.121936},{"id":4,"shopName":"一品关东煮","shopType":0,"longitude":116.984078,"latitude":28.121904},{"id":16,"shopName":"神农三宝","shopType":0,"longitude":116.984482,"latitude":28.122167},{"id":17,"shopName":"小哥烤面筋","shopType":0,"longitude":116.984536,"latitude":28.122195},{"id":29,"shopName":"关中切糕","shopType":0,"longitude":116.985448,"latitude":28.122625},{"id":28,"shopName":"铁板鸭肠","shopType":0,"longitude":116.98543,"latitude":28.122629},{"id":27,"shopName":"周小姐的鲜果时光","shopType":0,"longitude":116.985394,"latitude":28.122637},{"id":26,"shopName":"山药苦荞坊","shopType":0,"longitude":116.985376,"latitude":28.122629},{"id":25,"shopName":"河南信阳固始鹅块","shopType":3,"longitude":116.98534,"latitude":28.122633},{"id":24,"shopName":"轰炸鱿鱼","shopType":0,"longitude":116.985057,"latitude":28.122521},{"id":23,"shopName":"开心凉粉","shopType":0,"longitude":116.984985,"latitude":28.122486},{"id":22,"shopName":"乡巴佬糍粑","shopType":0,"longitude":116.984924,"latitude":28.122446},{"id":21,"shopName":"千里香馄饨","shopType":0,"longitude":116.984864,"latitude":28.122406},{"id":20,"shopName":"西安羊肉泡馍","shopType":0,"longitude":116.984702,"latitude":28.122306},{"id":19,"shopName":"陕西凉皮","shopType":0,"longitude":116.984644,"latitude":28.122263},{"id":18,"shopName":"肉夹馍","shopType":0,"longitude":116.984608,"latitude":28.122247},{"id":3,"shopName":"解忧杂货铺","shopType":1,"longitude":116.984065,"latitude":28.121888}]
     * msg :
     * errorMsg :
     */

    private int code;
    private String msg;
    private String errorMsg;
    private List<Point> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public List<Point> getData() {
        return data;
    }

    public void setData(List<Point> data) {
        this.data = data;
    }

    public static class Point {
        /**
         * id : 44
         * shopName : 龙虎宝藏
         * shopType : 2
         * longitude : 116.985084
         * latitude : 28.121462
         */

        private int id;
        private String shopName;
        private int shopType;
        private double longitude;
        private double latitude;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getShopName() {
            return shopName;
        }

        public void setShopName(String shopName) {
            this.shopName = shopName;
        }

        public int getShopType() {
            return shopType;
        }

        public void setShopType(int shopType) {
            this.shopType = shopType;
        }

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }
    }
}

package entity;

/**
 * 地址类 — 通过组合方式被 Student 使用
 */
public class Address {
    private String province;   // 省份
    private String city;       // 城市
    private String street;     // 街道
    private String doorNumber; // 门牌号

    public Address(String province, String city, String street, String doorNumber) {
        this.province = province;
        this.city = city;
        this.street = street;
        this.doorNumber = doorNumber;
    }

    /** 格式化展示地址 */
    public String format() {
        return province + "省 " + city + "市 " + street + " " + doorNumber + "号";
    }

    @Override
    public String toString() {
        return format();
    }

    // ==================== Getters / Setters ====================

    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }

    public String getDoorNumber() { return doorNumber; }
    public void setDoorNumber(String doorNumber) { this.doorNumber = doorNumber; }
}

package com.mo9.raptor.entity;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by jyou on 2018/10/22.
 *
 * @author jyou
 */
@Entity
@Table(name = "ip")
public class IpEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ip_start")
    private String ipStart;

    @Column(name = "ip_end")
    private String ipEnd;

    @Column(name = "ip_start_num")
    private Long ipStartNum;

    @Column(name = "ip_end_num")
    private Long ipEndNum;

    @Column(name = "continent")
    private String continent;

    @Column(name = "country")
    private String country;

    @Column(name = "province")
    private String province;

    @Column(name = "city")
    private String city;

    @Column(name = "district")
    private String district;

    @Column(name = "isp")
    private String isp;

    @Column(name = "area_code")
    private String areaCode;

    @Column(name = "country_english")
    private String countryEnglish;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "longitude")
    private BigDecimal longitude;

    @Column(name = "latitude")
    private BigDecimal latitude;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIpStart() {
        return ipStart;
    }

    public void setIpStart(String ipStart) {
        this.ipStart = ipStart;
    }

    public String getIpEnd() {
        return ipEnd;
    }

    public void setIpEnd(String ipEnd) {
        this.ipEnd = ipEnd;
    }

    public Long getIpStartNum() {
        return ipStartNum;
    }

    public void setIpStartNum(Long ipStartNum) {
        this.ipStartNum = ipStartNum;
    }

    public Long getIpEndNum() {
        return ipEndNum;
    }

    public void setIpEndNum(Long ipEndNum) {
        this.ipEndNum = ipEndNum;
    }

    public String getContinent() {
        return continent;
    }

    public void setContinent(String continent) {
        this.continent = continent;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getIsp() {
        return isp;
    }

    public void setIsp(String isp) {
        this.isp = isp;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getCountryEnglish() {
        return countryEnglish;
    }

    public void setCountryEnglish(String countryEnglish) {
        this.countryEnglish = countryEnglish;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public BigDecimal getLongitude() {
        return longitude;
    }

    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    public BigDecimal getLatitude() {
        return latitude;
    }

    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }
}

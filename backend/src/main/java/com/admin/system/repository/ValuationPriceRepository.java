package com.admin.system.repository;

import com.admin.system.entity.ValuationPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ValuationPriceRepository extends JpaRepository<ValuationPrice, Long> {

    /**
     * 按城市和地址精确匹配
     */
    Optional<ValuationPrice> findFirstByCityAndAddress(String city, String address);

    /**
     * 按城市和地址模糊匹配（用于回退查询）
     */
    Optional<ValuationPrice> findFirstByCityAndAddressContaining(String city, String address);

    /** 精确匹配地址 */
    Optional<ValuationPrice> findFirstByAddress(String address);

    /** 模糊匹配地址（用于缺少城市时的回退查询） */
    Optional<ValuationPrice> findFirstByAddressContaining(String address);

    //@Query("SELECT DISTINCT v.city FROM ValuationPrice v WHERE v.city IS NOT NULL AND v.city <> '' ORDER BY v.city ASC")
    @Query(value = "select name from d_city where province_id=18961 and type=1", nativeQuery = true)
    List<String> findDistinctCities();

    //@Query("SELECT DISTINCT v.district FROM ValuationPrice v WHERE v.city = :city AND v.district IS NOT NULL AND v.district <> '' ORDER BY v.district ASC")
    @Query(value = "select a.name from d_district a left join d_city b on a.city_id=b.id where b.province_id=18961 and b.name= :city and a.parent_id is null", nativeQuery = true)
    List<String> findDistinctDistrictsByCity(@Param("city") String city);
}

package com.furniture.miley.sales.repository.order;

import com.furniture.miley.sales.dto.dashboard.OrdersCountByStatus;
import com.furniture.miley.sales.dto.dashboard.SalesByMonth;
import com.furniture.miley.sales.dto.dashboard.SalesByUser;
import com.furniture.miley.sales.enums.OrderStatus;
import com.furniture.miley.sales.model.order.Order;
import com.furniture.miley.security.model.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByUser(User user, Sort sort);
    long countByStatus(OrderStatus orderStatus);
    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o WHERE o.status = 'ENTREGADO'")
    BigDecimal getTotalCompleteOrders();

    @Query("SELECT NEW com.furniture.miley.sales.dto.dashboard.SalesByMonth(m.month, COALESCE(SUM(o.total), 0))" +
            "FROM (SELECT 1 AS month UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 " +
            "UNION SELECT 5 UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 " +
            "UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12) m " +
            "LEFT JOIN Order o ON m.month = EXTRACT(MONTH FROM o.completedDate) " +
            "AND EXTRACT(YEAR FROM o.completedDate) = :year " +
            "GROUP BY m.month " +
            "ORDER BY m.month")
    List<SalesByMonth> findVentasMensualesPorAno(@Param("year") int year);


    @Query(value = "SELECT " +
            "  CASE " +
            "    WHEN os.distance >= 0 AND os.distance < 10 THEN '0-10 km' " +
            "    WHEN os.distance >= 10 AND os.distance < 40 THEN '10-40 km' " +
            "    WHEN os.distance >= 40 AND os.distance < 100 THEN '40-100 km' " +
            "    ELSE 'Over 100 km' " +
            "  END AS distanceRange, " +
            "  AVG(DATEDIFF(o.completed_date, o.created_date)) AS avgDurationInDays " +
            "FROM orders o " +
            "INNER JOIN order_shipping os ON o.id = os.order_id " +
            "WHERE o.completed_date IS NOT NULL " +
            "GROUP BY distanceRange " +
            "ORDER BY distanceRange",
            nativeQuery = true)
    List<Object[]> findAverageDurationByDistanceRange();

    @Query("SELECT NEW com.furniture.miley.sales.dto.dashboard.OrdersCountByStatus(o.status, COUNT(o)) FROM Order o GROUP BY o.status")
    List<OrdersCountByStatus> getOrdersCountByStatus();

    @Query("SELECT COALESCE(SUM(o.total), 0) FROM Order o " +
            "WHERE o.status = 'ENTREGADO'")
    BigDecimal getTotalSales();

    // Consulta para obtener el número de ventas por cliente
    @Query("SELECT NEW com.furniture.miley.sales.dto.dashboard.SalesByUser(CONCAT(pi.firstName, ' ', pi.lastName), COUNT(o.id)) " +
            "FROM Order o " +
            "JOIN o.user u " +
            "JOIN u.personalInformation pi " +
            "WHERE o.status = 'ENTREGADO' " +
            "GROUP BY u.id " +
            "ORDER BY COUNT(o.id) DESC " +
            "LIMIT :top")
    List<SalesByUser> findTopSalesByUser(@Param("top") int top);
}
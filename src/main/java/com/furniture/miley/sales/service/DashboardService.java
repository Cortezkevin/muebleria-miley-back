package com.furniture.miley.sales.service;

import com.furniture.miley.catalog.repository.CategoryRepository;
import com.furniture.miley.catalog.repository.ProductRepository;
import com.furniture.miley.catalog.repository.SubCategoryRepository;
import com.furniture.miley.sales.dto.dashboard.*;
import com.furniture.miley.sales.enums.OrderStatus;
import com.furniture.miley.sales.repository.order.OrderDetailRepository;
import com.furniture.miley.sales.repository.order.OrderRepository;
import com.furniture.miley.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final UserRepository userRepository;

    public List<SalesByProduct> getTopSalesByProduct(Integer top, Integer year, Integer month, String order){
        List<SalesByProduct> topSalesProductByYear;
        if( order.equals("DESC") ){
            topSalesProductByYear = productRepository.findTopMoreSalesProducts(top, year, month);
        }else {
            topSalesProductByYear = productRepository.findTopLowSalesProducts(top, year, month);
        }
        return topSalesProductByYear;
    }

    public List<SalesByUser> getTopSalesByUser(Integer top){
        return orderRepository.findTopSalesByUser(top);
    }

    public List<SalesByMonth> getSalesMonthByYear(Integer year){
        return orderRepository.findVentasMensualesPorAno(year);
    }

    public SalesDashboard getSales(){
        BigDecimal totalSales = orderRepository.getTotalSales();
        Integer products = (int) productRepository.count();
        Integer categories = (int) categoryRepository.count();
        Integer subcategories = (int) subCategoryRepository.count();
        Integer users = (int) userRepository.count();
        Integer orders = (int) orderRepository.count();
        Integer pendingOrders = (int) orderRepository.countByStatus(OrderStatus.PENDING);
        Integer completedOrders = (int) orderRepository.countByStatus(OrderStatus.ENTREGADO);
        Integer cancelledOrders = (int) orderRepository.countByStatus(OrderStatus.ANULADO);

        Timestamp date = new Timestamp(System.currentTimeMillis());
        List<SalesByProduct> topSalesProductByYear = productRepository.findTopMoreSalesProducts(5, date.toLocalDateTime().getYear(), date.toLocalDateTime().getMonth().getValue());
        List<SalesByMonth> salesByMonth = orderRepository.findVentasMensualesPorAno(2024);
        List<SalesByUser> salesByUsers = orderRepository.findTopSalesByUser(5);

        List<OrdersCountByStatus> ordersCountByStatus = new ArrayList<>();
        ordersCountByStatus.add(new OrdersCountByStatus(OrderStatus.ENTREGADO, completedOrders.longValue()));
        ordersCountByStatus.add(new OrdersCountByStatus(OrderStatus.PENDING, pendingOrders.longValue()));
        ordersCountByStatus.add(new OrdersCountByStatus(OrderStatus.ANULADO, cancelledOrders.longValue()));
        ordersCountByStatus.add(new OrdersCountByStatus(OrderStatus.IN_PROGRESS, (long) (orders - (pendingOrders + cancelledOrders + completedOrders))));

        List<OrderDurationPerDistanceAVG> avgByDistanceRange = orderRepository.findAverageDurationByDistanceRange().stream().map(a -> {
            return new OrderDurationPerDistanceAVG( a[0].toString(), Double.parseDouble(a[1].toString()) );
        }).toList();

        SalesDashboard salesDashboard = new SalesDashboard(
                products,
                categories,
                subcategories,
                users,
                orders,
                totalSales,
                avgByDistanceRange,
                ordersCountByStatus,
                topSalesProductByYear,
                salesByUsers,
                salesByMonth
        );
        return salesDashboard;
    }
}

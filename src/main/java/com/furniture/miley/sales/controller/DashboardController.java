package com.furniture.miley.sales.controller;

import com.furniture.miley.commons.constants.ResponseMessage;
import com.furniture.miley.commons.dto.SuccessResponseDTO;
import com.furniture.miley.sales.dto.dashboard.SalesByMonth;
import com.furniture.miley.sales.dto.dashboard.SalesByProduct;
import com.furniture.miley.sales.dto.dashboard.SalesByUser;
import com.furniture.miley.sales.dto.dashboard.SalesDashboard;
import com.furniture.miley.sales.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/sales")
    public ResponseEntity<SuccessResponseDTO<SalesDashboard>> getSales(){
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        dashboardService.getSales()
                )
        );
    }

    @GetMapping("/sales/month/{year}")
    public ResponseEntity<SuccessResponseDTO<List<SalesByMonth>>> getSalesMonthByYear(
            @PathVariable Integer year
    ){
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        dashboardService.getSalesMonthByYear( year)
                )
        );
    }


    @GetMapping("/sales/product")
    public ResponseEntity<SuccessResponseDTO<List<SalesByProduct>>> getSalesTopByProduct(
            @RequestParam("top") Integer top,
            @RequestParam("year") Integer year,
            @RequestParam("month") Integer month,
            @RequestParam("order") String order
    ){
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        dashboardService.getTopSalesByProduct( top, year, month, order )
                )
        );
    }

    @GetMapping("/sales/user/{top}")
    public ResponseEntity<SuccessResponseDTO<List<SalesByUser>>> getSalesTopByUser(
            @PathVariable Integer top
    ){
        return ResponseEntity.ok(
                new SuccessResponseDTO<>(
                        ResponseMessage.SUCCESS,
                        HttpStatus.OK.name(),
                        dashboardService.getTopSalesByUser( top )
                )
        );
    }

}


package com.emart.parking.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.emart.parking.model.ParkingLot;
import com.emart.parking.model.ParkingRecord;
import com.emart.parking.model.ParkingSpace;
import com.emart.parking.util.CsvExporter;

/**
 * 주차장 서비스 클래스 - 주차장 로직 처리 담당
 */
public class ParkingService {
  private final ParkingLot parkingLot;
  private final CsvExporter csvExporter;

  public ParkingService() {
    this.parkingLot = new ParkingLot();
    this.csvExporter = new CsvExporter();
  }

  // 차량 입차 처리
  public boolean parkCar(int floor, int spaceNumber, String carNumber) {
    return parkingLot.parkCar(floor, spaceNumber, carNumber);
  }

  // 차량 출차 처리
  public ParkingRecord exitCar(int floor, int spaceNumber) {
    return parkingLot.exitCar(floor, spaceNumber);
  }

  // 특정 층의 주차 공간 목록 조회
  public List<ParkingSpace> getParkingSpacesByFloor(int floor) {
    return parkingLot.getParkingSpacesByFloor(floor);
  }

  // 특정 주차 공간 조회
  public ParkingSpace getParkingSpace(int floor, int spaceNumber) {
    return parkingLot.getParkingSpace(floor, spaceNumber);
  }

  // 층별 주차 현황 통계
  public Map<Integer, Map<String, Integer>> getStatisticsByFloor() {
    return parkingLot.getStatisticsByFloor();
  }

  // 전체 주차장 현황 통계
  public Map<String, Integer> getTotalStatistics() {
    return parkingLot.getTotalStatistics();
  }

  // 특정 날짜의 매출 조회
  public int getDailyRevenue(LocalDate date) {
    return parkingLot.getDailyRevenue(date);
  }

  // 오늘의 매출 조회
  public int getTodayRevenue() {
    return getDailyRevenue(LocalDate.now());
  }

  // 주차 기록 조회
  public List<ParkingRecord> getParkingRecords() {
    return parkingLot.getParkingRecords();
  }

  // 주차 기록을 CSV 파일로 내보내기
  public boolean exportParkingRecordsToCsv(String filePath) {
    return csvExporter.exportParkingRecords(parkingLot.getParkingRecords(), filePath);
  }

  // 주차장 층 목록 조회
  public List<Integer> getFloorList() {
    return parkingLot.getFloorList();
  }
}